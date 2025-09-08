#!/usr/bin/env python3
"""
NASA NEO Data Scraper

Fetches Near Earth Object data from NASA's API and saves it in Parquet format
with S3-like partitioning for scalable data processing.
"""

import os
import sys
from pathlib import Path
from dotenv import load_dotenv
from typing import List, Dict, Any

# Add current directory to path for imports
sys.path.append(str(Path(__file__).parent))

from utils.api_client import NASAAPIClient, APIConfig
from utils.data_processor import NEODataProcessor
from utils.file_manager import FileManager


class NEOScraper:
    def __init__(self, api_key: str, target_count: int = 200):
        self.api_client = NASAAPIClient(api_key)
        self.data_processor = NEODataProcessor()
        self.file_manager = FileManager()
        self.target_count = target_count
        self.processed_count = 0

    def run(self) -> Dict[str, Any]:
        """Execute the complete data scraping and processing pipeline."""
        print(f"Starting NEO data collection for {self.target_count} objects...")
        
        all_neo_data = []
        batch_size = 20
        
        try:
            for neo in self.api_client.fetch_neo_data(self.target_count):
                all_neo_data.append(neo)
                self.processed_count += 1
                
                if self.processed_count % batch_size == 0:
                    print(f"Processed {self.processed_count}/{self.target_count} NEOs...")
            
            print(f"Successfully fetched {len(all_neo_data)} NEO records")
            
            # Process data
            print("Processing and transforming data...")
            processed_df = self.data_processor.process_batch(all_neo_data)
            
            # Save raw data
            print("Saving raw data to Parquet...")
            raw_file_path = self.file_manager.save_raw_data(processed_df)
            print(f"Raw data saved to: {raw_file_path}")
            
            # Calculate aggregations
            print("Calculating aggregations...")
            aggregations = self.data_processor.calculate_aggregations(processed_df, all_neo_data)
            aggregations['processed_objects'] = all_neo_data
            
            # Save aggregations
            agg_file_path = self.file_manager.save_aggregations(aggregations)
            print(f"Aggregations saved to: {agg_file_path}")
            
            # Print summary
            self._print_summary(processed_df, aggregations)
            
            return {
                'success': True,
                'processed_count': len(all_neo_data),
                'raw_file': raw_file_path,
                'aggregations_file': agg_file_path,
                'aggregations': aggregations
            }
            
        except Exception as e:
            print(f"Error during processing: {e}")
            return {'success': False, 'error': str(e)}

    def _print_summary(self, df, aggregations):
        """Print processing summary."""
        print("\n" + "="*50)
        print("PROCESSING SUMMARY")
        print("="*50)
        print(f"Total NEOs processed: {len(df)}")
        print(f"Potentially hazardous asteroids: {df['is_potentially_hazardous_asteroid'].sum()}")
        print(f"Close approaches under 0.2 AU: {aggregations['close_approaches_under_02_au']}")
        print(f"Yearly approach breakdown: {aggregations['yearly_approaches']}")
        
        data_summary = self.file_manager.get_data_summary()
        print(f"Total data size: {data_summary['total_size_mb']:.2f} MB")
        print("="*50)


def load_api_key() -> str:
    """Load NASA API key from environment."""
    env_path = Path(__file__).parent / ".env"
    load_dotenv(env_path)
    
    api_key = os.getenv('NASA_API_KEY')
    if not api_key:
        raise ValueError(
            "NASA_API_KEY not found. Please create a .env file with your API key.\n"
            "Example: NASA_API_KEY=your_api_key_here"
        )
    return api_key


def main():
    """Main entry point."""
    try:
        api_key = load_api_key()
        scraper = NEOScraper(api_key, target_count=200)
        result = scraper.run()
        
        if result['success']:
            print("\n✅ Data collection completed successfully!")
            sys.exit(0)
        else:
            print(f"\n❌ Data collection failed: {result['error']}")
            sys.exit(1)
            
    except Exception as e:
        print(f"❌ Fatal error: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()
