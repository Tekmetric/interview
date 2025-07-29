#!/usr/bin/env python3
"""
NASA Near Earth Object Data Processor

This script fetches Near Earth Object data from NASA's API and processes it
using PySpark for scalability. It extracts, transforms, and saves the data
in Parquet format with aggregations.

Author: Tekmetric Data Engineering Interview
"""

import os
import sys
import json
import time
import logging
from datetime import datetime
from typing import List, Dict, Optional, Any
from pathlib import Path

import requests
import pandas as pd
from pyspark.sql import SparkSession, DataFrame
from pyspark.sql.functions import col, when, count, year, sum as spark_sum, lit, explode, regexp_extract
from pyspark.sql.types import (
    StructType, StructField, StringType, FloatType, BooleanType, 
    IntegerType, ArrayType, DoubleType
)
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# Setup logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


class NASAAPIError(Exception):
    """Custom exception for NASA API errors"""
    pass


class NEODataProcessor:
    """Process Near Earth Object data from NASA API using PySpark"""
    
    def __init__(self, api_key: Optional[str] = None, spark_session: Optional[SparkSession] = None):
        self.api_key = api_key or os.getenv('NASA_API_KEY', 'DEMO_KEY')
        self.base_url = "https://ssd-api.jpl.nasa.gov"
        self.spark = spark_session or self._create_spark_session()
        self.session = requests.Session()
        
        # API endpoints
        self.close_approach_endpoint = f"{self.base_url}/cad.api"
        self.sbdb_endpoint = f"{self.base_url}/sbdb.api"
        
        # Create output directory structure (S3-like)
        self.base_output_dir = Path("data")
        self.raw_data_dir = self.base_output_dir / "raw" / "neo" / f"year={datetime.now().year}"
        self.processed_data_dir = self.base_output_dir / "processed" / "neo" / f"year={datetime.now().year}"
        self.aggregations_dir = self.base_output_dir / "aggregations" / "neo" / f"year={datetime.now().year}"
        
        # Create directories
        for directory in [self.raw_data_dir, self.processed_data_dir, self.aggregations_dir]:
            directory.mkdir(parents=True, exist_ok=True)
    
    def _create_spark_session(self) -> SparkSession:
        """Create and configure Spark session for local and distributed execution"""
        return SparkSession.builder \
            .appName("NASA_NEO_Data_Processor") \
            .config("spark.sql.adaptive.enabled", "true") \
            .config("spark.sql.adaptive.coalescePartitions.enabled", "true") \
            .config("spark.sql.adaptive.coalescePartitions.minPartitionNum", "1") \
            .config("spark.sql.adaptive.advisoryPartitionSizeInBytes", "64MB") \
            .config("spark.sql.execution.arrow.pyspark.enabled", "true") \
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer") \
            .getOrCreate()
    
    def fetch_neo_objects(self, limit: int = 200) -> List[Dict[str, Any]]:
        """
        Fetch first 200 Near Earth Objects using Close Approach Data API
        
        Args:
            limit: Number of objects to fetch (default 200)
            
        Returns:
            List of NEO objects data
        """
        logger.info(f"Fetching first {limit} Near Earth Objects...")
        
        # Parameters for Close Approach Data API to get NEOs
        params = {
            'neo': 'true',  # Limit to NEOs only
            'limit': limit,  # Limit to first 200 objects
            'sort': 'object',  # Sort by object name for consistency
            'fullname': 'true',  # Include full names
            'diameter': 'true',  # Include diameter data
        }
        
        try:
            response = self.session.get(
                self.close_approach_endpoint,
                params=params,
                timeout=60
            )
            response.raise_for_status()
            
            data = response.json()
            
            if 'data' not in data:
                raise NASAAPIError(f"No data field in response: {data}")
            
            logger.info(f"Successfully fetched {data.get('count', 0)} close approach records")
            return data
            
        except requests.RequestException as e:
            logger.error(f"API request failed: {e}")
            raise NASAAPIError(f"Failed to fetch NEO data: {e}")
        except json.JSONDecodeError as e:
            logger.error(f"Failed to decode JSON response: {e}")
            raise NASAAPIError(f"Invalid JSON response: {e}")
    
    def fetch_detailed_object_data(self, object_designations: List[str]) -> List[Dict[str, Any]]:
        """
        Fetch detailed data for specific objects using SBDB API
        
        Args:
            object_designations: List of object designations to fetch
            
        Returns:
            List of detailed object data
        """
        logger.info(f"Fetching detailed data for {len(object_designations)} objects...")
        
        detailed_objects = []
        
        for i, designation in enumerate(object_designations):
            if i > 0 and i % 10 == 0:
                logger.info(f"Processed {i}/{len(object_designations)} objects...")
                time.sleep(1)  # Rate limiting
            
            try:
                params = {
                    'sstr': designation,
                    'phys-par': 'true',  # Include physical parameters
                    'ca-data': 'true',   # Include close approach data
                    'ca-body': 'Earth'   # Earth close approaches only
                }
                
                response = self.session.get(
                    self.sbdb_endpoint,
                    params=params,
                    timeout=30
                )
                response.raise_for_status()
                
                obj_data = response.json()
                if 'object' in obj_data:
                    detailed_objects.append(obj_data)
                
            except requests.RequestException as e:
                logger.warning(f"Failed to fetch data for {designation}: {e}")
                continue
            except json.JSONDecodeError as e:
                logger.warning(f"Invalid JSON for {designation}: {e}")
                continue
        
        logger.info(f"Successfully fetched detailed data for {len(detailed_objects)} objects")
        return detailed_objects
    
    def extract_neo_data(self, close_approach_data: Dict[str, Any], detailed_objects: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """
        Extract and combine NEO data from API responses
        
        Args:
            close_approach_data: Data from Close Approach API
            detailed_objects: Detailed object data from SBDB API
            
        Returns:
            List of processed NEO records
        """
        logger.info("Extracting and processing NEO data...")
        
        # Create lookup for detailed object data
        detailed_lookup = {}
        for obj in detailed_objects:
            if 'object' in obj and 'des' in obj['object']:
                detailed_lookup[obj['object']['des']] = obj
        
        processed_records = []
        fields = close_approach_data.get('fields', [])
        
        # Process each close approach record
        for record in close_approach_data.get('data', []):
            try:
                # Map fields to values
                record_dict = dict(zip(fields, record))
                
                # Extract object designation
                designation = record_dict.get('des', '')
                
                # Get detailed object data if available
                detailed_obj = detailed_lookup.get(designation, {})
                object_data = detailed_obj.get('object', {})
                orbit_data = detailed_obj.get('orbit', {})
                phys_data = detailed_obj.get('phys_par', [])
                ca_data = detailed_obj.get('ca_data', [])
                
                # Extract required fields
                neo_record = {
                    # Basic identification
                    'id': designation,
                    'neo_reference_id': object_data.get('spkid', ''),
                    'name': record_dict.get('fullname', '').strip(),
                    'name_limited': object_data.get('shortname', ''),
                    'designation': designation,
                    'nasa_jpl_url': f"https://ssd.jpl.nasa.gov/tools/sbdb_lookup.html#/?sstr={designation}",
                    
                    # Physical parameters
                    'absolute_magnitude_h': self._extract_physical_param(phys_data, 'H'),
                    'is_potentially_hazardous_asteroid': object_data.get('pha', False),
                    
                    # Diameter estimates
                    'minimum_estimated_diameter_meters': self._get_diameter_min(record_dict, phys_data),
                    'maximum_estimated_diameter_meters': self._get_diameter_max(record_dict, phys_data),
                    
                    # Closest approach data
                    'closest_approach_miss_distance_kilometers': self._convert_au_to_km(float(record_dict.get('dist', 0))),
                    'closest_approach_date': record_dict.get('cd', ''),
                    'closest_approach_relative_velocity_kms': float(record_dict.get('v_rel', 0)),
                    
                    # Observation data
                    'first_observation_date': orbit_data.get('first_obs', ''),
                    'last_observation_date': orbit_data.get('last_obs', ''),
                    'observations_used': orbit_data.get('n_obs_used', 0),
                    'orbital_period': self._extract_orbital_element(orbit_data, 'per'),
                    
                    # Raw close approach data for aggregations
                    'miss_distance_astronomical': float(record_dict.get('dist', 0))
                }
                
                processed_records.append(neo_record)
                
            except Exception as e:
                logger.warning(f"Error processing record {record}: {e}")
                continue
        
        logger.info(f"Successfully processed {len(processed_records)} NEO records")
        return processed_records
    
    def _extract_physical_param(self, phys_data: List[Dict], param_name: str) -> Optional[float]:
        """Extract physical parameter by name"""
        for param in phys_data:
            if param.get('name') == param_name:
                try:
                    return float(param.get('value', 0))
                except (ValueError, TypeError):
                    return None
        return None
    
    def _extract_orbital_element(self, orbit_data: Dict, element_name: str) -> Optional[float]:
        """Extract orbital element by name"""
        elements = orbit_data.get('elements', [])
        for element in elements:
            if element.get('name') == element_name:
                try:
                    return float(element.get('value', 0))
                except (ValueError, TypeError):
                    return None
        return None
    
    def _get_diameter_min(self, record_dict: Dict, phys_data: List[Dict]) -> Optional[float]:
        """Get minimum diameter estimate"""
        # Try from close approach data first
        diameter = record_dict.get('diameter')
        if diameter:
            try:
                return float(diameter) * 1000  # Convert km to meters
            except (ValueError, TypeError):
                pass
        
        # Try from physical parameters
        diameter_param = self._extract_physical_param(phys_data, 'diameter')
        if diameter_param:
            return diameter_param * 1000  # Convert km to meters
        
        return None
    
    def _get_diameter_max(self, record_dict: Dict, phys_data: List[Dict]) -> Optional[float]:
        """Get maximum diameter estimate (same as min for now, could be enhanced)"""
        return self._get_diameter_min(record_dict, phys_data)
    
    def _convert_au_to_km(self, au_distance: float) -> float:
        """Convert astronomical units to kilometers"""
        AU_TO_KM = 149597870.7  # 1 AU in kilometers
        return au_distance * AU_TO_KM
    
    def create_spark_dataframe(self, neo_records: List[Dict[str, Any]]) -> DataFrame:
        """
        Create Spark DataFrame from NEO records
        
        Args:
            neo_records: List of NEO data records
            
        Returns:
            Spark DataFrame
        """
        logger.info("Creating Spark DataFrame...")
        
        # Define schema for better performance and type safety
        schema = StructType([
            StructField("id", StringType(), True),
            StructField("neo_reference_id", StringType(), True),
            StructField("name", StringType(), True),
            StructField("name_limited", StringType(), True),
            StructField("designation", StringType(), True),
            StructField("nasa_jpl_url", StringType(), True),
            StructField("absolute_magnitude_h", DoubleType(), True),
            StructField("is_potentially_hazardous_asteroid", BooleanType(), True),
            StructField("minimum_estimated_diameter_meters", DoubleType(), True),
            StructField("maximum_estimated_diameter_meters", DoubleType(), True),
            StructField("closest_approach_miss_distance_kilometers", DoubleType(), True),
            StructField("closest_approach_date", StringType(), True),
            StructField("closest_approach_relative_velocity_kms", DoubleType(), True),
            StructField("first_observation_date", StringType(), True),
            StructField("last_observation_date", StringType(), True),
            StructField("observations_used", IntegerType(), True),
            StructField("orbital_period", DoubleType(), True),
            StructField("miss_distance_astronomical", DoubleType(), True)
        ])
        
        # Create DataFrame
        df = self.spark.createDataFrame(neo_records, schema)
        
        logger.info(f"Created DataFrame with {df.count()} records")
        return df
    
    def save_raw_data(self, neo_records: List[Dict[str, Any]]) -> None:
        """Save raw NEO data to Parquet format"""
        logger.info("Saving raw data...")
        
        # Save as JSON for debugging and backup
        raw_json_path = self.raw_data_dir / "neo_raw_data.json"
        with open(raw_json_path, 'w') as f:
            json.dump(neo_records, f, indent=2, default=str)
        
        # Save as Parquet using Spark
        df = self.create_spark_dataframe(neo_records)
        parquet_path = str(self.raw_data_dir / "neo_raw_data.parquet")
        
        df.coalesce(1).write \
            .mode("overwrite") \
            .option("compression", "snappy") \
            .parquet(parquet_path)
        
        logger.info(f"Raw data saved to {parquet_path}")
    
    def save_processed_data(self, df: DataFrame) -> None:
        """Save processed NEO data to Parquet format"""
        logger.info("Saving processed data...")
        
        parquet_path = str(self.processed_data_dir / "neo_processed_data.parquet")
        
        df.coalesce(1).write \
            .mode("overwrite") \
            .option("compression", "snappy") \
            .parquet(parquet_path)
        
        logger.info(f"Processed data saved to {parquet_path}")
    
    def calculate_aggregations(self, df: DataFrame) -> Dict[str, Any]:
        """
        Calculate required aggregations
        
        Args:
            df: Spark DataFrame with NEO data
            
        Returns:
            Dictionary with aggregation results
        """
        logger.info("Calculating aggregations...")
        
        # Aggregation 1: Number of close approaches closer than 0.2 AU
        close_approaches_count = df.filter(col("miss_distance_astronomical") < 0.2).count()
        
        # Aggregation 2: Number of close approaches by year
        # Extract year from closest_approach_date and count
        df_with_year = df.withColumn(
            "approach_year",
            regexp_extract(col("closest_approach_date"), r"(\d{4})", 1).cast(IntegerType())
        ).filter(col("approach_year").isNotNull())
        
        yearly_counts = df_with_year.groupBy("approach_year") \
            .agg(count("*").alias("approach_count")) \
            .orderBy("approach_year") \
            .collect()
        
        # Convert to dictionary
        yearly_counts_dict = {row["approach_year"]: row["approach_count"] for row in yearly_counts}
        
        aggregations = {
            "close_approaches_under_02_au": close_approaches_count,
            "approaches_by_year": yearly_counts_dict,
            "total_objects_processed": df.count(),
            "calculation_timestamp": datetime.now().isoformat()
        }
        
        logger.info(f"Aggregations calculated: {aggregations}")
        return aggregations
    
    def save_aggregations(self, aggregations: Dict[str, Any]) -> None:
        """Save aggregations to JSON and Parquet formats"""
        logger.info("Saving aggregations...")
        
        # Save as JSON
        json_path = self.aggregations_dir / "neo_aggregations.json"
        with open(json_path, 'w') as f:
            json.dump(aggregations, f, indent=2, default=str)
        
        # Save yearly data as Parquet
        if aggregations.get("approaches_by_year"):
            yearly_data = [
                {"year": year, "approach_count": count}
                for year, count in aggregations["approaches_by_year"].items()
            ]
            
            yearly_df = self.spark.createDataFrame(yearly_data)
            yearly_parquet_path = str(self.aggregations_dir / "approaches_by_year.parquet")
            
            yearly_df.coalesce(1).write \
                .mode("overwrite") \
                .option("compression", "snappy") \
                .parquet(yearly_parquet_path)
        
        logger.info(f"Aggregations saved to {self.aggregations_dir}")
    
    def process_neo_data(self, limit: int = 200) -> None:
        """
        Main processing pipeline for NEO data
        
        Args:
            limit: Number of NEO objects to process
        """
        logger.info("Starting NEO data processing pipeline...")
        
        try:
            # Step 1: Fetch close approach data to get first 200 NEOs
            close_approach_data = self.fetch_neo_objects(limit)
            
            # Step 2: Extract unique object designations
            unique_objects = set()
            for record in close_approach_data.get('data', []):
                if len(record) > 0:
                    unique_objects.add(record[0])  # First field is designation
            
            unique_objects = list(unique_objects)[:limit]  # Limit to first 200 unique objects
            logger.info(f"Found {len(unique_objects)} unique objects")
            
            # Step 3: Fetch detailed data for each object
            detailed_objects = self.fetch_detailed_object_data(unique_objects)
            
            # Step 4: Extract and process NEO data
            neo_records = self.extract_neo_data(close_approach_data, detailed_objects)
            
            # Step 5: Create Spark DataFrame
            df = self.create_spark_dataframe(neo_records)
            
            # Step 6: Save raw data
            self.save_raw_data(neo_records)
            
            # Step 7: Save processed data
            self.save_processed_data(df)
            
            # Step 8: Calculate and save aggregations
            aggregations = self.calculate_aggregations(df)
            self.save_aggregations(aggregations)
            
            logger.info("NEO data processing pipeline completed successfully!")
            
        except Exception as e:
            logger.error(f"Pipeline failed: {e}")
            raise
        finally:
            # Cleanup
            if hasattr(self, 'spark'):
                self.spark.stop()


def main():
    """Main entry point"""
    try:
        # Initialize processor
        processor = NEODataProcessor()
        
        # Process NEO data
        processor.process_neo_data(limit=200)
        
        print("✅ Processing completed successfully!")
        print(f"📁 Output directory: {processor.base_output_dir}")
        print(f"📊 Raw data: {processor.raw_data_dir}")
        print(f"📈 Processed data: {processor.processed_data_dir}")
        print(f"📋 Aggregations: {processor.aggregations_dir}")
        
    except Exception as e:
        print(f"❌ Processing failed: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main() 