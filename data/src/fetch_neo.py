import os
import requests
import pandas as pd
import json
from datetime import datetime
from pathlib import Path
import time

# Configuration
API_BASE_URL = "https://api.nasa.gov/neo/rest/v1/neo/browse"
DATA_DIR = Path("../data")
NEO_DATA_DIR = DATA_DIR / "neo" / "raw"
AGGREGATIONS_DIR = DATA_DIR / "neo" / "aggregations"
NEO_LIMIT = 200
RATE_LIMIT_DELAY = 6  # seconds between API calls to respect rate limits

def load_api_key():
    """Load API key from environment variable or config file"""
    api_key = os.environ.get("NASA_API_KEY")
    if not api_key:
        try:
            with open("../config.json", "r") as f:
                config = json.load(f)
                api_key = config.get("api_key")
        except (FileNotFoundError, json.JSONDecodeError):
            pass
    
    if not api_key:
        raise ValueError("API key not found. Set NASA_API_KEY environment variable or create a config.json file.")
    
    return api_key

def fetch_neo_data(api_key, limit=NEO_LIMIT):
    """Fetch NEO data from NASA API"""
    neo_objects = []
    page = 0
    page_size = min(20, limit)  # NASA API allows max 20 items per page
    
    while len(neo_objects) < limit:
        params = {
            "page": page,
            "size": page_size,
            "api_key": api_key
        }
        
        print(f"Fetching page {page}...")
        response = requests.get(API_BASE_URL, params=params)
        
        if response.status_code != 200:
            raise Exception(f"API request failed: {response.status_code} - {response.text}")
        
        data = response.json()
        new_objects = data.get("near_earth_objects", [])
        
        if not new_objects:
            print("No more objects available")
            break
        
        neo_objects.extend(new_objects)
        print(f"Collected {len(neo_objects)}/{limit} objects")
        
        # Check if we need to fetch more and if more are available
        if len(neo_objects) >= limit or "next" not in data.get("links", {}):
            break
        
        page += 1
        time.sleep(RATE_LIMIT_DELAY)  # Respect rate limits
    
    # Ensure we don't exceed the requested limit
    return neo_objects[:limit]

def transform_neo_data(neo_objects):
    """Transform raw NEO data into the required format"""
    transformed_data = []
    close_approaches_data = []
    
    for neo in neo_objects:
        # Extract base NEO data
        neo_id = neo.get("id")
        orbital_data = neo.get("orbital_data", {})
        
        # Process closest approach
        closest_approach = None
        min_distance = float('inf')
        
        for approach in neo.get("close_approach_data", []):
            # Add each approach to the approaches list for aggregation
            km_distance = float(approach.get("miss_distance", {}).get("kilometers", float('inf')))
            au_distance = float(approach.get("miss_distance", {}).get("astronomical", float('inf')))
            
            approach_data = {
                "neo_id": neo_id,
                "approach_date": approach.get("close_approach_date"),
                "miss_distance_km": km_distance,
                "miss_distance_au": au_distance,
                "relative_velocity_kps": float(approach.get("relative_velocity", {}).get("kilometers_per_second", 0)),
                "orbiting_body": approach.get("orbiting_body")
            }
            close_approaches_data.append(approach_data)
            
            # Check if this is the closest approach
            if km_distance < min_distance:
                min_distance = km_distance
                closest_approach = approach
        
        # If no close approaches, use defaults
        if closest_approach is None:
            closest_approach_date = None
            closest_approach_distance = None
            closest_approach_velocity = None
        else:
            closest_approach_date = closest_approach.get("close_approach_date")
            closest_approach_distance = float(closest_approach.get("miss_distance", {}).get("kilometers", 0))
            closest_approach_velocity = float(closest_approach.get("relative_velocity", {}).get("kilometers_per_second", 0))
        
        # Create the entry for this NEO
        transformed_neo = {
            "id": neo_id,
            "neo_reference_id": neo.get("neo_reference_id"),
            "name": neo.get("name"),
            "name_limited": neo.get("name_limited"),
            "designation": neo.get("designation"),
            "nasa_jpl_url": neo.get("nasa_jpl_url"),
            "absolute_magnitude_h": neo.get("absolute_magnitude_h"),
            "is_potentially_hazardous_asteroid": neo.get("is_potentially_hazardous_asteroid"),
            "minimum_estimated_diameter_meters": neo.get("estimated_diameter", {}).get("meters", {}).get("estimated_diameter_min"),
            "maximum_estimated_diameter_meters": neo.get("estimated_diameter", {}).get("meters", {}).get("estimated_diameter_max"),
            "closest_approach_miss_distance_kilometers": closest_approach_distance,
            "closest_approach_date": closest_approach_date,
            "closest_approach_relative_velocity_kps": closest_approach_velocity,
            "first_observation_date": orbital_data.get("first_observation_date"),
            "last_observation_date": orbital_data.get("last_observation_date"),
            "observations_used": orbital_data.get("observations_used"),
            "orbital_period": orbital_data.get("orbital_period")
        }
        
        transformed_data.append(transformed_neo)
    
    # Create DataFrames
    neo_df = pd.DataFrame(transformed_data)
    approaches_df = pd.DataFrame(close_approaches_data)
    
    return neo_df, approaches_df

