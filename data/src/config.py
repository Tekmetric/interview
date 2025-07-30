"""
Configuration management for NEO Data Processor
"""

import os
from pathlib import Path
from typing import Optional
from dataclasses import dataclass
from dotenv import load_dotenv

# Load environment variables
load_dotenv()


@dataclass
class APIConfig:
    """NASA NeoWs API configuration"""
    api_key: str
    base_url: str = "https://api.nasa.gov"
    request_timeout: int = 30
    rate_limit_delay: float = 3.0  # Increased from 1.0 to 3.0 for Browse API
    max_retries: int = 3
    
    @property
    def neo_browse_endpoint(self) -> str:
        return f"{self.base_url}/neo/rest/v1/neo/browse"


@dataclass
class SparkConfig:
    """Spark configuration for local and distributed execution"""
    app_name: str = "NASA_NEO_Data_Processor"
    adaptive_enabled: bool = True
    adaptive_coalesce_partitions: bool = True
    min_partition_num: int = 1
    advisory_partition_size: str = "64MB"
    arrow_enabled: bool = True
    serializer: str = "org.apache.spark.serializer.KryoSerializer"
    
    def to_spark_configs(self) -> dict:
        """Convert to Spark configuration dictionary"""
        return {
            "spark.sql.adaptive.enabled": str(self.adaptive_enabled).lower(),
            "spark.sql.adaptive.coalescePartitions.enabled": str(self.adaptive_coalesce_partitions).lower(),
            "spark.sql.adaptive.coalescePartitions.minPartitionNum": str(self.min_partition_num),
            "spark.sql.adaptive.advisoryPartitionSizeInBytes": self.advisory_partition_size,
            "spark.sql.execution.arrow.pyspark.enabled": str(self.arrow_enabled).lower(),
            "spark.serializer": self.serializer,
        }


@dataclass
class StorageConfig:
    """Storage configuration for data lake structure"""
    base_output_dir: Path = Path("data")
    compression: str = "snappy"
    partition_by_year: bool = True
    
    def get_raw_data_path(self, year: int) -> Path:
        """Get raw data storage path"""
        if self.partition_by_year:
            return self.base_output_dir / "raw" / "neo" / f"year={year}"
        return self.base_output_dir / "raw" / "neo"
    
    def get_processed_data_path(self, year: int) -> Path:
        """Get processed data storage path"""
        if self.partition_by_year:
            return self.base_output_dir / "processed" / "neo" / f"year={year}"
        return self.base_output_dir / "processed" / "neo"
    
    def get_aggregations_path(self, year: int) -> Path:
        """Get aggregations storage path"""
        if self.partition_by_year:
            return self.base_output_dir / "aggregations" / "neo" / f"year={year}"
        return self.base_output_dir / "aggregations" / "neo"


@dataclass
class ProcessingConfig:
    """Data processing configuration"""
    object_limit: int = 200
    batch_size: int = 10
    close_approach_threshold_au: float = 0.2


class Config:
    """Main configuration class that combines all configs"""
    
    def __init__(self, api_key: Optional[str] = None):
        self.api = APIConfig(
            api_key=api_key or os.getenv('NASA_API_KEY', 'DEMO_KEY')
        )
        self.spark = SparkConfig()
        self.storage = StorageConfig()
        self.processing = ProcessingConfig()
    
    @classmethod
    def from_env(cls) -> 'Config':
        """Create configuration from environment variables"""
        return cls()
    
    def ensure_directories(self, year: int) -> None:
        """Ensure all necessary directories exist"""
        directories = [
            self.storage.get_raw_data_path(year),
            self.storage.get_processed_data_path(year),
            self.storage.get_aggregations_path(year),
        ]
        
        for directory in directories:
            directory.mkdir(parents=True, exist_ok=True) 