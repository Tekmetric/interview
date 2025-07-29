"""
Data processing module for NEO data transformation and aggregation
"""

import logging
from typing import List, Dict, Any
from datetime import datetime
from pyspark.sql import SparkSession, DataFrame
from pyspark.sql.functions import col, count, regexp_extract, lit
from pyspark.sql.types import IntegerType

from .config import Config
from .models import NEORecord, CloseApproachData, ObjectDetails, Aggregations
from .exceptions import DataProcessingError, SparkError

logger = logging.getLogger(__name__)


class SparkSessionManager:
    """Manages Spark session lifecycle"""
    
    def __init__(self, config: Config):
        self.config = config
        self._spark = None
    
    @property
    def spark(self) -> SparkSession:
        """Get or create Spark session"""
        if self._spark is None:
            self._spark = self._create_spark_session()
        return self._spark
    
    def _create_spark_session(self) -> SparkSession:
        """Create optimized Spark session"""
        try:
            builder = SparkSession.builder.appName(self.config.spark.app_name)
            
            # Apply all Spark configurations
            for key, value in self.config.spark.to_spark_configs().items():
                builder = builder.config(key, value)
            
            spark = builder.getOrCreate()
            spark.sparkContext.setLogLevel("WARN")
            
            logger.info(f"Spark session created: {spark.version}")
            return spark
            
        except Exception as e:
            raise SparkError(f"Failed to create Spark session: {e}")
    
    def close(self):
        """Close Spark session"""
        if self._spark:
            self._spark.stop()
            self._spark = None


class NEODataTransformer:
    """Transforms raw API data into structured NEO records"""
    
    def __init__(self, config: Config):
        self.config = config
    
    def combine_data_sources(
        self, 
        close_approaches: List[CloseApproachData],
        object_details: List[ObjectDetails]
    ) -> List[NEORecord]:
        """
        Combine close approach and detailed object data
        
        Args:
            close_approaches: List of close approach data
            object_details: List of detailed object data
            
        Returns:
            List of combined NEO records
        """
        logger.info("Combining data sources...")
        
        # Create lookup dictionary for object details
        details_lookup = {
            obj.designation: obj for obj in object_details
        }
        
        neo_records = []
        
        for approach in close_approaches:
            # Find matching object details
            details = details_lookup.get(approach.designation)
            
            # Create NEO record
            neo_record = self._create_neo_record(approach, details)
            neo_records.append(neo_record)
        
        logger.info(f"Created {len(neo_records)} NEO records")
        return neo_records
    
    def _create_neo_record(
        self,
        approach: CloseApproachData,
        details: ObjectDetails = None
    ) -> NEORecord:
        """Create a NEO record from approach and detail data"""
        
        return NEORecord(
            id=approach.designation,
            neo_reference_id=details.neo_reference_id if details else None,
            name=details.object_name if details else approach.object_name,
            name_limited=self._get_limited_name(approach.object_name),
            designation=approach.designation,
            nasa_jpl_url=self._generate_jpl_url(approach.designation),
            absolute_magnitude_h=details.absolute_magnitude if details else None,
            is_potentially_hazardous_asteroid=details.is_pha if details else None,
            minimum_estimated_diameter_meters=self._convert_diameter_to_meters(
                details.diameter_km if details else None
            ),
            maximum_estimated_diameter_meters=self._convert_diameter_to_meters(
                details.diameter_km if details else None
            ),
            closest_approach_miss_distance_kilometers=approach.miss_distance_km,
            closest_approach_date=approach.close_approach_date,
            closest_approach_relative_velocity_kms=approach.relative_velocity_kms,
            first_observation_date=details.first_obs if details else None,
            last_observation_date=details.last_obs if details else None,
            observations_used=details.obs_used if details else None,
            orbital_period=details.orbital_period if details else None,
        )
    
    def _get_limited_name(self, full_name: str) -> str:
        """Generate limited name from full name"""
        if not full_name:
            return ""
        
        # Take first 30 characters for limited name
        return full_name[:30] + "..." if len(full_name) > 30 else full_name
    
    def _generate_jpl_url(self, designation: str) -> str:
        """Generate JPL database URL for object"""
        return f"https://ssd.jpl.nasa.gov/tools/sbdb_lookup.html#/?sstr={designation}"
    
    def _convert_diameter_to_meters(self, diameter_km: float = None) -> float:
        """Convert diameter from kilometers to meters"""
        if diameter_km is None:
            return None
        return diameter_km * 1000


