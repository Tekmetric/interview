from data_fetcher import fetch_data
from data_processor import process_data
from data_aggregator import save_parquet_files, aggregate_data

def main():
    print("Fetching Data")
    data = fetch_data()
    if data:
        df = process_data(data)
        save_parquet_files(df)
        aggregate_data(df)
        print("Processing Completed")

if __name__ == '__main__':
    main()
