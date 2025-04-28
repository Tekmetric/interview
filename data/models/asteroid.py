from datetime import datetime
from typing import Optional

from pydantic import BaseModel, model_validator


class AsteroidValidatedObject(BaseModel):
    id: str
    neo_reference_id: Optional[str] = None
    name: Optional[str] = None
    name_limited: Optional[str] = None
    designation: Optional[str] = None
    nasa_jpl_url: Optional[str] = None
    absolute_magnitude_h: Optional[float] = None
    is_potentially_hazardous_asteroid: Optional[bool] = None
    estimated_diameter_min_m: Optional[float] = None
    estimated_diameter_max_m: Optional[float] = None
    closest_approach_miss_distance_km: Optional[float] = None
    closest_approach_date: Optional[datetime] = None
    closest_approach_relative_velocity_kmps: Optional[float] = None
    first_observation_date: Optional[datetime] = None
    last_observation_date: Optional[datetime] = None
    observations_used: Optional[int] = None
    orbital_period: Optional[float] = None

    @model_validator(mode="before")
    def extract_estimated_diameter_min_max(cls, values):
        diameter = values.get("estimated_diameter", {})
        if not diameter:
            return values

        meters = diameter.get("meters", {})
        if not meters:
            return values

        values["estimated_diameter_min_m"] = meters.get("estimated_diameter_min")
        values["estimated_diameter_max_m"] = meters.get("estimated_diameter_max")
        return values

    @model_validator(mode="before")
    def extract_closest_approach_data(cls, values):
        close_approach_data = values.get("close_approach_data", [])
        if not close_approach_data:
            return values

        sorted_approaches = sorted(close_approach_data, key=lambda x: float(x["miss_distance"]["kilometers"]))
        values["closest_approach_miss_distance_km"] = float(sorted_approaches[0]["miss_distance"]["kilometers"])
        values["closest_approach_date"] = datetime.strptime(sorted_approaches[0]["close_approach_date"], "%Y-%m-%d")
        values["closest_approach_relative_velocity_kmps"] = float(
            sorted_approaches[0]["relative_velocity"]["kilometers_per_second"]
        )
        return values

    @model_validator(mode="before")
    def extract_orbital_data(cls, values):
        orbital_data = values.get("orbital_data", {})
        if not orbital_data:
            return values

        values["first_observation_date"] = datetime.strptime(orbital_data.get("first_observation_date", ""), "%Y-%m-%d")
        values["last_observation_date"] = datetime.strptime(orbital_data.get("last_observation_date", ""), "%Y-%m-%d")
        values["observations_used"] = int(orbital_data.get("observations_used", 0))
        values["orbital_period"] = float(orbital_data.get("orbital_period", 0))

        return values
