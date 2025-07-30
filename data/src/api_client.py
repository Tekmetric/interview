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
from .models import CloseApproachData, ObjectDetails, NEOObject
from .models import NASAAPIError, RateLimitError, APITimeoutError

logger = logging.getLogger(__name__)


class NASAAPIClient:
    """NASA API client using Spark for parallel processing"""
    
    def __init__(self, config: APIConfig, spark: SparkSession):
        self.config = config
        self.spark = spark
        
    def fetch_neo_objects(self, limit: int = 200, neo_only: bool = True) -> List[NEOObject]:
        """
        Fetch Near Earth Objects using Close Approach Data API
        This is the logical first step - get the objects we want to analyze
        
        Note: We use the Close Approach Data API to get NEO objects since it provides
        a reliable way to get a list of NEOs. Then we'll get ALL close approach data
        for these specific objects.
        
        Args:
            limit: Maximum number of NEO objects to fetch
            neo_only: If True, limit to NEOs only
            
        Returns:
            List of NEOObject objects
        """
        logger.info(f"Fetching {limit} NEO objects using Close Approach Data API")
        
        session = self._create_session()
        
        # Use Close Approach Data API to get a list of NEO objects
        # We'll extract unique NEO designations from recent close approaches
        params = {
            'neo': 'true' if neo_only else 'false',
            'limit': limit * 3,  # Get more to ensure we have enough unique objects
            'sort': 'object',
            'fullname': 'true',
            'diameter': 'true'
        }
        
        try:
            response = self._make_request(session, f"{self.config.base_url}/cad.api", params)
            data = response.json()
            
            if 'data' not in data:
                raise NASAAPIError("Invalid response format: missing 'data' field")
            
            # Extract unique NEO objects from close approach data
            unique_objects = {}
            fields = data.get('fields', [])
            
            for record in data['data']:
                try:
                    # Extract designation from the record (first field)
                    designation = record[0] if len(record) > 0 else ""
                    if designation and designation not in unique_objects:
                        # Create a simple NEOObject from the available data
                        neo_obj = NEOObject(
                            designation=designation,
                            full_name=record[-1] if len(record) > 13 and record[-1] else None,  # fullname field
                            h_magnitude=float(record[11]) if len(record) > 11 and record[11] else None,  # H field
                            neo=True if neo_only else None,
                            diameter_km=float(record[12]) if len(record) > 12 and record[12] else None  # diameter field
                        )
                        unique_objects[designation] = neo_obj
                        
                        # Stop once we have enough unique objects
                        if len(unique_objects) >= limit:
                            break
                            
                except Exception as e:
                    logger.warning(f"Failed to parse NEO record {record}: {e}")
                    continue
            
            neo_objects = list(unique_objects.values())
            logger.info(f"Successfully extracted {len(neo_objects)} unique NEO objects")
            return neo_objects
            
        except requests.RequestException as e:
            raise NASAAPIError(f"Close Approach Data API request failed: {e}")
        except json.JSONDecodeError as e:
            raise NASAAPIError(f"Invalid JSON response: {e}")
        finally:
            session.close()
    
    def fetch_close_approaches_for_neos_distributed(self, neo_objects: List[NEOObject], 
                                                  parallelism: Optional[int] = None) -> List[CloseApproachData]:
        """
        Fetch ALL close approach data for specific NEO objects using distributed processing
        This is the second step - get all close approach data for our selected NEOs
        
        Args:
            neo_objects: List of NEO objects to get close approach data for
            parallelism: Number of parallel partitions for processing
            
        Returns:
            List of CloseApproachData objects
        """
        logger.info(f"Fetching close approach data for {len(neo_objects)} NEOs using distributed processing")
        
        if not neo_objects:
            return []
        
        # Extract designations for processing
        designations = [obj.designation for obj in neo_objects]
        
        # Create DataFrame with designations
        schema = StructType([
            StructField("designation", StringType(), False),
            StructField("batch_id", IntegerType(), False)
        ])
        
        # Add batch IDs for rate limiting
        designation_data = [
            (designation, i // 10)  # Group into batches of 10 for rate limiting
            for i, designation in enumerate(designations)
        ]
        
        designations_df = self.spark.createDataFrame(designation_data, schema)
        
        # Set optimal parallelism
        if parallelism:
            designations_df = designations_df.repartition(parallelism)
        else:
            # Calculate optimal partitions based on available cores
            num_cores = int(self.spark.conf.get("spark.executor.instances", "1")) * \
                       int(self.spark.conf.get("spark.executor.cores", "2"))
            optimal_partitions = min(max(num_cores, 2), len(designations) // 5)
            designations_df = designations_df.repartition(optimal_partitions)
        
        logger.info(f"Using {designations_df.rdd.getNumPartitions()} partitions for distributed processing")
        
        # Broadcast config for workers
        broadcast_config = self.spark.sparkContext.broadcast({
            'base_url': self.config.base_url,
            'request_timeout': self.config.request_timeout,
            'rate_limit_delay': self.config.rate_limit_delay,
            'max_retries': self.config.max_retries,
        })
        
        # Process in parallel using mapPartitions
        def process_partition(partition: Iterator) -> Iterator[Dict[str, Any]]:
            """Process a partition of NEO designations to get their close approach data"""
            return _process_close_approach_partition(partition, broadcast_config.value)
        
        # Execute distributed processing
        results_rdd = designations_df.rdd.mapPartitions(process_partition)
        
        # Collect results
        results = results_rdd.collect()
        
        # Convert results to CloseApproachData objects
        close_approaches = []
        successful_count = 0
        
        for result in results:
            if result.get('success') and result.get('data'):
                try:
                    # Each result can contain multiple close approaches for one NEO
                    for ca_record in result['data']:
                        ca = CloseApproachData.from_api_record(ca_record)
                        close_approaches.append(ca)
                    successful_count += 1
                except Exception as e:
                    logger.warning(f"Failed to parse close approach result: {e}")
            else:
                logger.warning(f"Failed to fetch close approaches for {result.get('designation')}: {result.get('error')}")
        
        logger.info(f"Successfully fetched close approach data for {successful_count}/{len(neo_objects)} NEOs")
        logger.info(f"Total close approaches found: {len(close_approaches)}")
        return close_approaches
    
    def fetch_object_details_distributed(self, neo_objects: List[NEOObject], 
                                       parallelism: Optional[int] = None) -> List[ObjectDetails]:
        """
        Fetch detailed object data for NEO objects using distributed processing
        This gets additional detailed information for our NEOs
        
        Args:
            neo_objects: List of NEO objects to get details for
            parallelism: Number of parallel partitions
            
        Returns:
            List of ObjectDetails objects
        """
        logger.info(f"Fetching detailed data for {len(neo_objects)} NEOs using distributed processing")
        
        if not neo_objects:
            return []
        
        # For detailed data, we'll use the existing distributed approach
        # but adapted for NEO objects instead of designations from close approaches
        designations = [obj.designation for obj in neo_objects]
        
        # Similar distributed processing as before but focused on NEO details
        schema = StructType([
            StructField("designation", StringType(), False),
            StructField("batch_id", IntegerType(), False)
        ])
        
        designation_data = [
            (designation, i // self.config.max_retries) 
            for i, designation in enumerate(designations)
        ]
        
        designations_df = self.spark.createDataFrame(designation_data, schema)
        
        if parallelism:
            designations_df = designations_df.repartition(parallelism)
        else:
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
            'sbdb_endpoint': f"{self.config.base_url}/sbdb.api"
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
        
        logger.info(f"Successfully fetched detailed data for {successful_count}/{len(neo_objects)} objects")
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


def _process_close_approach_partition(partition: Iterator, config: Dict[str, Any]) -> Iterator[Dict[str, Any]]:
    """
    Process a partition of NEO designations to get their close approach data
    This function runs on Spark workers
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
        
        # Additional delay for different batches
        if batch_id > 0:
            time.sleep(config['rate_limit_delay'] * 0.5)
        
        try:
            # Use Close Approach Data API to get ALL close approaches for this NEO
            params = {
                'des': designation,  # Specific object designation
                'date-min': '1900-01-01',  # Get historical data
                'date-max': '2100-01-01',  # Get future data
                'fullname': 'true',
                'sort': 'date'
            }
            
            response = session.get(
                f"{config['base_url']}/cad.api",
                params=params,
                timeout=config['request_timeout']
            )
            response.raise_for_status()
            
            data = response.json()
            
            if 'data' not in data:
                results.append({
                    'designation': designation,
                    'success': False,
                    'error': 'No close approach data found',
                    'data': None
                })
                continue
            
            results.append({
                'designation': designation,
                'success': True,
                'error': None,
                'data': data['data']  # All close approach records for this NEO
            })
            
        except Exception as e:
            results.append({
                'designation': designation,
                'success': False,
                'error': str(e),
                'data': None
            })
    
    session.close()
    return iter(results)


def _process_designation_partition(partition: Iterator, config: Dict[str, Any]) -> Iterator[Dict[str, Any]]:
    """
    Process a partition of designations for detailed object data
    (Reusing existing function for object details)
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