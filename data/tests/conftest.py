import pytest

from _neo.client.models import (
    ClosestApproachData,
    EstimatedDiameterMeters,
    MissDistance,
    NearEarthObject,
    OrbitalData,
    RelativeVelocity,
)


@pytest.fixture
def sample_neos_models():
    """Fixture with sample NearEarthObject models for testing."""
    return [
        NearEarthObject(
            id="1",
            neo_reference_id="1",
            name="Neo 1",
            name_limited="Neo 1",
            designation="2023 A",
            nasa_jpl_url="url",
            absolute_magnitude_h=22.0,
            is_potentially_hazardous_asteroid=False,
            estimated_diameter=EstimatedDiameterMeters(
                meters={"estimated_diameter_min": 1.0, "estimated_diameter_max": 2.0}
            ),
            close_approach_data=[
                ClosestApproachData(
                    close_approach_date="2023-01-01",
                    miss_distance=MissDistance(kilometers="1000", astronomical="0.1"),
                    relative_velocity=RelativeVelocity(kilometers_per_second="5"),
                ),
                ClosestApproachData(
                    close_approach_date="2024-05-10",
                    miss_distance=MissDistance(kilometers="2000", astronomical="0.2"),
                    relative_velocity=RelativeVelocity(kilometers_per_second="6"),
                ),
            ],
            orbital_data=OrbitalData(
                first_observation_date=None,
                last_observation_date=None,
                observations_used=0,
                orbital_period=0.0,
            ),
        ),
        NearEarthObject(
            id="2",
            neo_reference_id="2",
            name="Neo 2",
            name_limited="Neo 2",
            designation="2024 B",
            nasa_jpl_url="url",
            absolute_magnitude_h=21.0,
            is_potentially_hazardous_asteroid=False,
            estimated_diameter=EstimatedDiameterMeters(
                meters={"estimated_diameter_min": 1.5, "estimated_diameter_max": 3.0}
            ),
            close_approach_data=[
                ClosestApproachData(
                    close_approach_date="2024-07-15",
                    miss_distance=MissDistance(kilometers="1500", astronomical="0.15"),
                    relative_velocity=RelativeVelocity(kilometers_per_second="4"),
                ),
            ],
            orbital_data=OrbitalData(
                first_observation_date=None,
                last_observation_date=None,
                observations_used=0,
                orbital_period=0.0,
            ),
        ),
        NearEarthObject(
            id="3",
            neo_reference_id="3",
            name="Neo 3",
            name_limited="Neo 3",
            designation="2023 C",
            nasa_jpl_url="url",
            absolute_magnitude_h=20.0,
            is_potentially_hazardous_asteroid=False,
            estimated_diameter=EstimatedDiameterMeters(
                meters={"estimated_diameter_min": 0.5, "estimated_diameter_max": 1.0}
            ),
            close_approach_data=[
                ClosestApproachData(
                    close_approach_date="2023-12-31",
                    miss_distance=MissDistance(kilometers="500", astronomical="0.05"),
                    relative_velocity=RelativeVelocity(kilometers_per_second="3"),
                ),
            ],
            orbital_data=OrbitalData(
                first_observation_date=None,
                last_observation_date=None,
                observations_used=0,
                orbital_period=0.0,
            ),
        ),
    ]
