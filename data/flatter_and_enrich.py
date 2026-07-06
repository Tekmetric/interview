import pandas as pd
import json
import os
import config
from collections import defaultdict


def save_file(data, folder, file, structured):
    if structured:
        try:
            parquet_file_agg = os.path.join(folder, file)
            data.to_parquet(parquet_file_agg, index=False, engine='pyarrow')
            print(f"Saved DataFrame to {parquet_file_agg}")
        except Exception as e:
            print(f"Warning: Error saving DataFrame to parquet: {e}")
            # Try alternative if pyarrow is not available
            try:
                data.to_parquet(parquet_file_agg, index=False, engine='fastparquet')
                print(f"✓ Saved DataFrame to {parquet_file_agg} (using fastparquet)")
            except Exception as e2:
                print(f"Error: Could not save DataFrame to parquet format: {e2}")
    else:
        base_faulty_records_file = os.path.join(folder, file)
        try:
            with open(base_faulty_records_file, 'w', encoding='utf-8') as f:
                json.dump(data, f, indent=2, ensure_ascii=False)
            print(f"Saved {len(data)} faulty record(s) to {base_faulty_records_file}")
        except Exception as e:
            print(f"Error saving faulty records to file: {e}")


def parse_neo_data_from_landing(landing_folder, foundation_folder, core_folder, faulty_records_folder):
    """
    Parse NEO data from JSON files in the landing folder using pandas.
    Extracts all specified fields and returns a pandas DataFrame.
    Records that cannot be inserted into the DataFrame are saved to a separate
    JSON file (faulty_records.json) in the landing folder.

    Args:
        landing_folder: Path to the folder containing JSON files

    Returns:
        pandas DataFrame with parsed NEO data containing the following fields:
        - id
        - neo_reference_id
        - name
        - name_limited
        - designation
        - nasa_jpl_url
        - absolute_magnitude_h
        - is_potentially_hazardous_asteroid
        - minimum_estimated_diameter_meters
        - maximum_estimated_diameter_meters
        - closest_approach_miss_distance_km
        - closest_approach_date
        - closest_approach_relative_velocity_km_s
        - first_observation_date
        - last_observation_date
        - observations_used
        - orbital_period
    """

    json_files = [os.path.join(landing_folder, f) for f in os.listdir(landing_folder)
                  if f.endswith('.json')]

    if not json_files:
        raise ValueError(f"No JSON files found in '{landing_folder}' folder")

    print(f"Found {len(json_files)} JSON files to process")

    # Read all JSON files and extract near_earth_objects
    all_data = []
    for json_file in sorted(json_files):
        with open(json_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
            all_data.extend(data['near_earth_objects'])

    print(f"Total NEOs found: {len(all_data)}")

    # Parse and extract required fields
    parsed_data = []
    base_faulty_records = []
    major_faulty_records = []
    close_approach_dict = defaultdict(dict)
    total_02_approach = 0

    for idx, neo in enumerate(all_data):
        try:
            # Basic fields
            record = {
                'id': neo.get('id'),
                'neo_reference_id': neo.get('neo_reference_id'),
                'name': neo.get('name'),
                'name_limited': neo.get('name_limited'),
                'designation': neo.get('designation'),
                'nasa_jpl_url': neo.get('nasa_jpl_url'),
                'absolute_magnitude_h': neo.get('absolute_magnitude_h'),
                'is_potentially_hazardous_asteroid': neo.get('is_potentially_hazardous_asteroid', False),
            }

            # Estimated diameter in meters
            meters_diam = neo['estimated_diameter']['meters']
            record['minimum_estimated_diameter_meters'] = meters_diam.get('estimated_diameter_min')
            record['maximum_estimated_diameter_meters'] = meters_diam.get('estimated_diameter_max')

            closest_approach = None
            min_distance = float('inf')

            # populate parsed data table
            try:
                for approach in neo['close_approach_data']:
                    miss_dist_str = approach.get('miss_distance', {}).get('astronomical', '0')
                    miss_dist = float(miss_dist_str)
                    if miss_dist < min_distance:
                        min_distance = miss_dist
                        closest_approach = approach

                record['closest_approach_miss_distance_km'] = (closest_approach.get('miss_distance', {})
                                                               .get('kilometers', 0))
                record['closest_approach_date'] = closest_approach.get('close_approach_date')
                record['closest_approach_relative_velocity_km_s'] = (closest_approach.get('relative_velocity', {})
                                                                     .get('kilometers_per_second', 0))

            except (ValueError, TypeError, AttributeError) as e:
                record['closest_approach_miss_distance_km'] = None
                record['closest_approach_date'] = None
                record['closest_approach_relative_velocity_km_s'] = None

                neo_id = neo.get('id', 'unknown')
                neo_name = neo.get('name', 'unknown')
                faulty_record = {
                    'record_processed': 'True - without closest approach',
                    'record_index': idx + 1,
                    'error': str(e),
                    'error_type': type(e).__name__,
                    'neo_id': neo_id,
                    'neo_name': neo_name,
                    'neo_data': neo  # Save the full original NEO data
                }
                base_faulty_records.append(faulty_record)
                print(f"Error processing record {idx + 1} (ID: {neo_id}, Name: {neo_name}): {str(e)}")

            # Try to append the record
            parsed_data.append(record)

            # !!!!!!!!!!!!!!!!!! AGGREGATION !!!!!!!!!!!!!!!!!!!!
            try:
                for approach in neo['close_approach_data']:
                    miss_dist_str = approach.get('miss_distance', {}).get('astronomical', '0')
                    miss_dist = float(miss_dist_str)
                    approach_year = approach.get('close_approach_date')[:4]
                    year_struct = close_approach_dict[approach_year]
                    year_struct['close_encounter'] = year_struct.get('close_encounter', 0) + 1
                    if miss_dist < 0.2:
                        year_struct['less_than_02_encounter'] = year_struct.get('less_than_02_encounter', 0) + 1
                        total_02_approach += 1

            except Exception as e:
                print("!!!!!!!!!!! ERROR !!!!!!!!!!!!!!!")
                print(e)
                print(f'error: {str(e)}')
                print(f'error_type: {type(e).__name__}')

            # !!!!!!!!!!!!!!!!!! AGGREGATION END !!!!!!!!!!!!!!!!!!!!

            # Orbital data
            orbital = neo['orbital_data']
            record['first_observation_date'] = orbital.get('first_observation_date')
            record['last_observation_date'] = orbital.get('last_observation_date')
            record['observations_used'] = orbital.get('observations_used')
            record['orbital_period'] = orbital.get('orbital_period')

        except Exception as e:
            # Save faulty record with error information
            neo_id = neo.get('id', 'unknown')
            neo_name = neo.get('name', 'unknown')
            faulty_record = {
                'record_processed': 'False',
                'record_index': idx + 1,
                'error': str(e),
                'error_type': type(e).__name__,
                'neo_id': neo_id,
                'neo_name': neo_name,
                'neo_data': neo  # Save the full original NEO data
            }
            major_faulty_records.append(faulty_record)
            print(f"Error processing record {idx + 1} (ID: {neo_id}, Name: {neo_name}): {str(e)}")
            continue

    # get faultry record count
    if major_faulty_records:
        major_faulty_records_cnt = len(major_faulty_records)
    else:
        major_faulty_records_cnt = 0

    if base_faulty_records:
        base_faulty_records_cnt = len(base_faulty_records)
    else:
        base_faulty_records_cnt = 0

    # Save faulty records to JSON file
    if base_faulty_records:
        save_file(data=base_faulty_records, folder=faulty_records_folder, file='base_faulty_records.json', structured=False)

    if major_faulty_records:
        save_file(data=major_faulty_records, folder=faulty_records_folder, file='major_faulty_records.json', structured=False)

    print(f"✓ Successfully parsed {len(parsed_data)} records")

    if len(parsed_data) == 0:
        print("No valid records to create DataFrame")
        return pd.DataFrame()

    # Create pandas DataFrame with error handling
    try:
        df = pd.DataFrame(parsed_data)
    except Exception as e:
        print(f"Error creating DataFrame: {e}")
        return pd.DataFrame()

    # Convert data types appropriately with error handling
    try:
        # Numeric columns
        numeric_cols = [
            'absolute_magnitude_h',
            'minimum_estimated_diameter_meters',
            'maximum_estimated_diameter_meters',
            'closest_approach_miss_distance_km',
            'closest_approach_relative_velocity_km_s',
            'orbital_period'
        ]
        for col in numeric_cols:
            if col in df.columns:
                df[col] = pd.to_numeric(df[col], errors='coerce')

        # Convert observations_used to nullable integer
        if 'observations_used' in df.columns:
            df['observations_used'] = pd.to_numeric(df['observations_used'], errors='coerce').astype('Int64')
    except Exception as e:
        print(f"Warning: Error during data type conversion: {e}")

    # seve base dataframe to parquet
    save_file(data=df, folder=foundation_folder, file='base.parquet', structured=True)

    # !!!!!!!!!!!!!!!!!!!!!!! AGGREGATE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    try:
        df_agg_init = pd.DataFrame.from_dict(close_approach_dict, orient='index')
        df_agg = df_agg_init.reset_index().rename(columns={'index': 'year'})
        df_agg['year'] = df_agg['year'].astype(int)
        df_agg = df_agg.sort_values(by='year')
    except Exception as e:
        print(f"Error creating DataFrame: {e}")
        return pd.DataFrame()

    # seve aggregated dataframe to parquet
    save_file(data=df_agg, folder=core_folder, file='aggregation.parquet', structured=True)

    # !!!!!!!!!!!!!!!!!!!!!!! AGGREGATE END !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    # !!!!!!!!!!!!!!!!!!!!!! TOTAL !!!!!!!!!!!!!!!!!!!!!!!!
    df_total = pd.DataFrame({'total_cole_encounters': [total_02_approach]})
    save_file(data=df_total, folder=core_folder, file='total.parquet', structured=True)

    return df, df_agg, base_faulty_records_cnt, major_faulty_records_cnt, total_02_approach


def main():
    """Main function to run the parser and display results"""
    landing_folder = config.landing_folder
    faulty_records_folder = config.faulty_records_folder
    foundation_folder = config.foundation_folder
    core_folder = config.core_folder

    try:
        # Parse the data
        df, df_agg, base_faulty_records_cnt, major_faulty_records_cnt, total_02_approach = (
            parse_neo_data_from_landing(landing_folder=landing_folder,
                                        foundation_folder=foundation_folder,
                                        core_folder=core_folder,
                                        faulty_records_folder=faulty_records_folder)
        )

        return df, df_agg, base_faulty_records_cnt, major_faulty_records_cnt, total_02_approach

    except Exception as e:
        print(f"Error: {e}")
        return None


if __name__ == "__main__":
    df = main()
