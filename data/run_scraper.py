#!/usr/bin/env python3
"""
Convenience script to run the NASA NEO scraper with different configurations.
"""

import argparse
import sys
from pathlib import Path
from nasa_neo_scraper import NEOScraper, load_api_key


def main():
    parser = argparse.ArgumentParser(description='NASA NEO Data Scraper')
    parser.add_argument(
        '--count', 
        type=int, 
        default=200, 
        help='Number of NEOs to fetch (default: 200)'
    )
    parser.add_argument(
        '--test', 
        action='store_true', 
        help='Run in test mode with 5 NEOs'
    )
    
    args = parser.parse_args()
    
    try:
        api_key = load_api_key()
        target_count = 5 if args.test else args.count
        
        print(f"Starting NEO data collection for {target_count} objects...")
        scraper = NEOScraper(api_key, target_count=target_count)
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
