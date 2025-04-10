from enum import Enum


class Columns(Enum):
    ID = "id"
    NEO_REFERENCE_ID = "neo_reference_id"
    NAME = "name"
    NAME_LIMITED = "name_limited"
    DESIGNATION = "designation"
    NASA_JPL_URL = "nasa_jpl_url"
    ABSOLUTE_MAGNITUDE_H = "absolute_magnitude_h"
    IS_POTENTIALLY_HAZARDOUS_ASTEROID = "is_potentially_hazardous_asteroid"
    MINIMUM_ESTIMATED_DIAMETER_METERS = (
        "estimated_diameter.meters.estimated_diameter_min"
    )
    MAXIMUM_ESTIMATED_DIAMETER_METERS = (
        "estimated_diameter.meters.estimated_diameter_max"
    )
    CLOSEST_APPROACH_MISS_DISTANCE_KM = "close_approach_data.miss_distance.kilometers"
    CLOSEST_APPROACH_MISS_DISTANCE_ASTRONOMICAL = (
        "close_approach_data.miss_distance.astronomical"
    )
    CLOSEST_APPROACH_DATE = "close_approach_data.close_approach_date"
    CLOSEST_APPROACH_RELATIVE_VELOCITY_KM_S = (
        "close_approach_data.relative_velocity.kilometers_per_second"
    )
    FIRST_OBSERVATION_DATE = "orbital_data.first_observation_date"
    LAST_OBSERVATION_DATE = "orbital_data.last_observation_date"
    OBSERVATIONS_USED = "orbital_data.observations_used"
    ORBITAL_PERIOD = "orbital_data.orbital_period"
