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
    close_approaches_under_threshold: int  # Close approaches < 0.2 AU
    approaches_by_year: Dict[int, int]  # Year -> count of approaches
    average_miss_distance_km: float
    average_relative_velocity_kms: float
    potentially_hazardous_count: int
    size_distribution: Dict[str, int]  # Size category -> count
    velocity_statistics: Dict[str, float] = field(default_factory=dict)
    distance_statistics: Dict[str, float] = field(default_factory=dict)
    hazard_distribution: Dict[str, int] = field(default_factory=dict)
    
    def to_dict(self) -> Dict[str, Any]:
        """Convert Aggregations to dictionary for JSON serialization"""
        return {
            "close_approaches_under_threshold": self.close_approaches_under_threshold,
            "approaches_by_year": self.approaches_by_year,
            "average_miss_distance_km": self.average_miss_distance_km,
            "average_relative_velocity_kms": self.average_relative_velocity_kms,
            "potentially_hazardous_count": self.potentially_hazardous_count,
            "size_distribution": self.size_distribution,
            "velocity_statistics": self.velocity_statistics,
            "distance_statistics": self.distance_statistics,
            "hazard_distribution": self.hazard_distribution
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