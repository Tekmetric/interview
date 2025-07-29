"""
Main NEO Data Processor - orchestrates the complete data pipeline
"""

import logging
import time
from typing import Optional, Dict, Any
from datetime import datetime

from .config import Config
from .api_client import NASAAPIClient
from .data_processor import NEODataProcessor as DataProcessor
from .storage import DataLakeStorage
from .models import ProcessingResult
from .exceptions import NEOProcessorError, NASAAPIError, DataProcessingError, StorageError

logger = logging.getLogger(__name__)


class NEOPipeline:
    """
    Main NEO Data Processor that orchestrates the complete ETL pipeline
    
    This class coordinates all components to:
    1. Extract data from NASA APIs
    2. Transform and process the data using PySpark
    3. Load/store data in the data lake structure
    4. Calculate aggregations and generate reports
    """
    
    def __init__(self, config: Optional[Config] = None):
        """
        Initialize the NEO Data Processor
        
        Args:
            config: Configuration object (uses defaults if None)
        """
        self.config = config or Config.from_env()
        
        # Initialize components
        self.api_client = NASAAPIClient(self.config.api)
        self.data_processor = DataProcessor(self.config)
        self.storage = DataLakeStorage(self.config)
        
        # Setup logging
        self._setup_logging()
    
    def _setup_logging(self):
        """Configure logging for the processor"""
        logging.basicConfig(
            level=logging.INFO,
            format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
        )
    
    def run_pipeline(self, limit: Optional[int] = None) -> ProcessingResult:
        """
        Run the complete NEO data processing pipeline
        
        Args:
            limit: Number of objects to process (uses config default if None)
            
        Returns:
            ProcessingResult with pipeline execution details
        """
        start_time = time.time()
        limit = limit or self.config.processing.object_limit
        
        logger.info("="*60)
        logger.info("🚀 Starting NEO data processing pipeline...")
        logger.info(f"📊 Processing limit: {limit} objects")
        logger.info("="*60)
        
        try:
            # Phase 1: Extract data from NASA APIs
            logger.info("Phase 1: Extracting data from NASA APIs")
            close_approaches = self._extract_close_approach_data(limit)
            object_details = self._extract_object_details(close_approaches)
            
            # Phase 2: Transform and combine data sources
            logger.info("Phase 2: Transforming and combining data")
            neo_records = self.data_processor.transformer.combine_data_sources(
                close_approaches, object_details
            )
            
            # Phase 3: Create Spark DataFrame and process
            logger.info("Phase 3: Creating Spark DataFrame and processing")
            df = self.data_processor.create_dataframe(neo_records)
            
            # Phase 4: Validate data quality
            logger.info("Phase 4: Validating data quality")
            quality_metrics = self.data_processor.validate_data_quality(df)
            
            # Phase 5: Calculate aggregations
            logger.info("Phase 5: Calculating aggregations")
            aggregations = self.data_processor.calculate_aggregations(df)
            
            # Phase 6: Save all data to storage
            logger.info("Phase 6: Saving data to storage")
            output_paths = self._save_all_data(neo_records, df, aggregations)
            
            # Create processing result
            end_time = time.time()
            processing_time = end_time - start_time
            
            result = ProcessingResult(
                total_objects_processed=len(neo_records),
                successful_records=len(neo_records),
                failed_records=0,  # We could track this better in a full implementation
                processing_time_seconds=processing_time,
                output_paths=output_paths,
                aggregations=aggregations.to_dict()
            )
            
            # Log success
            logger.info("="*60)
            logger.info("✅ NEO data processing pipeline completed successfully!")
            logger.info(f"📊 Processed {result.total_objects_processed} objects in {processing_time:.2f} seconds")
            logger.info(f"📁 Output directory: {self.config.storage.base_output_dir}")
            logger.info("="*60)
            
            return result
            
        except Exception as e:
            logger.error(f"❌ Pipeline failed: {e}")
            raise NEOProcessorError(f"Pipeline execution failed: {e}") from e
        
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
    
    def _extract_object_details(self, close_approaches):
        """Extract detailed object information"""
        try:
            # Get unique designations
            designations = list({approach.designation for approach in close_approaches})
            
            logger.info(f"Fetching detailed data for {len(designations)} unique objects")
            object_details = self.api_client.fetch_object_details(designations)
            
            logger.info(f"✅ Successfully fetched detailed data for {len(object_details)} objects")
            return object_details
            
        except Exception as e:
            raise NASAAPIError(f"Failed to extract object details: {e}")
    
    def _save_all_data(self, neo_records, df, aggregations):
        """Save all data to storage"""
        try:
            output_paths = {}
            
            # Convert NEO records to dictionaries for JSON storage
            raw_data = [record.to_dict() for record in neo_records]
            
            # Save raw data
            raw_paths = self.storage.save_raw_data(raw_data, df)
            output_paths.update(raw_paths)
            
            # Save processed data
            processed_path = self.storage.save_processed_data(df)
            output_paths["processed"] = processed_path
            
            # Save aggregations
            agg_paths = self.storage.save_aggregations(aggregations)
            output_paths.update({f"agg_{k}": v for k, v in agg_paths.items()})
            
            return output_paths
            
        except Exception as e:
            raise StorageError(f"Failed to save data: {e}")
    
    def _cleanup(self):
        """Cleanup resources"""
        try:
            if self.api_client:
                self.api_client.close()
            if self.data_processor:
                self.data_processor.close()
        except Exception as e:
            logger.warning(f"Error during cleanup: {e}")
    
    def get_pipeline_status(self) -> Dict[str, Any]:
        """
        Get current pipeline status and configuration
        
        Returns:
            Dictionary with status information
        """
        return {
            "config": {
                "api_key_configured": bool(self.config.api.api_key and self.config.api.api_key != "DEMO_KEY"),
                "object_limit": self.config.processing.object_limit,
                "close_approach_threshold_au": self.config.processing.close_approach_threshold_au,
                "storage_compression": self.config.storage.compression,
            },
            "storage": self.storage.get_storage_stats(),
            "available_data": self.storage.list_available_data(),
        }
    
    def load_existing_data(self):
        """
        Load previously processed data for analysis
        
        Returns:
            Tuple of (DataFrame, Aggregations) if data exists
        """
        try:
            # Load processed DataFrame
            df = self.storage.load_processed_data(self.data_processor.spark)
            
            # Load aggregations
            aggregations = self.storage.load_aggregations()
            
            logger.info("✅ Successfully loaded existing data")
            return df, aggregations
            
        except StorageError as e:
            logger.warning(f"No existing data found: {e}")
            return None, None
    
    def run_custom_analysis(self, analysis_func, *args, **kwargs):
        """
        Run custom analysis on existing data
        
        Args:
            analysis_func: Function to run on the data
            *args, **kwargs: Arguments to pass to the analysis function
            
        Returns:
            Result of the analysis function
        """
        df, aggregations = self.load_existing_data()
        
        if df is None:
            raise DataProcessingError("No data available for analysis. Run the pipeline first.")
        
        logger.info("Running custom analysis...")
        return analysis_func(df, aggregations, *args, **kwargs)


