import pandas as pd
import pytest


@pytest.fixture
def bronze_objects_df():
    return pd.DataFrame(
        [
            {
                "id": "1",
                "neo_reference_id": "1",
                "name": "Object A",
                "name_limited": "A",
                "designation": "A-1",
                "nasa_jpl_url": "https://example.com/a",
                "absolute_magnitude_h": 12.3,
                "is_potentially_hazardous_asteroid": False,
                "estimated_diameter_meters_min": 1.0,
                "estimated_diameter_meters_max": 2.0,
                "first_observation_date": "2020-01-01",
                "last_observation_date": "2020-01-02",
                "observations_used": 5,
                "orbital_period": 100.0,
            }
        ]
    )


@pytest.fixture
def bronze_close_approaches_df():
    return pd.DataFrame(
        [
            {
                "neo_reference_id": "1",
                "close_approach_date": "2020-05-01",
                "miss_distance_astronomical": 0.30,
                "miss_distance_kilometers": 500.0,
                "relative_velocity_kilometers_per_second": 6.0,
                "orbiting_body": "Earth",
            },
            {
                "neo_reference_id": "1",
                "close_approach_date": "2020-03-01",
                "miss_distance_astronomical": 0.10,
                "miss_distance_kilometers": 100.0,
                "relative_velocity_kilometers_per_second": 4.0,
                "orbiting_body": "Earth",
            },
        ]
    )
