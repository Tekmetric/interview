# This is the entry point of the script.
import time
from loader import ParquetFileSystem
from transformer import Standard
from extractor import NasaApi
from config import Config
import asyncio

config = Config.load()


async def main():
    extractor = NasaApi(
        browse_api_url=config.extractor.url,
        api_key=config.extractor.api_key,
    )
    transformer = Standard(
        aggregation_rules=config.transformer.aggregations_rules
    )
    loader = ParquetFileSystem()

    # fetch first 200 neos
    limit = 10
    page_size = 20
    start_page = 0
    print(f"fetching {limit} pages with size {page_size} starting from page {start_page} ...")
    start = time.time()
    raw_data = await extractor.fetch_pages(page=start_page, page_size=page_size, limit=limit)
    print(f"fetch took: {time.time() - start}")

    # transform data from neos pages to dataframe
    start = time.time()
    print(f"processing pages data ...")
    processed_data = transformer.process(
        raw_data=raw_data,
    )
    print(f"process took: {time.time() - start}")
    if processed_data.empty:
        print("empty dataframe")
        return

    # compute aggregations
    start = time.time()
    print(f"computing aggregations  ...")
    total_approaches_under_threshold, approach_yearly_counts = transformer.compute_aggregations(processed_data)
    print(f"aggregations took: {time.time() - start}")
    
    # clean data to be stored
    start = time.time()
    storable_data = transformer.clean(
        df=processed_data, 
        columns_to_keep=config.transformer.columns_to_return,
    )
    print(f"clean took: {time.time() - start}")

    # write data
    loader.write(
        data=storable_data,
        path=config.loader.storage_path_raw,
        filename="raw_data",
    )
    # write aggregations
    loader.write(
        data=total_approaches_under_threshold,
        path=config.loader.storage_path_aggregations,
        filename="total_approaches_under_threshold",
    )
    loader.write(
        data=approach_yearly_counts,
        path=config.loader.storage_path_aggregations,
        filename="approach_yearly_counts",
    )

    file_data = loader.read(
        path=config.loader.storage_path_raw,
        filename="raw_data",
    )
    print("neos count in file: ", len(file_data))

    raw_data = loader.read(
        path=config.loader.storage_path_raw,
        filename="raw_data",
    )

if __name__ == "__main__":
    asyncio.run(main())
