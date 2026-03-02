"""Transform — flatten and aggregate raw NEO data using DuckDB SQL."""

import logging
from pathlib import Path

import duckdb

from nasa_neows.config import AGGREGATIONS_DIR, PROCESSED_DIR, RAW_DIR

logger = logging.getLogger(__name__)

SQL_DIR = Path(__file__).parent / "sql"


def _run_sql(sql_file: Path, source: Path, output: Path) -> None:
    """Execute a SQL template, substituting {source} and {output} placeholders."""
    query = sql_file.read_text().format(source=source, output=output)
    with duckdb.connect() as con:
        con.execute(query)
    logger.info("Wrote %s", output)


def flatten() -> None:
    """Flatten raw (bronze) into the processed (silver) layer."""
    _run_sql(
        SQL_DIR / "flatten_neos.sql",
        RAW_DIR / "neos.parquet",
        PROCESSED_DIR / "neos.parquet",
    )


def aggregate() -> None:
    """Compute aggregations from processed (silver) into the aggregations (gold) layer."""
    processed = PROCESSED_DIR / "neos.parquet"
    _run_sql(
        SQL_DIR / "close_approaches_under_0_2_au.sql",
        processed,
        AGGREGATIONS_DIR / "close_approaches_under_0_2_au.parquet",
    )
    _run_sql(
        SQL_DIR / "approaches_by_year.sql",
        processed,
        AGGREGATIONS_DIR / "approaches_by_year.parquet",
    )
