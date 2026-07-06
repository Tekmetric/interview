# You may use this file to write your script, or you may create files
# named in the manner and structure you see fit.import os
import pandas as pd
import requests
from dotenv import load_dotenv
import os
from collections import Counter

def fetch_pages(url, api_key):
    obj_size = 0
    while url and obj_size <= 200:
        try:
            #running a stream from NASA NEO Browse API
            response = requests.get(url, params={"api_key": api_key})
            if response.status_code == 200:
                json_data = response.json()
                url = json_data.get("links", {}).get("next") 
                obj_size = json_data.get("page", {}).get("size", 0)
            else:
                print(f"Request failed with status code {response.status_code}")
                break
            
            yield json_data.get("near_earth_objects", [])

            #Setting the object count to be the threshold
            print(f"Loading page with {obj_size} objects...")
            obj_size += obj_size
        except requests.exceptions.RequestException as e:
            print(f"Error fetching data: {e}")
            break

def data_processing(data):
    """
    This function processes the NEO data to parse through some fields that have lists of dictionaries
    """
    try:
        df = pd.DataFrame(data)        
        df['min_estimated_diameter_meters'] = df['estimated_diameter'].apply(lambda x: min(x.get('meters', {}).values()))
        df['max_estimated_diameter_meters'] = df['estimated_diameter'].apply(lambda x: max(x.get('meters', {}).values()))
        
        #Saving empty list to store parsed data
        closest_approach_miss_kilo = []
        closest_approach_date = []
        relative_velocity = []
        agg_list = []
        agg_by_year = []
        for row in df['close_approach_data']:
            #Skipping the None objects
            if not row:  
                closest_approach_miss_kilo.append(None)  
                closest_approach_date.append(None)
                relative_velocity.append(None)
                agg_list.append(None)  
                agg_by_year.append(None) 
                continue
            temp_df = pd.json_normalize(row, errors = 'ignore')
            
            min_index = temp_df['miss_distance.kilometers'].idxmin()
            temp_df['close_approach_date'] = pd.to_datetime(temp_df['close_approach_date'])
            
            #Appending the data to the lists
            closest_approach_miss_kilo.append(temp_df.iloc[min_index]['miss_distance.kilometers'])
            closest_approach_date.append(temp_df.iloc[min_index]['close_approach_date'])
            relative_velocity.append(temp_df.iloc[min_index]['relative_velocity.kilometers_per_second'])
            agg_list.append(len(temp_df.where(temp_df['miss_distance.astronomical'].astype(float) < 0.2)))
            agg_by_year.append(temp_df['close_approach_date'].dt.year.to_dict())   

        #Saving data parsed from 'close_approach_data' field
        df['closest_approach_miss_kilo'] = closest_approach_miss_kilo
        df['closest_approach_date'] = closest_approach_date
        df['closest_approach_velocity_kmps'] = relative_velocity
        df['close_miss_distance_astro'] = agg_list
        df['close_approach_date_year'] = agg_by_year

        #Parsing out values in dictionary in 'orbital_data'
        df['first_observation_date'] = df['orbital_data'].str['first_observation_date']
        df['last_observation_date'] = df['orbital_data'].str['last_observation_date']
        df['observations_used'] = df['orbital_data'].str['observations_used']
        df['orbital_period'] = df['orbital_data'].str['orbital_period']


    except Exception as e:
        print(f"Error during data cleaning: {e}")
        return None
    return df

def run_pipeline(url, api_key, chunk_size=200):
    """
    Runs the pipeline of feeding in the Browse API, cleaning the data, and saving it as Dataframe
    """
    neo_obj = []
    neo_df_list = []
    
    print(f"---Starting API Call---")

    for page_data in fetch_pages(url, api_key):
        # Add new data to our temporary buffer
        neo_obj.extend(page_data)
    
        if len(neo_obj) >= chunk_size:
            print(f"Processing chunk of {len(neo_obj)} records...")
            neo_df = data_processing(neo_obj)
            #Save each dataframe into a list
            neo_df_list.append(neo_df)
            neo_obj = [] 
            #Stop the loop after meeting the chunk size criteria
            break

    #Combinging the list of clean dataframe as final output
    neo_output = pd.concat(neo_df_list, ignore_index=True)

    print("--- API Call Complete ---")
    return neo_output

def save_as_parquet(data, name, output_dir="parquet_output"):
    """Writes the Pandas Dataframe into Parquet file."""

    filename = f"{name}.parquet"
    filepath = os.path.join(output_dir, filename)
    
    data.to_parquet(filepath)
    print(f"Saved {len(data)} records to {filename}")



if __name__ == "__main__":
    load_dotenv()
    NASA_NEO_URL = os.getenv("API_URL")
    API_KEY = os.getenv("API_KEY")

    output_dir = "parquet_output"
    if os.path.exists(f'{output_dir}'):
        print(f"Output directory {output_dir} already exists. Files will be overwritten.")
    else:
        os.mkdir(f'{output_dir}')
    
    print("---Starting NASA NEO ETL Pipeline---")
    neo_data = run_pipeline(NASA_NEO_URL, API_KEY, chunk_size=200)
    
    #Saving certain columns into parquet file for the first 200 objects
    columns_to_keep = ['id', 'neo_reference_id', 'name', 'name_limited',
                        'designation', 'nasa_jpl_url', 'absolute_magnitude_h', 'is_potentially_hazardous_asteroid',
                        'min_estimated_diameter_meters', 'max_estimated_diameter_meters',
                        'closest_approach_miss_kilo', 'closest_approach_date', 'closest_approach_velocity_kmps',
                        'first_observation_date', 'last_observation_date', 'observations_used', 'orbital_period' 
                        ]
    final_neo_df = neo_data[columns_to_keep]
    save_as_parquet(final_neo_df, "nasa_neo_data", output_dir=output_dir)

    #Aggregating the data by criteria for closest miss distance by astronomical metric
    neo_astro_aggregate_data = pd.DataFrame(neo_data.groupby(['id', 'neo_reference_id', 'name', 'name_limited','designation', 'nasa_jpl_url', 'absolute_magnitude_h', 'is_potentially_hazardous_asteroid'])['close_miss_distance_astro'].sum())  
    save_as_parquet(neo_astro_aggregate_data, "nasa_neo_aggregated_data", output_dir=output_dir)

    #Simple loop through the aggregated years for all the 'close_approach_date' by year to save into own separate parquet files
    years_list = []
    for row in neo_data['close_approach_date_year']:
        if row:
            years_list.extend(row.values())

    year_counts = Counter(years_list)
    year_counts_df = pd.DataFrame.from_dict(year_counts, orient='index', columns=['count'])
    year_counts_df.index.name = 'year'

    save_as_parquet(year_counts_df, "nasa_neo_yearly_counts", output_dir=output_dir)
    
    print("---Ending NASA NEO ETL Pipeline---")

