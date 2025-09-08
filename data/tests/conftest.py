import pytest
import pandas as pd
from datetime import datetime
from unittest.mock import Mock


@pytest.fixture
def sample_neo_data():
    """Sample NEO data based on actual NASA API response format from https://api.nasa.gov/"""
    return {
        "id": "2000433",
        "neo_reference_id": "2000433",
        "name": "433 Eros (A898 PA)",
        "name_limited": "",
        "designation": "433",
        "nasa_jpl_url": "http://ssd-api.jpl.nasa.gov/cad.api?sstr=2000433",
        "absolute_magnitude_h": 10.31,
        "is_potentially_hazardous_asteroid": False,
        "estimated_diameter": {
            "meters": {
                "estimated_diameter_min": 15000.0,
                "estimated_diameter_max": 34000.0
            }
        },
        "close_approach_data": [
            {
                "close_approach_date": "2024-01-15",
                "close_approach_date_full": "2024-Jan-15 12:34",
                "epoch_date_close_approach": 1705320000000,
                "miss_distance": {
                    "astronomical": "0.1",
                    "lunar": "40.0",
                    "kilometers": "15000000.0",
                    "miles": "9300000.0"
                },
                "relative_velocity": {
                    "kilometers_per_second": "15.5",
                    "kilometers_per_hour": "55800.0",
                    "miles_per_hour": "34600.0"
                },
                "orbiting_body": "Earth"
            },
            {
                "close_approach_date": "2025-03-20",
                "close_approach_date_full": "2025-Mar-20 08:15",
                "epoch_date_close_approach": 1742400000000,
                "miss_distance": {
                    "astronomical": "0.3",
                    "lunar": "120.0",
                    "kilometers": "45000000.0",
                    "miles": "28000000.0"
                },
                "relative_velocity": {
                    "kilometers_per_second": "12.8",
                    "kilometers_per_hour": "46080.0",
                    "miles_per_hour": "28600.0"
                },
                "orbiting_body": "Earth"
            }
        ],
        "orbital_data": {
            "orbit_id": "1",
            "orbit_determination_date": "2023-12-31",
            "first_observation_date": "1898-08-13",
            "last_observation_date": "2023-12-31",
            "data_arc_in_days": 45678,
            "observations_used": 5000,
            "orbit_uncertainty": "0",
            "minimum_orbit_intersection": "0.1",
            "jupiter_tisserand_invariant": "3.2",
            "epoch_osculation": "2450000.5",
            "eccentricity": "0.22",
            "semi_major_axis": "1.46",
            "inclination": "10.8",
            "ascending_node_longitude": "304.3",
            "orbital_period": "1.76",
            "perihelion_distance": "1.13",
            "perihelion_argument": "178.9",
            "aphelion_distance": "1.78",
            "perihelion_time": "2450000.5",
            "mean_anomaly": "180.0",
            "mean_motion": "0.57",
            "equinox": "J2000"
        }
    }


@pytest.fixture
def sample_neo_data_missing_fields():
    return {
        "id": "2000001",
        "name": "Test NEO",
        "designation": "2000001",
        "is_potentially_hazardous_asteroid": True,
        "close_approach_data": [],
        "orbital_data": {}
    }


@pytest.fixture
def sample_neo_data_invalid_types():
    return {
        "id": "2000002",
        "neo_reference_id": "2000002",
        "name": "Invalid Types NEO",
        "designation": "2000002",
        "absolute_magnitude_h": "not_a_number",
        "is_potentially_hazardous_asteroid": "maybe",
        "estimated_diameter": {
            "meters": {
                "estimated_diameter_min": "invalid",
                "estimated_diameter_max": None
            }
        },
        "close_approach_data": [
            {
                "close_approach_date": "invalid_date",
                "miss_distance": {
                    "astronomical": "not_numeric",
                    "kilometers": "also_invalid"
                },
                "relative_velocity": {
                    "kilometers_per_second": "bad_data"
                }
            }
        ],
        "orbital_data": {
            "orbital_period": "invalid"
        }
    }


@pytest.fixture
def sample_api_response():
    """Sample NASA API browse response based on actual format from https://api.nasa.gov/"""
    return {
        "near_earth_objects": [
            {
                "id": "2000433",
                "name": "433 Eros",
                "designation": "433",
                "is_potentially_hazardous_asteroid": False
            },
            {
                "id": "2000001",
                "name": "Test Asteroid",
                "designation": "2000001",
                "is_potentially_hazardous_asteroid": True
            }
        ],
        "page": {
            "size": 20,
            "total_elements": 2000,
            "total_pages": 100,
            "number": 0
        },
        "links": {
            "next": "https://api.nasa.gov/neo/rest/v1/neo/browse?page=1&size=20&api_key=DEMO_KEY",
            "self": "https://api.nasa.gov/neo/rest/v1/neo/browse?page=0&size=20&api_key=DEMO_KEY"
        }
    }


@pytest.fixture
def sample_processed_dataframe():
    return pd.DataFrame([
        {
            "id": "2000433",
            "name": "433 Eros",
            "designation": "433",
            "is_potentially_hazardous_asteroid": False,
            "closest_approach_distance_km": 15000000.0,
            "closest_approach_date": "2024-01-15",
            "closest_approach_velocity_km_s": 15.5
        },
        {
            "id": "2000001",
            "name": "Test Asteroid",
            "designation": "2000001",
            "is_potentially_hazardous_asteroid": True,
            "closest_approach_distance_km": 20000000.0,
            "closest_approach_date": "2024-06-10",
            "closest_approach_velocity_km_s": 18.2
        }
    ])


@pytest.fixture
def mock_api_client():
    client = Mock()
    client.fetch_neo_data.return_value = iter([
        {"id": "2000433", "name": "433 Eros"},
        {"id": "2000001", "name": "Test Asteroid"}
    ])
    return client
