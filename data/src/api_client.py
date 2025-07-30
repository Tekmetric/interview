"""
NASA NeoWs API client using Spark for scalable data processing
"""

import json
import time
import logging
from typing import List, Dict, Any, Optional
from datetime import datetime, timedelta
import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry
from pyspark.sql import SparkSession, DataFrame
from pyspark.sql.functions import col, lit, explode, size
from pyspark.sql.types import StructType, StructField, StringType, IntegerType

from .models import NASAAPIError

from .config import APIConfig
from .models import NASAAPIError, RateLimitError, APITimeoutError

logger = logging.getLogger(__name__)


class NASAAPIClient:
    """NASA NeoWs API client using Spark for scalable processing"""
    
    def __init__(self, config: APIConfig, spark: SparkSession):
        self.config = config
        self.spark = spark
        
    def fetch_neo_data_distributed(self, limit: int = 200, parallelism: Optional[int] = None) -> DataFrame:
        """
        Fetch NEO data with embedded close approach data using NeoWs API
        Simplified approach: fetch data sequentially, then distribute processing
        
        Args:
            limit: Maximum number of NEO objects to fetch
            parallelism: Number of parallel partitions for processing
            
        Returns:
            Spark DataFrame with all NEO and close approach data
        """
        logger.info(f"Fetching {limit} NEO objects with close approach data using NeoWs API")
        
        # Fetch data sequentially in the driver to avoid distributed HTTP issues
        all_neo_data = self._fetch_neo_data_sequential(limit)
        
        if not all_neo_data:
            logger.warning("No NEO data retrieved")
            return self._create_empty_neo_dataframe()
        
        logger.info(f"Successfully collected {len(all_neo_data)} NEO objects. Creating Spark DataFrame...")
        
        # Convert to Spark DataFrame for distributed processing
        try:
            # Convert dictionaries to JSON strings for proper schema inference
            import json
            json_strings = [json.dumps(neo) for neo in all_neo_data]
            neo_rdd = self.spark.sparkContext.parallelize(json_strings)
            neo_df = self.spark.read.json(neo_rdd)
            
            # Repartition for optimal processing if parallelism specified
            if parallelism:
                neo_df = neo_df.repartition(parallelism)
            
            logger.info(f"Created Spark DataFrame with {neo_df.count()} NEO records")
            return neo_df
            
        except Exception as e:
            logger.error(f"Failed to create Spark DataFrame: {e}")
            return self._create_empty_neo_dataframe()
    
    def _fetch_neo_data_sequential(self, limit: int) -> List[Dict[str, Any]]:
        """
        Fetch NEO data from NASA Browse API with simple pagination
        """
        logger.info(f"Starting fresh Browse API fetch for {limit} NEO objects")
        
        all_neo_data = []
        page = 0
        page_size = 20  # Browse API standard page size
        
        # Create a simple requests session
        session = requests.Session()
        session.headers.update({
            'User-Agent': 'NEO-Data-Processor/1.0',
            'Accept': 'application/json'
        })
        
        try:
            while len(all_neo_data) < limit:
                logger.info(f"Requesting page {page} (have {len(all_neo_data)}/{limit} objects)")
                
                # Simple rate limiting - wait 2 seconds between requests
                if page > 0:
                    logger.info("Waiting 2 seconds for rate limiting...")
                    time.sleep(2.0)
                
                # Build request parameters
                params = {
                    'page': page,
                    'size': min(page_size, limit - len(all_neo_data)),
                    'api_key': self.config.api_key
                }
                
                # Make the API request with timeout
                try:
                    logger.info(f"GET {self.config.neo_browse_endpoint} with params: {params}")
                    response = session.get(
                        self.config.neo_browse_endpoint,
                        params=params,
                        timeout=60  # Longer timeout to avoid hangs
                    )
                    
                    logger.info(f"Response received: {response.status_code}")
                    
                    # Simple error handling
                    if response.status_code == 429:
                        logger.warning("Rate limited (429) - waiting 30 seconds")
                        time.sleep(30)
                        continue  # Retry the same page
                    
                    if response.status_code != 200:
                        logger.error(f"API error {response.status_code}: {response.text[:200]}")
                        raise NASAAPIError(f"Browse API returned {response.status_code}")
                    
                    # Parse response
                    try:
                        data = response.json()
                    except Exception as e:
                        logger.error(f"Failed to parse JSON: {e}")
                        raise NASAAPIError(f"Invalid JSON response: {e}")
                    
                    # Extract NEO objects
                    if 'near_earth_objects' not in data:
                        logger.error(f"No 'near_earth_objects' key in response. Keys: {list(data.keys())}")
                        raise NASAAPIError("Unexpected API response structure")
                    
                    page_neos = data['near_earth_objects']
                    logger.info(f"Found {len(page_neos)} NEOs on page {page}")
                    
                    # Check if we've reached the end
                    if not page_neos:
                        logger.info("No more NEO objects available - reached end of dataset")
                        break
                    
                    # Add NEOs to our collection
                    before_count = len(all_neo_data)
                    for neo in page_neos:
                        if len(all_neo_data) >= limit:
                            break
                        all_neo_data.append(neo)
                    
                    added = len(all_neo_data) - before_count
                    logger.info(f"Added {added} NEOs (total: {len(all_neo_data)})")
                    
                    # Check if this was the last page (partial page indicates end)
                    if len(page_neos) < page_size:
                        logger.info("Received partial page - reached end of dataset")
                        break
                    
                    page += 1
                    
                    # Safety check to prevent infinite loops
                    if page > 50:  # Max 50 pages (1000 objects)
                        logger.warning("Reached maximum page limit (50) - stopping")
                        break
                        
                except requests.exceptions.Timeout:
                    logger.error(f"Request timeout on page {page}")
                    raise NASAAPIError(f"Request timeout on page {page}")
                    
                except requests.exceptions.RequestException as e:
                    logger.error(f"Request failed on page {page}: {e}")
                    raise NASAAPIError(f"Request failed: {e}")
            
            logger.info(f"Browse API fetch completed: {len(all_neo_data)} objects collected")
            return all_neo_data
            
        except Exception as e:
            logger.error(f"Browse API fetch failed: {e}")
            raise
        finally:
            session.close()
    
    def _generate_date_ranges(self, days_back: int = 365) -> List[tuple]:
        """
        Generate date ranges for API calls (kept for potential future use)
        NOTE: Browse API uses pagination instead of date ranges
        """
        from datetime import datetime, timedelta
        
        end_date = datetime.now().date()
        start_date = end_date - timedelta(days=days_back)
        date_ranges = []

        current_date = start_date
        
        while current_date < end_date:
            range_end = min(current_date + timedelta(days=6), end_date)  # Max 7 days
            date_ranges.append((
                current_date.strftime('%Y-%m-%d'),
                range_end.strftime('%Y-%m-%d')
            ))
            current_date = range_end + timedelta(days=1)
        
        return date_ranges
    
    def _create_empty_neo_dataframe(self) -> DataFrame:
        """Create empty DataFrame with expected NEO schema"""
        schema = StructType([
            StructField("id", StringType(), True),
            StructField("neo_reference_id", StringType(), True),
            StructField("name", StringType(), True),
            StructField("nasa_jpl_url", StringType(), True),
            StructField("absolute_magnitude_h", StringType(), True),
            StructField("estimated_diameter", StringType(), True),
            StructField("is_potentially_hazardous_asteroid", StringType(), True),
            StructField("close_approach_data", StringType(), True),
        ])
        
        return self.spark.createDataFrame([], schema)
    
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