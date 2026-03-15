from __future__ import annotations

import time
from typing import Any

import pandas as pd
import requests

from .config import (
    API_URL,
    MAX_RETRIES,
    NASA_API_KEY,
    REQUEST_TIMEOUT_SECONDS,
    RETRY_BACKOFF_SECONDS,
)


def require_api_key() -> str:
    if not NASA_API_KEY:
        raise ValueError(
            "NASA_API_KEY is not set. Export it in your shell before running the pipeline."
        )
    return NASA_API_KEY


def fetch_page(page: int) -> dict[str, Any]:
    api_key = require_api_key()
    params = {"page": page, "api_key": api_key}

    last_error: Exception | None = None

    for attempt in range(1, MAX_RETRIES + 1):
        try:
            response = requests.get(
                API_URL,
                params=params,
                timeout=REQUEST_TIMEOUT_SECONDS,
            )

            if response.status_code == 429:
                if attempt == MAX_RETRIES:
                    response.raise_for_status()
                time.sleep(RETRY_BACKOFF_SECONDS * attempt)
                continue

            response.raise_for_status()
            payload = response.json()

            if "near_earth_objects" not in payload:
                raise ValueError(
                    f"NASA API response missing 'near_earth_objects' on page {page}"
                )

            return payload

        except Exception as exc:
            last_error = exc
            if attempt == MAX_RETRIES:
                break
            time.sleep(RETRY_BACKOFF_SECONDS * attempt)

    raise RuntimeError(f"Failed to fetch page {page}") from last_error


# The API returns many numeric-looking values as strings, so convert them
# during flattening to keep downstream logic simple and explicit.
def _to_float(value: Any, field_name: str) -> float | None:
    if value in (None, ""):
        return None
    try:
        return float(value)
    except (TypeError, ValueError) as exc:
        raise ValueError(f"Failed to convert '{field_name}' to float: {value}") from exc


def _to_int(value: Any, field_name: str) -> int | None:
    if value in (None, ""):
        return None
    try:
        return int(value)
    except (TypeError, ValueError) as exc:
        raise ValueError(f"Failed to convert '{field_name}' to int: {value}") from exc


