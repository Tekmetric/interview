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
    stddev, year, month, dayofmonth, lit, row_number
)
from pyspark.sql.window import Window
from pyspark.sql.types import (
    StructType, StructField, StringType, FloatType, BooleanType, 
    IntegerType, DoubleType, ArrayType
)

from .config import SparkConfig
from .models import Aggregations, DataProcessingError

logger = logging.getLogger(__name__)


class NEODataProcessor:
    """
    Distributed data processor for NEO data using Spark
    """
    
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
            raise DataProcessingError(f"Failed to create Spark session: {e}")
    
    def process_neo_dataframe(self, neo_df: DataFrame) -> DataFrame:
        """
        DEPRECATED: This method has data quality issues and is replaced by extract_raw_data_with_closest_approach
        
        Process NeoWs DataFrame to extract all required columns
        
        Args:
            neo_df: Raw DataFrame from NeoWs API
            
        Returns:
            Processed DataFrame with flattened structure and required columns
        """
        logger.warning("DEPRECATED: process_neo_dataframe has data quality issues. Use extract_raw_data_with_closest_approach instead.")
        
        # For backward compatibility, just return a basic processed version
        # This should not be used in the main pipeline anymore (Option 1)
        try:
            logger.info(f"Input DataFrame has {neo_df.count()} records")
            
            # Basic processing for backward compatibility only
            processed_df = neo_df.select(
                col("id").alias("id"),
                col("neo_reference_id").alias("neo_reference_id"),
                col("name").alias("name"),
                col("absolute_magnitude_h").cast("float").alias("absolute_magnitude_h"),
                col("is_potentially_hazardous_asteroid").cast("boolean").alias("is_potentially_hazardous_asteroid")
            )
            
            logger.warning(f"Deprecated method processed {processed_df.count()} rows - consider using raw data approach")
            return processed_df
            
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
    
    def calculate_aggregations_from_raw(self, raw_df: DataFrame) -> Aggregations:
        """
        Calculate comprehensive aggregations from raw data (1 row per NEO with closest approach)
        
        Args:
            raw_df: Raw DataFrame with 17 columns, 1 row per NEO
            
        Returns:
            Aggregations object with calculated metrics
        """
        logger.info("Calculating aggregations from raw data")
        
        try:
            # Basic counts
            total_objects = raw_df.count()
            
            # Count objects that actually have close approach data (not null)
            total_approaches = raw_df.filter(
                col("closest_approach_miss_distance_km").isNotNull()
            ).count()
            
            objects_without_approaches = total_objects - total_approaches
            
            # Close approaches under 0.2 AU threshold (convert km to AU: 1 AU ≈ 149,597,870.7 km)
            au_to_km = 149597870.7
            threshold_km = 0.2 * au_to_km  # 0.2 AU in kilometers
            
            close_approaches_under_threshold = raw_df.filter(
                col("closest_approach_miss_distance_km").isNotNull() &
                (col("closest_approach_miss_distance_km") < threshold_km)
            ).count()
            
            logger.info(f"Objects with close approaches: {total_approaches}")
            logger.info(f"Objects without close approaches: {objects_without_approaches}")
            
            # Approaches by year (extract year from closest_approach_date)
            # Only for objects that have close approach data
            raw_with_year = raw_df.filter(
                col("closest_approach_date").isNotNull()
            ).withColumn(
                "approach_year",
                year(col("closest_approach_date"))
            )
            
            approaches_by_year_df = raw_with_year.filter(
                col("approach_year").isNotNull()
            ).groupBy("approach_year").count().collect()
            
            approaches_by_year = {
                int(row['approach_year']): row['count'] 
                for row in approaches_by_year_df
            }
            
            # Average metrics (only for objects with valid data)
            metrics = raw_df.agg(
                avg("closest_approach_miss_distance_km").alias("avg_distance"),  # Handles nulls automatically
                avg("closest_approach_relative_velocity_kms").alias("avg_velocity"),  # Handles nulls automatically
                avg("absolute_magnitude_h").alias("avg_magnitude"),
                avg("minimum_estimated_diameter_meters").alias("avg_min_diameter"),
                avg("maximum_estimated_diameter_meters").alias("avg_max_diameter")
            ).collect()[0]
            
            # Potentially hazardous count
            pha_count = raw_df.filter(
                col("is_potentially_hazardous_asteroid") == True
            ).count()
            
            # Size distribution based on max diameter
            raw_with_size = raw_df.withColumn(
                "size_category",
                when(col("maximum_estimated_diameter_meters") < 100, "Small")
                .when(col("maximum_estimated_diameter_meters") < 1000, "Medium") 
                .when(col("maximum_estimated_diameter_meters") < 10000, "Large")
                .otherwise("Very Large")
            )
            
            size_dist_df = raw_with_size.filter(
                col("size_category").isNotNull()
            ).groupBy("size_category").count().collect()
            
            size_distribution = {
                row['size_category']: row['count'] 
                for row in size_dist_df
            }
            
            # Velocity statistics (only for objects with close approach data)
            velocity_stats_df = raw_df.filter(
                col("closest_approach_relative_velocity_kms").isNotNull()
            ).agg(
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
            
            # Distance statistics (only for objects with close approach data)
            distance_stats_df = raw_df.filter(
                col("closest_approach_miss_distance_km").isNotNull()
            ).agg(
                avg("closest_approach_miss_distance_km").alias("mean"),
                stddev("closest_approach_miss_distance_km").alias("stddev"),
                spark_min("closest_approach_miss_distance_km").alias("min"),
                spark_max("closest_approach_miss_distance_km").alias("max")
            ).collect()[0]
            
            distance_statistics = {
                "mean": float(distance_stats_df['mean'] or 0),
                "stddev": float(distance_stats_df['stddev'] or 0),
                "min": float(distance_stats_df['min'] or 0),
                "max": float(distance_stats_df['max'] or 0)
            }
            
            # Additional metrics from raw data
            orbital_data_available = raw_df.filter(
                col("orbital_period").isNotNull() & 
                col("observations_used").isNotNull()
            ).count()
            
            avg_orbital_period = raw_df.agg(
                avg("orbital_period").alias("avg_period")
            ).collect()[0]['avg_period']
            
            logger.info(f"Calculated aggregations for {total_objects} NEO objects")
            
            return Aggregations(
                total_objects=total_objects,
                total_close_approaches=total_approaches,
                close_approaches_under_threshold=close_approaches_under_threshold,
                approaches_by_year=approaches_by_year,
                potentially_hazardous_count=pha_count,
                average_miss_distance_km=float(metrics['avg_distance'] or 0),
                average_velocity_kms=float(metrics['avg_velocity'] or 0),
                average_magnitude=float(metrics['avg_magnitude'] or 0),
                size_distribution=size_distribution,
                velocity_statistics=velocity_statistics,
                distance_statistics=distance_statistics,
                orbital_data_coverage=orbital_data_available / total_objects if total_objects > 0 else 0,
                average_orbital_period_days=float(avg_orbital_period or 0)
            )
            
        except Exception as e:
            logger.error(f"Failed to calculate aggregations from raw data: {e}")
            raise DataProcessingError(f"Aggregation calculation failed: {e}")
    
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

    def extract_raw_data_with_closest_approach(self, neo_df: DataFrame) -> DataFrame:
        """
        Extract raw data with 17 specified columns using closest approach per NEO (Option A)
        FIXED: Now keeps ALL NEO objects, including those without close approach data
        
        Args:
            neo_df: Raw DataFrame from Browse API
            
        Returns:
            DataFrame with 17 columns, one row per NEO (200 objects total)
        """
        logger.info("Extracting raw data with closest approach per NEO (keeping ALL objects)")
        
        try:
            total_input_count = neo_df.count()
            logger.info(f"Input: {total_input_count} NEO objects")
            
            # Separate NEOs with and without close approach data
            neos_with_approaches = neo_df.filter(
                col("close_approach_data").isNotNull() & 
                (size(col("close_approach_data")) > 0)
            )
            
            neos_without_approaches = neo_df.filter(
                col("close_approach_data").isNull() | 
                (size(col("close_approach_data")) == 0)
            )
            
            with_count = neos_with_approaches.count()
            without_count = neos_without_approaches.count()
            logger.info(f"NEOs with close approaches: {with_count}")
            logger.info(f"NEOs without close approaches: {without_count}")
            
            # Process NEOs WITH close approach data (find closest approach)
            if with_count > 0:
                # Explode close approach data to find closest approach per NEO
                exploded_df = neos_with_approaches.select(
                    "*",
                    explode(col("close_approach_data")).alias("approach")
                ).drop("close_approach_data")
                
                # Add miss distance as numeric for finding minimum
                with_distance = exploded_df.select(
                    "*",
                    col("approach.miss_distance.kilometers").cast("double").alias("miss_distance_km_numeric"),
                    col("approach.close_approach_date").alias("approach_date"),
                    col("approach.relative_velocity.kilometers_per_second").alias("approach_velocity_kms")
                )
                
                # Find closest approach for each NEO (minimum miss distance)
                window = Window.partitionBy("id").orderBy("miss_distance_km_numeric")
                closest_approaches = with_distance.select(
                    "*",
                    row_number().over(window).alias("rank")
                ).filter(col("rank") == 1).drop("rank")
                
                # Extract the 17 required columns for NEOs with approaches
                raw_data_with_approaches = closest_approaches.select(
                    col("id").alias("id"),
                    col("neo_reference_id").alias("neo_reference_id"), 
                    col("name").alias("name"),
                    col("name_limited").alias("name_limited"),
                    col("designation").alias("designation"),
                    col("nasa_jpl_url").alias("nasa_jpl_url"),
                    col("absolute_magnitude_h").alias("absolute_magnitude_h"),
                    col("is_potentially_hazardous_asteroid").alias("is_potentially_hazardous_asteroid"),
                    col("estimated_diameter.meters.estimated_diameter_min").cast("double").alias("minimum_estimated_diameter_meters"),
                    col("estimated_diameter.meters.estimated_diameter_max").cast("double").alias("maximum_estimated_diameter_meters"),
                    col("miss_distance_km_numeric").alias("closest_approach_miss_distance_km"),
                    col("approach_date").alias("closest_approach_date"),
                    col("approach_velocity_kms").cast("double").alias("closest_approach_relative_velocity_kms"),
                    col("orbital_data.first_observation_date").alias("first_observation_date"),
                    col("orbital_data.last_observation_date").alias("last_observation_date"),
                    col("orbital_data.observations_used").cast("long").alias("observations_used"),
                    col("orbital_data.orbital_period").cast("double").alias("orbital_period")
                )
            else:
                raw_data_with_approaches = None
            
            # Process NEOs WITHOUT close approach data (use null values)
            if without_count > 0:
                raw_data_without_approaches = neos_without_approaches.select(
                    col("id").alias("id"),
                    col("neo_reference_id").alias("neo_reference_id"), 
                    col("name").alias("name"),
                    col("name_limited").alias("name_limited"),
                    col("designation").alias("designation"),
                    col("nasa_jpl_url").alias("nasa_jpl_url"),
                    col("absolute_magnitude_h").alias("absolute_magnitude_h"),
                    col("is_potentially_hazardous_asteroid").alias("is_potentially_hazardous_asteroid"),
                    col("estimated_diameter.meters.estimated_diameter_min").cast("double").alias("minimum_estimated_diameter_meters"),
                    col("estimated_diameter.meters.estimated_diameter_max").cast("double").alias("maximum_estimated_diameter_meters"),
                    # Close approach fields as null for objects without approaches
                    lit(None).cast("double").alias("closest_approach_miss_distance_km"),
                    lit(None).cast("string").alias("closest_approach_date"),
                    lit(None).cast("double").alias("closest_approach_relative_velocity_kms"),
                    col("orbital_data.first_observation_date").alias("first_observation_date"),
                    col("orbital_data.last_observation_date").alias("last_observation_date"),
                    col("orbital_data.observations_used").cast("long").alias("observations_used"),
                    col("orbital_data.orbital_period").cast("double").alias("orbital_period")
                )
            else:
                raw_data_without_approaches = None
            
            # Union both sets to get ALL 200 objects
            if raw_data_with_approaches is not None and raw_data_without_approaches is not None:
                raw_data_df = raw_data_with_approaches.union(raw_data_without_approaches)
            elif raw_data_with_approaches is not None:
                raw_data_df = raw_data_with_approaches
            elif raw_data_without_approaches is not None:
                raw_data_df = raw_data_without_approaches
            else:
                # Should not happen, but handle edge case
                raise DataProcessingError("No NEO data found")
            
            final_count = raw_data_df.count()
            logger.info(f"Successfully extracted raw data: {final_count} NEO objects total")
            logger.info(f"  • With close approaches: {with_count}")
            logger.info(f"  • Without close approaches: {without_count}")
            
            if final_count != total_input_count:
                logger.warning(f"Object count mismatch: Input {total_input_count} → Output {final_count}")
            
            return raw_data_df
            
        except Exception as e:
            logger.error(f"Failed to extract raw data: {e}")
            raise DataProcessingError(f"Raw data extraction failed: {e}") 