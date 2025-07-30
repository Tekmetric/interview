"""
Data models for NEO processing pipeline
"""

from dataclasses import dataclass, fields, field
from typing import Optional, Dict, Any, List
from datetime import datetime
from pyspark.sql.types import StructType, StructField, StringType, FloatType, BooleanType, IntegerType


# Exception hierarchy for NEO processing errors
class NEOProcessorError(Exception):
    """Base exception for NEO processor errors"""
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
    """Exception raised for data processing errors"""
    pass


class SparkError(DataProcessingError):
    """Exception raised for Spark-related errors"""
    pass


class StorageError(NEOProcessorError):
    """Exception raised for data storage errors"""
    pass


class ConfigurationError(NEOProcessorError):
    """Exception raised for configuration errors"""
    pass


class ValidationError(NEOProcessorError):
    """Exception raised for data validation errors"""
    pass


@dataclass
class NEOObject:
    """Near Earth Object from SBDB Query API"""
    designation: str
    full_name: Optional[str] = None
    pdes: Optional[str] = None  # Primary designation
    name: Optional[str] = None
    prefix: Optional[str] = None
    neo: Optional[bool] = None
    pha: Optional[bool] = None  # Potentially Hazardous Asteroid
    h_magnitude: Optional[float] = None  # Absolute magnitude
    orbit_class: Optional[str] = None
    moid: Optional[float] = None  # Minimum Orbit Intersection Distance
    diameter_km: Optional[float] = None  # Diameter in kilometers
    
    @classmethod
    def from_query_api_record(cls, data: Dict[str, Any]) -> 'NEOObject':
        """Create NEOObject from SBDB Query API record"""
        return cls(
            designation=data.get('object', ''),
            full_name=data.get('full_name'),
            pdes=data.get('pdes'),
            name=data.get('name'),
            prefix=data.get('prefix'),
            neo=data.get('neo') == '1' if data.get('neo') else None,
            pha=data.get('pha') == '1' if data.get('pha') else None,
            h_magnitude=float(data.get('H')) if data.get('H') else None,
            orbit_class=data.get('class'),
            moid=float(data.get('moid')) if data.get('moid') else None
        )
    
    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for DataFrame creation"""
        return {
            field.name: getattr(self, field.name)
            for field in fields(self)
        }


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
            StructField("absolute_magnitude_h", FloatType(), True),
            StructField("is_potentially_hazardous_asteroid", BooleanType(), True),
            StructField("minimum_estimated_diameter_meters", FloatType(), True),
            StructField("maximum_estimated_diameter_meters", FloatType(), True),
            StructField("closest_approach_miss_distance_kilometers", FloatType(), True),
            StructField("closest_approach_date", StringType(), True),
            StructField("closest_approach_relative_velocity_kms", FloatType(), True),
            StructField("first_observation_date", StringType(), True),
            StructField("last_observation_date", StringType(), True),
            StructField("observations_used", IntegerType(), True),
            StructField("orbital_period", FloatType(), True),
        ])


@dataclass
class CloseApproachData:
    """Close approach data from NASA API"""
    designation: str
    close_approach_date: str
    miss_distance_km: float
    relative_velocity_kms: float
    
    @classmethod
    def from_api_record(cls, record: List[Any]) -> 'CloseApproachData':
        """
        Create CloseApproachData from Close Approach Data API record
        
        API record format (array):
        [designation, orbit_id, jd, cd, dist, dist_min, dist_max, v_rel, v_inf, t_sigma_f, body, h, diameter, diameter_sigma, fullname]
        """
        # Record is an array, map to expected positions
        designation = record[0] if len(record) > 0 else ""
        close_approach_date = record[3] if len(record) > 3 else ""  # cd field
        miss_distance_au = float(record[4]) if len(record) > 4 and record[4] else 0.0  # dist field in AU
        relative_velocity_kms = float(record[7]) if len(record) > 7 and record[7] else 0.0  # v_rel field
        
        # Convert distance from AU to kilometers (1 AU ≈ 149,597,870.7 km)
        miss_distance_km = miss_distance_au * 149597870.7
        
        return cls(
            designation=designation,
            close_approach_date=close_approach_date,
            miss_distance_km=miss_distance_km,
            relative_velocity_kms=relative_velocity_kms
        )
    
    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for DataFrame creation"""
        return {
            'designation': self.designation,
            'closest_approach_date': self.close_approach_date,
            'closest_approach_miss_distance_kilometers': self.miss_distance_km,
            'closest_approach_relative_velocity_kms': self.relative_velocity_kms,
            'is_potentially_hazardous': False  # Default, will be updated from ObjectDetails
        }


@dataclass
class ObjectDetails:
    """Detailed object information from SBDB API"""
    designation: str
    absolute_magnitude: Optional[float] = None
    diameter_km: Optional[float] = None
    orbital_period: Optional[float] = None
    is_potentially_hazardous: Optional[bool] = None
    
    @classmethod
    def from_api_response(cls, data: Dict[str, Any]) -> 'ObjectDetails':
        """Create ObjectDetails from SBDB API response"""
        obj_data = data.get('object', {})
        
        return cls(
            designation=obj_data.get('des', ''),
            absolute_magnitude=None,  # Would need to parse from phys_par section
            diameter_km=None,  # Would need to parse from phys_par section
            orbital_period=None,  # Would need to parse from orbit section
            is_potentially_hazardous=obj_data.get('pha', False)
        )
    
    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for DataFrame creation"""
        return {
            'designation': self.designation,
            'absolute_magnitude_h': self.absolute_magnitude,
            'estimated_diameter_min_km': self.diameter_km * 0.8 if self.diameter_km else None,
            'estimated_diameter_max_km': self.diameter_km * 1.2 if self.diameter_km else None,
            'is_sentry_object': False,  # Default
            'orbit_class': 'Unknown',  # Default
            'orbital_period_days': self.orbital_period,
            'perihelion_distance_au': None,  # Default
            'aphelion_distance_au': None,  # Default
            'eccentricity': None,  # Default
        }


@dataclass
class Aggregations:
    """Data aggregations and statistics"""
    total_objects: int
    total_close_approaches: int
    potentially_hazardous_count: int
    average_miss_distance_km: float
    min_miss_distance_km: float
    max_miss_distance_km: float
    average_velocity_kms: float
    time_range_start: str
    time_range_end: str
    velocity_statistics: Optional[Dict[str, float]] = None
    distance_statistics: Optional[Dict[str, float]] = None  
    hazard_distribution: Optional[Dict[str, int]] = None
    
    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for JSON serialization"""
        result = {}
        for field in fields(self):
            value = getattr(self, field.name)
            result[field.name] = value
        return result


@dataclass
class ProcessingResult:
    """Result of the complete NEO processing pipeline"""
    neo_objects_count: int
    close_approaches_count: int
    object_details_count: int
    processing_time_seconds: float
    aggregations: Aggregations
    data_quality_score: float
    files_created: List[str] = field(default_factory=list)
    errors: List[str] = field(default_factory=list)
    
    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for JSON serialization"""
        result = {}
        for field in fields(self):
            value = getattr(self, field.name)
            if field.name == 'aggregations':
                result[field.name] = value.to_dict() if value else None
            else:
                result[field.name] = value
        return result 