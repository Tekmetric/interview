"""
Storage module for handling data persistence in the data lake
"""

import json
import logging
from pathlib import Path
from typing import Dict
from datetime import datetime
from pyspark.sql import DataFrame

from .models import Aggregations
from .models import StorageError

logger = logging.getLogger(__name__)


class DataStorage:
    """Simplified storage class for NEO data"""
    
    def __init__(self):
        self.base_output_dir = Path("data")
        self.current_year = datetime.now().year
        self.current_month = datetime.now().month
        self.current_day = datetime.now().day
        self._ensure_directories()
    
    def _ensure_directories(self):
        """Ensure required directories exist"""
        directories = [
            self.base_output_dir / "raw" / "neo" / f"year={self.current_year}" / f"month={self.current_month}" / f"day={self.current_day}", # refactor this, we repeat this code 
            self.base_output_dir / "aggregations" / "neo" / f"year={self.current_year}" / f"month={self.current_month}" / f"day={self.current_day}",
        ]
        for directory in directories:
            directory.mkdir(parents=True, exist_ok=True)
    
    def save_aggregations(self, aggregations: Aggregations) -> str:
        """
        Save aggregations as JSON. For a real production system, we would use a different storage solution, like S3.
        
        Args:
            aggregations: Aggregations object to save
            
        Returns:
            Path to saved aggregations file
        """
        try:
            output_path = self.base_output_dir / "aggregations" / "neo" / f"year={self.current_year}"/ f"month={self.current_month}" / f"day={self.current_day}" / "neo_aggregations.json"
            output_path.parent.mkdir(parents=True, exist_ok=True)
            
            with open(output_path, 'w') as f:
                json.dump(aggregations.to_dict(), f, indent=2, default=str)
            
            logger.info(f"Saved aggregations to {output_path}")
            return str(output_path)
            
        except Exception as e:
            raise StorageError(f"Failed to save aggregations: {e}")
    
    def save_raw_data(self, raw_df: DataFrame) -> Dict[str, str]:
        """
        Save raw data with 17 specified columns
        
        Args:
            raw_df: DataFrame with extracted raw data (17 columns, one row per NEO)
            
        Returns:
            Dictionary with file paths
        """
        logger.info("Saving raw data...")
        
        try:
            # Use simple path construction like the constructor
            base_path = self.base_output_dir / "raw" / "neo" / f"year={self.current_year}" / f"month={self.current_month}" / f"day={self.current_day}"
            base_path.mkdir(parents=True, exist_ok=True)
            
            # Save as Parquet for efficient analytics
            parquet_path = base_path / "neo_raw_data.parquet"
            raw_df.write.mode("overwrite").parquet(str(parquet_path))
            
            # Save as JSON for backup and easy inspection
            json_path = base_path / "neo_raw_data.json"
            # Convert to JSON via pandas for cleaner format
            raw_data_list = raw_df.toPandas().to_dict('records')
            with open(json_path, 'w') as f:
                import json
                json.dump(raw_data_list, f, indent=2)
            
            paths = {
                "parquet": str(parquet_path),
                "json": str(json_path)
            }
            
            logger.info(f"Raw data saved to {base_path}")
            return paths
            
        except Exception as e:
            raise StorageError(f"Failed to save raw data: {e}") 