"""
Data models for NEO processing pipeline using NeoWs API
"""

from dataclasses import dataclass, field
from typing import Optional, Dict, Any


# Exception hierarchy for NEO processing errors
class NEOProcessorError(Exception):
    """Base exception for NEO processor errors"""
    pass


class NASAAPIError(NEOProcessorError):
    """Exception raised for NASA API errors"""
    pass


class DataProcessingError(NEOProcessorError):
    """Exception raised during data processing"""
    pass


class StorageError(NEOProcessorError):
    """Exception raised during data storage operations"""
    pass


@dataclass
class Aggregations:
    """Comprehensive aggregations calculated from NEO data"""
    total_objects: int
    total_close_approaches: int
    close_approaches_under_threshold: int  # Close approaches < 0.2 AU
    approaches_by_year: Dict[int, int]
    potentially_hazardous_count: int  # interesting to see how many are potentially hazardous
    # Additional fields from raw data analysis
    
    def to_dict(self) -> Dict[str, Any]:
        """Convert Aggregations to dictionary for JSON serialization"""
        return {
            "total_objects": self.total_objects,
            "total_close_approaches": self.total_close_approaches,
            "close_approaches_under_threshold": self.close_approaches_under_threshold,
            "approaches_by_year": self.approaches_by_year,
            "potentially_hazardous_count": self.potentially_hazardous_count
        }


@dataclass
class ProcessingResult:
    """Result of the NEO data processing pipeline"""
    total_objects_processed: int
    close_approaches_count: int
    processing_time_seconds: float
    aggregations: Optional[Aggregations]    
    success: bool
    error_message: Optional[str] = None 