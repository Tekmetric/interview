from pathlib import Path
from typing import Optional

import pyarrow as pa
import pyarrow.parquet as pq

from data.config import logger, OUTPUT_DIR, START_PAGE, END_PAGE, PAGES_PER_BATCH
from data.nasa_client import NASAClient
from data.process import CloseApproachMetrics, process_neo_responses
from data.schemas import neo_schema, yearly_count_schema, totals_schema


def collect_data(client: NASAClient, start: int = 0, end: Optional[int] = None):
    logger.info(f"fetching_pages start={start} end={end}")
    for page in range(start, end + 1):
        result = client.neo.browse(page=page)
        yield result


def collect_and_process_data_in_batches(
    nasa_client: NASAClient,
    start_page,
    end_page,
    pages_per_batch,
):
    for batch_start_page in range(start_page, end_page + 1, pages_per_batch):
        batch_end_page = min(end_page + 1, batch_start_page + pages_per_batch) - 1
        neo_responses = collect_data(
            nasa_client, start=batch_start_page, end=batch_end_page
        )
        records, metrics = process_neo_responses(neo_responses)
        yield records, metrics


def _write_metrics(metrics: CloseApproachMetrics, output_dir: Path):
    logger.info(f"writing_metrics")
    pq.write_table(
        pa.Table.from_pylist(
            list(metrics.yearly_approaches.items()), schema=yearly_count_schema
        ),
        output_dir / "yearly_approaches.parquet",
    )
    pq.write_table(
        pa.Table.from_pylist(
            [[metrics.near_miss_approaches_count]], schema=totals_schema
        ),
        output_dir / "near_miss_approach_count.parquet",
    )


def main():
    nasa_client = NASAClient.from_env()

    # write neo records in batches and aggregate metrics
    metrics = CloseApproachMetrics()
    with pq.ParquetWriter(
        OUTPUT_DIR / "neo.parquet", schema=neo_schema, use_dictionary=True
    ) as writer:
        for batch_records, batch_metrics in collect_and_process_data_in_batches(
            nasa_client, START_PAGE, END_PAGE, PAGES_PER_BATCH
        ):
            metrics += batch_metrics
            table = pa.Table.from_pylist(batch_records, schema=neo_schema)
            logger.info(f"writing_records rows={len(table)}")
            writer.write_table(table)

    _write_metrics(metrics, OUTPUT_DIR)


if __name__ == "__main__":
    main()
