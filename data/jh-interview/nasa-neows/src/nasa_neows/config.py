"""Application settings and data directory paths.

Loads NASA_API_KEY from environment variables or a .env file via pydantic-settings,
and defines the output directory constants (RAW_DIR, PROCESSED_DIR, AGGREGATIONS_DIR)
used by all file I/O across the project.
"""

from pathlib import Path

from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    """
    Loads NASA_API_KEY from environment variables
    or a .env file via pydantic-settings
    """

    nasa_api_key: str

    model_config = {"env_file": ".env"}


settings = Settings()

DATA_DIR = Path("data")
RAW_DIR = DATA_DIR / "raw" / "neo_browse"
PROCESSED_DIR = DATA_DIR / "processed" / "neos"
AGGREGATIONS_DIR = DATA_DIR / "aggregations"
