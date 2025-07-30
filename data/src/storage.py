"""
Storage module for handling data persistence in the data lake
"""

import json
import logging
from pathlib import Path
from typing import Dict, Any, List
from datetime import datetime
from pyspark.sql import DataFrame

from .config import Config
from .models import Aggregations, ProcessingResult
from .models import StorageError

logger = logging.getLogger(__name__)


class DataStorage:
    """Simplified storage class for NEO data"""
    
    def __init__(self):
        self.base_output_dir = Path("data")
        self.current_year = datetime.now().year
        self._ensure_directories()
    
    def _ensure_directories(self):
        """Ensure all necessary directories exist"""
        directories = [
            self.base_output_dir / "raw",
            self.base_output_dir / "processed", 
            self.base_output_dir / "aggregations"
        ]
        for directory in directories:
            directory.mkdir(parents=True, exist_ok=True)
    
    def save_dataframe(self, df: DataFrame, data_type: str, format: str = "parquet", 
                      partition_by: List[str] = None) -> str:
        """
        Save DataFrame to storage
        
        Args:
            df: Spark DataFrame to save
            data_type: Type of data (e.g., 'processed', 'raw')
            format: File format ('parquet', 'json')
            partition_by: List of columns to partition by
            
        Returns:
            Path to saved data
        """
        try:
            output_path = self.base_output_dir / data_type / f"neo_{data_type}_data"
            
            writer = df.coalesce(1).write.mode("overwrite")
            
            if partition_by:
                writer = writer.partitionBy(*partition_by)
            
            if format == "parquet":
                writer.option("compression", "snappy").parquet(str(output_path))
            elif format == "json":
                writer.json(str(output_path))
            
            logger.info(f"Saved {data_type} data to {output_path}")
            return str(output_path)
            
        except Exception as e:
            raise StorageError(f"Failed to save {data_type} data: {e}")
    
    def save_aggregations(self, aggregations: Aggregations) -> str:
        """
        Save aggregations as JSON
        
        Args:
            aggregations: Aggregations object to save
            
        Returns:
            Path to saved aggregations
        """
        try:
            output_path = self.base_output_dir / "aggregations" / "neo_aggregations.json"
            output_path.parent.mkdir(parents=True, exist_ok=True)
            
            with open(output_path, 'w') as f:
                json.dump(aggregations.to_dict(), f, indent=2, default=str)
            
            logger.info(f"Saved aggregations to {output_path}")
            return str(output_path)
            
        except Exception as e:
            raise StorageError(f"Failed to save aggregations: {e}")


