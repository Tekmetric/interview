#!/usr/bin/env python3
"""
NASA Near Earth Object Data Processor

High-performance NEO data processor using Spark for distributed processing.
Fetches data from NASA APIs and processes it for analysis.
"""

import sys
import logging
from datetime import datetime
import time
from pathlib import Path

# Add src directory to path so we can import our modules
sys.path.insert(0, str(Path(__file__).parent / "src"))

from src.neo_processor import process_neo_data_distributed, NEOPipeline


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
        result = process_neo_data_distributed(
            limit=200,  # Process 100 objects for demonstration
            parallelism=4  # Use 4 parallel partitions
        )
        end_time = time.time()
        processing_time = end_time - start_time
        
        print(f"\n✅ Processing Results:")
        print(f"📊 Objects processed: {result.total_objects_processed}")
        print(f"⏱️ Processing time: {processing_time:.2f} seconds")
        print(f"⚡ Processing speed: {result.total_objects_processed/processing_time:.1f} objects/second")
        print(f"🎯 Success rate: {(result.total_objects_processed if result.success else 0)/max(result.total_objects_processed, 1)*100:.1f}%")
        print(f"📁 Success: {'✅ Yes' if result.success else '❌ No'}")
        
        # Show aggregation results
        agg = result.aggregations
        print(f"\n📈 Enhanced Aggregations:")
        print(f"  • Close approaches < 0.2 AU: {agg.close_approaches_under_threshold}")
        print(f"  • Total objects processed: {agg.total_objects}")
        print(f"  • Approaches by year: {agg.approaches_by_year}")
        
        # Show enhanced statistics if available
        if agg.velocity_statistics:
            vel_stats = agg.velocity_statistics
            print(f"  • Velocity stats: avg={vel_stats.get('avg', 0):.1f} km/s, max={vel_stats.get('max', 0):.1f} km/s")
        
        if agg.distance_statistics:
            dist_stats = agg.distance_statistics
            print(f"  • Distance stats: avg={dist_stats.get('avg_au', 0):.3f} AU, min={dist_stats.get('min_au', 0):.3f} AU")
        
        if agg.hazard_distribution:
            hazard_dist = agg.hazard_distribution
            print(f"  • Hazard distribution: {hazard_dist}")
        

        
        # Show output structure (if available)
        print(f"\n📂 Data saved to:")
        print(f"  • Processed data: data/processed/neo/year={datetime.now().year}/")
        print(f"  • Aggregations: data/aggregations/neo/year={datetime.now().year}/")
        
        return 0
        
    except Exception as e:
        print(f"\n❌ Error: {e}")
        logging.error(f"Distributed pipeline failed: {e}", exc_info=True)
        return 1



if __name__ == "__main__":
    import argparse
    
    parser = argparse.ArgumentParser(description="NASA NEO Data Processor")

    parser.add_argument("--verbose", "-v", action="store_true", 
                        help="Enable verbose logging")
    
    args = parser.parse_args()
    
    exit(main()) 