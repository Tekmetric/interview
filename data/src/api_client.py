"""
NASA NeoWs API client using Spark for scalable data processing
"""

import json
import time
import logging
import requests
from typing import List, Dict, Any, Optional, Tuple
from pyspark.sql import SparkSession, DataFrame
from pyspark.sql.types import (
    StructType, StructField, StringType, FloatType, BooleanType, 
    ArrayType, IntegerType, DoubleType
)

from .config import APIConfig
from .models import NASAAPIError

logger = logging.getLogger(__name__)


def _fetch_pages_on_executor(page_ranges_iterator, api_key: str, endpoint_url: str) -> List[List[Dict[str, Any]]]:
    """
    Standalone function that runs on Spark executors to fetch API data
    Each executor processes one or more page ranges
    
    Args:
        page_ranges_iterator: Iterator of (start_page, end_page, max_objects) tuples
        api_key: NASA API key
        endpoint_url: NASA Browse API endpoint URL
    
    Returns:
        List of lists containing NEO data for each page range
    """
    executor_results = []
    
    for start_page, end_page, max_objects in page_ranges_iterator:
        try:
            logger.info(f"Executor fetching pages {start_page}-{end_page-1}")
            
            # Create session for this executor
            session = requests.Session()
            session.headers.update({
                'User-Agent': 'NEO-Data-Processor/1.0',
                'Accept': 'application/json'
            })
            
            partition_data = []
            objects_fetched = 0
            
            for page in range(start_page, end_page):
                if objects_fetched >= max_objects:
                    break
                    
                # Calculate objects to fetch for this page
                objects_needed = min(20, max_objects - objects_fetched)
                
                # Build request parameters
                params = {
                    'page': page,
                    'size': objects_needed,
                    'api_key': api_key
                }
                
                # Make API request with retry logic
                max_retries = 3
                for attempt in range(max_retries):
                    try:
                        response = session.get(
                            endpoint_url,
                            params=params,
                            timeout=30
                        )
                        
                        if response.status_code == 429:
                            # Rate limited - wait and retry
                            wait_time = 2 ** attempt  # Exponential backoff
                            logger.warning(f"Rate limited on page {page}, waiting {wait_time}s")
                            time.sleep(wait_time)
                            continue
                        
                        if response.status_code != 200:
                            logger.error(f"API error {response.status_code} on page {page}")
                            break
                        
                        data = response.json()
                        page_neos = data.get('near_earth_objects', [])
                        
                        if not page_neos:
                            logger.info(f"No more data on page {page}")
                            break
                        
                        # Add to results
                        # FIXED: Use objects_needed directly, not objects_needed - objects_fetched
                        objects_to_take = min(len(page_neos), objects_needed, max_objects - objects_fetched)
                        for neo in page_neos[:objects_to_take]:
                            partition_data.append(neo)
                            objects_fetched += 1
                            
                            if objects_fetched >= max_objects:
                                break
                        
                        logger.info(f"Executor fetched {len(page_neos)} objects from page {page}")
                        break  # Success
                        
                    except Exception as e:
                        logger.error(f"Request failed on page {page}, attempt {attempt + 1}: {e}")
                        if attempt == max_retries - 1:
                            raise
                        time.sleep(1)
            
            session.close()
            executor_results.append(partition_data)
            logger.info(f"Executor completed: {len(partition_data)} objects from pages {start_page}-{end_page-1}")
            
        except Exception as e:
            logger.error(f"Executor failed on pages {start_page}-{end_page-1}: {e}")
            executor_results.append([])  # Return empty list for failed partition
    
    return executor_results


