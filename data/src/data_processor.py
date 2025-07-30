"""
Data processing module using Spark for all operations
"""

import logging
from typing import List, Dict, Any, Optional
from datetime import datetime
from pyspark.sql import SparkSession, DataFrame
from pyspark.sql.functions import (
    col, count, regexp_extract, lit, year, month, dayofmonth,
    when, isnan, isnull, sum as spark_sum, avg, min as spark_min, 
    max as spark_max, stddev, desc, asc, broadcast
)
from pyspark.sql.types import (
    StructType, StructField, StringType, FloatType, BooleanType, 
    IntegerType, DoubleType, TimestampType
)

from .config import Config
from .models import NEORecord, CloseApproachData, ObjectDetails, Aggregations
from .models import DataProcessingError, SparkError

logger = logging.getLogger(__name__)


class NEODataProcessor:
    """
    Data processor that uses Spark for all operations
    Eliminates single-threaded Python bottlenecks
    """
    
    def __init__(self, config: Config):
        self.config = config
        self.spark = self._create_optimized_spark_session()
        
    def _create_optimized_spark_session(self) -> SparkSession:
        """Create and configure optimized Spark session"""
        try:
            builder = SparkSession.builder.appName(self.config.spark.app_name)
            
            # Apply base configurations
            for key, value in self.config.spark.to_spark_configs().items():
                builder = builder.config(key, value)
            
            # Add optimizations for distributed processing
            optimizations = {
                "spark.sql.adaptive.skewJoin.enabled": "true",
                "spark.sql.adaptive.localShuffleReader.enabled": "true",
                "spark.sql.broadcastTimeout": "36000",
                "spark.sql.adaptive.coalescePartitions.enabled": "true",
                "spark.sql.adaptive.coalescePartitions.initialPartitionNum": "200",
                "spark.sql.shuffle.partitions": "200",
                "spark.sql.execution.arrow.maxRecordsPerBatch": "10000",
            }
            
            for key, value in optimizations.items():
                builder = builder.config(key, value)
            
            spark = builder.getOrCreate()
            spark.sparkContext.setLogLevel("WARN")
            
            logger.info(f"Optimized Spark session created: {spark.version}")
            logger.info(f"Available cores: {spark.sparkContext.defaultParallelism}")
            
            return spark
            
        except Exception as e:
            raise SparkError(f"Failed to create optimized Spark session: {e}")
    
    def create_dataframe_from_sources(self, close_approaches: List[CloseApproachData], 
                                    object_details: List[ObjectDetails]) -> DataFrame:
        """
        Create Spark DataFrame directly from source data using distributed operations
        Eliminates single-threaded record combination
        """
        logger.info("Creating DataFrame from source data using distributed processing...")
        
        try:
            # Create DataFrames from source data
            close_approaches_df = self._create_close_approaches_dataframe(close_approaches)
            object_details_df = self._create_object_details_dataframe(object_details)
            
            # Perform distributed join instead of Python loops
            combined_df = self._join_data_sources(close_approaches_df, object_details_df)
            
            # Add computed columns using Spark operations
            enhanced_df = self._add_computed_columns(combined_df)
            
            # Optimize and cache for subsequent operations
            optimized_df = enhanced_df.cache()
            
            record_count = optimized_df.count()
            logger.info(f"Created distributed DataFrame with {record_count} records")
            
            return optimized_df
            
        except Exception as e:
            raise DataProcessingError(f"Failed to create distributed DataFrame: {e}")
    
    def _create_close_approaches_dataframe(self, close_approaches: List[CloseApproachData]) -> DataFrame:
        """Create DataFrame from close approach data"""
        if not close_approaches:
            return self.spark.createDataFrame([], self._get_close_approach_schema())
        
        # Convert to list of dictionaries for Spark
        data = [approach.to_dict() for approach in close_approaches]
        
        df = self.spark.createDataFrame(data, self._get_close_approach_schema())
        
        # Add partition hints for better distribution
        return df.repartition(col("designation"))
    
    def _create_object_details_dataframe(self, object_details: List[ObjectDetails]) -> DataFrame:
        """Create DataFrame from object details data"""
        if not object_details:
            return self.spark.createDataFrame([], self._get_object_details_schema())
        
        # Convert to list of dictionaries for Spark
        data = [details.to_dict() for details in object_details]
        
        df = self.spark.createDataFrame(data, self._get_object_details_schema())
        
        # Add partition hints for better distribution
        return df.repartition(col("designation"))
    
    def _join_data_sources(self, close_approaches_df: DataFrame, 
                          object_details_df: DataFrame) -> DataFrame:
        """
        Perform distributed join of data sources using Spark
        Much faster than Python loops for combining data
        """
        logger.info("Performing distributed join of close approaches and object details...")
        
        # Broadcast smaller DataFrame if one is significantly smaller
        ca_count = close_approaches_df.count()
        od_count = object_details_df.count()
        
        if od_count < ca_count * 0.1:  # If object details is < 10% of close approaches
            object_details_df = broadcast(object_details_df)
            logger.info("Broadcasting object details for optimized join")
        elif ca_count < od_count * 0.1:
            close_approaches_df = broadcast(close_approaches_df)
            logger.info("Broadcasting close approaches for optimized join")
        
        # Perform left join to keep all close approaches
        joined_df = close_approaches_df.alias("ca").join(
            object_details_df.alias("od"),
            col("ca.designation") == col("od.designation"),
            "left"
        )
        
        # Select and rename columns to match NEORecord schema
        result_df = joined_df.select(
            col("ca.designation").alias("designation"),
            col("ca.closest_approach_date").alias("closest_approach_date"),
            col("ca.closest_approach_miss_distance_kilometers").alias("closest_approach_miss_distance_kilometers"),
            col("ca.closest_approach_relative_velocity_kms").alias("closest_approach_relative_velocity_kms"),
            col("ca.is_potentially_hazardous").alias("is_potentially_hazardous"),
            col("od.absolute_magnitude_h").alias("absolute_magnitude_h"),
            col("od.estimated_diameter_min_km").alias("estimated_diameter_min_km"),
            col("od.estimated_diameter_max_km").alias("estimated_diameter_max_km"),
            col("od.is_sentry_object").alias("is_sentry_object"),
            col("od.orbit_class").alias("orbit_class"),
            col("od.orbital_period_days").alias("orbital_period_days"),
            col("od.perihelion_distance_au").alias("perihelion_distance_au"),
            col("od.aphelion_distance_au").alias("aphelion_distance_au"),
            col("od.eccentricity").alias("eccentricity")
        )
        
        logger.info("Distributed join completed successfully")
        return result_df
    
    def _add_computed_columns(self, df: DataFrame) -> DataFrame:
        """Add computed columns using Spark operations"""
        logger.info("Adding computed columns using distributed operations...")
        
        # Convert kilometers to astronomical units
        AU_TO_KM = 149597870.7
        
        enhanced_df = df.withColumn(
            "miss_distance_astronomical",
            col("closest_approach_miss_distance_kilometers") / AU_TO_KM
        ).withColumn(
            "approach_year",
            regexp_extract(col("closest_approach_date"), r"(\d{4})", 1).cast(IntegerType())
        ).withColumn(
            "approach_month", 
            regexp_extract(col("closest_approach_date"), r"\d{4}-(\d{2})", 1).cast(IntegerType())
        ).withColumn(
            "velocity_category",
            when(col("closest_approach_relative_velocity_kms") < 10, "Slow")
            .when(col("closest_approach_relative_velocity_kms") < 20, "Medium")
            .otherwise("Fast")
        ).withColumn(
            "size_category",
            when(col("estimated_diameter_max_km") < 0.1, "Small")
            .when(col("estimated_diameter_max_km") < 1.0, "Medium")
            .otherwise("Large")
        ).withColumn(
            "hazard_level",
            when(
                (col("is_potentially_hazardous") == True) & 
                (col("miss_distance_astronomical") < 0.05), "High"
            ).when(
                (col("is_potentially_hazardous") == True) & 
                (col("miss_distance_astronomical") < 0.2), "Medium"
            ).when(col("is_potentially_hazardous") == True, "Low")
            .otherwise("Minimal")
        )
        
        return enhanced_df
    
    def calculate_comprehensive_aggregations(self, df: DataFrame) -> Aggregations:
        """
        Calculate comprehensive aggregations using distributed Spark operations
        Much faster than collecting data to driver
        """
        logger.info("Calculating comprehensive aggregations using distributed processing...")
        
        try:
            # Cache DataFrame for multiple aggregation operations
            df.cache()
            
            # Basic counts
            total_objects = df.count()
            
            # Close approaches under threshold (distributed filter and count)
            close_approaches_count = df.filter(
                col("miss_distance_astronomical") < self.config.processing.close_approach_threshold_au
            ).count()
            
            # Yearly aggregations using distributed GroupBy
            yearly_stats = df.groupBy("approach_year") \
                .agg(
                    count("*").alias("approach_count"),
                    avg("miss_distance_astronomical").alias("avg_miss_distance"),
                    spark_min("miss_distance_astronomical").alias("min_miss_distance"),
                    spark_max("miss_distance_astronomical").alias("max_miss_distance"),
                    avg("closest_approach_relative_velocity_kms").alias("avg_velocity")
                ) \
                .orderBy("approach_year") \
                .collect()
            
            # Convert yearly stats to dictionary
            approaches_by_year = {
                row["approach_year"]: row["approach_count"] 
                for row in yearly_stats if row["approach_year"] is not None
            }
            
            # Additional distributed aggregations
            velocity_stats = df.agg(
                avg("closest_approach_relative_velocity_kms").alias("avg_velocity"),
                spark_min("closest_approach_relative_velocity_kms").alias("min_velocity"),
                spark_max("closest_approach_relative_velocity_kms").alias("max_velocity"),
                stddev("closest_approach_relative_velocity_kms").alias("stddev_velocity")
            ).collect()[0]
            
            distance_stats = df.agg(
                avg("miss_distance_astronomical").alias("avg_distance"),
                spark_min("miss_distance_astronomical").alias("min_distance"),
                spark_max("miss_distance_astronomical").alias("max_distance"),
                stddev("miss_distance_astronomical").alias("stddev_distance")
            ).collect()[0]
            
            # Hazard level distribution
            hazard_distribution = df.groupBy("hazard_level") \
                .count() \
                .orderBy(desc("count")) \
                .collect()
            
            # Create comprehensive aggregations
            aggregations = Aggregations(
                close_approaches_under_02_au=close_approaches_count,
                approaches_by_year=approaches_by_year,
                total_objects_processed=total_objects,
                calculation_timestamp=datetime.now().isoformat(),
                # Additional statistics
                velocity_statistics={
                    "avg": float(velocity_stats["avg_velocity"]) if velocity_stats["avg_velocity"] else 0,
                    "min": float(velocity_stats["min_velocity"]) if velocity_stats["min_velocity"] else 0,
                    "max": float(velocity_stats["max_velocity"]) if velocity_stats["max_velocity"] else 0,
                    "stddev": float(velocity_stats["stddev_velocity"]) if velocity_stats["stddev_velocity"] else 0,
                },
                distance_statistics={
                    "avg_au": float(distance_stats["avg_distance"]) if distance_stats["avg_distance"] else 0,
                    "min_au": float(distance_stats["min_distance"]) if distance_stats["min_distance"] else 0,
                    "max_au": float(distance_stats["max_distance"]) if distance_stats["max_distance"] else 0,
                    "stddev_au": float(distance_stats["stddev_distance"]) if distance_stats["stddev_distance"] else 0,
                },
                hazard_distribution={
                    row["hazard_level"]: row["count"] 
                    for row in hazard_distribution
                }
            )
            
            logger.info(f"Comprehensive aggregations calculated for {total_objects} objects")
            return aggregations
            
        except Exception as e:
            raise DataProcessingError(f"Failed to calculate distributed aggregations: {e}")
    
    def validate_data_quality_distributed(self, df: DataFrame) -> Dict[str, Any]:
        """
        Perform comprehensive data quality validation using distributed operations
        """
        logger.info("Performing distributed data quality validation...")
        
        try:
            df.cache()
            total_records = df.count()
            
            # Define critical columns for validation
            critical_columns = [
                "designation", "closest_approach_miss_distance_kilometers",
                "closest_approach_date", "closest_approach_relative_velocity_kms"
            ]
            
            # Calculate missing values for all columns in one pass
            null_counts = {}
            for column in critical_columns:
                null_count = df.filter(col(column).isNull() | isnan(col(column))).count()
                null_counts[column] = {
                    "count": null_count,
                    "percentage": (null_count / total_records) * 100 if total_records > 0 else 0
                }
            
            # Data range validations using distributed operations
            range_validations = df.agg(
                spark_sum(when(col("closest_approach_miss_distance_kilometers") < 0, 1).otherwise(0)).alias("negative_distances"),
                spark_sum(when(col("closest_approach_relative_velocity_kms") < 0, 1).otherwise(0)).alias("negative_velocities"),
                spark_sum(when(col("miss_distance_astronomical") < 0, 1).otherwise(0)).alias("negative_au_distances"),
                spark_sum(when(col("approach_year") < 1900, 1).otherwise(0)).alias("invalid_years"),
                spark_sum(when(col("approach_year") > 2100, 1).otherwise(0)).alias("future_years")
            ).collect()[0]
            
            # Distribution analysis
            distribution_stats = df.agg(
                count("designation").alias("total_records"),
                count(when(col("is_potentially_hazardous") == True, 1)).alias("hazardous_objects"),
                count(when(col("miss_distance_astronomical") < 0.1, 1)).alias("very_close_approaches"),
                count(when(col("closest_approach_relative_velocity_kms") > 30, 1)).alias("high_velocity_objects")
            ).collect()[0]
            
            quality_metrics = {
                "total_records": total_records,
                "missing_values": null_counts,
                "data_ranges": {
                    "negative_distances": int(range_validations["negative_distances"]),
                    "negative_velocities": int(range_validations["negative_velocities"]),
                    "negative_au_distances": int(range_validations["negative_au_distances"]),
                    "invalid_years": int(range_validations["invalid_years"]),
                    "future_years": int(range_validations["future_years"])
                },
                "distribution_stats": {
                    "hazardous_objects": int(distribution_stats["hazardous_objects"]),
                    "very_close_approaches": int(distribution_stats["very_close_approaches"]),
                    "high_velocity_objects": int(distribution_stats["high_velocity_objects"])
                },
                "data_quality_score": self._calculate_quality_score(null_counts, range_validations, total_records)
            }
            
            logger.info(f"Data quality validation completed. Score: {quality_metrics['data_quality_score']:.2f}/100")
            return quality_metrics
            
        except Exception as e:
            raise DataProcessingError(f"Failed to validate data quality: {e}")
    
    def _calculate_quality_score(self, null_counts: Dict, range_validations, total_records: int) -> float:
        """Calculate overall data quality score"""
        if total_records == 0:
            return 0.0
        
        # Calculate penalties
        null_penalty = sum(nc["percentage"] for nc in null_counts.values()) / len(null_counts)
        range_penalty = (
            range_validations["negative_distances"] + 
            range_validations["negative_velocities"] + 
            range_validations["invalid_years"]
        ) / total_records * 100
        
        # Quality score (higher is better)
        score = max(0, 100 - null_penalty - range_penalty)
        return score
    
    def _get_close_approach_schema(self) -> StructType:
        """Define schema for close approach data"""
        return StructType([
            StructField("designation", StringType(), False),
            StructField("closest_approach_date", StringType(), True),
            StructField("closest_approach_miss_distance_kilometers", FloatType(), True),
            StructField("closest_approach_relative_velocity_kms", FloatType(), True),
            StructField("is_potentially_hazardous", BooleanType(), True)
        ])
    
    def _get_object_details_schema(self) -> StructType:
        """Define schema for object details data"""
        return StructType([
            StructField("designation", StringType(), False),
            StructField("absolute_magnitude_h", FloatType(), True),
            StructField("estimated_diameter_min_km", FloatType(), True),
            StructField("estimated_diameter_max_km", FloatType(), True),
            StructField("is_sentry_object", BooleanType(), True),
            StructField("orbit_class", StringType(), True),
            StructField("orbital_period_days", FloatType(), True),
            StructField("perihelion_distance_au", FloatType(), True),
            StructField("aphelion_distance_au", FloatType(), True),
            StructField("eccentricity", FloatType(), True)
        ])
    
    def close(self):
        """Close Spark session and cleanup resources"""
        try:
            if self.spark:
                self.spark.stop()
                logger.info("Spark session closed successfully")
        except Exception as e:
            logger.warning(f"Error closing Spark session: {e}") 