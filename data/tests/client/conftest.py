import pytest


@pytest.fixture
def sample_neos():
    """Return a sample list of page dicts for NEOs."""
    return [
        {
            "near_earth_objects": [
                {
                    "id": "123",
                    "neo_reference_id": "456",
                    "name": "Test NEO",
                    "name_limited": "Test",
                    "designation": "2024 AB",
                    "nasa_jpl_url": "http://example.com",
                    "absolute_magnitude_h": 22.1,
                    "is_potentially_hazardous_asteroid": False,
                    "estimated_diameter": {
                        "meters": {
                            "estimated_diameter_min": 10.0,
                            "estimated_diameter_max": 20.0,
                        }
                    },
                    "close_approach_data": [
                        {
                            "miss_distance": {"kilometers": "12345.6", "astronomical": "0.0825"},
                            "close_approach_date": "2024-08-01",
                            "relative_velocity": {"kilometers_per_second": "5.6"},
                        }
                    ],
                    "orbital_data": {
                        "first_observation_date": "2024-01-01",
                        "last_observation_date": "2024-07-01",
                        "observations_used": 42,
                        "orbital_period": 365.25,
                    },
                }
            ]
        }
    ]
