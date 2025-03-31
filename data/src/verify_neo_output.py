#!/usr/bin/env python3
"""
Verification script for NASA NEO data pipeline output.
Run this after executing the neo_data_pipeline.py script.
"""

import os
import pandas as pd
import glob
from pathlib import Path
import json

# Paths
DATA_DIR = Path("../data")
NEO_DATA_DIR = DATA_DIR / "neo" / "raw"
AGGREGATIONS_DIR = DATA_DIR / "neo" / "aggregations"

def verify_directory_structure():
    """Check that the expected directory structure exists"""
    directories = [DATA_DIR, NEO_DATA_DIR, AGGREGATIONS_DIR]
    missing = [str(d) for d in directories if not d.exists()]
    
    if missing:
        print("ERROR: The following directories are missing:")
        for dir_path in missing:
            print(f"  - {dir_path}")
        return False
    
    print("Directory structure verified")
    return True

def find_latest_files():
    """Find the most recently created files in each directory"""
    files = {
        "neo_data": sorted(glob.glob(str(NEO_DATA_DIR / "neo_data_*.parquet")))[-1] if glob.glob(str(NEO_DATA_DIR / "neo_data_*.parquet")) else None,
        "neo_approaches": sorted(glob.glob(str(NEO_DATA_DIR / "neo_approaches_*.parquet")))[-1] if glob.glob(str(NEO_DATA_DIR / "neo_approaches_*.parquet")) else None,
        "aggregations": sorted(glob.glob(str(AGGREGATIONS_DIR / "neo_aggregations_*.parquet")))[-1] if glob.glob(str(AGGREGATIONS_DIR / "neo_aggregations_*.parquet")) else None
    }
    
    return files

def verify_files_exist(files):
    """Verify that all expected files exist"""
    missing = [k for k, v in files.items() if v is None]
    
    if missing:
        print("ERROR: The following files are missing:")
        for file_type in missing:
            print(f"  - {file_type}")
        return False
    
    print("All required files exist:")
    for file_type, file_path in files.items():
        print(f"  - {file_type}: {file_path}")
    return True

def check_neo_data(neo_data_path):
    """Check the NEO data file"""
    try:
        df = pd.read_parquet(neo_data_path)
        
        object_count = len(df)
        print(f"\nNEO Data Summary ({object_count} objects)")
        
        # Check for required columns
        required_columns = [
            "id", "neo_reference_id", "name", "name_limited", "designation", 
            "nasa_jpl_url", "absolute_magnitude_h", "is_potentially_hazardous_asteroid",
            "minimum_estimated_diameter_meters", "maximum_estimated_diameter_meters",
            "closest_approach_miss_distance_kilometers", "closest_approach_date",
            "closest_approach_relative_velocity_kps", "first_observation_date",
            "last_observation_date", "observations_used", "orbital_period"
        ]
        
        missing_columns = [col for col in required_columns if col not in df.columns]
        if missing_columns:
            print("The following required columns are missing:")
            for col in missing_columns:
                print(f"  - {col}")
        else:
            print("All required columns are present")
        
        # Display hazardous asteroid count
        hazardous_count = df["is_potentially_hazardous_asteroid"].sum()
        print(f"Found {hazardous_count} potentially hazardous asteroids")
        
        # Show diameter range
        min_diameter = df["minimum_estimated_diameter_meters"].min()
        max_diameter = df["maximum_estimated_diameter_meters"].max()
        print(f"Diameter range: {min_diameter:.2f} to {max_diameter:.2f} meters")
        
        # Show observation period
        first_obs = df["first_observation_date"].min()
        last_obs = df["last_observation_date"].max()
        print(f"Observation period: {first_obs} to {last_obs}")
        
        return object_count, hazardous_count
    
    except Exception as e:
        print(f"Error reading NEO data: {str(e)}")
        return None, None

def check_approaches_data(approaches_path):
    """Check the approaches data file"""
    try:
        df = pd.read_parquet(approaches_path)
        approach_count = len(df)
        unique_neos = df["neo_id"].nunique()
        
        print(f"\nClose Approaches Summary ({approach_count} approaches)")
        print(f"Found close approaches for {unique_neos} unique NEOs")
        
        # Check for close approaches
        close_approaches = df[df["miss_distance_au"] < 0.2]
        close_approach_count = len(close_approaches)
        close_neos_count = close_approaches["neo_id"].nunique()
        print(f"Found {close_approach_count} approaches closer than 0.2 AU from {close_neos_count} unique NEOs")
        
        # Check approach years
        df["approach_year"] = df["approach_date"].apply(lambda x: int(x.split("-")[0]) if x else None)
        year_counts = df["approach_year"].value_counts().sort_index()
        print("Approaches by year (top 5):")
        for year, count in year_counts.head(5).items():
            print(f"  - {year}: {count} approaches")
        
        # Find NEOs without close approaches
        if unique_neos < 200:
            print(f"Note: {200 - unique_neos} NEOs don't have any recorded close approaches")
            
        return approach_count, close_approach_count
        
    except Exception as e:
        print(f"Error reading approaches data: {str(e)}")
        return None, None

def check_aggregations_data(aggregations_path):
    """Check the aggregations data file"""
    try:
        df = pd.read_parquet(aggregations_path)
        print("\nAggregations Summary")
        
        # Display the aggregation results
        for column in df.columns:
            if column == "yearly_approaches":
                yearly_data = json.loads(df["yearly_approaches"].iloc[0])
                print(f"Yearly approaches (top 5):")
                for year, count in sorted(yearly_data.items(), key=lambda x: int(x[0]))[:5]:
                    print(f"  - {year}: {count} approaches")
            elif column != "timestamp":
                print(f"{column}: {df[column].iloc[0]}")
                
        return True
        
    except Exception as e:
        print(f"Error reading aggregations data: {str(e)}")
        return False

def main():
    """Main verification function"""
    print("Starting verification of NASA NEO data pipeline output...")
    
    # Verify directory structure
    if not verify_directory_structure():
        print("\nVerification failed: Directory structure issues")
        return
    
    # Find and verify files
    files = find_latest_files()
    if not verify_files_exist(files):
        print("\nVerification failed: Missing files")
        return
    
    # Check the content of each file
    neo_count, hazardous_count = check_neo_data(files["neo_data"])
    approach_count, close_count = check_approaches_data(files["neo_approaches"])
    agg_success = check_aggregations_data(files["aggregations"])
    
    # Final verification
    if all([neo_count, approach_count, agg_success]):
        print("\nVerification completed successfully")
        print(f"Summary: {neo_count} NEOs with {approach_count} approaches, {close_count} closer than 0.2 AU")
    else:
        print("\nVerification failed: Data issues detected")

if __name__ == "__main__":
    main()