def calculate_aggregations(neo_df, approaches_df):
    """Calculate the required aggregations"""
    # 1. Total number of times our 200 near earth objects approached closer than 0.2 AU
    close_approaches = approaches_df[approaches_df["miss_distance_au"] < 0.2]
    close_neos_count = len(close_approaches)  # Count all approaches, not just unique NEOs
    
    # 2. Number of close approaches recorded by year
    approaches_df["approach_year"] = approaches_df["approach_date"].apply(lambda x: int(x.split("-")[0]) if x else None)
    yearly_approaches = approaches_df["approach_year"].value_counts().to_dict()
    
    # Create aggregations DataFrame
    aggregations = {
        "timestamp": datetime.now().isoformat(),
        "total_neos_collected": len(neo_df),
        "neos_closer_than_0.2_au": close_neos_count,
        "yearly_approaches": json.dumps(yearly_approaches)
    }
    
    return pd.DataFrame([aggregations])

def save_data(neo_df, approaches_df, aggregations_df):
    """Save the DataFrames to Parquet files"""
    # Create necessary directories
    NEO_DATA_DIR.mkdir(parents=True, exist_ok=True)
    AGGREGATIONS_DIR.mkdir(parents=True, exist_ok=True)
    
    # Generate timestamp for filenames
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    
    # Save NEO data
    neo_file_path = NEO_DATA_DIR / f"neo_data_{timestamp}.parquet"
    neo_df.to_parquet(neo_file_path, index=False)
    print(f"Saved NEO data to {neo_file_path}")
    
    # Save approaches data
    approaches_file_path = NEO_DATA_DIR / f"neo_approaches_{timestamp}.parquet"
    approaches_df.to_parquet(approaches_file_path, index=False)
    print(f"Saved approach data to {approaches_file_path}")
    
    # Save aggregations
    agg_file_path = AGGREGATIONS_DIR / f"neo_aggregations_{timestamp}.parquet"
    aggregations_df.to_parquet(agg_file_path, index=False)
    print(f"Saved aggregations to {agg_file_path}")
    
    return neo_file_path, approaches_file_path, agg_file_path

def main():
    """Main execution function"""
    try:
        # Step 1: Load API Key
        print("Loading API key...")
        api_key = load_api_key()
        
        # Step 2: Fetch data from API
        print(f"Fetching up to {NEO_LIMIT} NEO objects from NASA API...")
        neo_objects = fetch_neo_data(api_key, NEO_LIMIT)
        print(f"Successfully retrieved {len(neo_objects)} NEO objects")
        
        # Step 3: Transform the data into the required format
        print("Transforming data...")
        neo_df, approaches_df = transform_neo_data(neo_objects)
        print(f"Transformed {len(neo_df)} NEO objects with {len(approaches_df)} close approaches")
        
        # Step 4: Calculate aggregations
        print("Calculating aggregations...")
        aggregations_df = calculate_aggregations(neo_df, approaches_df)
        
        # Step 5: Save data to Parquet files
        print("Saving data to Parquet files...")
        save_data(neo_df, approaches_df, aggregations_df)
        
        print("NEO data pipeline completed successfully")
        
    except Exception as e:
        print(f"Error in NEO data pipeline: {str(e)}")
        raise

if __name__ == "__main__":
    main()