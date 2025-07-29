"""
Custom exceptions for NEO Data Processor
"""


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