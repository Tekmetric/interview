from datetime import datetime

from pydantic import BaseModel, model_validator


class AsteroidValidatedObject(BaseModel):
    id: str
    neo_reference_id: str
    name: str
    name_limited: str
    designation: str
    nasa_jpl_url: str
    absolute_magnitude_h: float
    is_potentially_hazardous_asteroid: bool
    estimated_diameter_min_m: float
    estimated_diameter_max_m: float
    closest_approach_miss_distance_km: float
    closest_approach_date: datetime
    closest_approach_relative_velocity_kmps: float
    first_observation_date: datetime
    last_observation_date: datetime
    observations_used: int
    orbital_period: float
    is_sentry_object: bool

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
