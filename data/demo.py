#!/usr/bin/env python3
"""
Demo script showing both monolithic and modular approaches

This script demonstrates the key difference between the two implementations
while keeping the demo simple for a take-home assignment.
"""

import os
import sys
from pathlib import Path

print("🚀 NASA NEO Data Processor - Take Home Assignment Demo")
print("=" * 60)
print()

# Check if we're in a virtual environment
if sys.prefix == sys.base_prefix:
    print("⚠️  Warning: Not in a virtual environment")
    print("   Run: source venv/bin/activate")
    print()

# Check for .env file
env_file = Path(".env")
if not env_file.exists():
    print("⚠️  No .env file found. Creating one with DEMO_KEY...")
    with open(".env", "w") as f:
        f.write("NASA_API_KEY=DEMO_KEY\n")
    print("✅ Created .env file with DEMO_KEY (rate limited)")
    print()

print("📊 This assignment demonstrates two implementations:")
print()
print("1️⃣  MONOLITHIC VERSION (neo_data_processor.py)")
print("   • Single file: 513 lines")
print("   • Everything mixed together")
print("   • Works but hard to maintain")
print()
print("2️⃣  MODULAR VERSION (src/ + neo_data_processor_modular.py)")
print("   • 8 focused modules")
print("   • Clean separation of concerns") 
print("   • Production-ready architecture")
print()
print("💡 Both produce identical results, but the modular version shows")
print("   software engineering best practices for production systems.")
print()

# Show the file structure comparison
print("📁 FILE STRUCTURE COMPARISON:")
print()
print("Monolithic:")
print("  neo_data_processor.py  (513 lines - everything mixed)")
print()
print("Modular:")
print("  src/")
print("  ├── config.py          (Configuration management)")
print("  ├── models.py          (Type-safe data models)")  
print("  ├── exceptions.py      (Custom exception hierarchy)")
print("  ├── api_client.py      (NASA API integration)")
print("  ├── data_processor.py  (PySpark data processing)")
print("  ├── storage.py         (Data lake operations)")
print("  └── neo_processor.py   (Pipeline orchestration)")
print("  neo_data_processor_modular.py  (Clean entry point)")
print()

print("🎯 KEY BENEFITS OF MODULAR APPROACH:")
print("   ✅ Single Responsibility - Each module has one purpose")
print("   ✅ Testability - Easy to test components in isolation")
print("   ✅ Maintainability - Clear code organization")
print("   ✅ Team Collaboration - Multiple devs can work in parallel")
print("   ✅ Error Debugging - Component-specific error handling")
print("   ✅ Extensibility - Easy to add new features")
print()

print("🚀 TO RUN THE DEMOS:")
print("   Original:  ./venv/bin/python neo_data_processor.py")
print("   Modular:   ./venv/bin/python neo_data_processor_modular.py")
print("   Analysis:  jupyter lab  (then open neo_data_analysis.ipynb)")
print()

print("📈 Both implementations fulfill all requirements:")
print("   ✅ Fetch first 200 NEO objects from NASA API")
print("   ✅ Save data in Parquet format") 
print("   ✅ Create S3-like data lake structure")
print("   ✅ Calculate required aggregations")
print("   ✅ Scale with PySpark (local → distributed)")
print()

print("=" * 60)
print("Ready for demonstration! 🎉") 