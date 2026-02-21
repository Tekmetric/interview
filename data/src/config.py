"""Configuration management for NASA NEO Data Pipeline"""

import os
from dataclasses import dataclass, field
from typing import List


@dataclass
class PipelineConfig:
    """Pipeline configuration loaded from environment variables
    
    Attributes:
        api_key: NASA API key (required)
        api_base_url: Base URL for NASA NEO API
        max_objects: Maximum NEOs to fetch
        page_size: Records per API page
        base_data_path: Root directory for data storage
        *_layer_path: Relative paths for each data layer
        *_partition_columns: Partition keys for each layer
        compression: Parquet compression codec
        batch_size: Records per batch for streaming writes
        close_approach_threshold_au: Distance threshold for "close" approaches
    """
    
    api_key: str
    api_base_url: str = "https://api.nasa.gov/neo/rest/v1"
    max_objects: int = 200
    page_size: int = 20
    
    base_data_path: str = "s3"
    raw_layer_path: str = "raw/neo"
    curated_layer_path: str = "curated/neo"
    aggregates_layer_path: str = "aggregates/neo"
    
    raw_partition_columns: List[str] = field(
        default_factory=lambda: ['ingestion_year', 'ingestion_month', 'ingestion_day']
    )
    curated_partition_columns: List[str] = field(
        default_factory=lambda: ['approach_year', 'approach_month', 'approach_day']
    )
    aggregates_partition_columns: List[str] = field(
        default_factory=lambda: ['year', 'month', 'day']
    )
    compression: str = "snappy"
    
    batch_size: int = 1000
    streaming_threshold: int = 10000
    
    close_approach_threshold_au: float = 0.2
    
    @classmethod
    def from_env(cls) -> 'PipelineConfig':
        """Load configuration from environment variables
        
        Environment variables:
            NASA_API_KEY (required): API key for authentication
            NASA_API_BASE_URL: Override default API URL
            MAX_OBJECTS: Override max objects to fetch
            BATCH_SIZE: Override batch size for writes
            BASE_DATA_PATH: Override data storage path
        """
        api_key = os.getenv('NASA_API_KEY')
        if not api_key:
            raise ValueError(
                "NASA_API_KEY environment variable is required. "
                "Please set it to your NASA API key."
            )
        
        return cls(
            api_key=api_key,
            api_base_url=os.getenv('NASA_API_BASE_URL', cls.api_base_url),
            max_objects=int(os.getenv('MAX_OBJECTS', str(cls.max_objects))),
            batch_size=int(os.getenv('BATCH_SIZE', str(cls.batch_size))),
            base_data_path=os.getenv('BASE_DATA_PATH', cls.base_data_path)
        )
    
    def get_raw_path(self, year: int, month: int, day: int) -> str:
        """Construct full path for raw data partition by ingestion date"""
        return os.path.join(
            self.base_data_path,
            self.raw_layer_path,
            f"year={year}/month={month:02d}/day={day:02d}"
        )
    
    def get_curated_path(self) -> str:
        """Get base path for curated layer (partitions created within this path)"""
        return os.path.join(self.base_data_path, self.curated_layer_path)
    
    def get_aggregates_path(self, agg_type: str) -> str:
        """Construct path for specific aggregate type (e.g., 'close_approaches')"""
        return os.path.join(
            self.base_data_path,
            self.aggregates_layer_path,
            agg_type
        )
