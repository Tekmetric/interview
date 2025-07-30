"""
NEO data processing pipeline using NeoWs API and distributed Spark processing
"""

import time
import logging
from typing import Optional, Dict, Any

from .config import APIConfig, SparkConfig, ProcessingConfig
from .api_client import NASAAPIClient
from .data_processor import NEODataProcessor
from .storage import DataStorage
from .models import (
    ProcessingResult, Aggregations,
    DataProcessingError, NASAAPIError, StorageError
)

logger = logging.getLogger(__name__)


class NEOPipeline:
    """
    Streamlined NEO data processing pipeline using NeoWs API
    
    This pipeline is optimized for scalability and efficiency:
    1. Single threaded API call to NeoWs gets all required data (NEO + close approaches) (distributed fetching TBD - it poses some challenges on its own)
    2. Distributed Spark processing handles hundreds of GB
    """
    
    def __init__(self, api_config: APIConfig, spark_config: SparkConfig, 
                 processing_config: ProcessingConfig):
        self.api_config = api_config
        self.spark_config = spark_config
        self.processing_config = processing_config
        
        # Components will be initialized when needed
        self.api_client = None
        self.data_processor = None
        self.storage = None
    
    def run_pipeline(self, neo_limit: Optional[int] = None, 
                    parallelism: Optional[int] = None) -> ProcessingResult:
        """
        Run the complete NEO data processing pipeline
        
        Args:
            neo_limit: Maximum number of NEO objects to process
            parallelism: Number of parallel partitions for Spark processing - can be edited based on the hardware available and the expected data size
            
        Returns:
            ProcessingResult with pipeline execution details
        """
        start_time = time.time()
        logger.info("Starting streamlined NEO data processing pipeline using NeoWs API")
        
        try:
            # Initialize components
            self._initialize_components()
            
            # Use provided limit or default from config
            limit = neo_limit or self.processing_config.object_limit
            
            # Single step: Extract all NEO data with embedded close approaches
            logger.info(f"Extracting {limit} NEO objects with close approach data from NeoWs API")
            neo_dataframe = self.api_client.fetch_neo_data_distributed(limit, parallelism)
            
            logger.info(f"Successfully fetched NEO data. Extracting raw data...")
            
            # Extract raw data with closest approach per NEO
            raw_data_df = self.data_processor.extract_raw_data_with_closest_approach(neo_dataframe)
            
            # Save raw data
            logger.info("Saving raw data files...")
            self.storage.save_raw_data(raw_data_df)
            
            logger.info(f"Raw data saved. Calculating aggregations from raw data...")
            
            # Calculate aggregations directly from clean raw data (Option 1)
            aggregations = self.data_processor.calculate_aggregations_from_raw(raw_data_df)
            
            
            # Save aggregations (no longer saving processed_df)
            self._save_aggregations(aggregations)
            
            # Calculate metrics
            processing_time = time.time() - start_time
            
            result = ProcessingResult(
                total_objects_processed=raw_data_df.count(),
                close_approaches_count=aggregations.total_close_approaches,
                processing_time_seconds=processing_time,
                aggregations=aggregations,
                success=True
            )
            
            logger.info(f"Pipeline completed successfully in {processing_time:.2f} seconds")
            logger.info(f"Processed {result.total_objects_processed} NEO objects")
            logger.info(f"Found {result.close_approaches_count} close approaches")
            
            return result
            
        except Exception as e:
            processing_time = time.time() - start_time
            error_msg = f"Pipeline failed: {e}"
            logger.error(error_msg)
            
            return ProcessingResult(
                total_objects_processed=0,
                close_approaches_count=0,
                processing_time_seconds=processing_time,
                aggregations=None,
                data_quality_score=0.0,
                success=False,
                error_message=error_msg
            )
        finally:
            self._cleanup()
    
    def _initialize_components(self):
        """Initialize pipeline components"""
        try:
            logger.info("Initializing pipeline components")
            
            # Initialize data processor (creates Spark session)
            self.data_processor = NEODataProcessor(self.spark_config)
            
            # Initialize API client with the Spark session
            self.api_client = NASAAPIClient(self.api_config, self.data_processor.spark)
            
            # Initialize storage
            self.storage = DataStorage()
            
        except Exception as e:
            raise DataProcessingError(f"Failed to initialize components: {e}")
    
    def _save_aggregations(self, aggregations: Aggregations):
        """Save aggregations (streamlined pipeline - Option 1)"""
        try:
            logger.info("Saving aggregations")
            
            # Save aggregations
            self.storage.save_aggregations(aggregations)
            
            logger.info("Aggregations successfully saved")
            
        except Exception as e:
            raise StorageError(f"Failed to save aggregations: {e}")
    
    def _cleanup(self):
        """Clean up resources"""
        try:
            if self.data_processor:
                self.data_processor.cleanup()
        except Exception as e:
            logger.warning(f"Error during cleanup: {e}")
    





