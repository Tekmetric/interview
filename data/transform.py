from __future__ import annotations

from collections import defaultdict
from dataclasses import dataclass
from datetime import date

import pyarrow as pa


# Canonical Arrow schema for the transformed NEO data. 
SCHEMA = pa.schema([
    ("id", pa.string()),
    ("neo_reference_id", pa.string()),
    ("name", pa.string()),
    ("name_limited", pa.string()),
    ("designation", pa.string()),
    ("nasa_jpl_url", pa.string()),
    ("absolute_magnitude_h", pa.float64()),
    ("is_potentially_hazardous_asteroid", pa.bool_()),
    ("estimated_diameter_min_meters", pa.float64()),
    ("estimated_diameter_max_meters", pa.float64()),
    ("closest_approach_date", pa.date32()),
    ("closest_approach_miss_distance_km", pa.float64()),
    ("closest_approach_relative_velocity_km_s", pa.float64()),
    ("first_observation_date", pa.date32()),
    ("last_observation_date", pa.date32()),
    ("observations_used", pa.int64()),
    ("orbital_period", pa.float64()),
])


@dataclass
class PageResult:
    records: pa.Table
    approaches_under_0_2_au: int
    approaches_by_year: dict[str, int]


def transform_page(raw_objects: list[dict]) -> PageResult:
    """Transform a list of raw NEO API objects into a flat DataFrame plus aggregations.

    Aggregations are computed over ALL close approaches for each object, not just
    the closest one stored in the DataFrame.
    """
    rows = []
    approaches_under_0_2_au = 0
    approaches_by_year: defaultdict[str, int] = defaultdict(int)

    for obj in raw_objects:
        orbital = obj.get("orbital_data", {})
        close_approaches = obj.get("close_approach_data", [])

        closest_approach = _find_closest_approach(close_approaches)

        # --- per-object aggregations over all approaches ---
        for approach in close_approaches:
            au = _parse_float(approach.get("miss_distance", {}).get("astronomical"))
            if au is not None and au < 0.2:
                approaches_under_0_2_au += 1

            approach_date = approach.get("close_approach_date") or ""
            if len(approach_date) >= 4:
                year = approach_date[:4]
                approaches_by_year[year] += 1

        diameter = obj.get("estimated_diameter", {}).get("meters", {})

        rows.append(
            {
                "id": obj.get("id"),
                "neo_reference_id": obj.get("neo_reference_id"),
                "name": obj.get("name"),
                "name_limited": obj.get("name_limited"),
                "designation": obj.get("designation"),
                "nasa_jpl_url": obj.get("nasa_jpl_url"),
                "absolute_magnitude_h": _parse_float(obj.get("absolute_magnitude_h")),
                "is_potentially_hazardous_asteroid": obj.get("is_potentially_hazardous_asteroid"),
                "estimated_diameter_min_meters": _parse_float(diameter.get("estimated_diameter_min")),
                "estimated_diameter_max_meters": _parse_float(diameter.get("estimated_diameter_max")),
                "closest_approach_date": (
                    _parse_iso_date(closest_approach.get("close_approach_date"))
                    if closest_approach
                    else None
                ),
                "closest_approach_miss_distance_km": (
                    _parse_float(closest_approach.get("miss_distance", {}).get("kilometers"))
                    if closest_approach
                    else None
                ),
                "closest_approach_relative_velocity_km_s": (
                    _parse_float(closest_approach.get("relative_velocity", {}).get("kilometers_per_second"))
                    if closest_approach
                    else None
                ),
                "first_observation_date": _parse_iso_date(orbital.get("first_observation_date")),
                "last_observation_date": _parse_iso_date(orbital.get("last_observation_date")),
                "observations_used": _parse_int(orbital.get("observations_used")),
                "orbital_period": _parse_float(orbital.get("orbital_period")),
            }
        )

    return PageResult(
        records=pa.Table.from_pylist(rows, schema=SCHEMA),
        approaches_under_0_2_au=approaches_under_0_2_au,
        approaches_by_year=approaches_by_year,
    )


def _parse_iso_date(value: object) -> date | None:
    if value is None or value == "":
        return None

    try:
        return date.fromisoformat(str(value))
    except (TypeError, ValueError):
        return None


def _parse_int(value: object) -> int | None:
    if value is None or value == "":
        return None

    try:
        return int(value)
    except (TypeError, ValueError):
        return None


def _parse_float(value: object) -> float | None:
    if value is None or value == "":
        return None

    try:
        return float(value)
    except (TypeError, ValueError):
        return None


def _find_closest_approach(close_approaches: list[dict]) -> dict | None:
    """Find the closest approach to any orbiting body by miss distance in kilometers.
    
    TBD: Are we interested in the closest approach to Earth specifically? If so, this
    function should filter valid approaches by approach["orbiting_body"] == "Earth"
    """
    if not close_approaches:
        return None
    valid_approaches = []
    for approach in close_approaches:
        miss_distance_km = _parse_float(approach.get("miss_distance", {}).get("kilometers"))
        if miss_distance_km is not None:
            valid_approaches.append((miss_distance_km, approach))
    if not valid_approaches:
        return None
    return min(valid_approaches, key=lambda t: t[0])[1]
