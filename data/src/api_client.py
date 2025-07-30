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
            neo_df = self.spark.createDataFrame(all_neo_data)
            
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
        Fetch NEO data sequentially from NeoWs API
        This avoids distributed HTTP call issues
        """
        logger.info("Fetching NEO data sequentially from NeoWs API")
        
        session = self._create_session()
        all_neo_data = []
        seen_neos = set()
        
        try:
            # Generate date ranges for the past year
            date_ranges = self._generate_date_ranges(days_back=365)
            
            for i, (start_date, end_date) in enumerate(date_ranges):
                if len(all_neo_data) >= limit:
                    break
                    
                logger.info(f"Fetching NEOs for date range {start_date} to {end_date} ({i+1}/{len(date_ranges)})")
                
                # Rate limiting
                if i > 0:
                    time.sleep(self.config.rate_limit_delay)
                
                try:
                    params = {
                        'start_date': start_date,
                        'end_date': end_date,
                        'api_key': self.config.api_key
                    }
                    
                    response = self._make_request(session, self.config.neo_feed_endpoint, params)
                    data = response.json()
                    
                    # Extract NEO data from the response
                    if 'near_earth_objects' in data:
                        for date_key, neos_for_date in data['near_earth_objects'].items():
                            for neo_data in neos_for_date:
                                neo_id = neo_data.get('id')
                                if neo_id and neo_id not in seen_neos:
                                    all_neo_data.append(neo_data)
                                    seen_neos.add(neo_id)
                                    
                                    # Stop if we've reached the limit
                                    if len(all_neo_data) >= limit:
                                        break
                            if len(all_neo_data) >= limit:
                                break
                    
                    logger.info(f"Collected {len(all_neo_data)} unique NEOs so far...")
                    
                except Exception as e:
                    logger.warning(f"Failed to fetch data for range {start_date} to {end_date}: {e}")
                    continue
            
            logger.info(f"Successfully fetched {len(all_neo_data)} unique NEO objects")
            return all_neo_data[:limit]  # Ensure we don't exceed limit
            
        except Exception as e:
            logger.error(f"Failed to fetch NEO data: {e}")
            return []
        finally:
            session.close()
    
    def _generate_date_ranges(self, days_back: int = 365) -> List[tuple]:
        """Generate date ranges for NeoWs feed API (max 7 days per request)"""
        end_date = datetime.now()
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