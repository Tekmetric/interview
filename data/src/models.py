"""
Data models, schemas, and exceptions for NEO data processing
"""

from typing import Optional, List, Dict, Any
from dataclasses import dataclass
from datetime import datetime
from pyspark.sql.types import (
    StructType, StructField, StringType, FloatType, BooleanType, 
    IntegerType, DoubleType
)


# Custom Exceptions
class NEOProcessorError(Exception):
    """Base exception for NEO data processor"""
    pass


class NASAAPIError(NEOProcessorError):
    """Exception raised for NASA API errors"""
    pass


class RateLimitError(NASAAPIError):
    """Exception raised when API rate limit is exceeded"""
    pass


class APITimeoutError(NASAAPIError):
    """Exception raised when API request times out"""
    pass


class DataProcessingError(NEOProcessorError):
    """Exception raised during data processing"""
    pass


class SparkError(DataProcessingError):
    """Exception raised for Spark-related errors"""
    pass


class StorageError(NEOProcessorError):
    """Exception raised for storage operations"""
    pass


class ConfigurationError(NEOProcessorError):
    """Exception raised for configuration issues"""
    pass


class ValidationError(NEOProcessorError):
    """Exception raised for data validation errors"""
    pass


@dataclass
class NEORecord:
    """Near Earth Object data record"""
    id: Optional[str] = None
    neo_reference_id: Optional[str] = None
    name: Optional[str] = None
    name_limited: Optional[str] = None
    designation: Optional[str] = None
    nasa_jpl_url: Optional[str] = None
    absolute_magnitude_h: Optional[float] = None
    is_potentially_hazardous_asteroid: Optional[bool] = None
    minimum_estimated_diameter_meters: Optional[float] = None
    maximum_estimated_diameter_meters: Optional[float] = None
    closest_approach_miss_distance_kilometers: Optional[float] = None
    closest_approach_date: Optional[str] = None
    closest_approach_relative_velocity_kms: Optional[float] = None
    first_observation_date: Optional[str] = None
    last_observation_date: Optional[str] = None
    observations_used: Optional[int] = None
    orbital_period: Optional[float] = None
    
    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for DataFrame creation"""
        return {
            field.name: getattr(self, field.name)
            for field in self.__dataclass_fields__.values()
        }
    
    @classmethod
    def get_spark_schema(cls) -> StructType:
        """Get Spark DataFrame schema for NEO records"""
        return StructType([
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
            StructField("miss_distance_astronomical", DoubleType(), True),
        ])


@dataclass
class CloseApproachData:
    """Close approach data from NASA API"""
    designation: str
    object_name: str
    close_approach_date: str
    miss_distance_km: float
    miss_distance_au: float
    relative_velocity_kms: float
    
    @classmethod
    def from_api_record(cls, record: List[str]) -> 'CloseApproachData':
        """Create from NASA API record format"""
        return cls(
            designation=record[0] if len(record) > 0 else "",
            object_name=record[11] if len(record) > 11 else "",
            close_approach_date=record[3] if len(record) > 3 else "",
            miss_distance_km=float(record[4]) if len(record) > 4 and record[4] else 0.0,
            miss_distance_au=float(record[5]) if len(record) > 5 and record[5] else 0.0,
            relative_velocity_kms=float(record[7]) if len(record) > 7 and record[7] else 0.0,
        )


@dataclass
class ObjectDetails:
    """Detailed object data from SBDB API"""
    object_name: str
    designation: str
    neo_reference_id: Optional[str] = None
    absolute_magnitude: Optional[float] = None
    diameter_km: Optional[float] = None
    is_pha: Optional[bool] = None
    orbital_period: Optional[float] = None
    first_obs: Optional[str] = None
    last_obs: Optional[str] = None
    obs_used: Optional[int] = None
    
    @classmethod
    def from_api_response(cls, api_data: Dict[str, Any]) -> 'ObjectDetails':
        """Create from SBDB API response"""
        obj_data = api_data.get('object', {})
        
        return cls(
            object_name=obj_data.get('fullname', ''),
            designation=obj_data.get('des', ''),
            neo_reference_id=obj_data.get('spkid', ''),
            absolute_magnitude=cls._safe_float(obj_data.get('H')),
            diameter_km=cls._extract_diameter(api_data.get('phys_par', [])),
            is_pha=obj_data.get('pha') == 'Y',
            orbital_period=cls._extract_orbital_period(api_data.get('orbit', {})),
            first_obs=obj_data.get('first_obs'),
            last_obs=obj_data.get('last_obs'),
            obs_used=cls._safe_int(obj_data.get('n_obs_used')),
        )
    
    @staticmethod
    def _safe_float(value: Any) -> Optional[float]:
        """Safely convert to float"""
        try:
            return float(value) if value is not None else None
        except (ValueError, TypeError):
            return None
    
    @staticmethod
    def _safe_int(value: Any) -> Optional[int]:
        """Safely convert to int"""
        try:
            return int(value) if value is not None else None
        except (ValueError, TypeError):
            return None
    
    @staticmethod
    def _extract_diameter(phys_params: List[Dict[str, Any]]) -> Optional[float]:
        """Extract diameter from physical parameters"""
        for param in phys_params:
            if param.get('name') == 'diameter':
                return ObjectDetails._safe_float(param.get('value'))
        return None
    
    @staticmethod
    def _extract_orbital_period(orbit_data: Dict[str, Any]) -> Optional[float]:
        """Extract orbital period from orbit data"""
        elements = orbit_data.get('elements', [])
        for element in elements:
            if element.get('name') == 'P':  # Period
                return ObjectDetails._safe_float(element.get('value'))
        return None


@dataclass
class ProcessingResult:
    """Result of data processing pipeline"""
    total_objects_processed: int
    successful_records: int
    failed_records: int
    processing_time_seconds: float
    output_paths: Dict[str, str]
    aggregations: Dict[str, Any]
    
    def __post_init__(self):
        """Validate processing result"""
        if self.total_objects_processed != (self.successful_records + self.failed_records):
            raise ValueError("Object counts don't match")


@dataclass 
class Aggregations:
    """Comprehensive aggregation results"""
    close_approaches_under_02_au: int
    approaches_by_year: Dict[int, int]
    total_objects_processed: int
    calculation_timestamp: str
    velocity_statistics: Optional[Dict[str, float]] = None
    distance_statistics: Optional[Dict[str, float]] = None
    hazard_distribution: Optional[Dict[str, int]] = None
    
    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for JSON serialization"""
        result = {
            "close_approaches_under_02_au": self.close_approaches_under_02_au,
            "approaches_by_year": self.approaches_by_year,
            "total_objects_processed": self.total_objects_processed,
            "calculation_timestamp": self.calculation_timestamp,
        }
        
        # Add optional enhanced statistics if present
        if self.velocity_statistics:
            result["velocity_statistics"] = self.velocity_statistics
        if self.distance_statistics:
            result["distance_statistics"] = self.distance_statistics
        if self.hazard_distribution:
            result["hazard_distribution"] = self.hazard_distribution
            
        return result
    
    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> 'Aggregations':
        """Create from dictionary"""
        return cls(
            close_approaches_under_02_au=data["close_approaches_under_02_au"],
            approaches_by_year=data["approaches_by_year"],
            total_objects_processed=data["total_objects_processed"],
            calculation_timestamp=data["calculation_timestamp"],
            velocity_statistics=data.get("velocity_statistics"),
            distance_statistics=data.get("distance_statistics"),
            hazard_distribution=data.get("hazard_distribution"),
        ) 