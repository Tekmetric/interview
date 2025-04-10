import pytest
import pandas as pd
from neo.handlers.pandas_handler import PandasDataHandler
from neo.handlers.schema import Columns


@pytest.fixture
def sample_records():
    return [
        {
            "id": "1",
            "neo_reference_id": "12345",
            "name": "Asteroid 1",
            "name_limited": "Ast1",
            "designation": "2021 AB",
            "nasa_jpl_url": "http://example.com",
            "absolute_magnitude_h": 22.1,
            "is_potentially_hazardous_asteroid": False,
            "estimated_diameter": {
                "meters": {
                    "estimated_diameter_min": 100.0,
                    "estimated_diameter_max": 300.0,
                }
            },
            "close_approach_data": [
                {
                    "close_approach_date": "2023-01-01",
                    "miss_distance": {"astronomical": "0.15", "kilometers": "150000"},
                    "relative_velocity": {"kilometers_per_second": "25.5"},
                }
            ],
            "orbital_data": {
                "first_observation_date": "2021-01-01",
                "last_observation_date": "2021-12-31",
                "observations_used": 50,
                "orbital_period": "400",
            },
        },
        {
            "id": "2",
            "neo_reference_id": "67890",
            "name": "Asteroid 2",
            "name_limited": "Ast2",
            "designation": "2022 XY",
            "nasa_jpl_url": "http://example.com",
            "absolute_magnitude_h": 20.5,
            "is_potentially_hazardous_asteroid": True,
            "estimated_diameter": {
                "meters": {
                    "estimated_diameter_min": 200.0,
                    "estimated_diameter_max": 500.0,
                }
            },
            "close_approach_data": [
                {
                    "close_approach_date": "2023-05-01",
                    "miss_distance": {"astronomical": "0.25", "kilometers": "250000"},
                    "relative_velocity": {"kilometers_per_second": "20.0"},
                }
            ],
            "orbital_data": {
                "first_observation_date": "2022-01-01",
                "last_observation_date": "2022-12-31",
                "observations_used": 100,
                "orbital_period": "500",
            },
        },
    ]


@pytest.fixture
def handler():
    """
    Fixture to provide an instance of PandasDataHandler.
    """
    return PandasDataHandler()


def test_to_dataframe_success(handler, sample_records):
    """
    Test the to_dataframe method with valid data.
    """
    df = handler.to_dataframe(sample_records)
    assert isinstance(df, pd.DataFrame)
    assert len(df) == 2
    assert "id" in df.columns
    assert "closest_approach_miss_distance_km" in df.columns


def test_to_dataframe_missing_column(handler):
    """
    Test the to_dataframe method with missing required columns.
    """
    records = [{"id": "1", "name": "Asteroid 1"}]  # Missing required columns
    df = handler.to_dataframe(records)
    assert isinstance(df, pd.DataFrame)
    assert df.empty


def test_calculate_total_close_approaches_success(handler, sample_records):
    """
    Test the _calculate_total_close_approaches method with valid data.
    """
    df = handler.to_dataframe(sample_records)
    result = handler._calculate_total_close_approaches(df)
    assert isinstance(result, pd.DataFrame)
    assert result.iloc[0, 0] == 1


def test_calculate_total_close_approaches_no_close_approaches(handler, sample_records):
    """
    Test the _calculate_total_close_approaches method with no close approaches within 0.2 AU.
    """
    sample_records[0]["close_approach_data"][0]["miss_distance"]["astronomical"] = "0.3"
    df = handler.to_dataframe(sample_records)
    result = handler._calculate_total_close_approaches(df)
    assert result.iloc[0, 0] == 0


def test_calculate_yearly_approaches_success(handler, sample_records):
    """
    Test the _calculate_yearly_approaches method with valid data.
    """
    df = handler.to_dataframe(sample_records)
    result = handler._calculate_yearly_approaches(df)
    assert isinstance(result, pd.DataFrame)
    assert len(result) == 1
    assert result.iloc[0]["year"] == 2023
    assert result.iloc[0]["count"] == 2


def test_run_aggregations_success(handler, sample_records):
    """
    Test the run_aggregations method with valid data.
    """
    df = handler.to_dataframe(sample_records)
    aggregations = handler.run_aggregations(df)
    assert "total_close_approaches_within_0.2_au" in aggregations
    assert "approaches_per_year" in aggregations
    assert aggregations["total_close_approaches_within_0.2_au"].iloc[0, 0] == 1
    assert len(aggregations["approaches_per_year"]) == 1
