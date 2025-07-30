"""
NASA NeoWs API client using Spark for scalable data processing
"""

import json
import time
import logging
from typing import List, Dict, Any, Optional
import requests
from pyspark.sql import SparkSession, DataFrame

from pyspark.sql.types import (
    StructType, StructField, StringType, FloatType, BooleanType, 
    ArrayType, IntegerType, DoubleType
)

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
        Simplified approach: initial iteration, fetch data sequentially, then later we'll migrate to distribute fetching
        Distributed data fetching will pose additional challenges, so we'll use a simpler approach for now
        
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
 