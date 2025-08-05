import asyncio
import logging
import logging.config
import time
import tracemalloc
from functools import wraps
from typing import Any, Callable

LOGGING_CONFIG_TEMPLATE = {
    "version": 1,
    "disable_existing_loggers": False,
    "formatters": {
        "console": {
            "format": "[%(levelname)s] :: %(message)s",
            "datefmt": "[%m/%d %H:%M:%S]",
        },
        "file": {
            "format": "%(asctime)s - %(levelname)s - %(name)s :: %(message)s",
            "datefmt": "[%m/%d %H:%M:%S]",
        },
    },
    "handlers": {
        "console": {
            "level": "INFO",
            "class": "rich.logging.RichHandler",
            "formatter": "console",
        },
    },
    "loggers": {
        "neo": {
            "level": "DEBUG",
            "handlers": ["console"],
            "propagate": False,
        },
    },
}


def profile(func: Callable) -> Callable:
    """Decorator to profile execution time and memory usage of sync or async functions."""

    def log_profile(start_time: float):
        end_time = time.perf_counter()
        current, peak = tracemalloc.get_traced_memory()
        tracemalloc.stop()
        logger.info(
            f"[PROFILE] {func.__name__} took {end_time - start_time:.4f}s | "
            f"Current memory: {current / 1024:.2f} KB | Peak memory: {peak / 1024:.2f} KB"
        )

    @wraps(func)
    async def async_wrapper(*args, **kwargs) -> Any:
        tracemalloc.start()
        start_time = time.perf_counter()
        try:
            return await func(*args, **kwargs)
        finally:
            log_profile(start_time)

    @wraps(func)
    def sync_wrapper(*args, **kwargs) -> Any:
        tracemalloc.start()
        start_time = time.perf_counter()
        try:
            return func(*args, **kwargs)
        finally:
            log_profile(start_time)

    return async_wrapper if asyncio.iscoroutinefunction(func) else sync_wrapper


def configure_logger(
    log_console_debug: bool = False,
    log_file_path: str | None = None,
    log_config: dict[str, Any] | None = LOGGING_CONFIG_TEMPLATE,
) -> None:
    """
    Configure logging based on dictionary config.

    Args:
        log_console_debug: If True, set console logging to DEBUG.
        log_file_path: Optional custom path for log file.
        log_config: Optional override config dictionary.
    """
    config = log_config or LOGGING_CONFIG_TEMPLATE.copy()

    if log_console_debug:
        config["handlers"]["console"]["level"] = "DEBUG"

    if log_file_path:
        config["handlers"]["file"] = {
            "level": "DEBUG",
            "class": "logging.FileHandler",
            "formatter": "file",
            "filename": log_file_path,
            "encoding": "utf-8",
        }
        config["loggers"]["neo"]["handlers"].append("file")

    logging.config.dictConfig(config)


logger = logging.getLogger("neo")
"""The default logger for the NEO package."""
