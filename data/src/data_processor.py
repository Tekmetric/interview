"""
Data processing module using Spark for NeoWs API data
"""

import logging
from typing import Dict, Any, Optional
from datetime import datetime
from pyspark.sql import SparkSession, DataFrame
from pyspark.sql.functions import (
    col, explode, size, when, isnan, isnull, count, 
    sum as spark_sum, avg, min as spark_min, max as spark_max, 
    stddev, year, month, dayofmonth, regexp_extract,
    from_json, split, lit
)
from pyspark.sql.types import (
    StructType, StructField, StringType, FloatType, BooleanType, 
    IntegerType, DoubleType, ArrayType
)

from .config import SparkConfig
from .models import Aggregations
from .models import DataProcessingError, SparkError

logger = logging.getLogger(__name__)


class NEODataProcessor:
    """Data processor for NeoWs API data using Spark"""
    
    def __init__(self, spark_config: SparkConfig):
        self.spark_config = spark_config
        self.spark = self._create_spark_session()
    
    def _create_spark_session(self) -> SparkSession:
        """Create optimized Spark session for NeoWs data processing"""
        try:
            builder = SparkSession.builder.appName(self.spark_config.app_name)
            
            # Apply Spark configurations
            for key, value in self.spark_config.to_spark_configs().items():
                builder = builder.config(key, value)
            
            # Additional optimizations for JSON processing
            builder = builder.config("spark.sql.adaptive.skewJoin.enabled", "true")
            builder = builder.config("spark.sql.adaptive.localShuffleReader.enabled", "true")
            
            spark = builder.getOrCreate()
            spark.sparkContext.setLogLevel("WARN")
            
            logger.info(f"Created Spark session: {self.spark_config.app_name}")
            return spark
            
        except Exception as e:
            raise SparkError(f"Failed to create Spark session: {e}")
    
    def process_neo_dataframe(self, neo_df: DataFrame) -> DataFrame:
        """
        Process NeoWs DataFrame to extract all required columns
        
        Args:
            neo_df: Raw DataFrame from NeoWs API
            
        Returns:
            Processed DataFrame with flattened structure and required columns
        """
        logger.info("Processing NeoWs DataFrame to extract required columns")
        
        try:
            # First, let's examine the structure of the DataFrame
            logger.info(f"Input DataFrame has {neo_df.count()} records")
            neo_df.printSchema()
            
            # Extract basic NEO information based on actual NeoWs schema
            processed_df = neo_df.select(
                col("id").alias("id"),
                col("neo_reference_id").alias("neo_reference_id"),
                col("name").alias("name"),
                col("name").alias("name_limited"),  # Use name for name_limited since it doesn't exist
                col("id").alias("designation"),  # Use id for designation since designation doesn't exist
                col("nasa_jpl_url").alias("nasa_jpl_url"),
                col("absolute_magnitude_h").cast("float").alias("absolute_magnitude_h"),
                col("is_potentially_hazardous_asteroid").cast("boolean").alias("is_potentially_hazardous_asteroid"),
                
                # Extract diameter information from estimated_diameter map
                col("estimated_diameter")["meters"]["estimated_diameter_min"].cast("float").alias("minimum_estimated_diameter_meters"),
                col("estimated_diameter")["meters"]["estimated_diameter_max"].cast("float").alias("maximum_estimated_diameter_meters"),
                
                # For orbital data, we'll use defaults since it's not in the NeoWs response
                lit(None).cast("string").alias("first_observation_date"),
                lit(None).cast("string").alias("last_observation_date"),
                lit(None).cast("int").alias("observations_used"),
                lit(None).cast("float").alias("orbital_period"),
                
                # Keep close approach data for explosion
                col("close_approach_data")
            )
            
            # Explode close approach data to get one row per close approach
            if "close_approach_data" in processed_df.columns:
                exploded_df = processed_df.select(
                    "*",
                    explode(col("close_approach_data")).alias("close_approach")
                ).drop("close_approach_data")
                
                # Extract close approach details from the struct format (Browse API)
                final_df = exploded_df.select(
                    "*",
                    col("close_approach.close_approach_date").alias("closest_approach_date"),
                    col("close_approach.miss_distance.kilometers").cast("float").alias("closest_approach_miss_distance_kilometers"),
                    col("close_approach.relative_velocity.kilometers_per_second").cast("float").alias("closest_approach_relative_velocity_kms"),
                    col("close_approach.miss_distance.astronomical").cast("float").alias("miss_distance_astronomical")
                ).drop("close_approach")
            else:
                final_df = processed_df
            
            # Add computed columns for analysis
            final_df = self._add_computed_columns(final_df)
            
            logger.info(f"Successfully processed DataFrame with {final_df.count()} rows")
            return final_df
            
        except Exception as e:
            raise DataProcessingError(f"Failed to process NEO DataFrame: {e}")
    
    def _add_computed_columns(self, df: DataFrame) -> DataFrame:
        """Add computed columns for analysis"""
        try:
            # Extract year from closest approach date for aggregations
            df_with_year = df.withColumn(
                "approach_year",
                year(col("closest_approach_date"))
            )
            
            # Add velocity categories
            df_with_velocity = df_with_year.withColumn(
                "velocity_category",
                when(col("closest_approach_relative_velocity_kms") < 5, "Slow")
                .when(col("closest_approach_relative_velocity_kms") < 15, "Medium")
                .when(col("closest_approach_relative_velocity_kms") < 25, "Fast")
                .otherwise("Very Fast")
            )
            
            # Add size categories based on diameter
            df_with_size = df_with_velocity.withColumn(
                "size_category",
                when(col("maximum_estimated_diameter_meters") < 100, "Small")
                .when(col("maximum_estimated_diameter_meters") < 1000, "Medium")
                .when(col("maximum_estimated_diameter_meters") < 10000, "Large")
                .otherwise("Very Large")
            )
            
            # Add distance categories
            df_final = df_with_size.withColumn(
                "distance_category",
                when(col("closest_approach_miss_distance_kilometers") < 1000000, "Very Close")
                .when(col("closest_approach_miss_distance_kilometers") < 10000000, "Close")
                .when(col("closest_approach_miss_distance_kilometers") < 50000000, "Moderate")
                .otherwise("Far")
            )
            
            return df_final
            
        except Exception as e:
            raise DataProcessingError(f"Failed to add computed columns: {e}")
    
    def calculate_aggregations(self, df: DataFrame) -> Aggregations:
        """Calculate comprehensive aggregations from processed data"""
        logger.info("Calculating aggregations from processed data")
        
        try:
            # Basic counts
            total_objects = df.select("id").distinct().count()
            total_approaches = df.count()
            
            # Close approaches under 0.2 AU threshold
            close_approaches_under_threshold = df.filter(
                col("miss_distance_astronomical") < 0.2
            ).count()
            
            # Approaches by year
            approaches_by_year_df = df.filter(
                col("approach_year").isNotNull()
            ).groupBy("approach_year").count().collect()
            
            approaches_by_year = {
                int(row['approach_year']): row['count'] 
                for row in approaches_by_year_df
            }
            
            # Average metrics
            metrics = df.agg(
                avg("closest_approach_miss_distance_kilometers").alias("avg_distance"),
                avg("closest_approach_relative_velocity_kms").alias("avg_velocity")
            ).collect()[0]
            
            # Potentially hazardous count
            pha_count = df.filter(
                col("is_potentially_hazardous_asteroid") == True
            ).select("id").distinct().count()
            
            # Size distribution
            size_dist_df = df.filter(
                col("size_category").isNotNull()
            ).groupBy("size_category").count().collect()
            
            size_distribution = {
                row['size_category']: row['count'] 
                for row in size_dist_df
            }
            
            # Velocity statistics
            velocity_stats_df = df.agg(
                avg("closest_approach_relative_velocity_kms").alias("mean"),
                stddev("closest_approach_relative_velocity_kms").alias("stddev"),
                spark_min("closest_approach_relative_velocity_kms").alias("min"),
                spark_max("closest_approach_relative_velocity_kms").alias("max")
            ).collect()[0]
            
            velocity_statistics = {
                "mean": float(velocity_stats_df['mean'] or 0),
                "stddev": float(velocity_stats_df['stddev'] or 0),
                "min": float(velocity_stats_df['min'] or 0),
                "max": float(velocity_stats_df['max'] or 0)
            }
            
            # Distance statistics
            distance_stats_df = df.agg(
                avg("closest_approach_miss_distance_kilometers").alias("mean"),
                stddev("closest_approach_miss_distance_kilometers").alias("stddev"),
                spark_min("closest_approach_miss_distance_kilometers").alias("min"),
                spark_max("closest_approach_miss_distance_kilometers").alias("max")
            ).collect()[0]
            
            distance_statistics = {
                "mean": float(distance_stats_df['mean'] or 0),
                "stddev": float(distance_stats_df['stddev'] or 0),
                "min": float(distance_stats_df['min'] or 0),
                "max": float(distance_stats_df['max'] or 0)
            }
            
            # Hazard distribution
            hazard_dist_df = df.groupBy("is_potentially_hazardous_asteroid").count().collect()
            hazard_distribution = {
                str(row['is_potentially_hazardous_asteroid']): row['count']
                for row in hazard_dist_df
            }
            
            aggregations = Aggregations(
                total_objects=total_objects,
                close_approaches_under_threshold=close_approaches_under_threshold,
                approaches_by_year=approaches_by_year,
                average_miss_distance_km=float(metrics['avg_distance'] or 0),
                average_relative_velocity_kms=float(metrics['avg_velocity'] or 0),
                potentially_hazardous_count=pha_count,
                size_distribution=size_distribution,
                velocity_statistics=velocity_statistics,
                distance_statistics=distance_statistics,
                hazard_distribution=hazard_distribution
            )
            
            logger.info(f"Calculated aggregations for {total_objects} NEO objects")
            return aggregations
            
        except Exception as e:
            raise DataProcessingError(f"Failed to calculate aggregations: {e}")
    
    def validate_data_quality(self, df: DataFrame) -> float:
        """Validate data quality and return quality score"""
        logger.info("Validating data quality")
        
        try:
            total_rows = df.count()
            if total_rows == 0:
                return 0.0
            
            # Check completeness of key fields
            key_fields = [
                "id", "name", "absolute_magnitude_h", 
                "closest_approach_date", "closest_approach_miss_distance_kilometers",
                "closest_approach_relative_velocity_kms"
            ]
            
            completeness_scores = []
            for field in key_fields:
                if field in df.columns:
                    non_null_count = df.filter(col(field).isNotNull()).count()
                    completeness = non_null_count / total_rows
                    completeness_scores.append(completeness)
                else:
                    completeness_scores.append(0.0)
            
            # Calculate overall quality score (average completeness)
            quality_score = sum(completeness_scores) / len(completeness_scores)
            
            logger.info(f"Data quality score: {quality_score:.2f}")
            return quality_score
            
        except Exception as e:
            logger.warning(f"Failed to validate data quality: {e}")
            return 0.0
    
    def cleanup(self):
        """Clean up Spark resources"""
        try:
            if self.spark:
                self.spark.stop()
                logger.info("Spark session stopped")
        except Exception as e:
            logger.warning(f"Error cleaning up Spark session: {e}") 