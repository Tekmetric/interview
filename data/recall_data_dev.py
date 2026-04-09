"""
NASA NeoWs ETL script — Polars (dev) variant.

Fetches Near Earth Objects from the NeoWs Browse API, writes raw records
as a single Parquet file, and computes two close-approach aggregations.

Lighter-weight alternative to recall_data.py: replaces PySpark with Polars,
eliminating JVM startup overhead. Intended for local development against
200–500 records. For cluster/production use, see recall_data.py.

Usage:
    uv run python recall_data_dev.py [--count N]
"""

import os

import polars as pl
from dotenv import load_dotenv

from neo_ingest import get_api_key, ingest, parse_args

# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------


def main() -> None:
    load_dotenv()
    args = parse_args("NASA NeoWs ETL (dev)")

    api_key, count = get_api_key(args.count)
    neo_records, all_approaches = ingest(count, api_key)
    print(f"Fetched {len(neo_records)} NEO records, {len(all_approaches)} close approaches.")

    os.makedirs("neo/raw", exist_ok=True)
    os.makedirs("neo/aggregations", exist_ok=True)

    # --- Raw NEO records ---
    neo_df = pl.DataFrame(neo_records)
    neo_df.write_parquet("neo/raw/neo_data.parquet")
    print(f"Wrote raw NEO data → neo/raw/neo_data.parquet ({neo_df.shape[0]} rows)")

    # --- Aggregations ---
    approaches_df = pl.DataFrame(all_approaches)

    # 1. Close approaches under 0.2 AU
    under_02 = approaches_df.filter(pl.col("miss_distance_au") < 0.2).height
    agg1_df = pl.DataFrame([{"total_close_approaches_under_0_2_au": under_02}])
    agg1_df.write_parquet("neo/aggregations/close_approaches_under_0_2_au.parquet")
    print(f"Agg 1: {under_02} close approaches under 0.2 AU")

    # 2. Close approaches per year
    agg2_df = (
        approaches_df
        .with_columns(
            pl.col("close_approach_date").str.slice(0, 4).cast(pl.Int32).alias("year")
        )
        .group_by("year")
        .agg(pl.len().alias("close_approach_count"))
        .sort("year")
    )
    agg2_df.write_parquet("neo/aggregations/close_approaches_per_year.parquet")
    print("Agg 2: close approaches per year written → neo/aggregations/close_approaches_per_year.parquet")


if __name__ == "__main__":
    main()