class NEODataProcessor:
    """Main data processor for NEO data pipeline"""
    
    def __init__(self, config: Config):
        self.config = config
        self.spark_manager = SparkSessionManager(config)
        self.transformer = NEODataTransformer(config)
    
    @property
    def spark(self) -> SparkSession:
        return self.spark_manager.spark
    
    def create_dataframe(self, neo_records: List[NEORecord]) -> DataFrame:
        """
        Create Spark DataFrame from NEO records
        
        Args:
            neo_records: List of NEO records
            
        Returns:
            Spark DataFrame with enhanced columns
        """
        logger.info("Creating Spark DataFrame...")
        
        try:
            # Convert records to dictionaries
            records_data = [record.to_dict() for record in neo_records]
            
            # Create DataFrame with schema
            df = self.spark.createDataFrame(
                records_data, 
                schema=NEORecord.get_spark_schema()
            )
            
            # Add computed columns
            df = self._add_computed_columns(df)
            
            logger.info(f"Created DataFrame with {df.count()} records")
            return df
            
        except Exception as e:
            raise DataProcessingError(f"Failed to create DataFrame: {e}")
    
    def _add_computed_columns(self, df: DataFrame) -> DataFrame:
        """Add computed columns to DataFrame"""
        # Convert kilometers to astronomical units
        AU_TO_KM = 149597870.7
        
        df = df.withColumn(
            "miss_distance_astronomical",
            col("closest_approach_miss_distance_kilometers") / AU_TO_KM
        )
        
        return df
    
    def calculate_aggregations(self, df: DataFrame) -> Aggregations:
        """
        Calculate required aggregations from DataFrame
        
        Args:
            df: Spark DataFrame with NEO data
            
        Returns:
            Aggregations object with calculated metrics
        """
        logger.info("Calculating aggregations...")
        
        try:
            # Aggregation 1: Close approaches under 0.2 AU
            close_approaches_count = df.filter(
                col("miss_distance_astronomical") < self.config.processing.close_approach_threshold_au
            ).count()
            
            # Aggregation 2: Approaches by year
            yearly_counts = self._calculate_yearly_counts(df)
            
            # Total objects processed
            total_objects = df.count()
            
            aggregations = Aggregations(
                close_approaches_under_02_au=close_approaches_count,
                approaches_by_year=yearly_counts,
                total_objects_processed=total_objects,
                calculation_timestamp=datetime.now().isoformat()
            )
            
            logger.info(f"Aggregations calculated: {aggregations.to_dict()}")
            return aggregations
            
        except Exception as e:
            raise DataProcessingError(f"Failed to calculate aggregations: {e}")
    
    def _calculate_yearly_counts(self, df: DataFrame) -> Dict[int, int]:
        """Calculate approach counts by year"""
        # Extract year from closest_approach_date
        df_with_year = df.withColumn(
            "approach_year",
            regexp_extract(col("closest_approach_date"), r"(\d{4})", 1).cast(IntegerType())
        ).filter(col("approach_year").isNotNull())
        
        # Group by year and count
        yearly_counts = df_with_year.groupBy("approach_year") \
            .agg(count("*").alias("approach_count")) \
            .orderBy("approach_year") \
            .collect()
        
        # Convert to dictionary
        return {row["approach_year"]: row["approach_count"] for row in yearly_counts}
    
    def validate_data_quality(self, df: DataFrame) -> Dict[str, Any]:
        """
        Validate data quality and return metrics
        
        Args:
            df: DataFrame to validate
            
        Returns:
            Dictionary with quality metrics
        """
        logger.info("Validating data quality...")
        
        total_records = df.count()
        quality_metrics = {
            "total_records": total_records,
            "missing_values": {},
            "data_ranges": {},
        }
        
        # Check for missing values in critical columns
        critical_columns = [
            "designation",
            "closest_approach_miss_distance_kilometers",
            "closest_approach_date",
            "closest_approach_relative_velocity_kms"
        ]
        
        for column in critical_columns:
            null_count = df.filter(col(column).isNull()).count()
            quality_metrics["missing_values"][column] = {
                "count": null_count,
                "percentage": (null_count / total_records) * 100 if total_records > 0 else 0
            }
        
        # Check data ranges
        if total_records > 0:
            # Miss distance should be positive
            negative_distances = df.filter(
                col("closest_approach_miss_distance_kilometers") < 0
            ).count()
            
            # Velocity should be positive
            negative_velocities = df.filter(
                col("closest_approach_relative_velocity_kms") < 0
            ).count()
            
            quality_metrics["data_ranges"] = {
                "negative_distances": negative_distances,
                "negative_velocities": negative_velocities,
            }
        
        logger.info(f"Data quality metrics: {quality_metrics}")
        return quality_metrics
    
    def close(self):
        """Close resources"""
        self.spark_manager.close() 