# This is the entry point of the script.
import time

import pandas as pd
from data import Pages
from loader import Loader, LoaderParquet, LoaderJSON
from transformer import Standard, Transformer
from extractor import Extractor, NasaApi
from config import Config
import asyncio

config = Config.load()


async def extract(extractor: Extractor) -> Pages:
    # fetch first 200 neos
    limit = 10
    page_size = 20
    start_page = 0
    print(f"fetching {limit} pages with size {page_size} starting from page {start_page} ...")

    start = time.time()
    pages_data = await extractor.fetch_pages(page=start_page, page_size=page_size, limit=limit)
    print(f"fetch took: {time.time() - start}")

    return pages_data


def transform(transformer: Transformer, data: Pages) -> pd.DataFrame:
    start = time.time()
    print(f"processing pages data ...")
    processed_data = transformer.process(
        raw_data=data,
    )
    print(f"process took: {time.time() - start}")
    return processed_data


def write_raw_data(loader_json: Loader, pages: Pages): 
    loader_json.write(
        data=pages.to_bytes(),
        path=config.loader.storage_path_raw,
        filename="data"
    )


def write_processed_data(
        loader_parquet: Loader,
        processed: pd.DataFrame,
        aggregations: list[pd.DataFrame]
):  
    # write cleaned data
    loader_parquet.write(
        data=processed.to_parquet(),
        path=config.loader.storage_path_processed,
        filename="data",
    )
    # write aggregations
    for aggr in aggregations:
        # aggregations df should have `aggregation_name` field set
        if not hasattr(aggr, "aggregation_name") or not isinstance(aggr.aggregation_name, str):
            continue
        aggr_name = aggr.aggregation_name
        loader_parquet.write(
            data=aggr.to_parquet(),
            path=config.loader.storage_path_aggregations,
            filename=aggr_name,
        )


def show_stats(loader_json: Loader, loader_parquet: Loader, config: Config):
    json_data = loader_json.read(
        path=config.loader.storage_path_raw,
        filename="data",
    )
    print("neos pages in json file: ", len(json_data))

    pages_data = loader_parquet.read(
        path=config.loader.storage_path_processed,
        filename="data",
    )
    print("neos count in parquet file: ", len(pages_data))


def clean_data(transformer: Transformer, df: pd.DataFrame, config: Config) -> pd.DataFrame:
    start = time.time()
    print(f"cleaning  ...")
    cleaned_data = transformer.clean(
        df=df, 
        columns_to_keep=config.transformer.columns_to_return,
    )
    print(f"clean took: {time.time() - start}")
    return cleaned_data


def compute_aggregations(transformer: Transformer, df: pd.DataFrame) -> list[pd.DataFrame]:
    start = time.time()
    print(f"computing aggregations  ...")
    total_approaches_under_threshold, approach_yearly_counts = transformer.compute_aggregations(df)
    aggregations = [total_approaches_under_threshold, approach_yearly_counts]
    print(f"computing aggregations took: {time.time() - start}")
    return aggregations


def init() -> tuple[Extractor, Transformer, Loader, Loader]:
    extractor = NasaApi(
        browse_api_url=config.extractor.url,
        api_key=config.extractor.api_key,
    )
    transformer = Standard(
        aggregation_rules=config.transformer.aggregations_rules
    )
    loader_json = LoaderJSON()
    loader_parquet = LoaderParquet()
    return extractor, transformer, loader_json, loader_parquet


async def main():
    [extractor, transformer, loader_json, loader_parquet]= init()

    pages_data = await extract(extractor)

    write_raw_data(loader_json=loader_json, pages=pages_data)

    processed_data = transform(transformer, pages_data)
    if processed_data.empty:
        print("empty dataframe")
        return

    aggregations = compute_aggregations(
        transformer=transformer,
        df=processed_data
    )
    
    cleaned_data = clean_data(
        transformer=transformer,
        df=processed_data, 
        config=config
    )

    write_processed_data(
        loader_parquet=loader_parquet,
        processed=cleaned_data,
        aggregations=aggregations
    )

    show_stats(
        loader_json=loader_json,
        loader_parquet=loader_parquet,
        config=config
    )


if __name__ == "__main__":
    asyncio.run(main())
