"""
NASA API client using Spark for parallel data extraction
"""

import json
import time
import logging
from typing import List, Dict, Any, Optional, Iterator
import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry
from pyspark.sql import SparkSession
from pyspark.sql.functions import col, lit, collect_list
from pyspark.sql.types import StructType, StructField, StringType, IntegerType

from .config import APIConfig
from .models import CloseApproachData, ObjectDetails
from .models import NASAAPIError, RateLimitError, APITimeoutError

logger = logging.getLogger(__name__)


class NASAAPIClient:
    """NASA API client using Spark for parallel processing"""
    
    def __init__(self, config: APIConfig, spark: SparkSession):
        self.config = config
        self.spark = spark
        
    def fetch_close_approach_data(self, limit: int = 200) -> List[CloseApproachData]:
        """
        Fetch close approach data for NEO objects (same as original - single API call)
        """
        logger.info(f"Fetching close approach data (limit: {limit})")
        
        # This remains single-threaded as it's one API call that returns all data
        session = self._create_session()
        params = {
            'neo': 'true',
            'limit': limit,
            'sort': 'object',
            'fullname': 'true',
            'diameter': 'true',
        }
        
        try:
            response = self._make_request(session, self.config.close_approach_endpoint, params)
            data = response.json()
            
            if 'data' not in data:
                raise NASAAPIError("Invalid response format: missing 'data' field")
            
            # Parse records into CloseApproachData objects
            close_approaches = []
            for record in data['data']:
                try:
                    approach = CloseApproachData.from_api_record(record)
                    close_approaches.append(approach)
                except Exception as e:
                    logger.warning(f"Failed to parse record {record}: {e}")
                    continue
            
            logger.info(f"Successfully fetched {len(close_approaches)} close approach records")
            return close_approaches
            
        except requests.RequestException as e:
            raise NASAAPIError(f"Close approach API request failed: {e}")
        except json.JSONDecodeError as e:
            raise NASAAPIError(f"Invalid JSON response: {e}")
        finally:
            session.close()
    
    def fetch_object_details_distributed(self, designations: List[str], 
                                       parallelism: Optional[int] = None) -> List[ObjectDetails]:
        """
        Fetch detailed object data using Spark for parallel processing
        
        Args:
            designations: List of object designations
            parallelism: Number of parallel partitions (defaults to Spark's default)
            
        Returns:
            List of ObjectDetails objects
        """
        logger.info(f"Fetching detailed data for {len(designations)} objects using Spark distributed processing")
        
        if not designations:
            return []
        
        # Create DataFrame with designations
        schema = StructType([
            StructField("designation", StringType(), False),
            StructField("batch_id", IntegerType(), False)
        ])
        
        # Add batch IDs for rate limiting within partitions
        designation_data = [
            (designation, i // self.config.max_retries) 
            for i, designation in enumerate(designations)
        ]
        
        designations_df = self.spark.createDataFrame(designation_data, schema)
        
        # Set optimal parallelism
        if parallelism:
            designations_df = designations_df.repartition(parallelism)
        else:
            # Use number of cores or reasonable default
            num_cores = int(self.spark.conf.get("spark.executor.instances", "1")) * \
                       int(self.spark.conf.get("spark.executor.cores", "2"))
            optimal_partitions = min(max(num_cores, 2), len(designations) // 5)
            designations_df = designations_df.repartition(optimal_partitions)
        
        logger.info(f"Using {designations_df.rdd.getNumPartitions()} partitions for distributed processing")
        
        # Broadcast config for workers
        broadcast_config = self.spark.sparkContext.broadcast({
            'api_key': self.config.api_key,
            'base_url': self.config.base_url,
            'request_timeout': self.config.request_timeout,
            'rate_limit_delay': self.config.rate_limit_delay,
            'max_retries': self.config.max_retries,
            'sbdb_endpoint': self.config.sbdb_endpoint
        })
        
        # Process in parallel using mapPartitions
        def process_partition(partition: Iterator) -> Iterator[Dict[str, Any]]:
            """Process a partition of designations"""
            return _process_designation_partition(partition, broadcast_config.value)
        
        # Execute distributed processing
        results_rdd = designations_df.rdd.mapPartitions(process_partition)
        
        # Collect results
        results = results_rdd.collect()
        
        # Convert results to ObjectDetails objects
        object_details = []
        successful_count = 0
        
        for result in results:
            if result.get('success'):
                try:
                    # Reconstruct the API response format
                    api_response = {'object': result['data']}
                    details = ObjectDetails.from_api_response(api_response)
                    object_details.append(details)
                    successful_count += 1
                except Exception as e:
                    logger.warning(f"Failed to parse result for {result.get('designation')}: {e}")
            else:
                logger.warning(f"Failed to fetch {result.get('designation')}: {result.get('error')}")
        
        logger.info(f"Successfully fetched detailed data for {successful_count}/{len(designations)} objects using distributed processing")
        return object_details
    
    def _create_session(self) -> requests.Session:
        """Create HTTP session with retry strategy"""
        session = requests.Session()
        
        # Configure retry strategy
        retry_strategy = Retry(
            total=self.config.max_retries,
            status_forcelist=[429, 500, 502, 503, 504],
            allowed_methods=["HEAD", "GET", "OPTIONS"],
            backoff_factor=1
        )
        
        adapter = HTTPAdapter(max_retries=retry_strategy)
        session.mount("http://", adapter)
        session.mount("https://", adapter)
        
        return session
    
    def _make_request(self, session: requests.Session, url: str, 
                     params: Dict[str, Any]) -> requests.Response:
        """Make HTTP request with timeout and error handling"""
        try:
            response = session.get(url, params=params, timeout=self.config.request_timeout)
            response.raise_for_status()
            return response
            
        except requests.Timeout:
            raise APITimeoutError(f"Request timeout for {url}")
        except requests.RequestException as e:
            if hasattr(e, 'response') and e.response is not None:
                if e.response.status_code == 429:
                    raise RateLimitError("Rate limit exceeded")
                elif e.response.status_code >= 500:
                    raise NASAAPIError(f"Server error ({e.response.status_code})")
            raise NASAAPIError(f"Request failed: {e}")


def _process_designation_partition(partition: Iterator, config: Dict[str, Any]) -> Iterator[Dict[str, Any]]:
    """
    Process a partition of designations (runs on Spark workers)
    
    This function runs on individual Spark workers and processes a batch of designations
    """
    import requests
    from requests.adapters import HTTPAdapter
    from urllib3.util.retry import Retry
    import time
    import json
    
    # Create session for this worker
    session = requests.Session()
    retry_strategy = Retry(
        total=config['max_retries'],
        status_forcelist=[429, 500, 502, 503, 504],
        allowed_methods=["HEAD", "GET", "OPTIONS"],
        backoff_factor=1
    )
    
    adapter = HTTPAdapter(max_retries=retry_strategy)
    session.mount("http://", adapter)
    session.mount("https://", adapter)
    
    results = []
    partition_items = list(partition)
    
    for i, row in enumerate(partition_items):
        designation = row['designation']
        batch_id = row['batch_id']
        
        # Rate limiting within partition
        if i > 0:
            time.sleep(config['rate_limit_delay'])
        
        # Additional delay for different batches to spread load
        if batch_id > 0:
            time.sleep(config['rate_limit_delay'] * 0.5)
        
        try:
            result = _fetch_single_object_details_worker(session, designation, config)
            results.append(result)
            
        except Exception as e:
            results.append({
                'designation': designation,
                'success': False,
                'error': str(e),
                'data': None
            })
    
    session.close()
    return iter(results)


def _fetch_single_object_details_worker(session: requests.Session, designation: str, 
                                      config: Dict[str, Any]) -> Dict[str, Any]:
    """Fetch details for a single object (worker function)"""
    params = {
        'sstr': designation,
        'phys-par': 'true',
        'ca-data': 'true',
        'ca-body': 'Earth'
    }
    
    try:
        response = session.get(
            config['sbdb_endpoint'],
            params=params,
            timeout=config['request_timeout']
        )
        response.raise_for_status()
        
        data = response.json()
        
        if 'object' not in data:
            return {
                'designation': designation,
                'success': False,
                'error': 'No object data found',
                'data': None
            }
        
        return {
            'designation': designation,
            'success': True,
            'error': None,
            'data': data['object']
        }
        
    except requests.Timeout:
        return {
            'designation': designation,
            'success': False,
            'error': 'Request timeout',
            'data': None
        }
    except requests.RequestException as e:
        error_msg = f"Request failed: {e}"
        if hasattr(e, 'response') and e.response is not None:
            if e.response.status_code == 429:
                error_msg = "Rate limit exceeded"
            elif e.response.status_code >= 500:
                error_msg = f"Server error ({e.response.status_code})"
        
        return {
            'designation': designation,
            'success': False,
            'error': error_msg,
            'data': None
        }
    except json.JSONDecodeError as e:
        return {
            'designation': designation,
            'success': False,
            'error': f"Invalid JSON response: {e}",
            'data': None
        } 