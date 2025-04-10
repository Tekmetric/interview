import config

from neo.ingesters.sync_ingester import SyncDataIngester
from neo.handlers.pandas_handler import PandasDataHandler
from neo.storage.local_storage import LocalStorage
from neo.logger import logger


def main():
    logger.info("Fetching NEO data...")

    ingester = SyncDataIngester(api_key=config.API_KEY)
    handler = PandasDataHandler()
    storage = LocalStorage(config.BASE_PATH)
    data = ingester.fetch_objects()

    logger.info("Parsing data...")

    data_df = handler.to_dataframe(data)
    if data_df.empty:
        logger.warning(
            "The DataFrame is empty. Data might be missing or not in correct format."
        )
        return

    storage_df = handler.prepare(data_df)
    storage.save(storage_df, "last_200_neo_data.parquet")

    logger.info("Computing aggregations...")
    aggregations = handler.run_aggregations(data_df)
    for aggregation_name, aggregation_df in aggregations.items():
        storage.save(aggregation_df, f"{aggregation_name}.parquet")


if __name__ == "__main__":
    main()
