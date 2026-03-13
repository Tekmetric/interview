"""
Logging configuration for NASA NEO Data Pipeline.

This module provides centralized logging configuration with:
- Standardized log format with timestamp, name, level, and message
- Sensitive data filtering to prevent API key exposure
- Configurable log levels (DEBUG, INFO, WARNING, ERROR, CRITICAL)

Requirements: 6.7, 7.3, 7.4
"""

import logging
import re
from typing import Optional


class SensitiveDataFilter(logging.Filter):
    """
    Filter that redacts sensitive information from log messages.
    
    Prevents exposure of:
    - API keys in URLs and configuration
    - Credentials and authentication tokens
    - Sensitive query parameters
    
    Requirements: 7.3, 7.4
    """
    
    # Patterns to detect and redact sensitive data
    SENSITIVE_PATTERNS = [
        # API keys in URLs (e.g., ?api_key=DEMO_KEY)
        (re.compile(r'api_key=[^&\s]+', re.IGNORECASE), 'api_key=***REDACTED***'),
        # API keys in configuration or logs
        (re.compile(r'(api[_-]?key\s*[:=]\s*)["\']?[^"\'\s]+["\']?', re.IGNORECASE), r'\1***REDACTED***'),
        # Generic tokens and secrets
        (re.compile(r'(token\s*[:=]\s*)["\']?[^"\'\s]+["\']?', re.IGNORECASE), r'\1***REDACTED***'),
        (re.compile(r'(secret\s*[:=]\s*)["\']?[^"\'\s]+["\']?', re.IGNORECASE), r'\1***REDACTED***'),
        # Authorization headers
        (re.compile(r'(Authorization\s*:\s*)[^\s]+', re.IGNORECASE), r'\1***REDACTED***'),
    ]
    
    def filter(self, record: logging.LogRecord) -> bool:
        """
        Filter log record to redact sensitive information.
        
        Args:
            record: Log record to filter
            
        Returns:
            True (always allow record, but modify message)
        """
        # Redact sensitive data from the message
        if hasattr(record, 'msg') and isinstance(record.msg, str):
            for pattern, replacement in self.SENSITIVE_PATTERNS:
                record.msg = pattern.sub(replacement, record.msg)
        
        # Also filter args if present
        if hasattr(record, 'args') and record.args:
            filtered_args = []
            for arg in record.args if isinstance(record.args, tuple) else [record.args]:
                if isinstance(arg, str):
                    for pattern, replacement in self.SENSITIVE_PATTERNS:
                        arg = pattern.sub(replacement, arg)
                filtered_args.append(arg)
            record.args = tuple(filtered_args) if isinstance(record.args, tuple) else filtered_args[0]
        
        return True


def setup_logging(
    level: str = "INFO",
    log_format: Optional[str] = None,
    include_sensitive_filter: bool = True
) -> None:
    """
    Configure logging for the NASA NEO Data Pipeline.
    
    Sets up:
    - Log format with timestamp, logger name, level, and message
    - Log level (DEBUG, INFO, WARNING, ERROR, CRITICAL)
    - Sensitive data filtering to prevent credential exposure
    
    Args:
        level: Logging level (DEBUG, INFO, WARNING, ERROR, CRITICAL)
        log_format: Custom log format string (uses default if None)
        include_sensitive_filter: Whether to add sensitive data filter
        
    Requirements: 6.7, 7.3, 7.4
    """
    # Default log format: timestamp - logger name - level - message
    if log_format is None:
        log_format = '%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    
    # Convert string level to logging constant
    numeric_level = getattr(logging, level.upper(), logging.INFO)
    
    # Configure root logger
    logging.basicConfig(
        level=numeric_level,
        format=log_format,
        datefmt='%Y-%m-%d %H:%M:%S'
    )
    
    # Add sensitive data filter to all handlers
    if include_sensitive_filter:
        sensitive_filter = SensitiveDataFilter()
        root_logger = logging.getLogger()
        for handler in root_logger.handlers:
            handler.addFilter(sensitive_filter)


def get_logger(name: str) -> logging.Logger:
    """
    Get a logger instance for a specific module.
    
    Args:
        name: Logger name (typically __name__ of the module)
        
    Returns:
        Configured logger instance
        
    Example:
        logger = get_logger(__name__)
        logger.info("Processing NEO data")
    """
    return logging.getLogger(name)


# Log level constants for convenience
DEBUG = "DEBUG"
INFO = "INFO"
WARNING = "WARNING"
ERROR = "ERROR"
CRITICAL = "CRITICAL"
