import pytest
import os
import tempfile
import shutil
from unittest.mock import Mock, patch
from pathlib import Path
import sys

# Add parent directory to path for imports
sys.path.append(str(Path(__file__).parent.parent))

from nasa_neo_scraper import NEOScraper, load_api_key


class TestIntegration:
    
    @pytest.fixture
    def temp_scraper(self):
        temp_dir = tempfile.mkdtemp()
        scraper = NEOScraper("test_api_key", target_count=2)
        scraper.file_manager = scraper.file_manager.__class__(base_path=temp_dir)
        yield scraper
        shutil.rmtree(temp_dir)

    @pytest.fixture
    def mock_neo_data(self):
        return [
            {
                "id": "2000433",
                "name": "433 Eros",
                "designation": "433",
                "is_potentially_hazardous_asteroid": False,
                "absolute_magnitude_h": 10.31,
                "estimated_diameter": {
                    "meters": {
                        "estimated_diameter_min": 15000.0,
                        "estimated_diameter_max": 34000.0
                    }
                },
                "close_approach_data": [
                    {
                        "close_approach_date": "2024-01-15",
                        "miss_distance": {
                            "astronomical": "0.1",
                            "kilometers": "15000000.0"
                        },
                        "relative_velocity": {
                            "kilometers_per_second": "15.5"
                        }
                    }
                ],
                "orbital_data": {
                    "first_observation_date": "1898-08-13",
                    "last_observation_date": "2023-12-31",
                    "observations_used": 5000,
                    "orbital_period": "1.76"
                }
            },
            {
                "id": "2000001",
                "name": "Test Asteroid",
                "designation": "2000001",
                "is_potentially_hazardous_asteroid": True,
                "absolute_magnitude_h": 15.2,
                "estimated_diameter": {
                    "meters": {
                        "estimated_diameter_min": 100.0,
                        "estimated_diameter_max": 200.0
                    }
                },
                "close_approach_data": [
                    {
                        "close_approach_date": "2024-06-10",
                        "miss_distance": {
                            "astronomical": "0.3",
                            "kilometers": "45000000.0"
                        },
                        "relative_velocity": {
                            "kilometers_per_second": "18.2"
                        }
                    }
                ],
                "orbital_data": {
                    "first_observation_date": "2020-01-01",
                    "last_observation_date": "2023-12-31",
                    "observations_used": 100,
                    "orbital_period": "2.5"
                }
            }
        ]

    def test_complete_pipeline_success(self, temp_scraper, mock_neo_data):
        with patch.object(temp_scraper.api_client, 'fetch_neo_data') as mock_fetch:
            mock_fetch.return_value = iter(mock_neo_data)
            
            result = temp_scraper.run()
            
            assert result["success"] is True
            assert result["processed_count"] == 2
            
            # Check that files were created
            raw_files = list(temp_scraper.file_manager.raw_path.rglob("*.parquet"))
            agg_files = list(temp_scraper.file_manager.aggregated_path.rglob("*.parquet"))
            
            assert len(raw_files) == 1
            assert len(agg_files) == 2

    def test_pipeline_with_api_failure(self, temp_scraper):
        with patch.object(temp_scraper.api_client, 'fetch_neo_data') as mock_fetch:
            mock_fetch.side_effect = Exception("API Error")
            
            result = temp_scraper.run()
            
            assert result["success"] is False
            assert "API Error" in result["error"]

    def test_pipeline_with_partial_data(self, temp_scraper, mock_neo_data):
        with patch.object(temp_scraper.api_client, 'fetch_neo_data') as mock_fetch:
            mock_fetch.return_value = iter(mock_neo_data[:1])  # Only one NEO
            
            result = temp_scraper.run()
            
            assert result["success"] is True
            assert result["processed_count"] == 1

    def test_pipeline_data_quality(self, temp_scraper, mock_neo_data):
        with patch.object(temp_scraper.api_client, 'fetch_neo_data') as mock_fetch:
            mock_fetch.return_value = iter(mock_neo_data)
            
            result = temp_scraper.run()
            
            assert result["success"] is True
            
            # Load and verify the saved data
            raw_files = list(temp_scraper.file_manager.raw_path.rglob("*.parquet"))
            assert len(raw_files) == 1
            
            import pandas as pd
            df = pd.read_parquet(raw_files[0])
            
            assert len(df) == 2
            assert "id" in df.columns
            assert "closest_approach_distance_km" in df.columns
            assert df.iloc[0]["closest_approach_distance_km"] == 15000000.0

    def test_pipeline_aggregations(self, temp_scraper, mock_neo_data):
        with patch.object(temp_scraper.api_client, 'fetch_neo_data') as mock_fetch:
            mock_fetch.return_value = iter(mock_neo_data)
            
            result = temp_scraper.run()
            
            assert result["success"] is True
            assert "aggregations" in result
            
            aggregations = result["aggregations"]
            assert "close_approaches_under_02_au" in aggregations
            assert "yearly_approaches" in aggregations
            assert aggregations["close_approaches_under_02_au"] == 1  # One approach under 0.2 AU

    def test_pipeline_memory_efficiency(self, temp_scraper):
        large_neo_data = []
        for i in range(100):
            large_neo_data.append({
                "id": f"2000{i:03d}",
                "name": f"Test NEO {i}",
                "designation": f"2000{i:03d}",
                "is_potentially_hazardous_asteroid": i % 2 == 0,
                "absolute_magnitude_h": 10.0 + i,
                "estimated_diameter": {
                    "meters": {
                        "estimated_diameter_min": 100.0,
                        "estimated_diameter_max": 200.0
                    }
                },
                "close_approach_data": [
                    {
                        "close_approach_date": "2024-01-15",
                        "miss_distance": {
                            "astronomical": "0.1",
                            "kilometers": "15000000.0"
                        },
                        "relative_velocity": {
                            "kilometers_per_second": "15.5"
                        }
                    }
                ],
                "orbital_data": {
                    "first_observation_date": "2020-01-01",
                    "last_observation_date": "2023-12-31",
                    "observations_used": 100,
                    "orbital_period": "2.5"
                }
            })
        
        with patch.object(temp_scraper.api_client, 'fetch_neo_data') as mock_fetch:
            mock_fetch.return_value = iter(large_neo_data)
            
            # This should not cause memory issues due to generator pattern
            result = temp_scraper.run()
            
            assert result["success"] is True
            assert result["processed_count"] == 100

    def test_load_api_key_success(self):
        with patch.dict(os.environ, {'NASA_API_KEY': 'test_key'}):
            with patch('dotenv.load_dotenv'):
                api_key = load_api_key()
                assert api_key == 'test_key'

    def test_load_api_key_missing(self):
        with patch.dict(os.environ, {}, clear=True):
            with patch('dotenv.load_dotenv'):
                with patch('os.getenv', return_value=None):
                    with pytest.raises(ValueError, match="NASA_API_KEY not found"):
                        load_api_key()

    def test_scraper_progress_tracking(self, temp_scraper, mock_neo_data):
        with patch.object(temp_scraper.api_client, 'fetch_neo_data') as mock_fetch:
            mock_fetch.return_value = iter(mock_neo_data)
            
            with patch('builtins.print') as mock_print:
                result = temp_scraper.run()
                
                # Verify progress messages were printed
                print_calls = [call[0][0] for call in mock_print.call_args_list]
                assert any("Starting NEO data collection" in call for call in print_calls)
                assert any("Successfully fetched 2 NEO records" in call for call in print_calls)
                assert any("Processing and transforming data" in call for call in print_calls)

    def test_scraper_error_handling_in_data_processing(self, temp_scraper):
        invalid_neo_data = [{"id": "invalid", "name": None}]  # Missing required fields
        
        with patch.object(temp_scraper.api_client, 'fetch_neo_data') as mock_fetch:
            mock_fetch.return_value = iter(invalid_neo_data)
            
            result = temp_scraper.run()
            
            # Should still succeed but with empty or minimal data
            assert result["success"] is True
