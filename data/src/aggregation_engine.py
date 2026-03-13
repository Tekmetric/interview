"""Aggregation engine for NASA NEO Data Pipeline"""

import os
from typing import Dict, Iterator, Tuple
import pyarrow as pa
import pyarrow.parquet as pq

from .storage_manager import ParquetStorageManager


class AggregationEngine:
    """Computes summary statistics from curated NEO data using streaming for O(1) memory"""
    
    def __init__(self, data_path: str):
        """Initialize with path to curated data layer"""
        self.data_path = data_path
    
    def _au_to_km(self, au: float) -> float:
        """Convert astronomical units to kilometers using IAU standard (1 AU = 149,597,870.7 km)"""
        AU_TO_KM = 149597870.7
        return au * AU_TO_KM
    
    def _read_parquet_streaming(self, path: str) -> Iterator[pa.RecordBatch]:
        """Read Parquet data in batches to avoid loading entire dataset into memory"""
        if not os.path.exists(path):
            return
        
        if os.path.isdir(path):
            dataset = pq.ParquetDataset(path, use_legacy_dataset=False)
            table = dataset.read()
            for batch in table.to_batches(max_chunksize=1000):
                yield batch
        else:
            parquet_file = pq.ParquetFile(path)
            for batch in parquet_file.iter_batches(batch_size=1000):
                yield batch
    
    def compute_close_approaches(self, threshold_au: float) -> int:
        """Count approaches with miss distance < threshold
        
        Args:
            threshold_au: Distance threshold in AU (e.g., 0.2)
            
        Returns:
            Count of close approaches
        """
        threshold_km = self._au_to_km(threshold_au)
        count = 0
        
        for batch in self._read_parquet_streaming(self.data_path):
            if 'closest_miss_distance_km' in batch.schema.names:
                distances = batch.column('closest_miss_distance_km').to_pylist()
                count += sum(1 for d in distances if d is not None and d < threshold_km)
        
        return count
    
    def compute_approaches_by_year(self) -> Dict[int, int]:
        """Count approaches grouped by year, leveraging partition structure for efficiency
        
        Returns:
            Dictionary mapping year to count
        """
        year_counts = {}
        
        if not os.path.exists(self.data_path):
            return year_counts
        
        # Use partition structure (year= directories) for efficient counting
        try:
            for item in os.listdir(self.data_path):
                if item.startswith('year='):
                    year_str = item.split('=')[1]
                    year = int(year_str)
                    
                    year_path = os.path.join(self.data_path, item)
                    
                    count = 0
                    for batch in self._read_parquet_streaming(year_path):
                        count += len(batch)
                    
                    year_counts[year] = count
        
        except (OSError, ValueError):
            # Fallback: stream through all data and count by year
            for batch in self._read_parquet_streaming(self.data_path):
                if 'approach_year' in batch.schema.names:
                    years = batch.column('approach_year').to_pylist()
                    
                    for year in years:
                        if year is not None:
                            year_counts[year] = year_counts.get(year, 0) + 1
        
        return year_counts
    
    def write_aggregates(self, storage_manager: ParquetStorageManager,
                        close_approaches_count: int,
                        approaches_by_year: Dict[int, int],
                        agg_date: Tuple[int, int, int]) -> None:
        """Write computed aggregates to storage layer
        
        Args:
            storage_manager: Storage manager for writing
            close_approaches_count: Count of close approaches
            approaches_by_year: Year -> count mapping
            agg_date: Aggregation date for metadata
        """
        # Write close approaches aggregate as single file
        close_approaches_data = {
            'count': close_approaches_count,
            'threshold_au': 0.2,
            'threshold_km': self._au_to_km(0.2),
            'aggregation_date': f"{agg_date[0]}-{agg_date[1]:02d}-{agg_date[2]:02d}"
        }
        storage_manager.write_aggregate_single(
            'close_approaches',
            close_approaches_data
        )
        
        # Write approaches by year as single file with all years
        year_records = [
            {
                'year': year,
                'count': count,
                'aggregation_date': f"{agg_date[0]}-{agg_date[1]:02d}-{agg_date[2]:02d}"
            }
            for year, count in sorted(approaches_by_year.items())
        ]
        storage_manager.write_aggregate_multiple(
            'approaches_by_year',
            year_records
        )
