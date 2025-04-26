import pytest

from tekmetric_data.data import data_to_record_dict


@pytest.fixture
def sample_data():
    return {
        "id": "123",
        "neo_reference_id": "456",
        "name": "Test Asteroid",
        "name_limited": "Test",
        "designation": "Test Designation",
        "nasa_jpl_url": "http://example.com",
        "absolute_magnitude_h": "22.1",
        "is_potentially_hazardous_asteroid": True,
        "estimated_diameter": {
            "meters": {
                "estimated_diameter_min": "10.5",
                "estimated_diameter_max": "20.7"
            }
        },
        "close_approach_data": [
            {
                "miss_distance": {
                    "lunar": "0.5",
                    "kilometers": "192384.1"
                },
                "close_approach_date_full": "2024-Jan-01 12:34",
                "relative_velocity": {
                    "kilometers_per_hour": "12345.6"
                }
            }
        ],
        "orbital_data": {
            "first_observation_date": "2020-01-01",
            "last_observation_date": "2024-01-01",
            "observations_used": "42",
            "orbital_period": "365.25"
        }
    }


def test_data_to_record_dict_with_approach(sample_data):
    record = data_to_record_dict(sample_data)
    assert record["id"] == "123"
    assert record["closest_approach_miss_distance_km"] == 192384.1
    assert record["closest_approach_date"] == "2024-Jan-01 12:34"
    assert record["closest_approach_relative_velocity_kmh"] == 12345.6


def test_data_to_record_dict_no_approach(sample_data):
    sample_data["close_approach_data"] = []
    record = data_to_record_dict(sample_data)
    assert record["closest_approach_miss_distance_km"] is None
    assert record["closest_approach_date"] is None
    assert record["closest_approach_relative_velocity_kmh"] is None
