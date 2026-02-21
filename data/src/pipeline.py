"""
Pipeline orchestrator for NASA NEO Data Pipeline.

This module provides the NEOPipeline class which coordinates the execution
of all pipeline stages: fetching data from the API, staging raw data,
transforming to curated format, and computing aggregates.

The pipeline follows a three-layer architecture:
1. Raw layer: Data as fetched from API (partitioned by ingestion date)
2. Curated layer: Validated and transformed data (partitioned by event date)
3. Aggregates layer: Pre-computed summary statistics
"""

import os
import logging
from datetime import datetime
from typing import Tuple, Dict
from dataclasses import dataclass

from .config import PipelineConfig
from .api_client import NEOAPIClient
from .data_extractor import NEODataExtractor
from .data_transformer import NEODataTransformer
from .storage_manager import ParquetStorageManager
from .aggregation_engine import AggregationEngine


logger = logging.getLogger(__name__)


@dataclass
class PipelineResult:
    """
    Result of pipeline execution containing counts and paths.
    
    Attributes:
        raw_records_count: Number of records written to raw layer
        curated_records_count: Number of records written to curated layer
        close_approaches_count: Count of approaches < 0.2 AU
        approaches_by_year: Dictionary mapping year to count
        raw_path: Path to raw data partition
        curated_path: Path to curated data layer
        aggregates_path: Path to aggregates layer
    """
    raw_records_count: int
    curated_records_count: int
    close_approaches_count: int
    approaches_by_year: Dict[int, int]
    raw_path: str
    curated_path: str
    aggregates_path: str


