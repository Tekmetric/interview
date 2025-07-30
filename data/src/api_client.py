"""
NASA NeoWs API client using Spark for scalable data extraction
"""

import json
import time
import logging
from typing import List, Dict, Any, Optional, Iterator
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
        This is the single source of truth - gets all required data in one call
        
        Args:
            limit: Maximum number of NEO objects to fetch
            parallelism: Number of parallel partitions for processing
            
        Returns:
            Spark DataFrame with all NEO and close approach data
        """
        logger.info(f"Fetching {limit} NEO objects with close approach data using NeoWs API")
        
        # Calculate date ranges to cover for distributed fetching
        # NeoWs feed API works with date ranges (max 7 days per request)
        date_ranges = self._generate_date_ranges(days_back=365)  # Cover last year
        
        # Create DataFrame with date ranges for distributed processing
        schema = StructType([
            StructField("start_date", StringType(), False),
            StructField("end_date", StringType(), False),
            StructField("batch_id", IntegerType(), False)
        ])
        
        date_range_data = [
            (start_date, end_date, i)
            for i, (start_date, end_date) in enumerate(date_ranges)
        ]
        
        date_ranges_df = self.spark.createDataFrame(date_range_data, schema)
        
        # Set optimal parallelism
        if parallelism:
            date_ranges_df = date_ranges_df.repartition(parallelism)
        else:
            # Calculate optimal partitions
            num_cores = int(self.spark.conf.get("spark.executor.instances", "1")) * \
                       int(self.spark.conf.get("spark.executor.cores", "2"))
            optimal_partitions = min(max(num_cores, 2), len(date_ranges))
            date_ranges_df = date_ranges_df.repartition(optimal_partitions)
        
        logger.info(f"Using {date_ranges_df.rdd.getNumPartitions()} partitions for distributed NeoWs processing")
        
        # Broadcast config for workers
        broadcast_config = self.spark.sparkContext.broadcast({
            'api_key': self.config.api_key,
            'base_url': self.config.base_url,
            'neo_feed_endpoint': self.config.neo_feed_endpoint,
            'request_timeout': self.config.request_timeout,
            'rate_limit_delay': self.config.rate_limit_delay,
            'max_retries': self.config.max_retries,
        })
        
        # Process in parallel using mapPartitions
        def process_date_range_partition(partition: Iterator) -> Iterator[Dict[str, Any]]:
            """Process a partition of date ranges to get NEO data"""
            return _process_neows_partition(partition, broadcast_config.value)
        
        # Execute distributed processing
        results_rdd = date_ranges_df.rdd.mapPartitions(process_date_range_partition)
        
        # Collect results and create unified DataFrame
        results = results_rdd.collect()
        
        # Combine all NEO data from different date ranges
        all_neo_data = []
        seen_neos = set()  # Track unique NEOs to avoid duplicates
        
        for result in results:
            if result.get('success') and result.get('neo_data'):
                for neo_data in result['neo_data']:
                    neo_id = neo_data.get('id')
                    if neo_id and neo_id not in seen_neos:
                        all_neo_data.append(neo_data)
                        seen_neos.add(neo_id)
                        
                        # Stop if we've reached the limit
                        if len(all_neo_data) >= limit:
                            break
                if len(all_neo_data) >= limit:
                    break
        
        # Limit to requested number
        all_neo_data = all_neo_data[:limit]
        
        logger.info(f"Successfully collected {len(all_neo_data)} unique NEO objects with close approach data")
        
        # Convert to Spark DataFrame
        if all_neo_data:
            neo_df = self.spark.createDataFrame(all_neo_data)
            logger.info(f"Created Spark DataFrame with {neo_df.count()} NEO records")
            return neo_df
        else:
            # Return empty DataFrame with expected schema
            return self._create_empty_neo_dataframe()
    
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
        # This would be based on the actual NeoWs response schema
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


def _process_neows_partition(partition: Iterator, config: Dict[str, Any]) -> Iterator[Dict[str, Any]]:
    """
    Process a partition of date ranges to get NEO data from NeoWs API
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
        start_date = row['start_date']
        end_date = row['end_date']
        batch_id = row['batch_id']
        
        # Rate limiting within partition
        if i > 0:
            time.sleep(config['rate_limit_delay'])
        
        # Additional delay for different batches
        if batch_id > 0:
            time.sleep(config['rate_limit_delay'] * 0.5)
        
        try:
            # Use NeoWs Feed API to get NEO data with close approaches
            params = {
                'start_date': start_date,
                'end_date': end_date,
                'api_key': config['api_key']
            }
            
            response = session.get(
                config['neo_feed_endpoint'],
                params=params,
                timeout=config['request_timeout']
            )
            response.raise_for_status()
            
            data = response.json()
            
            # Extract NEO data from the response
            neo_data = []
            if 'near_earth_objects' in data:
                for date_key, neos_for_date in data['near_earth_objects'].items():
                    neo_data.extend(neos_for_date)
            
            results.append({
                'date_range': f"{start_date} to {end_date}",
                'success': True,
                'error': None,
                'neo_data': neo_data
            })
            
        except Exception as e:
            results.append({
                'date_range': f"{start_date} to {end_date}",
                'success': False,
                'error': str(e),
                'neo_data': []
            })
    
    session.close()
    return iter(results) 