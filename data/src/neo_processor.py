"""
NEO Data Processor - fully parallel pipeline using Spark
"""

import logging
import time
from typing import Optional, Dict, Any
from datetime import datetime

from .config import Config
from .api_client import NASAAPIClient
from .data_processor import NEODataProcessor
from .storage import DataLakeStorage
from .models import ProcessingResult
from .models import NEOProcessorError, NASAAPIError, DataProcessingError, StorageError

logger = logging.getLogger(__name__)


class NEOPipeline:
    """
    NEO Data Processor using Spark for all operations
    
    This class coordinates all components to:
    1. Extract data from NASA APIs using Spark parallelization
    2. Transform and process the data using distributed Spark operations
    3. Load/store data in the data lake structure
    4. Calculate comprehensive aggregations using distributed computing
    """
    
    def __init__(self, config: Optional[Config] = None):
        """
        Initialize the Distributed NEO Data Processor
        
        Args:
            config: Configuration object (uses defaults if None)
        """
        self.config = config or Config.from_env()
        
        # Initialize data processor first (creates Spark session)
        self.data_processor = NEODataProcessor(self.config)
        
        # Initialize API client with Spark session
        self.api_client = NASAAPIClient(
            self.config.api, 
            self.data_processor.spark
        )
        
        # Initialize storage
        self.storage = DataLakeStorage(self.config)
        
        # Setup logging
        self._setup_logging()
    
    def _setup_logging(self):
        """Configure logging for the processor"""
        logging.basicConfig(
            level=logging.INFO,
            format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
        )
    
    def run_distributed_pipeline(self, limit: Optional[int] = None, 
                                parallelism: Optional[int] = None) -> ProcessingResult:
        """
        Run the complete distributed NEO data processing pipeline
        
        Args:
            limit: Number of objects to process (uses config default if None)
            parallelism: Number of parallel partitions for API calls
            
        Returns:
            ProcessingResult with pipeline execution details
        """
        start_time = time.time()
        limit = limit or self.config.processing.object_limit
        
        logger.info("="*80)
        logger.info("🚀 Starting DISTRIBUTED NEO data processing pipeline...")
        logger.info(f"📊 Processing limit: {limit} objects")
        logger.info(f"⚡ Spark cores available: {self.data_processor.spark.sparkContext.defaultParallelism}")
        if parallelism:
            logger.info(f"🔀 API parallelism: {parallelism} partitions")
        logger.info("="*80)
        
        try:
            # Phase 1: Extract data from NASA APIs (parallelized)
            logger.info("Phase 1: DISTRIBUTED extraction from NASA APIs")
            close_approaches = self._extract_close_approach_data(limit)
            object_details = self._extract_object_details_distributed(close_approaches, parallelism)
            
            # Phase 2: Transform and combine data using distributed operations
            logger.info("Phase 2: DISTRIBUTED data transformation and joining")
            df = self.data_processor.create_dataframe_from_sources(
                close_approaches, object_details
            )
            
            # Phase 3: Validate data quality using distributed operations
            logger.info("Phase 3: DISTRIBUTED data quality validation")
            quality_metrics = self.data_processor.validate_data_quality_distributed(df)
            
            # Phase 4: Calculate comprehensive aggregations using distributed operations
            logger.info("Phase 4: DISTRIBUTED aggregation calculations")
            aggregations = self.data_processor.calculate_comprehensive_aggregations(df)
            
            # Phase 5: Save all data to storage
            logger.info("Phase 5: Saving distributed processing results")
            output_paths = self._save_all_data(df, aggregations)
            
            # Create processing result
            end_time = time.time()
            processing_time = end_time - start_time
            
            result = ProcessingResult(
                total_objects_processed=len(close_approaches),
                successful_records=len(object_details),
                failed_records=len(close_approaches) - len(object_details),
                processing_time_seconds=processing_time,
                output_paths=output_paths,
                aggregations=aggregations.to_dict()
            )
            
            # Log success with performance metrics
            logger.info("="*80)
            logger.info("✅ DISTRIBUTED NEO data processing pipeline completed successfully!")
            logger.info(f"📊 Processed {result.total_objects_processed} objects in {processing_time:.2f} seconds")
            logger.info(f"⚡ Processing speed: {result.total_objects_processed/processing_time:.1f} objects/second")
            logger.info(f"🎯 Success rate: {(result.successful_records/result.total_objects_processed)*100:.1f}%")
            logger.info(f"📁 Output directory: {self.config.storage.base_output_dir}")
            logger.info(f"🔧 Data quality score: {quality_metrics.get('data_quality_score', 0):.1f}/100")
            logger.info("="*80)
            
            return result
            
        except Exception as e:
            logger.error(f"❌ Distributed pipeline failed: {e}")
            raise NEOProcessorError(f"Distributed pipeline execution failed: {e}") from e
        
        finally:
            # Cleanup resources
            self._cleanup()
    
    def _extract_close_approach_data(self, limit: int):
        """Extract close approach data from NASA API"""
        try:
            logger.info(f"Fetching close approach data (limit: {limit})")
            close_approaches = self.api_client.fetch_close_approach_data(limit)
            
            if not close_approaches:
                raise NASAAPIError("No close approach data received")
            
            logger.info(f"✅ Successfully fetched {len(close_approaches)} close approach records")
            return close_approaches
            
        except Exception as e:
            raise NASAAPIError(f"Failed to extract close approach data: {e}")
    
    def _extract_object_details_distributed(self, close_approaches, parallelism: Optional[int] = None):
        """Extract detailed object information using distributed processing"""
        try:
            # Get unique designations
            designations = list({approach.designation for approach in close_approaches})
            
            logger.info(f"Fetching detailed data for {len(designations)} unique objects using DISTRIBUTED processing")
            
            # Use distributed API client for parallel processing
            object_details = self.api_client.fetch_object_details_distributed(
                designations, parallelism=parallelism
            )
            
            logger.info(f"✅ Successfully fetched detailed data for {len(object_details)} objects using distributed processing")
            return object_details
            
        except Exception as e:
            raise NASAAPIError(f"Failed to extract object details using distributed processing: {e}")
    
    def _save_all_data(self, df, aggregations):
        """Save all data to storage"""
        try:
            output_paths = {}
            
            # Convert DataFrame to list of dictionaries for JSON storage
            logger.info("Converting Spark DataFrame for raw data storage...")
            raw_data = [row.asDict() for row in df.collect()]
            
            # Save raw data (both JSON and Parquet)
            raw_paths = self.storage.save_raw_data(raw_data, df)
            output_paths.update(raw_paths)
            
            # Save processed data (already a DataFrame)
            processed_path = self.storage.save_processed_data(df)
            output_paths["processed"] = processed_path
            
            # Save aggregations
            agg_paths = self.storage.save_aggregations(aggregations)
            output_paths.update({f"agg_{k}": v for k, v in agg_paths.items()})
            
            return output_paths
            
        except Exception as e:
            raise StorageError(f"Failed to save distributed processing results: {e}")
    
    def _cleanup(self):
        """Cleanup resources"""
        try:
            if self.data_processor:
                self.data_processor.close()
        except Exception as e:
            logger.warning(f"Error during cleanup: {e}")
    
    def get_distributed_pipeline_status(self) -> Dict[str, Any]:
        """
        Get current distributed pipeline status and configuration
        
        Returns:
            Dictionary with status information including Spark details
        """
        spark_info = {
            "spark_version": self.data_processor.spark.version,
            "default_parallelism": self.data_processor.spark.sparkContext.defaultParallelism,
            "app_name": self.data_processor.spark.sparkContext.appName,
            "master": self.data_processor.spark.sparkContext.master,
        }
        
        return {
            "config": {
                "api_key_configured": bool(self.config.api.api_key and self.config.api.api_key != "DEMO_KEY"),
                "object_limit": self.config.processing.object_limit,
                "close_approach_threshold_au": self.config.processing.close_approach_threshold_au,
                "storage_compression": self.config.storage.compression,
            },
            "spark_cluster": spark_info,
            "storage": self.storage.get_storage_stats(),
            "available_data": self.storage.list_available_data(),
        }
    


