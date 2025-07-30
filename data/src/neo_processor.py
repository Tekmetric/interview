"""
NEO data processing pipeline using NeoWs API and distributed Spark processing
"""

import time
import logging
from typing import Optional, Dict, Any
from datetime import datetime

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
    Streamlined NEO data processing pipeline using only NeoWs API
    
    This pipeline is optimized for scalability and efficiency:
    1. Single API call to NeoWs gets all required data (NEO + close approaches)
    2. Distributed Spark processing handles hundreds of GB
    3. Minimal code with maximum efficiency
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
            parallelism: Number of parallel partitions for Spark processing
            
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
            
            logger.info(f"Successfully fetched NEO data. Processing with Spark...")
            
            # Process and analyze the data
            processed_df = self.data_processor.process_neo_dataframe(neo_dataframe)
            
            # Calculate aggregations
            aggregations = self.data_processor.calculate_aggregations(processed_df)
            
            # Validate data quality
            quality_score = self.data_processor.validate_data_quality(processed_df)
            
            # Save results
            self._save_results(processed_df, aggregations)
            
            # Calculate metrics
            processing_time = time.time() - start_time
            
            result = ProcessingResult(
                total_objects_processed=processed_df.count(),
                close_approaches_count=self._count_close_approaches(processed_df),
                processing_time_seconds=processing_time,
                aggregations=aggregations,
                data_quality_score=quality_score,
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
    
    def _count_close_approaches(self, df) -> int:
        """Count total close approaches in the processed data"""
        try:
            # Count rows where close approach data exists
            from pyspark.sql.functions import col, isnotnull, size
            
            # If close_approach_data is an array, count total elements
            # If it's a single record per row, count non-null rows
            if 'close_approach_data' in df.columns:
                close_approach_count = df.filter(
                    isnotnull(col('close_approach_data'))
                ).count()
            else:
                # If data is already flattened, count rows with approach data
                close_approach_count = df.filter(
                    isnotnull(col('closest_approach_date'))
                ).count()
            
            return close_approach_count
            
        except Exception as e:
            logger.warning(f"Failed to count close approaches: {e}")
            return 0
    
    def _save_results(self, processed_df, aggregations: Aggregations):
        """Save processed data and aggregations"""
        try:
            logger.info("Saving processed data and aggregations")
            
            # Save processed NEO data
            self.storage.save_processed_data(processed_df)
            
            # Save aggregations
            self.storage.save_aggregations(aggregations)
            
            logger.info("Data successfully saved")
            
        except Exception as e:
            raise StorageError(f"Failed to save results: {e}")
    
    def _cleanup(self):
        """Clean up resources"""
        try:
            if self.data_processor:
                self.data_processor.cleanup()
        except Exception as e:
            logger.warning(f"Error during cleanup: {e}")
    
    def get_pipeline_status(self) -> Dict[str, Any]:
        """Get status information about the pipeline"""
        return {
            "pipeline_type": "streamlined_neows_spark",
            "api_config": {
                "base_url": self.api_config.base_url,
                "rate_limit_delay": self.api_config.rate_limit_delay,
                "max_retries": self.api_config.max_retries
            },
            "spark_config": {
                "app_name": self.spark_config.app_name,
                "adaptive_enabled": self.spark_config.adaptive_enabled,
                "arrow_enabled": self.spark_config.arrow_enabled
            },
            "processing_config": {
                "object_limit": self.processing_config.object_limit,
                "batch_size": self.processing_config.batch_size,
                "close_approach_threshold_au": self.processing_config.close_approach_threshold_au
            },
            "components_initialized": {
                "api_client": self.api_client is not None,
                "data_processor": self.data_processor is not None,
                "storage": self.storage is not None
            }
        }


def process_neo_data_distributed(limit: Optional[int] = None, 
                                parallelism: Optional[int] = None) -> ProcessingResult:
    """
    Main entry point for NEO data processing
    
    Args:
        limit: Maximum number of NEO objects to process
        parallelism: Number of parallel partitions for processing
        
    Returns:
        ProcessingResult with execution details
    """
    from .config import APIConfig, SparkConfig, ProcessingConfig
    
    # Create configurations
    api_config = APIConfig(api_key='DEMO_KEY')  # Will be overridden by env var if available
    spark_config = SparkConfig()
    processing_config = ProcessingConfig()
    
    # Override limit if provided
    if limit:
        processing_config.object_limit = limit
    
    # Create and run pipeline
    pipeline = NEOPipeline(api_config, spark_config, processing_config)
    return pipeline.run_pipeline(limit, parallelism)


def main():
    """Main execution function"""
    import argparse
    import sys
    
    # Setup argument parser
    parser = argparse.ArgumentParser(description='Process NEO data using distributed Spark processing')
    parser.add_argument('--limit', type=int, default=200, 
                       help='Maximum number of NEO objects to process (default: 200)')
    parser.add_argument('--parallelism', type=int, 
                       help='Number of parallel partitions for processing')
    parser.add_argument('--api-key', type=str, 
                       help='NASA API key (or set NASA_API_KEY environment variable)')
    
    args = parser.parse_args()
    
    # Setup logging
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    )
    
    try:
        logger.info("Starting distributed NEO data processing")
        logger.info(f"Processing up to {args.limit} NEO objects")
        if args.parallelism:
            logger.info(f"Using {args.parallelism} parallel partitions")
        
        # Run distributed processing
        result = process_neo_data_distributed(
            limit=args.limit,
            parallelism=args.parallelism
        )
        
        # Display results
        print("\n" + "="*60)
        print("DISTRIBUTED NEO PROCESSING COMPLETE")
        print("="*60)
        print(f"NEO Objects Processed: {result.total_objects_processed}")
        print(f"Close Approaches Found: {result.close_approaches_count}")
        print(f"Processing Time: {result.processing_time_seconds:.2f} seconds")
        print(f"Data Quality Score: {result.data_quality_score:.2f}")
        
        if result.aggregations:
            print(f"\nAggregations:")
            print(f"  Total Objects: {result.aggregations.total_objects}")
            print(f"  Total Close Approaches: {result.aggregations.total_close_approaches}")
            print(f"  Potentially Hazardous: {result.aggregations.potentially_hazardous_count}")
            print(f"  Average Miss Distance: {result.aggregations.average_miss_distance_km:,.0f} km")
            print(f"  Average Velocity: {result.aggregations.average_velocity_kms:.2f} km/s")
        
        if result.errors:
            print(f"\nErrors encountered: {len(result.errors)}")
            for error in result.errors[:5]:  # Show first 5 errors
                print(f"  - {error}")
        
        print("="*60)
        
    except Exception as e:
        logger.error(f"Processing failed: {e}")
        print(f"Error: {e}")
        return 1
    
    return 0


if __name__ == "__main__":
    exit(main()) 