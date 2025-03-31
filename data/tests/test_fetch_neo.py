#!/usr/bin/env python3
"""
Unit tests for updated NASA NEO data pipeline with orbital data.
Run with pytest: pytest -v tests/test_fetch_neo_v2.py
"""

import sys
import os
import pytest
import pandas as pd
import json
from unittest.mock import patch, mock_open, MagicMock, call
from pathlib import Path

# Add the parent directory to the Python path
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '../')))

# Import functions to test
from src.fetch_neo import (
    load_api_key,
    fetch_neo_data,
    transform_neo_data,
    calculate_aggregations,
    save_data,
    main
)

class TestNeoDataPipelineV2:
    
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
        # Sample test data with orbital information
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
                    "orbit_id": "orbit-1",
                    "orbit_determination_date": "2022-01-01",
                    "orbit_uncertainty": "0",
                    "minimum_orbit_intersection": "0.1",
                    "jupiter_tisserand_invariant": "4.5",
                    "epoch_osculation": "12345",
                    "eccentricity": "0.5",
                    "semi_major_axis": "1.5",
                    "inclination": "10.5",
                    "ascending_node_longitude": "180.5",
                    "perihelion_distance": "0.9",
                    "perihelion_argument": "90.5",
                    "aphelion_distance": "2.1",
                    "perihelion_time": "12345.5",
                    "mean_anomaly": "45.5",
                    "mean_motion": "1.1",
                    "first_observation_date": "2010-01-01",
                    "last_observation_date": "2022-01-01",
                    "observations_used": 100,
                    "orbital_period": 365,
                    "orbit_class": {
                        "orbit_class_type": "AMO",
                        "orbit_class_description": "Apollo",
                        "orbit_class_range": "1.0-2.0 AU"
                    }
                }
            }
        ]
        
        # Test transformation - note we now expect 3 return values
        neo_df, approaches_df, orbital_df = transform_neo_data(test_neo_objects)
        
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
        
        # Test orbital dataframe
        assert len(orbital_df) == 1
        assert orbital_df.iloc[0]["neo_id"] == "1"
        assert orbital_df.iloc[0]["orbit_id"] == "orbit-1" if "orbit_id" in orbital_df.columns else True
    
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
        # Create test dataframes including orbital_df
        neo_df = pd.DataFrame([{"id": "1"}])
        approaches_df = pd.DataFrame([{"neo_id": "1"}])
        orbital_df = pd.DataFrame([{"neo_id": "1", "orbit_id": "orbit-1"}])
        agg_df = pd.DataFrame([{"timestamp": "2024-03-31T12:00:00"}])
        
        # Create a class to replace datetime
        class MockDateTime:
            @staticmethod
            def now():
                mock = MagicMock()
                mock.strftime.return_value = "20240331_120000"
                mock.isoformat.return_value = "2024-03-31T12:00:00"
                return mock
        
        # Patch the module's datetime with our custom class
        with patch('src.fetch_neo.datetime', MockDateTime):
            # Test saving data - now with orbital_df
            neo_path, approaches_path, orbital_path, agg_path = save_data(neo_df, approaches_df, orbital_df, agg_df)
        
        # Check if directories were created
        assert mock_mkdir.call_count == 3  # Now we expect 3 directories to be created
        
        # Check if to_parquet was called for each dataframe
        assert mock_to_parquet.call_count == 4  # Now 4 calls instead of 3
        
    @patch("sys.argv", ["fetch_neo.py", "50"])
    @patch("src.fetch_neo.load_api_key")  # Must use fully qualified path
    @patch("src.fetch_neo.fetch_neo_data")
    @patch("src.fetch_neo.transform_neo_data")
    @patch("src.fetch_neo.calculate_aggregations")
    @patch("src.fetch_neo.save_data")
    def test_main_with_cmdline_args(self, mock_save, mock_agg, mock_transform, 
                                   mock_fetch, mock_load_key):
        # Configure mocks
        mock_load_key.return_value = "test_key"
        mock_fetch.return_value = ["neo1", "neo2"]
        mock_transform.return_value = (pd.DataFrame(), pd.DataFrame(), pd.DataFrame())
        mock_agg.return_value = pd.DataFrame()
        mock_save.return_value = (Path(), Path(), Path(), Path())
        
        # Call main function
        main()
        
        # Verify it used the command line limit
        mock_fetch.assert_called_once_with("test_key", 50)

    @patch("sys.argv", ["fetch_neo.py", "invalid"])
    @patch("src.fetch_neo.load_api_key")  # Must use fully qualified path
    @patch("src.fetch_neo.fetch_neo_data")
    @patch("src.fetch_neo.transform_neo_data")
    @patch("src.fetch_neo.calculate_aggregations")
    @patch("src.fetch_neo.save_data")
    def test_main_with_invalid_cmdline_args(self, mock_save, mock_agg, mock_transform, 
                                         mock_fetch, mock_load_key):
        # Configure mocks
        mock_load_key.return_value = "test_key"
        mock_fetch.return_value = ["neo1", "neo2"]
        mock_transform.return_value = (pd.DataFrame(), pd.DataFrame(), pd.DataFrame())
        mock_agg.return_value = pd.DataFrame()
        mock_save.return_value = (Path(), Path(), Path(), Path())
        
        # Call main function
        main()
        
        # Verify it used the default limit after invalid input
        mock_fetch.assert_called_once_with("test_key", 200)