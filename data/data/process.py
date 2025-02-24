from decimal import Decimal
from typing import Optional

from data.config import logger

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
        "name_limited": entry["name_limited"],
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
