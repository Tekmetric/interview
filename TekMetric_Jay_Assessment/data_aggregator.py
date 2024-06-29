import os
import pandas as pd
from tqdm import tqdm

BASE_PATH = '/Users/vimal/Documents/Tekmetric/TekMetric_Jay_Assessment/local-s3-emulation/recalls'

def save_parquet_files(df):
    years = df['report_received_date'].dt.year.unique()
    for year in tqdm(years, desc="Saving Parquet Files by Year"):
        year_group = df[df['report_received_date'].dt.year == year]
        year_path = os.path.join(BASE_PATH, 'parquet', str(year))
        os.makedirs(year_path, exist_ok=True)
        
        manufacturers = year_group['manufacturer'].unique()
        for manufacturer in tqdm(manufacturers, desc=f"Year {year} - Saving Parquet Files by Manufacturer"):
            manu_group = year_group[year_group['manufacturer'] == manufacturer]
            manufacturer_path = os.path.join(year_path, manufacturer)
            os.makedirs(manufacturer_path, exist_ok=True)
            
            file_path = os.path.join(manufacturer_path, 'data.parquet')
            manu_group.to_parquet(file_path, index=False)

def save_csv(file_path, df):
    os.makedirs(os.path.dirname(file_path), exist_ok=True)
    df.to_csv(file_path, index=False)

def aggregate_data(df):
    print("Aggregating Data")
    # Aggregation 1: Number of recalls per manufacturer per year
    agg_manufacturer_year = df.groupby([df['report_received_date'].dt.year, 'manufacturer']).size().reset_index(name='recall_count')
    agg_manufacturer_year.columns = ['year', 'manufacturer', 'recall_count']
    save_csv(os.path.join(BASE_PATH, 'csv', 'aggregations', 'recalls_per_manufacturer_per_year.csv'), agg_manufacturer_year)

    # Aggregation 2: Number of recalls per component per year
    agg_component_year = df.groupby([df['report_received_date'].dt.year, 'component']).size().reset_index(name='recall_count')
    agg_component_year.columns = ['year', 'component', 'recall_count']
    save_csv(os.path.join(BASE_PATH, 'csv', 'aggregations', 'recalls_per_component_per_year.csv'), agg_component_year)

    # Aggregation 3: Number of recalls per type per manufacturer
    agg_type_manufacturer = df.groupby(['recall_type', 'manufacturer']).size().reset_index(name='recall_count')
    agg_type_manufacturer.columns = ['recall_type', 'manufacturer', 'recall_count']
    save_csv(os.path.join(BASE_PATH, 'csv', 'aggregations', 'recalls_per_type_per_manufacturer.csv'), agg_type_manufacturer)
