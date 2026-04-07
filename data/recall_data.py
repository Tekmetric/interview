"""
NASA NeoWs ETL script — Spark (production/cluster) variant.

Fetches Near Earth Objects from the NeoWs Browse API, writes raw records
as partitioned Parquet, and computes two close-approach aggregations.

For local development against small record counts, see recall_data_dev.py.

Usage:
    uv run python recall_data.py [--count N]
"""

import os

from dotenv import load_dotenv
from pyspark.sql import SparkSession
from pyspark.sql.functions import col, to_date, year

from neo_ingest import ingest, parse_args

# ---------------------------------------------------------------------------
# Constants
# ---------------------------------------------------------------------------

ROWS_PER_PARTITION = 250_000

# ---------------------------------------------------------------------------
# Spark
# ---------------------------------------------------------------------------


def build_spark_session() -> SparkSession:
    master = os.environ.get("SPARK_MASTER", "local[*]")
    return (
        SparkSession.builder
        .master(master)
        .appName("neo-etl")
        .getOrCreate()
    )


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------


def main() -> None:
    load_dotenv()
    args = parse_args("NASA NeoWs ETL (Spark)")

    api_key = os.environ.get("NASA_API_KEY")
    if not api_key:
        raise ValueError("NASA_API_KEY is not set. Add it to .env or export it.")

    print(f"Fetching {args.count} NEOs...")
    neo_records, all_approaches = ingest(args.count, api_key)
    print(f"Fetched {len(neo_records)} NEO records, {len(all_approaches)} close approaches.")

    spark = build_spark_session()

    # --- Raw NEO records ---
    from datetime import datetime, timezone
    now = datetime.now(timezone.utc)

    neo_df = spark.createDataFrame(neo_records)
    neo_df = neo_df.withColumn("year", col("_processed_at").substr(1, 4)) \
                   .withColumn("month", col("_processed_at").substr(6, 2))

    num_partitions = max(1, len(neo_records) // ROWS_PER_PARTITION)
    (
        neo_df.coalesce(num_partitions)
        .write.mode("overwrite")
        .partitionBy("year", "month")
        .parquet("neo/raw/neo_data.parquet")
    )
    print(f"Wrote raw NEO data → neo/raw/neo_data.parquet (year={now.year}/month={now.month:02d})")

    # --- Aggregations ---
    approaches_df = spark.createDataFrame(all_approaches)

    # 1. Close approaches under 0.2 AU
    under_02 = approaches_df.filter(col("miss_distance_au") < 0.2).count()
    agg1_df = spark.createDataFrame([{"total_close_approaches_under_0_2_au": under_02}])
    agg1_df.coalesce(1).write.mode("overwrite").parquet("neo/aggregations/close_approaches_under_0_2_au.parquet")
    print(f"Agg 1: {under_02} close approaches under 0.2 AU")

    # 2. Close approaches per year
    agg2_df = (
        approaches_df
        .withColumn("approach_year", year(to_date(col("close_approach_date"))))
        .groupBy("approach_year")
        .count()
        .withColumnRenamed("approach_year", "year")
        .withColumnRenamed("count", "close_approach_count")
        .orderBy("year")
    )
    agg2_df.coalesce(1).write.mode("overwrite").parquet("neo/aggregations/close_approaches_per_year.parquet")
    print("Agg 2: close approaches per year written → neo/aggregations/close_approaches_per_year.parquet")

    spark.stop()


if __name__ == "__main__":
    main()
