from __future__ import annotations
from dataclasses import dataclass
from typing import Optional

@dataclass
class NEOData:
    id: str
    neo_reference_id: str
    name: str
    name_limited: str
    designation: str
    nasa_jpl_url: str
    absolute_magnitude_h: float
    is_potentially_hazardous_asteroid: bool
    minimum_estimated_diameter_in_meters: Optional[float]
    maximum_estimated_diameter_in_meters: Optional[float]
    closest_approach_miss_distance_in_kilometers: Optional[str]
    closest_approach_date: Optional[str]
    closest_approach_relative_velocity_in_kilometers_per_second: Optional[str]
    first_observation_date: Optional[str]
    last_observation_date: Optional[str]
    observations_used: Optional[int]
    orbital_period: Optional[str]

    def to_dict(self) -> dict:
        return {
            "id": self.id,
            "neo_reference_id": self.neo_reference_id,
            "name": self.name,
            "name_limited": self.name_limited,
            "designation": self.designation,
            "nasa_jpl_url": self.nasa_jpl_url,
            "absolute_magnitude_h": self.absolute_magnitude_h,
            "is_potentially_hazardous_asteroid": self.is_potentially_hazardous_asteroid,
            "minimum_estimated_diameter_in_meters": self.minimum_estimated_diameter_in_meters,
            "maximum_estimated_diameter_in_meters": self.maximum_estimated_diameter_in_meters,
            "closest_approach_miss_distance_in_kilometers": self.closest_approach_miss_distance_in_kilometers,
            "closest_approach_date": self.closest_approach_date,
            "closest_approach_relative_velocity_in_kilometers_per_second": self.closest_approach_relative_velocity_in_kilometers_per_second,
            "first_observation_date": self.first_observation_date,
            "last_observation_date": self.last_observation_date,
            "observations_used": self.observations_used,
            "orbital_period": self.orbital_period,
        }
    
    @staticmethod
    def from_dict(data: dict) -> NEOData:
        estimated_diameter_meters = data.get("estimated_diameter", {}).get("meters", {})
        estimated_diameter_min = estimated_diameter_meters.get("estimated_diameter_min", None)
        estimated_diameter_max = estimated_diameter_meters.get("estimated_diameter_max", None)

        close_approach_data = data.get("close_approach_data", [])
        closest_approach_miss_distance_in_kilometers, closest_approach_date, relative_velocity = None, None, None
        if len(close_approach_data) > 0:
            closest_approach_miss_distance_in_kilometers = close_approach_data[0].get("miss_distance", {}).get("kilometers", None)
            closest_approach_date = close_approach_data[0].get("close_approach_date", None)
            relative_velocity = close_approach_data[0].get("relative_velocity", {}).get("kilometers_per_second", None)

        first_observation_date = data.get("orbital_data", {}).get("first_observation_date", None)
        last_observation_date = data.get("orbital_data", {}).get("last_observation_date", None)
        observations_used = data.get("orbital_data", {}).get("observations_used", None)
        orbital_period = data.get("orbital_data", {}).get("orbital_period", None)

        return NEOData(
            id=str(data["id"]),
            neo_reference_id=str(data["neo_reference_id"]),
            name=str(data["name"]),
            name_limited=str(data["name_limited"]),
            designation=str(data["designation"]),
            nasa_jpl_url=str(data["nasa_jpl_url"]),
            absolute_magnitude_h=float(data["absolute_magnitude_h"]),   
            is_potentially_hazardous_asteroid=bool(data["is_potentially_hazardous_asteroid"]),

            minimum_estimated_diameter_in_meters=float(estimated_diameter_min) if estimated_diameter_min is not None else None,
            maximum_estimated_diameter_in_meters=float(estimated_diameter_max) if estimated_diameter_max is not None else None,
            
            closest_approach_miss_distance_in_kilometers=closest_approach_miss_distance_in_kilometers,
            closest_approach_date=closest_approach_date,
            closest_approach_relative_velocity_in_kilometers_per_second=relative_velocity,

            first_observation_date=first_observation_date,
            last_observation_date=last_observation_date,
            observations_used=observations_used,
            orbital_period=orbital_period,
        )
