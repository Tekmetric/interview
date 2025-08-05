import asyncio
from collections import Counter

import pyarrow as pa

from _neo.client.models import NearEarthObject
from _neo.client.nasa_client import guard_semaphore
from _neo.logger import profile

NEO_SCHEMA = pa.schema(
    [
        ("id", pa.string()),
        ("neo_reference_id", pa.string()),
        ("name", pa.string()),
        ("name_limited", pa.string()),
        ("designation", pa.string()),
        ("nasa_jpl_url", pa.string()),
        ("absolute_magnitude_h", pa.float64()),
        ("is_potentially_hazardous_asteroid", pa.bool_()),
        ("observations_used", pa.int64()),
        ("orbital_period", pa.float64()),
        ("estimated_diameter_min_m", pa.float64()),
        ("estimated_diameter_max_m", pa.float64()),
        ("closest_approach_miss_distance", pa.string()),
        ("closest_approach_date", pa.string()),
        ("closest_relative_velocity_kps", pa.string()),
    ]
)


async def _process_neo(neo: NearEarthObject) -> dict:
    """
    Process a single NearEarthObject model into a dataframe row.

    Args:
        neo: NearEarthObject Pydantic model instance.

    Returns:
        A row with selected and computed NEO fields.
    """
    row = neo.model_dump(exclude={"estimated_diameter", "close_approach_data"})
    row.update(
        {
            "estimated_diameter_min_m": neo.estimated_diameter.meters.estimated_diameter_min,
            "estimated_diameter_max_m": neo.estimated_diameter.meters.estimated_diameter_max,
            "closest_approach_miss_distance": neo.closest_approach_miss_distance,
            "closest_approach_date": neo.closest_approach_date,
            "closest_relative_velocity_kps": neo.closest_relative_velocity_kps,
        }
    )
    return row


@profile
async def process_neos(neos: list[NearEarthObject], max_concurrency: int = 10) -> pa.Table:
    """
    Concurrently process a list of NearEarthObject models into a dataframe.

    Args:
        neos: List of NearEarthObject Pydantic model instances.
        max_concurrency: Maximum number of concurrent processing tasks.

    Returns:
        A PyArrow Table containing the processed NEO data.
    """
    semaphore = asyncio.Semaphore(max_concurrency)

    rows = await asyncio.gather(
        *guard_semaphore(
            [_process_neo(neo) for neo in neos],
            semaphore,
        )
    )
    return pa.Table.from_pylist(rows, schema=NEO_SCHEMA)


@profile
def count_close_approaches(neos: list[NearEarthObject], threshold_au: float = 0.2) -> int:
    """Count the number of close approaches below a given threshold.

    Args:
        neos: A list of NEO objects.
        threshold_au: The distance threshold in astronomical units (AU).

    Returns:
        The count of close approaches below the threshold.
    """
    return sum(
        1
        for neo in neos
        for approach in neo.close_approach_data
        if float(approach.miss_distance.astronomical) < threshold_au
    )


@profile
def count_close_approaches_per_year(neos: list[NearEarthObject]) -> dict[int, int]:
    """Count the number of close approaches recorded in each year.

    Args:
        neos: List of NEO objects.

    Returns:
        A dictionary mapping year to count of close approaches in that year.
    """
    years = (int(approach.close_approach_date[:4]) for neo in neos for approach in neo.close_approach_data)
    return dict(Counter(years))
