#!/usr/bin/env python3
"""
NASA Near Earth Object Data Processor (Modular Version)

This is the main entry point for the refactored, modular NEO data processor.
It demonstrates the clean separation of concerns and improved maintainability.
"""

import sys
import logging
from pathlib import Path

# Add src directory to path so we can import our modules
sys.path.insert(0, str(Path(__file__).parent / "src"))

from src.neo_processor import process_neo_data


def main():
    """Main entry point using the refactored modular architecture"""
    
    print("🚀 NASA Near Earth Object Data Processor (Modular Version)")
    print("="*60)
    print("This version demonstrates:")
    print("✅ Modular architecture with separated concerns")
    print("✅ Configuration management")
    print("✅ Type-safe data models")
    print("✅ Robust error handling")
    print("✅ Comprehensive logging")
    print("✅ Easy testing and maintainability")
    print("="*60)
    
    try:
        # Run the pipeline using the convenience function
        result = process_neo_data(limit=50)  # Smaller limit for demo
        
        print("\n🎉 Processing completed successfully!")
        print(f"📊 Objects processed: {result.total_objects_processed}")
        print(f"⏱️ Processing time: {result.processing_time_seconds:.2f} seconds")
        print(f"📁 Output files created: {len(result.output_paths)}")
        
        # Show output structure
        print("\n📂 Generated data structure:")
        for name, path in result.output_paths.items():
            if path:
                print(f"  • {name}: {path}")
        
        print(f"\n📈 Aggregations:")
        agg = result.aggregations
        print(f"  • Close approaches < 0.2 AU: {agg['close_approaches_under_02_au']}")
        print(f"  • Total objects: {agg['total_objects_processed']}")
        print(f"  • Approaches by year: {agg['approaches_by_year']}")
        
        return 0
        
    except Exception as e:
        print(f"\n❌ Error: {e}")
        logging.error(f"Pipeline failed: {e}", exc_info=True)
        return 1


if __name__ == "__main__":
    # Configure logging
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    )
    
    exit(main()) 