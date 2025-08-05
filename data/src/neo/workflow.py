import json
import uuid
from datetime import date
from pathlib import Path
from typing import Any

import fsspec
import pyarrow as pa
import pyarrow.parquet as pq

from _neo.client import NasaClient
from _neo.logger import logger
from _neo.process import count_close_approaches, count_close_approaches_per_year, process_neos


async def run(limit: int, threshold_au: float, output_dir: Path):
    """Run NEO workflow."""
    logger.info(f"Fetching {limit} Near Earth Object(s)")

    close_approaches = 0
    close_approaches_per_year: dict[int, Any] = {}

    tables: list[pa.Table] = []

    async with NasaClient() as client:
        async for batch in client.neo.list_entries_batch(limit=limit):
            logger.info(f"Processing batch of {len(batch)} Near Earth Object(s)")

            table = await process_neos(batch, max_concurrency=10)
            tables.append(table)

            close_approaches += count_close_approaches(batch, threshold_au=threshold_au)
            for year, count in count_close_approaches_per_year(batch).items():
                close_approaches_per_year[year] = close_approaches_per_year.get(year, 0) + count

    logger.info(
        f"Found {close_approaches} times when our {limit} Near Earth Object(s) "
        f"approached closer than {threshold_au} astronomical units"
    )

    partition_dir = (
        output_dir / f"neos/year={date.today().year}/month={date.today().month:02d}/day={date.today().day:02d}"
    )
    partition_dir.mkdir(parents=True, exist_ok=True)

    neos_filepath = partition_dir / f"neos-{uuid.uuid4().hex}.parquet"
    logger.debug(f"Writing NEO(s) to {neos_filepath}")
    with fsspec.open(neos_filepath, "wb") as f:
        pq.write_table(pa.concat_tables(tables), f)

    aggregations_filepath = partition_dir / f"aggregations-{uuid.uuid4().hex}.json"
    logger.debug(f"Writing aggregations to {aggregations_filepath}")
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