# Convenience function for distributed processing
def process_neo_data_distributed(api_key: Optional[str] = None, 
                                limit: Optional[int] = None,
                                parallelism: Optional[int] = None) -> ProcessingResult:
    """
    Convenience function to run the complete distributed NEO data processing pipeline
    
    Args:
        api_key: NASA API key (uses environment variable if None)
        limit: Number of objects to process (uses default if None)
        parallelism: Number of parallel partitions for API calls
        
    Returns:
        ProcessingResult with execution details
    """
    config = Config(api_key=api_key) if api_key else Config.from_env()
    
    if limit:
        config.processing.object_limit = limit
    
    processor = NEOPipeline(config)
    return processor.run_distributed_pipeline(parallelism=parallelism)


# Main entry point for script execution
def main():
    """Main entry point when run as a distributed script"""
    import argparse
    
    parser = argparse.ArgumentParser(description="NASA Near Earth Object Data Processor")
    parser.add_argument("--limit", type=int, default=200, help="Number of objects to process")
    parser.add_argument("--parallelism", type=int, help="Number of parallel partitions for API calls")
    parser.add_argument("--api-key", type=str, help="NASA API key")
    parser.add_argument("--verbose", "-v", action="store_true", help="Enable verbose logging")
    
    args = parser.parse_args()
    
    if args.verbose:
        logging.getLogger().setLevel(logging.DEBUG)
    
    try:
        result = process_neo_data_distributed(
            api_key=args.api_key, 
            limit=args.limit,
            parallelism=args.parallelism
        )
        
        print("🎉 Processing completed successfully!")
        print(f"📊 Objects processed: {result.total_objects_processed}")
        print(f"⏱️ Processing time: {result.processing_time_seconds:.2f} seconds")
        print(f"⚡ Processing speed: {result.total_objects_processed/result.processing_time_seconds:.1f} objects/second")
        print(f"📁 Output paths: {list(result.output_paths.keys())}")
        
    except Exception as e:
        print(f"❌ Error: {e}")
        return 1
    
    return 0


if __name__ == "__main__":
    exit(main()) 