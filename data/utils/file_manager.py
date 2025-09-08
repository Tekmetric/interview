import os
import pandas as pd
from pathlib import Path
from typing import Dict, Any
from datetime import datetime


class FileManager:
    def __init__(self, base_path: str = "s3_data_lake"):
        self.base_path = Path(base_path)
        self.raw_path = self.base_path / "raw"
        self.aggregated_path = self.base_path / "aggregated"
        self._ensure_directories()

    def _ensure_directories(self):
        """Create necessary directory structure."""
        self.raw_path.mkdir(parents=True, exist_ok=True)
        self.aggregated_path.mkdir(parents=True, exist_ok=True)

    def _get_partitioned_path(self, timestamp: datetime = None) -> Path:
        """Generate S3-like partitioned path based on timestamp."""
        if timestamp is None:
            timestamp = datetime.now()
        
        year = timestamp.strftime("%Y")
        month = timestamp.strftime("%m")
        
        return self.raw_path / f"year={year}" / f"month={month}"

    def save_raw_data(self, df: pd.DataFrame, filename: str = "neo_data.parquet") -> str:
        """Save raw NEO data in partitioned Parquet format."""
        partitioned_path = self._get_partitioned_path()
        partitioned_path.mkdir(parents=True, exist_ok=True)
        
        file_path = partitioned_path / filename
        
        # Optimize Parquet settings for performance
        df.to_parquet(
            file_path,
            engine='pyarrow',
            compression='snappy',
            index=False
        )
        
        return str(file_path)

    def save_aggregations(self, aggregations: Dict[str, Any]) -> str:
        """Save aggregation results to Parquet files."""
        # Save yearly approaches data
        yearly_df = pd.DataFrame([
            {'year': year, 'approach_count': count}
            for year, count in aggregations['yearly_approaches'].items()
        ])
        
        yearly_file = self.aggregated_path / "yearly_approaches.parquet"
        yearly_df.to_parquet(
            yearly_file,
            engine='pyarrow',
            compression='snappy',
            index=False
        )
        
        # Save summary statistics
        summary_data = {
            'close_approaches_under_02_au': [aggregations['close_approaches_under_02_au']],
            'total_objects': [len(aggregations.get('processed_objects', []))],
            'generated_at': [datetime.now().isoformat()]
        }
        
        summary_df = pd.DataFrame(summary_data)
        summary_file = self.aggregated_path / "summary_stats.parquet"
        summary_df.to_parquet(
            summary_file,
            engine='pyarrow',
            compression='snappy',
            index=False
        )
        
        return str(summary_file)

    def load_latest_data(self) -> pd.DataFrame:
        """Load the most recent raw data file."""
        raw_files = list(self.raw_path.rglob("*.parquet"))
        if not raw_files:
            return pd.DataFrame()
        
        latest_file = max(raw_files, key=os.path.getmtime)
        return pd.read_parquet(latest_file)

    def get_data_summary(self) -> Dict[str, Any]:
        """Get summary of stored data files."""
        raw_files = list(self.raw_path.rglob("*.parquet"))
        agg_files = list(self.aggregated_path.rglob("*.parquet"))
        
        return {
            'raw_files_count': len(raw_files),
            'aggregated_files_count': len(agg_files),
            'latest_raw_file': max(raw_files, key=os.path.getmtime).name if raw_files else None,
            'total_size_mb': sum(f.stat().st_size for f in raw_files + agg_files) / (1024 * 1024)
        }
