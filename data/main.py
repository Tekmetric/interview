# This is the entry point of the script.
from loader import Parquet
from transformer import Standard
from extractor import NasaApi
from config import Config
import asyncio

config = Config()

async def main():
    extractor = NasaApi(browse_api_url=config.browse_api_url, api_key=config.api_key)
    transformer = Standard()
    loader = Parquet(config.storage_path)

    raw_data = await extractor.fetch_pages(page=0, page_size=20, limit=200)
    processed_data = transformer.process(raw_data)
    loader.write(processed_data)

    file_data = loader.read()
    print("file_data length: ", len(file_data))

if __name__ == "__main__":
    asyncio.run(main())