class NEOPipeline:
    """
    Orchestrates the NASA NEO data pipeline execution.
    
    This class coordinates all pipeline stages:
    1. Fetch data from NASA API
    2. Stage raw data (raw layer, partitioned by ingestion date)
    3. Transform and validate data
    4. Write curated data (curated layer, partitioned by event date)
    5. Compute aggregates
    6. Write aggregates (aggregates layer)
    
    The pipeline uses streaming approaches throughout to maintain O(1) memory
    complexity, enabling it to scale to 100x data growth.
    
    Attributes:
        config: Pipeline configuration
        api_client: Client for NASA NEO API
        extractor: Data extraction component
        transformer: Data transformation component
    """
    
    def __init__(self, config: PipelineConfig):
        """
        Initialize pipeline with configuration and components.
        
        Args:
            config: PipelineConfig instance with all settings
            
        Example:
            >>> config = PipelineConfig.from_env()
            >>> pipeline = NEOPipeline(config)
            >>> result = pipeline.run()
        """
        self.config = config
        
        # Initialize components
        self.api_client = NEOAPIClient(
            api_key=config.api_key,
            base_url=config.api_base_url
        )
        self.extractor = NEODataExtractor()
        self.transformer = NEODataTransformer()
        
        logger.info("NEOPipeline initialized with config: max_objects=%d, batch_size=%d",
                   config.max_objects, config.batch_size)
    
    def _stage_raw_data(self) -> Tuple[int, int, int]:
        """
        Fetch data from API and write to raw layer.
        
        This stage:
        1. Fetches NEO data from NASA API (streaming)
        2. Extracts fields from JSON responses
        3. Adds ingestion timestamp fields
        4. Writes to raw layer partitioned by ingestion date
        
        The raw layer preserves data exactly as extracted from the API,
        enabling reprocessing if transformation logic changes.
        
        Returns:
            Tuple[int, int, int]: Ingestion date as (year, month, day)
            
        Raises:
            NEOAPIError: If API communication fails
            DataExtractionError: If data extraction fails
            StorageError: If writing to storage fails
        """
        logger.info("Stage 1: Fetching data from API and staging to raw layer")
        
        # Get current date for ingestion timestamp
        now = datetime.now()
        ingestion_date = (now.year, now.month, now.day)
        
        logger.info("Ingestion date: %04d-%02d-%02d", *ingestion_date)
        
        # Initialize storage manager for raw layer
        # Use base path without partition - write_raw_data will add the partition
        raw_base_path = os.path.join(self.config.base_data_path, self.config.raw_layer_path)
        raw_storage = ParquetStorageManager(
            base_path=raw_base_path,
            layer='raw',
            batch_size=self.config.batch_size,
            compression=self.config.compression
        )
        
        # Fetch data from API (streaming)
        logger.info("Fetching up to %d NEO records from API", self.config.max_objects)
        neo_iterator = self.api_client.fetch_neos(
            page_size=self.config.page_size,
            max_objects=self.config.max_objects
        )
        
        # Extract and transform data (streaming)
        def extract_and_add_ingestion_date():
            """Generator that extracts data and adds ingestion date fields"""
            for neo_json in neo_iterator:
                # Extract fields
                extracted = self.extractor.extract_neo_data(neo_json)
                
                # Add ingestion date fields
                extracted['ingestion_year'] = ingestion_date[0]
                extracted['ingestion_month'] = ingestion_date[1]
                extracted['ingestion_day'] = ingestion_date[2]
                
                yield extracted
        
        # Write to raw layer
        logger.info("Writing data to raw layer: %s", raw_storage.base_path)
        raw_count = raw_storage.write_raw_data(
            extract_and_add_ingestion_date(),
            ingestion_date
        )
        
        logger.info("Stage 1 complete: Wrote %d records to raw layer", raw_count)
        
        return ingestion_date
    
    def _process_to_curated(self, ingestion_date: Tuple[int, int, int]) -> None:
        """
        Read raw data, transform, and write to curated layer.
        
        This stage:
        1. Reads data from raw layer
        2. Validates and transforms data
        3. Adds derived fields (approach_year, approach_month, approach_day)
        4. Writes to curated layer partitioned by event date
        
        The curated layer contains validated, normalized data optimized
        for analytical queries.
        
        Args:
            ingestion_date: Tuple of (year, month, day) for the raw data partition
            
        Raises:
            ValidationError: If data validation fails
            StorageError: If reading or writing fails
        """
        logger.info("Stage 2: Processing raw data to curated layer")
        
        # Initialize storage managers
        raw_path = self.config.get_raw_path(*ingestion_date)
        curated_path = self.config.get_curated_path()
        
        logger.info("Reading from raw layer: %s", raw_path)
        logger.info("Writing to curated layer: %s", curated_path)
        
        # Read raw data using PyArrow
        import pyarrow.parquet as pq
        import os
        
        raw_file_path = os.path.join(raw_path, "data.parquet")
        
        if not os.path.exists(raw_file_path):
            logger.warning("Raw data file not found: %s", raw_file_path)
            return
        
        # Read and transform data (streaming)
        def read_and_transform():
            """Generator that reads raw data and transforms it"""
            parquet_file = pq.ParquetFile(raw_file_path)
            
            for batch in parquet_file.iter_batches(batch_size=self.config.batch_size):
                # Convert batch to list of dicts
                records = batch.to_pylist()
                
                for record in records:
                    # Transform and validate
                    transformed = self.transformer.transform(record, ingestion_date)
                    yield transformed
        
        # Initialize curated storage manager
        curated_storage = ParquetStorageManager(
            base_path=curated_path,
            layer='curated',
            batch_size=self.config.batch_size,
            compression=self.config.compression
        )
        
        # Write to curated layer (pass ingestion_date for error partitioning)
        curated_count = curated_storage.write_curated_data(read_and_transform(), ingestion_date)
        
        logger.info("Stage 2 complete: Wrote %d records to curated layer", curated_count)
    
    def _compute_aggregates(self) -> Tuple[int, Dict[int, int]]:
        """
        Compute aggregates from curated data and write to aggregates layer.
        
        This stage:
        1. Reads data from curated layer (streaming)
        2. Computes close approaches count (< 0.2 AU)
        3. Computes approaches by year
        4. Writes results to aggregates layer
        
        Returns:
            Tuple containing:
                - int: Count of close approaches (< 0.2 AU)
                - Dict[int, int]: Approaches by year
                
        Raises:
            StorageError: If reading or writing fails
        """
        logger.info("Stage 3: Computing aggregates from curated data")
        
        # Initialize aggregation engine
        curated_path = self.config.get_curated_path()
        agg_engine = AggregationEngine(curated_path)
        
        # Compute close approaches count
        logger.info("Computing close approaches (threshold: %.2f AU)", 
                   self.config.close_approach_threshold_au)
        close_count = agg_engine.compute_close_approaches(
            self.config.close_approach_threshold_au
        )
        logger.info("Found %d close approaches", close_count)
        
        # Compute approaches by year
        logger.info("Computing approaches by year")
        year_counts = agg_engine.compute_approaches_by_year()
        logger.info("Approaches by year: %s", year_counts)
        
        # Write aggregates
        now = datetime.now()
        agg_date = (now.year, now.month, now.day)
        
        aggregates_path = self.config.get_aggregates_path('')
        agg_storage = ParquetStorageManager(
            base_path=aggregates_path,
            layer='aggregates',
            batch_size=self.config.batch_size,
            compression=self.config.compression
        )
        
        logger.info("Writing aggregates to: %s", aggregates_path)
        agg_engine.write_aggregates(agg_storage, close_count, year_counts, agg_date)
        
        logger.info("Stage 3 complete: Aggregates computed and written")
        
        return close_count, year_counts
    
    def run(self) -> PipelineResult:
        """
        Execute the complete pipeline.
        
        Orchestrates all pipeline stages in sequence:
        1. Stage raw data (fetch from API, write to raw layer)
        2. Process to curated (transform and validate, write to curated layer)
        3. Compute aggregates (compute statistics, write to aggregates layer)
        
        Returns:
            PipelineResult: Summary of pipeline execution with counts and paths
            
        Raises:
            NEOAPIError: If API communication fails
            DataExtractionError: If data extraction fails
            ValidationError: If data validation fails
            StorageError: If storage operations fail
            
        Example:
            >>> config = PipelineConfig.from_env()
            >>> pipeline = NEOPipeline(config)
            >>> result = pipeline.run()
            >>> print(f"Processed {result.curated_records_count} records")
            >>> print(f"Found {result.close_approaches_count} close approaches")
        """
        logger.info("=" * 80)
        logger.info("Starting NASA NEO Data Pipeline")
        logger.info("=" * 80)
        
        try:
            # Stage 1: Fetch and stage raw data
            ingestion_date = self._stage_raw_data()
            
            # Stage 2: Transform to curated
            self._process_to_curated(ingestion_date)
            
            # Stage 3: Compute aggregates
            close_count, year_counts = self._compute_aggregates()
            
            # Get paths for result
            raw_path = self.config.get_raw_path(*ingestion_date)
            curated_path = self.config.get_curated_path()
            aggregates_path = self.config.get_aggregates_path('')
            
            # Count records (read from storage)
            import os
            import pyarrow.parquet as pq
            
            # Count raw records
            raw_file = os.path.join(raw_path, "data.parquet")
            raw_count = 0
            if os.path.exists(raw_file):
                table = pq.read_table(raw_file)
                raw_count = len(table)
            
            # Count curated records
            curated_count = 0
            if os.path.exists(curated_path):
                dataset = pq.ParquetDataset(curated_path, use_legacy_dataset=False)
                table = dataset.read()
                curated_count = len(table)
            
            # Create result
            result = PipelineResult(
                raw_records_count=raw_count,
                curated_records_count=curated_count,
                close_approaches_count=close_count,
                approaches_by_year=year_counts,
                raw_path=raw_path,
                curated_path=curated_path,
                aggregates_path=aggregates_path
            )
            
            logger.info("=" * 80)
            logger.info("Pipeline execution complete!")
            logger.info("Raw records: %d", result.raw_records_count)
            logger.info("Curated records: %d", result.curated_records_count)
            logger.info("Close approaches: %d", result.close_approaches_count)
            logger.info("Approaches by year: %s", result.approaches_by_year)
            logger.info("=" * 80)
            
            return result
            
        except Exception as e:
            logger.error("Pipeline execution failed: %s", str(e), exc_info=True)
            raise
