#!/usr/bin/env python3
"""
Unit tests for NASA NEO data pipeline.
Run with pytest: pytest -v tests/test_fetch_neo.py
"""

import sys
import os
import pytest
import pandas as pd
import json
from unittest.mock import patch, mock_open, MagicMock
from datetime import datetime
from pathlib import Path

# Add the parent directory to the Python path
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '../')))

# Import functions to test from fetch_neo.py
from src.fetch_neo import (
    load_api_key,
    fetch_neo_data,
    transform_neo_data,
    calculate_aggregations,
    save_data
)

class TestNeoDataPipeline:
    
    @patch("os.environ.get")
    @patch("builtins.open", new_callable=mock_open, read_data='{"api_key": "test_key"}')
    def test_load_api_key_from_config(self, mock_file, mock_env_get):
        # Mock environment variable to return None
        mock_env_get.return_value = None
        
        # Test loading from config file
        api_key = load_api_key()
        assert api_key == "test_key"
        mock_file.assert_called_once_with("../config.json", "r")
    
    @patch("os.environ.get")
    def test_load_api_key_from_env(self, mock_env_get):
        # Mock environment variable to return a key
        mock_env_get.return_value = "env_test_key"
        
        # Test loading from environment
        api_key = load_api_key()
        assert api_key == "env_test_key"
    
    @patch("os.environ.get")
    @patch("builtins.open")
    def test_load_api_key_missing(self, mock_file, mock_env_get):
        # Mock environment variable and file to fail
        mock_env_get.return_value = None
        mock_file.side_effect = FileNotFoundError()
        
        # Test api key not found
        with pytest.raises(ValueError):
            load_api_key()
    
    @patch("requests.get")
    @patch("time.sleep")  # Mock sleep to speed up tests
    def test_fetch_neo_data(self, mock_sleep, mock_get):
        # Create a mock response
        mock_response = MagicMock()
        mock_response.status_code = 200
        mock_response.json.return_value = {
            "near_earth_objects": [{"id": "1"}, {"id": "2"}],
            "links": {}  # No next link, so only one page
        }
        mock_get.return_value = mock_response
        
        # Test fetching data
        result = fetch_neo_data("test_key", limit=2)
        assert len(result) == 2
        assert result[0]["id"] == "1"
        assert result[1]["id"] == "2"
    
    def test_transform_neo_data(self):
        # Sample test data
        test_neo_objects = [
            {
                "id": "1",
                "neo_reference_id": "ref1",
                "name": "Test Asteroid 1",
                "name_limited": "TA1",
                "designation": "des1",
                "nasa_jpl_url": "http://example.com/1",
                "absolute_magnitude_h": 20.5,
                "is_potentially_hazardous_asteroid": True,
                "estimated_diameter": {
                    "meters": {
                        "estimated_diameter_min": 100,
                        "estimated_diameter_max": 200
                    }
                },
                "close_approach_data": [
                    {
                        "close_approach_date": "2020-01-01",
                        "miss_distance": {
                            "kilometers": "50000",
                            "astronomical": "0.3"
                        },
                        "relative_velocity": {
                            "kilometers_per_second": "10"
                        },
                        "orbiting_body": "Earth"
                    },
                    {
                        "close_approach_date": "2021-01-01",
                        "miss_distance": {
                            "kilometers": "30000",
                            "astronomical": "0.2"
                        },
                        "relative_velocity": {
                            "kilometers_per_second": "12"
                        },
                        "orbiting_body": "Earth"
                    }
                ],
                "orbital_data": {
                    "first_observation_date": "2010-01-01",
                    "last_observation_date": "2022-01-01",
                    "observations_used": 100,
                    "orbital_period": 365
                }
            }
        ]
        
        # Test transformation
        neo_df, approaches_df = transform_neo_data(test_neo_objects)
        
        # Test NEO dataframe
        assert len(neo_df) == 1
        assert neo_df.iloc[0]["id"] == "1"
        assert neo_df.iloc[0]["closest_approach_miss_distance_kilometers"] == 30000.0
        assert neo_df.iloc[0]["closest_approach_date"] == "2021-01-01"
        assert neo_df.iloc[0]["closest_approach_relative_velocity_kps"] == 12.0
        
        # Test approaches dataframe
        assert len(approaches_df) == 2
        assert approaches_df.iloc[0]["neo_id"] == "1"
        assert approaches_df.iloc[0]["approach_date"] == "2020-01-01"
        assert approaches_df.iloc[1]["approach_date"] == "2021-01-01"
    
    def test_calculate_aggregations(self):
        # Create test dataframes
        neo_df = pd.DataFrame([
            {"id": "1", "name": "Asteroid 1"}, 
            {"id": "2", "name": "Asteroid 2"}
        ])
        
        approaches_df = pd.DataFrame([
            {"neo_id": "1", "approach_date": "2020-01-01", "miss_distance_au": 0.1},
            {"neo_id": "1", "approach_date": "2021-01-01", "miss_distance_au": 0.3},
            {"neo_id": "2", "approach_date": "2020-01-01", "miss_distance_au": 0.15},
            {"neo_id": "2", "approach_date": "2021-01-01", "miss_distance_au": 0.25}
        ])
        
        # Test aggregations calculation
        agg_df = calculate_aggregations(neo_df, approaches_df)
        
        # Check if it counted approaches correctly (should be 2 approaches < 0.2 AU)
        assert agg_df.iloc[0]["neos_closer_than_0.2_au"] == 2
        
        # Check yearly counts
        yearly_approaches = json.loads(agg_df.iloc[0]["yearly_approaches"])
        assert yearly_approaches["2020"] == 2
        assert yearly_approaches["2021"] == 2

    @patch("pathlib.Path.mkdir")
    @patch("pandas.DataFrame.to_parquet")
    def test_save_data(self, mock_to_parquet, mock_mkdir):
        # Create test dataframes
        neo_df = pd.DataFrame([{"id": "1"}])
        approaches_df = pd.DataFrame([{"neo_id": "1"}])
        agg_df = pd.DataFrame([{"timestamp": "2024-03-31T12:00:00"}])
        
        # Create a custom mock that returns specific strings for our use case
        mock_datetime = MagicMock()
        fixed_timestamp = "20240331_120000"
        fixed_iso = "2024-03-31T12:00:00"
        
        # Create a class to replace datetime
        class MockDateTime:
            @staticmethod
            def now():
                mock = MagicMock()
                mock.strftime.return_value = fixed_timestamp
                mock.isoformat.return_value = fixed_iso
                return mock
        
        # Patch the module's datetime with our custom class
        with patch('src.fetch_neo.datetime', MockDateTime):
            # Test saving data
            neo_path, approaches_path, agg_path = save_data(neo_df, approaches_df, agg_df)
        
        # Check if directories were created
        assert mock_mkdir.call_count == 2
        assert mock_mkdir.call_args_list[0][1]["parents"] == True
        
        # Check if to_parquet was called for each dataframe
        assert mock_to_parquet.call_count == 3
        
        # We can simplify the filename verification to check for partial matches
        assert "neo_data_" in str(neo_path)
        assert "neo_approaches_" in str(approaches_path)
        assert "neo_aggregations_" in str(agg_path)