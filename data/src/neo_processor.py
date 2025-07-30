"""
Main NEO data processing pipeline using distributed Spark processing
"""

import time
import logging
from typing import Optional, List, Dict, Any
from datetime import datetime

from .config import APIConfig, SparkConfig, ProcessingConfig
from .api_client import NASAAPIClient
from .data_processor import NEODataProcessor
from .storage import DataStorage
from .models import (
    ProcessingResult, Aggregations, NEOObject, CloseApproachData, ObjectDetails,
    DataProcessingError, NASAAPIError, StorageError
)

logger = logging.getLogger(__name__)


class NEOPipeline:
    """
    Distributed NEO data processing pipeline
    
    This pipeline follows the correct logical flow:
    1. Get NEO objects (potentially massive list, scalable to GBs)
    2. Get ALL close approach data for those specific NEOs
    3. Transform and process the data using distributed Spark operations
    4. Calculate aggregations using distributed computing
    """
    
    def __init__(self, api_config: APIConfig, spark_config: SparkConfig, 
                 processing_config: ProcessingConfig):
        self.api_config = api_config
        self.spark_config = spark_config
        self.processing_config = processing_config
        self.api_client = None
        self.data_processor = None
        self.storage = None
        
    def run_distributed_pipeline(self, neo_limit: Optional[int] = None,
                                parallelism: Optional[int] = None) -> ProcessingResult:
        """
        Run the complete distributed NEO processing pipeline
        
        Args:
            neo_limit: Maximum number of NEO objects to process (defaults to config)
            parallelism: Number of parallel partitions for distributed processing
            
        Returns:
            ProcessingResult with pipeline results and metrics
        """
        start_time = time.time()
        logger.info("Starting distributed NEO processing pipeline")
        
        try:
            # Initialize components
            self._initialize_components()
            
            # Use provided limit or default from config
            limit = neo_limit or self.processing_config.object_limit
            
            # Step 1: Extract NEO objects (the logical first step)
            logger.info(f"Step 1: Extracting {limit} NEO objects")
            neo_objects = self._extract_neo_objects(limit)
            logger.info(f"Successfully extracted {len(neo_objects)} NEO objects")
            
            # Step 2: Extract close approach data for these NEOs (distributed)
            logger.info(f"Step 2: Extracting close approach data for {len(neo_objects)} NEOs")
            close_approaches = self._extract_close_approaches_for_neos(neo_objects, parallelism)
            logger.info(f"Successfully extracted {len(close_approaches)} close approach records")
            
            # Step 3: Extract detailed object data (distributed, optional but recommended)
            logger.info(f"Step 3: Extracting detailed data for {len(neo_objects)} NEOs")
            object_details = self._extract_object_details_for_neos(neo_objects, parallelism)
            logger.info(f"Successfully extracted detailed data for {len(object_details)} objects")
            
            # Step 4: Transform and process data (distributed)
            logger.info("Step 4: Processing and transforming data using Spark")
            processed_df = self.data_processor.create_dataframe_from_sources(
                close_approaches, object_details
            )
            
            # Step 5: Validate data quality (distributed)
            logger.info("Step 5: Validating data quality")
            quality_score = self.data_processor.validate_data_quality_distributed(processed_df)
            logger.info(f"Data quality score: {quality_score:.2f}")
            
            # Step 6: Calculate aggregations (distributed)
            logger.info("Step 6: Calculating aggregations")
            aggregations = self.data_processor.calculate_comprehensive_aggregations(processed_df)
            
            # Step 7: Save all data (distributed write operations)
            logger.info("Step 7: Saving processed data")
            files_created = self._save_all_data(processed_df, aggregations)
            
            # Calculate processing time
            processing_time = time.time() - start_time
            
            # Create result summary
            result = ProcessingResult(
                neo_objects_count=len(neo_objects),
                close_approaches_count=len(close_approaches),
                object_details_count=len(object_details),
                processing_time_seconds=processing_time,
                aggregations=aggregations,
                data_quality_score=quality_score,
                files_created=files_created
            )
            
            logger.info(f"Pipeline completed successfully in {processing_time:.2f} seconds")
            logger.info(f"Processed {len(neo_objects)} NEO objects with {len(close_approaches)} close approaches")
            
            return result
            
        except Exception as e:
            logger.error(f"Pipeline failed: {e}")
            processing_time = time.time() - start_time
            
            # Return partial result with error information
            return ProcessingResult(
                neo_objects_count=0,
                close_approaches_count=0,
                object_details_count=0,
                processing_time_seconds=processing_time,
                aggregations=Aggregations(
                    total_objects=0,
                    total_close_approaches=0,
                    potentially_hazardous_count=0,
                    average_miss_distance_km=0.0,
                    min_miss_distance_km=0.0,
                    max_miss_distance_km=0.0,
                    average_velocity_kms=0.0,
                    time_range_start="",
                    time_range_end=""
                ),
                data_quality_score=0.0,
                errors=[str(e)]
            )
        
        finally:
            # Clean up Spark session
            if self.data_processor:
                self.data_processor.cleanup()
    
    def _initialize_components(self):
        """Initialize API client, data processor, and storage"""
        try:
            self.data_processor = NEODataProcessor(self.spark_config)
            self.api_client = NASAAPIClient(self.api_config, self.data_processor.spark)
            self.storage = DataStorage()
            
        except Exception as e:
            raise DataProcessingError(f"Failed to initialize components: {e}")
    
    def _extract_neo_objects(self, limit: int) -> List[NEOObject]:
        """Extract NEO objects using SBDB Query API"""
        try:
            return self.api_client.fetch_neo_objects(limit=limit, neo_only=True)
            
        except Exception as e:
            raise NASAAPIError(f"Failed to extract NEO objects: {e}")
    
    def _extract_close_approaches_for_neos(self, neo_objects: List[NEOObject], 
                                         parallelism: Optional[int]) -> List[CloseApproachData]:
        """Extract close approach data for specific NEO objects using distributed processing"""
        try:
            return self.api_client.fetch_close_approaches_for_neos_distributed(
                neo_objects, parallelism
            )
            
        except Exception as e:
            raise NASAAPIError(f"Failed to extract close approach data: {e}")
    
    def _extract_object_details_for_neos(self, neo_objects: List[NEOObject], 
                                       parallelism: Optional[int]) -> List[ObjectDetails]:
        """Extract detailed object data for NEO objects using distributed processing"""
        try:
            return self.api_client.fetch_object_details_distributed(
                neo_objects, parallelism
            )
            
        except Exception as e:
            raise NASAAPIError(f"Failed to extract object details: {e}")
    
    def _save_all_data(self, processed_df, aggregations: Aggregations) -> List[str]:
        """Save all processed data using distributed write operations"""
        try:
            files_created = []
            
            # Save processed data as Parquet (distributed write)
            parquet_path = self.storage.save_dataframe(
                processed_df, 
                "processed", 
                format="parquet",
                partition_by=["approach_year"]
            )
            files_created.append(parquet_path)
            
            # Save aggregations as JSON
            aggregations_path = self.storage.save_aggregations(aggregations)
            files_created.append(aggregations_path)
            
            logger.info(f"Successfully saved data to {len(files_created)} files")
            return files_created
            
        except Exception as e:
            raise StorageError(f"Failed to save data: {e}")
    
    def get_distributed_pipeline_status(self) -> Dict[str, Any]:
        """Get status information about the distributed pipeline"""
        return {
            "pipeline_type": "distributed_spark",
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


def process_neo_data_distributed(api_key: Optional[str] = None, 
                                limit: Optional[int] = None,
                                parallelism: Optional[int] = None) -> ProcessingResult:
    """
    Main entry point for distributed NEO data processing
    
    Args:
        api_key: NASA API key (if not provided, will use environment variable)
        limit: Maximum number of NEO objects to process
        parallelism: Number of parallel partitions for distributed processing
        
    Returns:
        ProcessingResult with pipeline results and metrics
    """
    try:
        # Initialize configurations
        api_config = APIConfig(api_key=api_key)
        spark_config = SparkConfig()
        processing_config = ProcessingConfig()
        
        # Override limit if provided
        if limit:
            processing_config.object_limit = limit
        
        # Create and run pipeline
        pipeline = NEOPipeline(api_config, spark_config, processing_config)
        return pipeline.run_distributed_pipeline(parallelism=parallelism)
        
    except Exception as e:
        logger.error(f"Distributed processing failed: {e}")
        raise DataProcessingError(f"Distributed pipeline execution failed: {e}")


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
            api_key=args.api_key,
            limit=args.limit,
            parallelism=args.parallelism
        )
        
        # Display results
        print("\n" + "="*60)
        print("DISTRIBUTED NEO PROCESSING COMPLETE")
        print("="*60)
        print(f"NEO Objects Processed: {result.neo_objects_count}")
        print(f"Close Approaches Found: {result.close_approaches_count}")
        print(f"Object Details Retrieved: {result.object_details_count}")
        print(f"Processing Time: {result.processing_time_seconds:.2f} seconds")
        print(f"Data Quality Score: {result.data_quality_score:.2f}")
        print(f"Files Created: {len(result.files_created)}")
        
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