from collections import Counter
from dataclasses import dataclass, field
from decimal import Decimal
from typing import Optional, Iterable

from data.config import logger, MISS_THRESHOLD_ASTR

_CLOSEST_MISS_KM_KEY = "closest_approach_miss_distance_kilometers"
_CLOSEST_DATE_KEY = "closest_approach_date"
_CLOSEST_REL_VELOCITY_KM_PER_SEC_KEY = (
    "closest_approach_relative_velocity_kilometers_per_second"
)


def _get_closest_approach(close_approaches: list[dict]) -> Optional[dict]:
    """Get closest approach by minimal collision distance and year."""
    if not close_approaches:
        return None
    return min(
        close_approaches,
        key=lambda x: (
            Decimal(x["miss_distance"]["astronomical"]),
            -int(x["close_approach_date"][:4]),
        ),
    )


def _get_closest_approach_fields(close_approaches: list[dict]) -> dict:
    closest_approach = _get_closest_approach(close_approaches)
    if closest_approach is None:
        return {
            _CLOSEST_MISS_KM_KEY: None,
            _CLOSEST_DATE_KEY: None,
            _CLOSEST_REL_VELOCITY_KM_PER_SEC_KEY: None,
        }

    return {
        _CLOSEST_MISS_KM_KEY: Decimal(closest_approach["miss_distance"]["kilometers"]),
        _CLOSEST_DATE_KEY: closest_approach["close_approach_date"],
        _CLOSEST_REL_VELOCITY_KM_PER_SEC_KEY: Decimal(
            closest_approach["relative_velocity"]["kilometers_per_second"]
        ),
    }


def map_neo_api_entry(entry: dict) -> dict:
    """Map NEO API entry to useful record.

    TODO: Use data structures instead of plain dictionaries
    """

    obj_id = entry["id"]
    logger.debug(f"mapping_entry id={obj_id}")
    est_diameter = entry["estimated_diameter"]["meters"]
    orbital_data = entry["orbital_data"]

    record = {
        "id": obj_id,
        "neo_reference_id": entry["neo_reference_id"],
        "name": entry["name"],
        "name_limited": entry.get("name_limited"),
        "designation": entry["designation"],
        "nasa_jpl_url": entry["nasa_jpl_url"],
        "absolute_magnitude_h": entry["absolute_magnitude_h"],
        "is_potentially_hazardous_asteroid": entry["is_potentially_hazardous_asteroid"],
        "estimated_diameter_min_meters": est_diameter["estimated_diameter_min"],
        "estimated_diameter_max_meters": est_diameter["estimated_diameter_max"],
        # closest approach
        **_get_closest_approach_fields(entry["close_approach_data"]),
        # orbital data
        "first_observation_date": orbital_data["first_observation_date"],
        "last_observation_date": orbital_data["last_observation_date"],
        "observations_used": int(orbital_data["observations_used"]),
        "orbital_period": Decimal(orbital_data["orbital_period"]),
    }
    logger.debug(f"mapped_record record={record}")
    return record


@dataclass
class CloseApproachMetrics:
    """Defines metrics to extract on top of NEO objects.

    yearly_encounters: Number of close approaches per year
    total_under_threshold: Total close approaches under
    """

    miss_threshold_astr: float = MISS_THRESHOLD_ASTR
    yearly_approaches: Counter = field(default_factory=Counter)
    near_miss_approaches_count: int = 0

    @classmethod
    def fill_from_api_data(cls, close_approaches: list[dict]) -> "CloseApproachMetrics":
        near_miss_approaches_count = 0
        yearly_approaches = Counter()
        for approach in close_approaches:
            if float(approach["miss_distance"]["astronomical"]) < 0.2:
                near_miss_approaches_count += 1
            year = approach["close_approach_date"].split("-")[0]
            yearly_approaches[year] += 1

        return cls(
            yearly_approaches=yearly_approaches,
            near_miss_approaches_count=near_miss_approaches_count,
        )

    def __add__(self, other):
        return CloseApproachMetrics(
            yearly_approaches=self.yearly_approaches + other.yearly_approaches,
            near_miss_approaches_count=sum(
                (self.near_miss_approaches_count, other.near_miss_approaches_count)
            ),
        )


def process_neo_responses(neo_responses: Iterable[dict]):
    """Map neo response and compute metrics."""
    records = []
    metrics = CloseApproachMetrics()

    for neo_response in neo_responses:
        neo_list = neo_response["near_earth_objects"]
        for obj in neo_list:
            close_approaches = obj["close_approach_data"]
            metrics += CloseApproachMetrics.fill_from_api_data(close_approaches)
            records.append(map_neo_api_entry(obj))
    return records, metrics