# Bronze preserves the full raw source shape, but flattened into parquet-friendly columns.
def flatten_neo_objects(objects: list[dict[str, Any]]) -> pd.DataFrame:
    rows: list[dict[str, Any]] = []

    for obj in objects:
        estimated_diameter = obj.get("estimated_diameter", {})
        orbital_data = obj.get("orbital_data", {})
        orbit_class = orbital_data.get("orbit_class", {})

        kilometers = estimated_diameter.get("kilometers", {})
        meters = estimated_diameter.get("meters", {})
        miles = estimated_diameter.get("miles", {})
        feet = estimated_diameter.get("feet", {})

        rows.append(
            {
                "links_self": obj.get("links", {}).get("self"),
                "id": obj.get("id"),
                "neo_reference_id": obj.get("neo_reference_id"),
                "name": obj.get("name"),
                "name_limited": obj.get("name_limited"),
                "designation": obj.get("designation"),
                "nasa_jpl_url": obj.get("nasa_jpl_url"),
                "absolute_magnitude_h": _to_float(
                    obj.get("absolute_magnitude_h"),
                    "absolute_magnitude_h",
                ),
                "is_potentially_hazardous_asteroid": obj.get(
                    "is_potentially_hazardous_asteroid"
                ),
                "is_sentry_object": obj.get("is_sentry_object"),
                "estimated_diameter_kilometers_min": _to_float(
                    kilometers.get("estimated_diameter_min"),
                    "estimated_diameter.kilometers.estimated_diameter_min",
                ),
                "estimated_diameter_kilometers_max": _to_float(
                    kilometers.get("estimated_diameter_max"),
                    "estimated_diameter.kilometers.estimated_diameter_max",
                ),
                "estimated_diameter_meters_min": _to_float(
                    meters.get("estimated_diameter_min"),
                    "estimated_diameter.meters.estimated_diameter_min",
                ),
                "estimated_diameter_meters_max": _to_float(
                    meters.get("estimated_diameter_max"),
                    "estimated_diameter.meters.estimated_diameter_max",
                ),
                "estimated_diameter_miles_min": _to_float(
                    miles.get("estimated_diameter_min"),
                    "estimated_diameter.miles.estimated_diameter_min",
                ),
                "estimated_diameter_miles_max": _to_float(
                    miles.get("estimated_diameter_max"),
                    "estimated_diameter.miles.estimated_diameter_max",
                ),
                "estimated_diameter_feet_min": _to_float(
                    feet.get("estimated_diameter_min"),
                    "estimated_diameter.feet.estimated_diameter_min",
                ),
                "estimated_diameter_feet_max": _to_float(
                    feet.get("estimated_diameter_max"),
                    "estimated_diameter.feet.estimated_diameter_max",
                ),
                "orbit_id": orbital_data.get("orbit_id"),
                "orbit_determination_date": orbital_data.get("orbit_determination_date"),
                "first_observation_date": orbital_data.get("first_observation_date"),
                "last_observation_date": orbital_data.get("last_observation_date"),
                "data_arc_in_days": _to_int(
                    orbital_data.get("data_arc_in_days"),
                    "orbital_data.data_arc_in_days",
                ),
                "observations_used": _to_int(
                    orbital_data.get("observations_used"),
                    "orbital_data.observations_used",
                ),
                "orbit_uncertainty": orbital_data.get("orbit_uncertainty"),
                "minimum_orbit_intersection": _to_float(
                    orbital_data.get("minimum_orbit_intersection"),
                    "orbital_data.minimum_orbit_intersection",
                ),
                "jupiter_tisserand_invariant": _to_float(
                    orbital_data.get("jupiter_tisserand_invariant"),
                    "orbital_data.jupiter_tisserand_invariant",
                ),
                "epoch_osculation": orbital_data.get("epoch_osculation"),
                "eccentricity": _to_float(
                    orbital_data.get("eccentricity"),
                    "orbital_data.eccentricity",
                ),
                "semi_major_axis": _to_float(
                    orbital_data.get("semi_major_axis"),
                    "orbital_data.semi_major_axis",
                ),
                "inclination": _to_float(
                    orbital_data.get("inclination"),
                    "orbital_data.inclination",
                ),
                "ascending_node_longitude": _to_float(
                    orbital_data.get("ascending_node_longitude"),
                    "orbital_data.ascending_node_longitude",
                ),
                "orbital_period": _to_float(
                    orbital_data.get("orbital_period"),
                    "orbital_data.orbital_period",
                ),
                "perihelion_distance": _to_float(
                    orbital_data.get("perihelion_distance"),
                    "orbital_data.perihelion_distance",
                ),
                "perihelion_argument": _to_float(
                    orbital_data.get("perihelion_argument"),
                    "orbital_data.perihelion_argument",
                ),
                "aphelion_distance": _to_float(
                    orbital_data.get("aphelion_distance"),
                    "orbital_data.aphelion_distance",
                ),
                "perihelion_time": orbital_data.get("perihelion_time"),
                "mean_anomaly": _to_float(
                    orbital_data.get("mean_anomaly"),
                    "orbital_data.mean_anomaly",
                ),
                "mean_motion": _to_float(
                    orbital_data.get("mean_motion"),
                    "orbital_data.mean_motion",
                ),
                "equinox": orbital_data.get("equinox"),
                "orbit_class_type": orbit_class.get("orbit_class_type"),
                "orbit_class_description": orbit_class.get("orbit_class_description"),
                "orbit_class_range": orbit_class.get("orbit_class_range"),
            }
        )

    return pd.DataFrame(rows)


def flatten_close_approaches(objects: list[dict[str, Any]]) -> pd.DataFrame:
    rows: list[dict[str, Any]] = []

    for obj in objects:
        for approach in obj.get("close_approach_data", []):
            relative_velocity = approach.get("relative_velocity", {})
            miss_distance = approach.get("miss_distance", {})

            rows.append(
                {
                    "neo_reference_id": obj.get("neo_reference_id"),
                    "close_approach_date": approach.get("close_approach_date"),
                    "close_approach_date_full": approach.get("close_approach_date_full"),
                    "epoch_date_close_approach": _to_int(
                        approach.get("epoch_date_close_approach"),
                        "close_approach_data.epoch_date_close_approach",
                    ),
                    "relative_velocity_kilometers_per_second": _to_float(
                        relative_velocity.get("kilometers_per_second"),
                        "relative_velocity.kilometers_per_second",
                    ),
                    "relative_velocity_kilometers_per_hour": _to_float(
                        relative_velocity.get("kilometers_per_hour"),
                        "relative_velocity.kilometers_per_hour",
                    ),
                    "relative_velocity_miles_per_hour": _to_float(
                        relative_velocity.get("miles_per_hour"),
                        "relative_velocity.miles_per_hour",
                    ),
                    "miss_distance_astronomical": _to_float(
                        miss_distance.get("astronomical"),
                        "miss_distance.astronomical",
                    ),
                    "miss_distance_lunar": _to_float(
                        miss_distance.get("lunar"),
                        "miss_distance.lunar",
                    ),
                    "miss_distance_kilometers": _to_float(
                        miss_distance.get("kilometers"),
                        "miss_distance.kilometers",
                    ),
                    "miss_distance_miles": _to_float(
                        miss_distance.get("miles"),
                        "miss_distance.miles",
                    ),
                    "orbiting_body": approach.get("orbiting_body"),
                }
            )

    return pd.DataFrame(rows)
