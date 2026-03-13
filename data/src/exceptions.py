"""Custom exception classes for the NASA NEO Data Pipeline"""

from typing import Any, Dict, Optional


class NEOPipelineError(Exception):
    """Base exception for all NASA NEO pipeline errors"""
    pass


class NEOAPIError(NEOPipelineError):
    """Exception raised for API communication errors"""
    
    def __init__(self, message: str, status_code: Optional[int] = None, 
                 response: Optional[str] = None):
        self.status_code = status_code
        self.response = response
        super().__init__(message)


class DataExtractionError(NEOPipelineError):
    """Exception raised for data extraction and parsing errors"""
    
    def __init__(self, message: str, field: Optional[str] = None, 
                 raw_data: Optional[Dict] = None):
        self.field = field
        self.raw_data = raw_data
        super().__init__(message)


class ValidationError(NEOPipelineError):
    """Exception raised for data validation errors"""
    
    def __init__(self, message: str, field: Optional[str] = None, 
                 value: Any = None):
        self.field = field
        self.value = value
        super().__init__(message)


class StorageError(NEOPipelineError):
    """Exception raised for storage and file system errors"""
    
    def __init__(self, message: str, path: Optional[str] = None):
        self.path = path
        super().__init__(message)
