import sys
sys.path.append("..")
from modules.data_aggregator import NEODataAggregator
import os 
from dotenv import load_dotenv
load_dotenv()

def main():
    params = {
        'source_parquet_path' : os.getenv('PARQUET_FILE_PATH_FLAT'),
        'closest_data_sql' : os.getenv('SQL_CLOSEST_DATA'),
        'closest_data_parquet_path' : os.getenv('CLOSEST_DATA_PARQUET_PATH'),
        'closer_approach_sql' : os.getenv('CLOSER_APPROACH_SQL'),
        'approach_count_year_sql' : os.getenv('APPROACH_COUNT_YEAR_SQL'),
        'closer_approach_parquet_path' : os.getenv('CLOSER_APPROACH_PARQUET_PATH'),
        'approach_count_year_parquet_path' : os.getenv('APPROACH_COUNT_YEAR_PARQUET_PATH')
        }
    

    aggregator = NEODataAggregator(params['source_parquet_path'])
    # 1. data with closest approach miss distance in kilometers
    aggregator.process_and_save_aggregations(params['closest_data_sql'],params['closest_data_parquet_path'])
    # 2. The total number of times our 200 near earth objects approached closer 
    aggregator.process_and_save_aggregations(params['closer_approach_sql'],params['closer_approach_parquet_path'] )
    # 3. The number of close approaches recorded in each year present in the data
    aggregator.process_and_save_aggregations(params['approach_count_year_sql'],params['approach_count_year_parquet_path'] )
    aggregator.stop()

if __name__ == '__main__':
    main()