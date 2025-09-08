#!/usr/bin/env python3
"""
Test script for NASA NEO Scraper
Tests the scraper with a small sample to verify functionality.
"""

import os
import sys
from pathlib import Path
from dotenv import load_dotenv

# Add current directory to path for imports
sys.path.append(str(Path(__file__).parent))

from nasa_neo_scraper import NEOScraper, load_api_key


def test_small_sample():
    """Test with a small sample of NEOs."""
    try:
        api_key = load_api_key()
        scraper = NEOScraper(api_key, target_count=5)  # Test with just 5 NEOs
        result = scraper.run()
        
        if result['success']:
            print("✅ Test completed successfully!")
            print(f"Processed {result['processed_count']} NEOs")
            return True
        else:
            print(f"❌ Test failed: {result['error']}")
            return False
            
    except Exception as e:
        print(f"❌ Test error: {e}")
        return False


if __name__ == "__main__":
    print("Running NASA NEO Scraper test...")
    success = test_small_sample()
    sys.exit(0 if success else 1)
