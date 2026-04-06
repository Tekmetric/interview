"""
NASA NeoWs ETL script — dev variant (Polars).

Fetches Near Earth Objects from the NeoWs Browse API, writes raw records
as a single Parquet file, and computes two close-approach aggregations.

Lighter-weight alternative to recall_data.py: replaces PySpark with Polars,
eliminating JVM startup overhead. Intended for local development against
200–500 records. For cluster/production use, see recall_data.py.

Usage:
    uv run python recall_data_dev.py [--count N]
"""

import argparse
import asyncio
import math
import os
from datetime import datetime, timezone

import aiohttp
import polars as pl
import requests
from dotenv import load_dotenv

# ---------------------------------------------------------------------------
# Constants
# ---------------------------------------------------------------------------

API_BASE = "https://api.nasa.gov/neo/rest/v1/neo/browse"
PAGE_SIZE = 20
ASYNC_SEMAPHORE = 10

# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="NASA NeoWs ETL (dev)")
    parser.add_argument(
        "--count",
        type=int,
        default=int(os.environ.get("NEO_COUNT", 200)),
        help="Number of NEOs to fetch (default: 200)",
    )
    return parser.parse_args()


# ---------------------------------------------------------------------------
# Extraction helpers
# ---------------------------------------------------------------------------


def extract_neo_record(neo: dict, processed_at: str) -> dict:
    diameter = neo.get("estimated_diameter", {}).get("meters", {})

    closest = min(
        neo.get("close_approach_data", []),
        key=lambda a: float(a["miss_distance"]["kilometers"]),
        default=None,
    )

    orbital = neo.get("orbital_data", {})

    return {
        "id": neo.get("id"),
        "neo_reference_id": neo.get("neo_reference_id"),
        "name": neo.get("name"),
        "name_limited": neo.get("name_limited"),
        "designation": neo.get("designation"),
        "nasa_jpl_url": neo.get("nasa_jpl_url"),
        "absolute_magnitude_h": neo.get("absolute_magnitude_h"),
        "is_potentially_hazardous_asteroid": neo.get("is_potentially_hazardous_asteroid"),
        "min_estimated_diameter_m": diameter.get("estimated_diameter_min"),
        "max_estimated_diameter_m": diameter.get("estimated_diameter_max"),
        "closest_miss_distance_km": float(closest["miss_distance"]["kilometers"]) if closest else None,
        "closest_approach_date": closest["close_approach_date"] if closest else None,
        "closest_approach_velocity_kps": float(closest["relative_velocity"]["kilometers_per_second"]) if closest else None,
        "first_observation_date": orbital.get("first_observation_date"),
        "last_observation_date": orbital.get("last_observation_date"),
        "observations_used": orbital.get("observations_used"),
        "orbital_period": orbital.get("orbital_period"),
        "_processed_at": processed_at,
    }


def extract_close_approaches(neo: dict) -> list[dict]:
    return [
        {
            "close_approach_date": a["close_approach_date"],
            "miss_distance_au": float(a["miss_distance"]["astronomical"]),
            "miss_distance_km": float(a["miss_distance"]["kilometers"]),
        }
        for a in neo.get("close_approach_data", [])
    ]


# ---------------------------------------------------------------------------
# Ingestion — sync path (count <= 500)
# ---------------------------------------------------------------------------


def fetch_page_sync(session: requests.Session, page: int, size: int, api_key: str) -> list[dict]:
    resp = session.get(API_BASE, params={"page": page, "size": size, "api_key": api_key})
    resp.raise_for_status()
    return resp.json().get("near_earth_objects", [])


def _ingest_sync(count: int, api_key: str) -> tuple[list[dict], list[dict]]:
    neo_records, all_approaches = [], []
    processed_at = datetime.now(timezone.utc).isoformat()
    pages = math.ceil(count / PAGE_SIZE)

    with requests.Session() as session:
        fetched = 0
        for page in range(pages):
            neos = fetch_page_sync(session, page, PAGE_SIZE, api_key)
            for neo in neos:
                if fetched >= count:
                    break
                neo_records.append(extract_neo_record(neo, processed_at))
                all_approaches.extend(extract_close_approaches(neo))
                fetched += 1

    return neo_records, all_approaches


# ---------------------------------------------------------------------------
# Ingestion — async path (count > 500)
# ---------------------------------------------------------------------------


async def fetch_page_async(
    session: aiohttp.ClientSession,
    semaphore: asyncio.Semaphore,
    page: int,
    size: int,
    api_key: str,
) -> list[dict]:
    async with semaphore:
        async with session.get(
            API_BASE, params={"page": page, "size": size, "api_key": api_key}
        ) as resp:
            resp.raise_for_status()
            data = await resp.json()
            return data.get("near_earth_objects", [])


async def _async_ingest(count: int, api_key: str) -> tuple[list[dict], list[dict]]:
    pages = math.ceil(count / PAGE_SIZE)
    semaphore = asyncio.Semaphore(ASYNC_SEMAPHORE)
    processed_at = datetime.now(timezone.utc).isoformat()

    async with aiohttp.ClientSession() as session:
        coroutines = [
            fetch_page_async(session, semaphore, page, PAGE_SIZE, api_key)
            for page in range(pages)
        ]
        results = await asyncio.gather(*coroutines)

    neo_records, all_approaches = [], []
    fetched = 0
    for page_neos in results:
        for neo in page_neos:
            if fetched >= count:
                break
            neo_records.append(extract_neo_record(neo, processed_at))
            all_approaches.extend(extract_close_approaches(neo))
            fetched += 1
        if fetched >= count:
            break

    return neo_records, all_approaches


# ---------------------------------------------------------------------------
# Ingestion dispatcher
# ---------------------------------------------------------------------------


def ingest(count: int, api_key: str) -> tuple[list[dict], list[dict]]:
    "Utility function to choose sync vs async ingestion based on count. Returns (neo_records, all_approaches)."
    if count <= 500:
        return _ingest_sync(count, api_key)
    return asyncio.run(_async_ingest(count, api_key))


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------


def main() -> None:
    load_dotenv()
    args = parse_args()

    api_key = os.environ.get("NASA_API_KEY")
    if not api_key:
        raise ValueError("NASA_API_KEY is not set. Add it to .env or export it.")

    print(f"Fetching {args.count} NEOs...")
    neo_records, all_approaches = ingest(args.count, api_key)
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