class DataLakeStorage:
    """Handles data storage operations for the NEO data lake"""
    
    def __init__(self, config: Config):
        self.config = config
        self.current_year = datetime.now().year
        
        # Ensure directories exist
        self.config.ensure_directories(self.current_year)
    
    def save_raw_data(self, data: List[Dict[str, Any]], df: DataFrame) -> Dict[str, str]:
        """
        Save raw data in both JSON and Parquet formats
        
        Args:
            data: Raw data as list of dictionaries
            df: Spark DataFrame with raw data
            
        Returns:
            Dictionary with file paths
        """
        logger.info("Saving raw data...")
        
        try:
            base_path = self.config.storage.get_raw_data_path(self.current_year)
            
            # Save as JSON for backup
            json_path = base_path / "neo_raw_data.json"
            self._save_json(data, json_path)
            
            # Save as Parquet for analytics
            parquet_path = base_path / "neo_raw_data.parquet"
            self._save_parquet(df, parquet_path)
            
            paths = {
                "json": str(json_path),
                "parquet": str(parquet_path)
            }
            
            logger.info(f"Raw data saved to {base_path}")
            return paths
            
        except Exception as e:
            raise StorageError(f"Failed to save raw data: {e}")
    
    def save_processed_data(self, df: DataFrame) -> str:
        """
        Save processed data in Parquet format
        
        Args:
            df: Processed Spark DataFrame
            
        Returns:
            Path to saved data
        """
        logger.info("Saving processed data...")
        
        try:
            # Use simple path construction like the constructor
            base_path = self.base_output_dir / "processed" / "neo" / f"year={self.current_year}"
            base_path.mkdir(parents=True, exist_ok=True)
            parquet_path = base_path / "neo_processed_data.parquet"
            
            # Use the existing save_dataframe method
            return self.save_dataframe(df, "processed", format="parquet")
            
        except Exception as e:
            raise StorageError(f"Failed to save processed data: {e}")
    
    def save_aggregations(self, aggregations: Aggregations) -> Dict[str, str]:
        """
        Save aggregations in JSON and Parquet formats
        
        Args:
            aggregations: Calculated aggregations
            
        Returns:
            Dictionary with file paths
        """
        logger.info("Saving aggregations...")
        
        try:
            # Use simple path construction like the constructor
            base_path = self.base_output_dir / "aggregations" / "neo" / f"year={self.current_year}"
            base_path.mkdir(parents=True, exist_ok=True)
            
            # Save as JSON
            json_path = base_path / "neo_aggregations.json"
            with open(json_path, 'w') as f:
                import json
                json.dump(aggregations.to_dict(), f, indent=2)
            
            # Save yearly approach data as JSON for simplicity  
            if aggregations.approaches_by_year:
                yearly_data = [
                    {"year": year, "approach_count": count}
                    for year, count in aggregations.approaches_by_year.items()
                ]
                
                # Save yearly data as separate JSON file
                yearly_json_path = base_path / "approaches_by_year.json"
                with open(yearly_json_path, 'w') as f:
                    json.dump(yearly_data, f, indent=2)
            else:
                yearly_json_path = None
            
            paths = {
                "json": str(json_path),
                "yearly_json": str(yearly_json_path) if yearly_json_path else None
            }
            
            logger.info(f"Aggregations saved to {base_path}")
            return paths
            
        except Exception as e:
            raise StorageError(f"Failed to save aggregations: {e}")
    
    def load_processed_data(self, spark_session) -> DataFrame:
        """
        Load processed data from Parquet files
        
        Args:
            spark_session: Active Spark session
            
        Returns:
            Loaded DataFrame
        """
        try:
            parquet_path = self.config.storage.get_processed_data_path(self.current_year) / "neo_processed_data.parquet"
            
            if not parquet_path.exists():
                raise StorageError(f"Processed data not found at {parquet_path}")
            
            df = spark_session.read.parquet(str(parquet_path))
            logger.info(f"Loaded processed data from {parquet_path}")
            return df
            
        except Exception as e:
            raise StorageError(f"Failed to load processed data: {e}")
    
    def load_aggregations(self) -> Aggregations:
        """
        Load aggregations from JSON file
        
        Returns:
            Aggregations object
        """
        try:
            json_path = self.config.storage.get_aggregations_path(self.current_year) / "neo_aggregations.json"
            
            if not json_path.exists():
                raise StorageError(f"Aggregations not found at {json_path}")
            
            with open(json_path, 'r') as f:
                data = json.load(f)
            
            aggregations = Aggregations.from_dict(data)
            logger.info(f"Loaded aggregations from {json_path}")
            return aggregations
            
        except Exception as e:
            raise StorageError(f"Failed to load aggregations: {e}")
    
    def list_available_data(self) -> Dict[str, List[str]]:
        """
        List all available data files in the data lake
        
        Returns:
            Dictionary with file listings by category
        """
        available_data = {
            "raw": [],
            "processed": [],
            "aggregations": []
        }
        
        try:
            # Check for raw data
            raw_path = self.config.storage.get_raw_data_path(self.current_year)
            if raw_path.exists():
                available_data["raw"] = [
                    str(f.relative_to(self.config.storage.base_output_dir))
                    for f in raw_path.rglob("*")
                    if f.is_file()
                ]
            
            # Check for processed data
            processed_path = self.config.storage.get_processed_data_path(self.current_year)
            if processed_path.exists():
                available_data["processed"] = [
                    str(f.relative_to(self.config.storage.base_output_dir))
                    for f in processed_path.rglob("*")
                    if f.is_file()
                ]
            
            # Check for aggregations
            agg_path = self.config.storage.get_aggregations_path(self.current_year)
            if agg_path.exists():
                available_data["aggregations"] = [
                    str(f.relative_to(self.config.storage.base_output_dir))
                    for f in agg_path.rglob("*")
                    if f.is_file()
                ]
            
            return available_data
            
        except Exception as e:
            logger.warning(f"Error listing available data: {e}")
            return available_data
    
    def _save_json(self, data: Any, file_path: Path) -> None:
        """Save data as JSON file"""
        try:
            file_path.parent.mkdir(parents=True, exist_ok=True)
            
            with open(file_path, 'w') as f:
                json.dump(data, f, indent=2, default=str)
                
        except Exception as e:
            raise StorageError(f"Failed to save JSON to {file_path}: {e}")
    
    def _save_parquet(self, df: DataFrame, file_path: Path) -> None:
        """Save DataFrame as Parquet file"""
        try:
            file_path.parent.mkdir(parents=True, exist_ok=True)
            
            df.coalesce(1).write \
                .mode("overwrite") \
                .option("compression", self.config.storage.compression) \
                .parquet(str(file_path))
                
        except Exception as e:
            raise StorageError(f"Failed to save Parquet to {file_path}: {e}")
    
    def _save_yearly_data_as_parquet(self, yearly_data: List[Dict], base_path: Path) -> Path:
        """Save yearly approach data as a simple CSV file (since we don't have Spark here)"""
        try:
            # For now, save as JSON since we don't have Spark context here
            # In a full implementation, this would be handled in the main processor
            csv_path = base_path / "approaches_by_year.json"
            self._save_json(yearly_data, csv_path)
            return csv_path
            
        except Exception as e:
            logger.warning(f"Failed to save yearly data: {e}")
            return None
    
    def get_storage_stats(self) -> Dict[str, Any]:
        """
        Get storage statistics for the data lake
        
        Returns:
            Dictionary with storage statistics
        """
        stats = {
            "base_directory": str(self.config.storage.base_output_dir),
            "current_year": self.current_year,
            "compression": self.config.storage.compression,
            "partitioned_by_year": self.config.storage.partition_by_year,
            "directory_sizes": {}
        }
        
        try:
            # Calculate directory sizes
            for category in ["raw", "processed", "aggregations"]:
                if category == "raw":
                    path = self.config.storage.get_raw_data_path(self.current_year)
                elif category == "processed":
                    path = self.config.storage.get_processed_data_path(self.current_year)
                else:
                    path = self.config.storage.get_aggregations_path(self.current_year)
                
                if path.exists():
                    total_size = sum(f.stat().st_size for f in path.rglob("*") if f.is_file())
                    stats["directory_sizes"][category] = {
                        "path": str(path),
                        "size_bytes": total_size,
                        "size_mb": round(total_size / (1024 * 1024), 2),
                        "file_count": len(list(path.rglob("*"))) if path.is_dir() else 0
                    }
                else:
                    stats["directory_sizes"][category] = {
                        "path": str(path),
                        "size_bytes": 0,
                        "size_mb": 0,
                        "file_count": 0
                    }
        
        except Exception as e:
            logger.warning(f"Error calculating storage stats: {e}")
        
        return stats 