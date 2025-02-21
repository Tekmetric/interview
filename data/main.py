# This is the entry point of the script.
from loader import Parquet
from transformer import Standard
from extractor import NasaApi
from config import Config
import asyncio

def get_nested_value(nested:dict, path:str) -> any:
    """
    Get value from nested dict based on a path.
    """
    keys = path.split('.')
    value = nested
    for key in keys:
        if not isinstance(value, dict):
            return None
        value = value.get(key)
        if not value:
            return None
    return value

config = Config()

async def main():
    extractor = NasaApi(browse_api_url=config.browse_api_url, api_key=config.api_key)
    transformer = Standard()
    loader = Parquet(config.storage_path)

    raw_data = await extractor.fetch_pages(page=0, page_size=20, limit=100)
    df = transformer.process(raw_data)
    loader.write(df)

    file_data = loader.read()
    print("file_data length: ", len(file_data))

    close_approach_aggregation_rule = config.aggregation_rules['close_approaches']
    threshold = close_approach_aggregation_rule['threshold']


    total_close_approaches = 0
    for approaches in df[close_approach_aggregation_rule['column']]:
        if approaches is None:
            continue
        for approach in approaches:
            if float(get_nested_value(approach, close_approach_aggregation_rule['path'])) < threshold:
                total_close_approaches += 1
    print(f"Aggregation: Total number of times neos approached closer than {threshold} astronomical: {total_close_approaches}")

    # count approaches per year
    yearly_counts = {}
    yearly_aggregation_rule = config.aggregation_rules['yearly_counts']
    i =0 
    for approaches in df[yearly_aggregation_rule['column']]:
        i += 1
        if approaches is None:
            continue
        for approach in approaches:
            year = get_nested_value(approach, yearly_aggregation_rule['path'])
            if year:
                yearly_counts[year] = yearly_counts.get(year, 0) + 1
    
    print("\nAggregation: Number of approaches per year:")
    for year in sorted(yearly_counts.keys()):
        print(f"{year}: {yearly_counts[year]}")

    df = loader.read()
    print("Available columns:", df.columns.tolist())

if __name__ == "__main__":
    asyncio.run(main())
