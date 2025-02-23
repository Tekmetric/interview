# This is the entry point of the script.
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

    raw_data = await extractor.fetch_pages(page=0, page_size=2, limit=2)
    [raw_data, total_approaches_under_threshold, approach_yearly_counts]  = transformer.process(
        raw_data=raw_data,
        columns_to_keep=config.transformer.columns_to_return,
    )

    if raw_data.empty:
        print("empty dataframe")
        return

    # write raw data
    loader.write(
        data=raw_data,
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
    print("file_data length: ", len(file_data))

    raw_data = loader.read(
        path=config.loader.storage_path_raw,
        filename="raw_data",
    )
    print("Available columns:", raw_data.columns.tolist())

if __name__ == "__main__":
    asyncio.run(main())
