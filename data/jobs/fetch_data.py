import sys
sys.path.append("..")
from modules.data_fetcher import NEODataFetcher
import os 
from dotenv import load_dotenv
load_dotenv()

def main():
    params ={
        'json_file_path' : os.getenv('JSON_FILE_NAME'),
        'sql_path' : os.getenv('SQL_ALL_DATA'),
        'parquet_file_path' : os.getenv('PARQUET_FILE_PATH_FLAT')
    }

    fetcher = NEODataFetcher()
    fetcher.fetch_data(10, params['json_file_path'])
    fetcher.flatten_json(params['sql_path'], params['json_file_path'], params['parquet_file_path'])
    fetcher.stop()

if __name__=="__main__":
    main()