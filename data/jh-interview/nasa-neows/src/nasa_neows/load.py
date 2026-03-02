"""Load — fetch NEOs from the API and write raw data to Parquet via DuckDB."""

import logging

import duckdb
import pyarrow as pa

from nasa_neows.config import RAW_DIR
from nasa_neows.fetch import fetch_neos

logger = logging.getLogger(__name__)


def extract_and_load() -> None:
    """Fetch NEOs from the API and load raw data into Parquet."""
    with duckdb.connect() as con:
        for i, raw_batch in enumerate(fetch_neos(total=200, page_size=20)):
            con.register("batch", pa.Table.from_pylist(raw_batch))

            if i == 0:
                con.execute("CREATE TABLE raw AS SELECT * FROM batch")
            else:
                con.execute("INSERT INTO raw BY NAME SELECT * FROM batch")

        con.execute(f"COPY raw TO '{RAW_DIR / 'neos.parquet'}' (FORMAT PARQUET)")

    logger.info("Wrote raw data to %s", RAW_DIR / "neos.parquet")