class NASAAPIClient:
    """NASA NeoWs API client with distributed fetching capabilities"""
    
    def __init__(self, config: APIConfig, spark: SparkSession):
        self.config = config
        self.spark = spark
    
    def fetch_neo_data_distributed(self, limit: int = 200, parallelism: Optional[int] = None) -> DataFrame:
        """
        Fetch NEO data with embedded close approach data using NeoWs API
        Choice between sequential (current) and parallel fetching
        
        Args:
            limit: Maximum number of NEO objects to fetch
            parallelism: Number of partitions for Spark DataFrame
        
        Returns:
            Spark DataFrame containing raw NEO data
        """
        logger.info(f"Fetching {limit} NEO objects with close approach data using NeoWs API")
        
        # For small datasets or testing, use sequential approach
        if limit <= 100:
            logger.info("Using sequential fetching for small dataset")
            return self._fetch_sequential_approach(limit, parallelism)
        else:
            logger.info("Using distributed fetching for large dataset")
            return self._fetch_distributed_approach(limit, parallelism)
    
    def _fetch_sequential_approach(self, limit: int, parallelism: Optional[int] = None) -> DataFrame:
        """Current sequential approach - good for small datasets"""
        all_neo_data = self._fetch_neo_data_sequential(limit)
        
        if not all_neo_data:
            logger.warning("No NEO data retrieved")
            return self._create_empty_neo_dataframe()
        
        logger.info(f"Successfully collected {len(all_neo_data)} NEO objects. Creating Spark DataFrame...")
        
        # Convert dictionaries to JSON strings for proper schema inference
        json_strings = [json.dumps(neo) for neo in all_neo_data]
        neo_rdd = self.spark.sparkContext.parallelize(json_strings)
        neo_df = self.spark.read.json(neo_rdd)
        
        # Repartition for optimal processing if parallelism specified
        if parallelism:
            neo_df = neo_df.repartition(parallelism)
        
        logger.info(f"Created Spark DataFrame with {neo_df.count()} NEO records")
        return neo_df
    
    def _fetch_distributed_approach(self, limit: int, parallelism: Optional[int] = None) -> DataFrame:
        """
        NEW: Distributed fetching using Spark executors
        Each executor fetches a range of pages in parallel
        """
        logger.info("Starting distributed API fetching")
        
        try:
            # Calculate page distribution
            page_size = 20  # Browse API standard page size
            total_pages = (limit + page_size - 1) // page_size  # Ceiling division
            
            # Determine number of partitions (executors to use)
            num_partitions = parallelism or min(total_pages, self.spark.sparkContext.defaultParallelism)
            pages_per_partition = max(1, total_pages // num_partitions)
            
            logger.info(f"Distributing {total_pages} pages across {num_partitions} executors")
            logger.info(f"~{pages_per_partition} pages per executor")
            
            # Create page ranges for each partition
            # FIXED: Ensure all pages are distributed, handle remainder properly
            page_ranges = []
            pages_assigned = 0
            
            for i in range(num_partitions):
                start_page = pages_assigned
                
                # Calculate pages for this partition
                if i == num_partitions - 1:
                    # Last partition gets all remaining pages
                    end_page = total_pages
                else:
                    # Regular partition gets standard allocation
                    end_page = min(start_page + pages_per_partition, total_pages)
                
                if start_page < total_pages:
                    pages_in_partition = end_page - start_page
                    objects_for_partition = min(page_size * pages_in_partition, 
                                              limit - start_page * page_size)
                    page_ranges.append((start_page, end_page, objects_for_partition))
                    pages_assigned = end_page
            
            logger.info(f"Created {len(page_ranges)} page ranges: {page_ranges}")
            
            # Verify all pages are covered
            total_pages_assigned = sum(end - start for start, end, _ in page_ranges)
            total_expected_objects = sum(objects for _, _, objects in page_ranges)
            logger.info(f"Coverage verification: {total_pages_assigned}/{total_pages} pages, expecting {total_expected_objects}/{limit} objects")
            
            # Create RDD with page ranges
            page_ranges_rdd = self.spark.sparkContext.parallelize(page_ranges, num_partitions)
            
            # Extract serializable config data for executors
            api_key = self.config.api_key
            endpoint_url = self.config.neo_browse_endpoint
            
            # Execute distributed fetching
            neo_data_rdd = page_ranges_rdd.mapPartitions(
                lambda iterator: _fetch_pages_on_executor(iterator, api_key, endpoint_url)
            )
            
            # Collect all results and flatten
            all_neo_data = neo_data_rdd.collect()
            flattened_data = []
            partition_counts = []
            
            for i, partition_data in enumerate(all_neo_data):
                partition_count = len(partition_data)
                partition_counts.append(partition_count)
                flattened_data.extend(partition_data)
                logger.info(f"Partition {i}: collected {partition_count} objects")
            
            logger.info(f"Distributed fetching completed: {len(flattened_data)} NEO objects")
            logger.info(f"Per-partition results: {partition_counts}, total: {sum(partition_counts)}")
            
            if not flattened_data:
                logger.warning("No NEO data retrieved from distributed fetch")
                return self._create_empty_neo_dataframe()
            
            # Convert to DataFrame
            json_strings = [json.dumps(neo) for neo in flattened_data]
            neo_rdd = self.spark.sparkContext.parallelize(json_strings)
            neo_df = self.spark.read.json(neo_rdd)
            
            logger.info(f"Created Spark DataFrame with {neo_df.count()} NEO records")
            return neo_df
            
        except Exception as e:
            logger.error(f"Distributed fetching failed, falling back to sequential: {e}")
            return self._fetch_sequential_approach(limit, parallelism)
    

    
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
    

    def _create_empty_neo_dataframe(self) -> DataFrame:
        """Create empty DataFrame with expected NEO schema matching Browse API response"""
        
        # Define nested structures to match Browse API response
        estimated_diameter_schema = StructType([
            StructField("kilometers", StructType([
                StructField("estimated_diameter_min", DoubleType(), True),
                StructField("estimated_diameter_max", DoubleType(), True)
            ]), True),
            StructField("meters", StructType([
                StructField("estimated_diameter_min", DoubleType(), True), 
                StructField("estimated_diameter_max", DoubleType(), True)
            ]), True),
            StructField("miles", StructType([
                StructField("estimated_diameter_min", DoubleType(), True),
                StructField("estimated_diameter_max", DoubleType(), True)
            ]), True),
            StructField("feet", StructType([
                StructField("estimated_diameter_min", DoubleType(), True),
                StructField("estimated_diameter_max", DoubleType(), True)
            ]), True)
        ])
        
        # Close approach data structure
        close_approach_schema = StructType([
            StructField("close_approach_date", StringType(), True),
            StructField("close_approach_date_full", StringType(), True),
            StructField("epoch_date_close_approach", DoubleType(), True),
            StructField("relative_velocity", StructType([
                StructField("kilometers_per_second", StringType(), True),
                StructField("kilometers_per_hour", StringType(), True),
                StructField("miles_per_hour", StringType(), True)
            ]), True),
            StructField("miss_distance", StructType([
                StructField("astronomical", StringType(), True),
                StructField("lunar", StringType(), True), 
                StructField("kilometers", StringType(), True),
                StructField("miles", StringType(), True)
            ]), True),
            StructField("orbiting_body", StringType(), True)
        ])
        
        # Orbital data structure
        orbital_data_schema = StructType([
            StructField("orbit_id", StringType(), True),
            StructField("orbit_determination_date", StringType(), True),
            StructField("first_observation_date", StringType(), True),
            StructField("last_observation_date", StringType(), True),
            StructField("data_arc_in_days", IntegerType(), True),
            StructField("observations_used", IntegerType(), True),
            StructField("orbit_uncertainty", StringType(), True),
            StructField("minimum_orbit_intersection", StringType(), True),
            StructField("jupiter_tisserand_invariant", StringType(), True),
            StructField("epoch_osculation", StringType(), True),
            StructField("eccentricity", StringType(), True),
            StructField("semi_major_axis", StringType(), True),
            StructField("inclination", StringType(), True),
            StructField("ascending_node_longitude", StringType(), True),
            StructField("orbital_period", StringType(), True),
            StructField("perihelion_distance", StringType(), True),
            StructField("perihelion_argument", StringType(), True),
            StructField("aphelion_distance", StringType(), True),
            StructField("perihelion_time", StringType(), True),
            StructField("mean_anomaly", StringType(), True),
            StructField("mean_motion", StringType(), True),
            StructField("equinox", StringType(), True),
            StructField("orbit_class", StructType([
                StructField("orbit_class_type", StringType(), True),
                StructField("orbit_class_description", StringType(), True),
                StructField("orbit_class_range", StringType(), True)
            ]), True)
        ])
        
        # Main schema matching Browse API response
        schema = StructType([
            StructField("id", StringType(), True),
            StructField("neo_reference_id", StringType(), True),
            StructField("name", StringType(), True),
            StructField("name_limited", StringType(), True),
            StructField("designation", StringType(), True),
            StructField("nasa_jpl_url", StringType(), True),
            StructField("absolute_magnitude_h", DoubleType(), True),
            StructField("estimated_diameter", estimated_diameter_schema, True),
            StructField("is_potentially_hazardous_asteroid", BooleanType(), True),
            StructField("close_approach_data", ArrayType(close_approach_schema), True),
            StructField("orbital_data", orbital_data_schema, True),
            StructField("is_sentry_object", BooleanType(), True)
        ])
        
        return self.spark.createDataFrame([], schema)
 