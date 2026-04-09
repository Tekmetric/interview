"""
Shared ingestion module for NASA NeoWs ETL.

Provides constants, argument parsing, extraction helpers, and the full
ingestion layer (sync for <=500 objects, async for >500). Imported by
recall_data.py (Spark) and recall_data_dev.py (Polars).
"""

import argparse
import asyncio
import os
from datetime import datetime, timezone

import aiohttp
import requests

# ---------------------------------------------------------------------------
# Constants
# ---------------------------------------------------------------------------

API_BASE = "https://api.nasa.gov/neo/rest/v1/neo/browse"
PAGE_SIZE = 20
ASYNC_SEMAPHORE = 10
DEMO_KEY_MAX_RECORDS = 5

# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------


def get_api_key(count: int) -> tuple[str, int]:
    key = os.environ.get("NASA_API_KEY") or "DEMO_KEY"
    if key == "DEMO_KEY":
        print("WARNING: no NASA_API_KEY set — using DEMO_KEY (rate limited to 30 req/hour)")
        max_count = DEMO_KEY_MAX_RECORDS
        if count > max_count:
            print(f"WARNING: DEMO_KEY caps records to {DEMO_KEY_MAX_RECORDS} — reducing count from {count} to {max_count}")
            count = max_count
    print(f"Fetching {count} NEOs (api_key: {key})")
    return key, count


def parse_args(description: str = "NASA NeoWs ETL") -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=description)
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


def _ingest_sync(count: int, api_key: str) -> tuple[list[dict], list[dict], bool]:
    neo_records, all_approaches = [], []
    processed_at = datetime.now(timezone.utc).isoformat()
    page = 0
    fetched = 0

    with requests.Session() as session:
        while fetched < count:
            neos = fetch_page_sync(session, page, PAGE_SIZE, api_key)
            if not neos:
                return neo_records, all_approaches, True  # API exhausted
            for neo in neos:
                if fetched >= count:
                    break
                neo_records.append(extract_neo_record(neo, processed_at))
                all_approaches.extend(extract_close_approaches(neo))
                fetched += 1
            page += 1

    return neo_records, all_approaches, False


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


async def _async_ingest(count: int, api_key: str) -> tuple[list[dict], list[dict], bool]:
    semaphore = asyncio.Semaphore(ASYNC_SEMAPHORE)
    processed_at = datetime.now(timezone.utc).isoformat()
    neo_records, all_approaches = [], []
    fetched = 0
    page = 0

    async with aiohttp.ClientSession() as session:
        while fetched < count:
            batch = [
                fetch_page_async(session, semaphore, page + i, PAGE_SIZE, api_key)
                for i in range(ASYNC_SEMAPHORE)
            ]
            results = await asyncio.gather(*batch)
            exhausted = False
            for page_neos in results:
                if not page_neos:
                    exhausted = True
                    break
                for neo in page_neos:
                    if fetched >= count:
                        break
                    neo_records.append(extract_neo_record(neo, processed_at))
                    all_approaches.extend(extract_close_approaches(neo))
                    fetched += 1
                if exhausted or fetched >= count:
                    break
            page += ASYNC_SEMAPHORE
            if exhausted:
                return neo_records, all_approaches, True

    return neo_records, all_approaches, False


# ---------------------------------------------------------------------------
# Ingestion dispatcher
# ---------------------------------------------------------------------------


def ingest(count: int, api_key: str) -> tuple[list[dict], list[dict]]:
    """Choose sync vs async ingestion based on count. Returns (neo_records, all_approaches)."""
    if count <= 500:
        neo_records, all_approaches, exhausted = _ingest_sync(count, api_key)
    else:
        neo_records, all_approaches, exhausted = asyncio.run(_async_ingest(count, api_key))

    if exhausted:
        print(f"WARNING: API exhausted after {len(neo_records)} records (requested {count})")
    elif len(neo_records) != count:
        raise ValueError(
            f"Expected {count} NEO records but got {len(neo_records)}. "
            "API returned inconsistent results."
        )

    return neo_records, all_approaches
