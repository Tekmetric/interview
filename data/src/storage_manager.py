"""Storage management for NASA NEO Data Pipeline"""

import os
from typing import Iterator, Dict, List, Tuple, Optional
import pyarrow as pa
import pyarrow.parquet as pq

from .exceptions import StorageError


class ParquetStorageManager:
    """Manages writing data to Parquet files with partitioning across raw, curated, and aggregates layers
    
    Uses PyArrow streaming writer for O(1) memory complexity regardless of dataset size.
    """
    
    # Explicit schema prevents PyArrow type inference issues (e.g., name_limited: null vs string)
    CURATED_SCHEMA = pa.schema([
        ('id', pa.string()),
        ('neo_reference_id', pa.string()),
        ('name', pa.string()),
        ('name_limited', pa.string()),
        ('designation', pa.string()),
        ('nasa_jpl_url', pa.string()),
        ('absolute_magnitude_h', pa.float64()),
        ('is_potentially_hazardous', pa.bool_()),
        ('diameter_min_meters', pa.float64()),
        ('diameter_max_meters', pa.float64()),
        ('closest_approach_date', pa.string()),
        ('closest_miss_distance_km', pa.float64()),
        ('closest_relative_velocity_kms', pa.float64()),
        ('first_observation_date', pa.string()),
        ('last_observation_date', pa.string()),
        ('observations_used', pa.int64()),
        ('orbital_period_days', pa.float64()),
        ('ingestion_year', pa.int64()),
        ('ingestion_month', pa.int64()),
        ('ingestion_day', pa.int64()),
        ('approach_year', pa.int64()),
        ('approach_month', pa.int64()),
        ('approach_day', pa.int64()),
    ])
    
    def __init__(self, base_path: str, layer: str, batch_size: int = 1000,
                 compression: str = 'snappy'):
        self.base_path = base_path
        self.layer = layer
        self.batch_size = batch_size
        self.compression = compression
    
    def _batch_iterator(self, data: Iterator[Dict], batch_size: int) -> Iterator[List[Dict]]:
        """Convert stream of individual records into batches for efficient Parquet writes"""
        batch = []
        for record in data:
            batch.append(record)
            if len(batch) >= batch_size:
                yield batch
                batch = []
        
        if batch:
            yield batch
    
    def _write_streaming(self, data: Iterator[Dict], path: str,
                        partition_cols: Optional[List[str]] = None,
                        schema: Optional[pa.Schema] = None) -> int:
        """Write data in batches using PyArrow streaming writer
        
        Args:
            data: Iterator of records
            path: Full file path
            partition_cols: Partition columns (unused, kept for API compatibility)
            schema: Explicit schema to prevent type inference issues
            
        Returns:
            Number of records written
        """
        try:
            os.makedirs(os.path.dirname(path), exist_ok=True)
            
            writer = None
            total_records = 0
            
            for batch in self._batch_iterator(data, self.batch_size):
                if schema:
                    table = pa.Table.from_pylist(batch, schema=schema)
                else:
                    table = pa.Table.from_pylist(batch)
                total_records += len(batch)
                
                if writer is None:
                    writer = pq.ParquetWriter(
                        path,
                        table.schema,
                        compression=self.compression,
                        use_dictionary=True,
                        write_statistics=True
                    )
                
                writer.write_table(table)
            
            if writer:
                writer.close()
            
            return total_records
            
        except PermissionError as e:
            raise StorageError(
                f"Permission denied writing to {path}: {e}",
                path=path
            )
        except OSError as e:
            raise StorageError(
                f"OS error writing to {path}: {e}",
                path=path
            )
        except Exception as e:
            raise StorageError(
                f"Unexpected error writing to {path}: {e}",
                path=path
            )

    def write_raw_data(self, data: Iterator[Dict], 
                      ingestion_date: Tuple[int, int, int]) -> int:
        """Write data to raw layer partitioned by ingestion date
        
        Path: {base_path}/year=YYYY/month=MM/day=DD/data.parquet
        """
        year, month, day = ingestion_date
        
        partition_path = os.path.join(
            self.base_path,
            f"year={year}",
            f"month={month:02d}",
            f"day={day:02d}"
        )
        
        file_path = os.path.join(partition_path, "data.parquet")
        return self._write_streaming(data, file_path)
    
    def write_curated_data(self, data: Iterator[Dict], 
                          ingestion_date: Optional[Tuple[int, int, int]] = None) -> int:
        """Write data to curated layer partitioned by approach date
        
        Records with missing approach dates are written to error layer partitioned by
        ingestion date for debugging/reprocessing. Error records include _error_reason
        and _error_details fields.
        
        Args:
            data: Iterator of curated records with approach_year/month/day fields
            ingestion_date: Used for error folder partitioning
            
        Returns:
            Total records written (valid + error)
        """
        partitions = {}
        failed_records = []
        
        for record in data:
            try:
                year = record['approach_year']
                month = record['approach_month']
                day = record['approach_day']
            except KeyError as e:
                raise ValueError(
                    f"Record missing required partition field: {e}. "
                    "Curated records must have approach_year, approach_month, approach_day."
                )
            
            # Records with None values go to error folder
            if year is None or month is None or day is None:
                record['_error_reason'] = 'missing_approach_date'
                record['_error_details'] = f"approach_year={year}, approach_month={month}, approach_day={day}"
                failed_records.append(record)
            else:
                partition_key = (year, month, day)
                
                if partition_key not in partitions:
                    partitions[partition_key] = []
                partitions[partition_key].append(record)
        
        # Write valid records to curated layer
        total_records = 0
        for (year, month, day), records in partitions.items():
            partition_path = os.path.join(
                self.base_path,
                f"year={year}",
                f"month={month:02d}",
                f"day={day:02d}"
            )
            
            file_path = os.path.join(partition_path, "data.parquet")
            count = self._write_streaming(iter(records), file_path, schema=self.CURATED_SCHEMA)
            total_records += count
        
        # Write failed records to error layer
        if failed_records:
            if ingestion_date:
                ing_year, ing_month, ing_day = ingestion_date
            else:
                from datetime import datetime
                now = datetime.now()
                ing_year, ing_month, ing_day = now.year, now.month, now.day
            
            # Replace 'curated' with 'error' in path
            base_parts = self.base_path.split('/')
            error_parts = [p if p != 'curated' else 'error' for p in base_parts]
            error_base = '/'.join(error_parts)
            
            error_path = os.path.join(
                error_base,
                f"year={ing_year}",
                f"month={ing_month:02d}",
                f"day={ing_day:02d}",
                "data.parquet"
            )
            count = self._write_streaming(iter(failed_records), error_path)
            total_records += count
        
        return total_records
    
    def write_aggregate_single(self, agg_type: str, data: Dict) -> int:
        """Write single aggregate record to unpartitioned file
        
        Path: {base_path}/{agg_type}/summary.parquet
        
        Args:
            agg_type: Aggregate type (e.g., 'close_approaches')
            data: Single aggregate record as dictionary
        """
        file_path = os.path.join(self.base_path, agg_type, "summary.parquet")
        data_iter = iter([data])
        return self._write_streaming(data_iter, file_path)
    
    def write_aggregate_multiple(self, agg_type: str, data: List[Dict]) -> int:
        """Write multiple aggregate records to unpartitioned file
        
        Path: {base_path}/{agg_type}/summary.parquet
        
        Args:
            agg_type: Aggregate type (e.g., 'approaches_by_year')
            data: List of aggregate records
        """
        file_path = os.path.join(self.base_path, agg_type, "summary.parquet")
        data_iter = iter(data)
        return self._write_streaming(data_iter, file_path)