# Convenience function for simple usage
def process_neo_data(api_key: Optional[str] = None, limit: Optional[int] = None) -> ProcessingResult:
    """
    Convenience function to run the complete NEO data processing pipeline
    
    Args:
        api_key: NASA API key (uses environment variable if None)
        limit: Number of objects to process (uses default if None)
        
    Returns:
        ProcessingResult with execution details
    """
    config = Config(api_key=api_key) if api_key else Config.from_env()
    
    if limit:
        config.processing.object_limit = limit
    
    processor = NEOPipeline(config)
    return processor.run_pipeline()


# Main entry point for script execution
def main():
    """Main entry point when run as a script"""
    import argparse
    
    parser = argparse.ArgumentParser(description="NASA Near Earth Object Data Processor")
    parser.add_argument("--limit", type=int, default=200, help="Number of objects to process")
    parser.add_argument("--api-key", type=str, help="NASA API key")
    parser.add_argument("--verbose", "-v", action="store_true", help="Enable verbose logging")
    
    args = parser.parse_args()
    
    if args.verbose:
        logging.getLogger().setLevel(logging.DEBUG)
    
    try:
        result = process_neo_data(api_key=args.api_key, limit=args.limit)
        
        print("🎉 Processing completed successfully!")
        print(f"📊 Objects processed: {result.total_objects_processed}")
        print(f"⏱️ Processing time: {result.processing_time_seconds:.2f} seconds")
        print(f"📁 Output paths: {list(result.output_paths.keys())}")
        
    except Exception as e:
        print(f"❌ Error: {e}")
        return 1
    
    return 0


if __name__ == "__main__":
    exit(main()) 