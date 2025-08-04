import asyncio
import math
import os
from collections import Counter

import aiohttp
import pyarrow as pa
from tenacity import retry, retry_if_exception_type, stop_after_attempt, wait_exponential

NEO_BASE_URL = "https://api.nasa.gov/neo/rest/v1/neo/browse"
"""Base URL for NASA NEO API"""

PAGE_SIZE = 20
"""Number of results per page"""


class NeosNotFoundError(Exception):
    """Custom exception for when no Near Earth Objects are found."""


@retry(
    stop=stop_after_attempt(3),
    wait=wait_exponential(multiplier=1, min=2, max=10),
    retry=retry_if_exception_type((aiohttp.ClientError, asyncio.TimeoutError)),
    reraise=True,
)
async def _fetch_neo(session: aiohttp.ClientSession, page: int) -> list[dict]:
    """Fetch NEO data from the NASA API.

    Args:
        session: The HTTP session to use.
        page: The page number to fetch.

    Returns:
        A list of NEO data dictionaries.
    """
    params = {"api_key": os.getenv("NASA_API_KEY"), "page": page, "size": PAGE_SIZE}
    async with session.get(NEO_BASE_URL, params=params) as response:
        response.raise_for_status()
        data = await response.json()
        return data.get("near_earth_objects", [])


async def fetch_neos(limit: int = 200) -> list[dict]:
    """Fetch multiple pages of NEO data from the NASA API.

    Args:
        limit: The maximum number of NEOs to fetch.

    Returns:
        A list of NEO data dictionaries.
    """
    if not os.getenv("NASA_API_KEY"):
        raise EnvironmentError("NASA_API_KEY environment variable is required")

    async with aiohttp.ClientSession() as session:
        tasks = [_fetch_neo(session, page) for page in range(math.ceil(limit / PAGE_SIZE))]
        pages = await asyncio.gather(*tasks)

        neos = [neo for page in pages for neo in page]
        if not neos:
            raise NeosNotFoundError("No Near Earth Objects found")

        return neos[:limit]


async def _process_neo(neo: dict) -> dict:
    """Process a single NEO dictionary."""
    closest_approach_data = min(
        neo["close_approach_data"], key=lambda x: float(x["miss_distance"]["kilometers"]), default=None
    )
    return {
        "id": neo.get("id"),
        "neo_reference_id": neo.get("neo_reference_id"),
        "name": neo.get("name"),
        "name_limited": neo.get("name_limited"),
        "designation": neo.get("designation"),
        "nasa_jpl_url": neo.get("nasa_jpl_url"),
        "absolute_magnitude_h": neo.get("absolute_magnitude_h"),
        "is_potentially_hazardous_asteroid": neo.get("is_potentially_hazardous_asteroid"),
        "estimated_diameter_min_m": (diameters := neo["estimated_diameter"]["meters"]).get(
            "estimated_diameter_min"
        ),
        "estimated_diameter_max_m": diameters.get("estimated_diameter_max"),
        "closest_approach_miss_distance": (
            closest_approach_data["miss_distance"]["kilometers"] if closest_approach_data else None
        ),
        "closest_approach_date": closest_approach_data["close_approach_date"] if closest_approach_data else None,
        "closest_relative_velocity_kps": (
            closest_approach_data["relative_velocity"]["kilometers_per_second"] if closest_approach_data else None
        ),
        "first_observation_date": (orbital_data := neo.get("orbital_data", {})).get("first_observation_date"),
        "last_observation_date": orbital_data.get("last_observation_date"),
        "observations_used": orbital_data.get("observations_used", 0),
        "orbital_period": orbital_data.get("orbital_period", 0.0),
    }

async def process_neos(neos: list[dict], max_concurrency: int = 10) -> pa.Table:
    """
    Process a list of NEOs concurrently and convert them into a PyArrow Table.

    Args:
        neos: List of NEO dictionaries to process.
        max_concurrency: Maximum number of concurrent tasks.

    Returns:
        A PyArrow Table containing the processed NEO data.
    """
    semaphore = asyncio.Semaphore(max_concurrency)
    rows: list[dict] = []

    async def guarded_semaphore(neo: dict) -> dict:
        async with semaphore:
            return await _process_neo(neo)

    rows = await asyncio.gather(*[guarded_semaphore(neo) for neo in neos])
    return pa.Table.from_pylist(rows)


def count_close_approaches(neos: list[dict], threshold_au: float = 0.2) -> int:
    """Count the number of close approaches below a given threshold.

    Args:
        neos: A list of NEO data dictionaries.
        threshold_au: The distance threshold in astronomical units (AU).

    Returns:
        The count of close approaches below the threshold.
    """
    return sum(
        1
        for neo in neos
        for approach in neo.get("close_approach_data", [])
        if float(approach["miss_distance"].get("astronomical", float("inf"))) < threshold_au
    )


def count_close_approaches_per_year(neos: list[dict]) -> dict[int, int]:
    """Count the number of close approaches recorded in each year.

    Args:
        neos: List of NEO data dictionaries.

    Returns:
        A dictionary mapping year to count of close approaches in that year.
    """
    years = (
        int(approach["close_approach_date"][:4])
        for neo in neos
        for approach in neo.get("close_approach_data", [])
        if "close_approach_date" in approach
    )
    return dict(Counter(years))
