from __future__ import annotations
from dataclasses import dataclass
from typing import Optional

@dataclass
class NEOData:
    id: str
    neo_reference_id: str
    name: str
    name_limited: Optional[str]
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
