"""
Data models for NEO processing pipeline using NeoWs API
"""

from dataclasses import dataclass, field
from typing import Optional, Dict, Any, List
from datetime import datetime


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
    """Exception raised during data processing"""
    pass


class SparkError(DataProcessingError):
    """Exception raised for Spark-related errors"""
    pass


class StorageError(NEOProcessorError):
    """Exception raised during data storage operations"""
    pass


class ConfigurationError(NEOProcessorError):
    """Exception raised for configuration errors"""
    pass


class ValidationError(DataProcessingError):
    """Exception raised during data validation"""
    pass


@dataclass
class Aggregations:
    """Comprehensive aggregations calculated from NEO data"""
    total_objects: int
    total_close_approaches: int
    close_approaches_under_threshold: int  # Close approaches < 0.2 AU
    approaches_by_year: Dict[int, int]  # Year -> count of approaches
    average_miss_distance_km: float
    average_velocity_kms: float  # Renamed for consistency with raw data method
    potentially_hazardous_count: int
    size_distribution: Dict[str, int]  # Size category -> count
    velocity_statistics: Dict[str, float] = field(default_factory=dict)
    distance_statistics: Dict[str, float] = field(default_factory=dict)
    hazard_distribution: Dict[str, int] = field(default_factory=dict)
    # Additional fields from raw data analysis
    average_magnitude: float = 0.0
    orbital_data_coverage: float = 0.0  # Percentage of objects with orbital data
    average_orbital_period_days: float = 0.0
    
    def to_dict(self) -> Dict[str, Any]:
        """Convert Aggregations to dictionary for JSON serialization"""
        return {
            "total_objects": self.total_objects,
            "total_close_approaches": self.total_close_approaches,
            "close_approaches_under_threshold": self.close_approaches_under_threshold,
            "approaches_by_year": self.approaches_by_year,
            "average_miss_distance_km": self.average_miss_distance_km,
            "average_velocity_kms": self.average_velocity_kms,
            "potentially_hazardous_count": self.potentially_hazardous_count,
            "size_distribution": self.size_distribution,
            "velocity_statistics": self.velocity_statistics,
            "distance_statistics": self.distance_statistics,
            "hazard_distribution": self.hazard_distribution,
            "average_magnitude": self.average_magnitude,
            "orbital_data_coverage": self.orbital_data_coverage,
            "average_orbital_period_days": self.average_orbital_period_days
        }


@dataclass
class ProcessingResult:
    """Result of the NEO data processing pipeline"""
    total_objects_processed: int
    close_approaches_count: int
    processing_time_seconds: float
    aggregations: Optional[Aggregations]
    data_quality_score: float
    success: bool
    error_message: Optional[str] = None 