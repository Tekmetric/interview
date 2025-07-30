#!/usr/bin/env python3
import sys
import logging
from datetime import datetime
import time
from pathlib import Path

# Add src directory to path so we can import our modules
sys.path.insert(0, str(Path(__file__).parent / "src"))

from src.neo_processor import NEOPipeline
from src.config import APIConfig, SparkConfig, ProcessingConfig


def main():
    """Main entry point for NASA NEO data processing"""
    
    # Configure logging
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    )
    
    try:
        # Run processing pipeline
        print("\n Running processing pipeline...")
        print("-" * 40)
        
        start_time = time.time()
        
        # Create configurations
        from src.config import Config
        config = Config()  # This will load from NASA_API_KEY env var or default to DEMO_KEY
        api_config = config.api
        spark_config = SparkConfig()
        processing_config = ProcessingConfig(object_limit=200)  # Per instructions, 200 objects
        
        # Create and run pipeline
        pipeline = NEOPipeline(api_config, spark_config, processing_config)
        result = pipeline.run_pipeline(
            neo_limit=200,  # Per instructions, 200 objects - this could be a param
            parallelism=4   # Use 4 parallel partitions - this could be a param
        )
        
        end_time = time.time()
        processing_time = end_time - start_time
        
        print(f"\nProcessing Results:")
        print(f"Objects processed: {result.total_objects_processed}")
        print(f"Processing time: {processing_time:.2f} seconds")
        print(f"Average Processing speed: {result.total_objects_processed/processing_time:.1f} objects/second")
        print(f"Success: {'Yes' if result.success else 'No'}")
        
        # Show aggregation results
        agg = result.aggregations
        print(f"\nEnhanced Aggregations:")
        print(f"* Close approaches < 0.2 AU: {agg.close_approaches_under_threshold}")
        print(f"* Total objects processed: {agg.total_objects}")
        print(f"* Approaches by year: {agg.approaches_by_year}")
        
        if agg.potentially_hazardous_count:
            print(f"* Potentially hazardous: {agg.potentially_hazardous_count}")
        

        
        # Show output structure
        print(f"\nData saved to:")
        print(f"* Raw data: data/raw/neo/year={datetime.now().year}/")
        print(f"* Aggregations: data/aggregations/neo/year={datetime.now().year}/")
        
        return 0
        
    except Exception as e:
        print(f"\nError: {e}")
        logging.error(f"Distributed pipeline failed: {e}", exc_info=True)
        return 1



if __name__ == "__main__":
    import argparse
    
    parser = argparse.ArgumentParser(description="NASA NEO Data Processor")

    parser.add_argument("--verbose", "-v", action="store_true", 
                        help="Enable verbose logging")
    
    args = parser.parse_args()
    
    exit(main()) 