import json
import uuid
from datetime import date
from pathlib import Path

import fsspec
import pyarrow.parquet as pq

from _neo.client import NasaClient
from _neo.logger import logger
from _neo.process import count_close_approaches, count_close_approaches_per_year, process_neos


async def run(limit: int, threshold_au: float, output_dir: Path):
    """Run NEO workflow."""
    logger.info(f"Fetching {limit} Near Earth Object(s)")
    async with NasaClient() as client:
        neos = await client.neo.list_entries(limit)

    logger.info(f"Processing {len(neos)} Near Earth Object(s)")
    processed_neos = await process_neos(neos, max_concurrency=10)

    partition_dir = (
        output_dir / f"neos/year={date.today().year}/month={date.today().month:02d}/day={date.today().day:02d}"
    )
    partition_dir.mkdir(parents=True, exist_ok=True)

    neos_filepath = partition_dir / f"neos-{uuid.uuid4().hex}.parquet"
    logger.info(f"Writing processed NEO data to {neos_filepath}")
    with fsspec.open(neos_filepath, "wb") as f:
        pq.write_table(processed_neos, f)

    close_approaches = count_close_approaches(neos, threshold_au=threshold_au)
    logger.info(
        f"Found {close_approaches} times when our {limit} Near Earth Object(s) "
        f"approached closer than {threshold_au} astronomical units"
    )

    close_approaches_per_year = count_close_approaches_per_year(neos)

    aggregations_filepath = partition_dir / f"aggregations-{uuid.uuid4().hex}.json"
    logger.info(f"Writing aggregations to {aggregations_filepath}")
    with fsspec.open(aggregations_filepath, "wt") as f:
        json.dump(
            {"total_close_approaches": close_approaches, "close_approaches_per_year": close_approaches_per_year}, f
        )

    years = len(close_approaches_per_year)
    approaches = sum(close_approaches_per_year.values())
    logger.info(f"📊 Summary: {approaches} approaches across {years} years (avg: {(approaches / years):.1f} per year)")

    logger.info("Top years with most approaches:")
    for year, count in sorted(close_approaches_per_year.items(), key=lambda x: x[1], reverse=True)[:10]:
        logger.info(f"   {year}: {count} approaches")
