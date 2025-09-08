import pytest
import pandas as pd
from utils.data_processor import NEODataProcessor


class TestNEODataProcessor:
    
    def test_init(self):
        processor = NEODataProcessor()
        assert len(processor.required_fields) == 8
        assert "id" in processor.required_fields
        assert "name" in processor.required_fields

    def test_extract_basic_info_complete_data(self, sample_neo_data):
        processor = NEODataProcessor()
        result = processor.extract_basic_info(sample_neo_data)
        
        assert result["id"] == "2000433"
        assert result["name"] == "433 Eros (A898 PA)"
        assert result["absolute_magnitude_h"] == 10.31
        assert result["is_potentially_hazardous_asteroid"] is False
        assert result["min_diameter_meters"] == 15000.0
        assert result["max_diameter_meters"] == 34000.0
        assert result["first_observation_date"] == "1898-08-13"
        assert result["orbital_period"] == "1.76"

    def test_extract_basic_info_missing_fields(self, sample_neo_data_missing_fields):
        processor = NEODataProcessor()
        result = processor.extract_basic_info(sample_neo_data_missing_fields)
        
        assert result["id"] == "2000001"
        assert result["name"] == "Test NEO"
        assert result["absolute_magnitude_h"] is None
        assert result["min_diameter_meters"] is None
        assert result["max_diameter_meters"] is None

    def test_extract_basic_info_invalid_types(self, sample_neo_data_invalid_types):
        processor = NEODataProcessor()
        result = processor.extract_basic_info(sample_neo_data_invalid_types)
        
        assert result["id"] == "2000002"
        assert result["absolute_magnitude_h"] == "not_a_number"
        assert result["is_potentially_hazardous_asteroid"] == "maybe"

    def test_find_closest_approach_normal_data(self, sample_neo_data):
        processor = NEODataProcessor()
        close_approach_data = sample_neo_data["close_approach_data"]
        
        distance, date, velocity = processor.find_closest_approach(close_approach_data)
        
        assert distance == 15000000.0
        assert date == "2024-01-15"
        assert velocity == 15.5

    def test_find_closest_approach_empty_data(self):
        processor = NEODataProcessor()
        
        distance, date, velocity = processor.find_closest_approach([])
        
        assert distance is None
        assert date is None
        assert velocity is None

    def test_find_closest_approach_missing_distance(self):
        processor = NEODataProcessor()
        close_approach_data = [
            {
                "close_approach_date": "2024-01-15",
                "miss_distance": {"kilometers": None},
                "relative_velocity": {"kilometers_per_second": "15.5"}
            }
        ]
        
        distance, date, velocity = processor.find_closest_approach(close_approach_data)
        
        assert distance is None
        assert date is None
        assert velocity is None

    def test_find_closest_approach_invalid_distance(self):
        processor = NEODataProcessor()
        close_approach_data = [
            {
                "close_approach_date": "2024-01-15",
                "miss_distance": {"kilometers": "invalid"},
                "relative_velocity": {"kilometers_per_second": "15.5"}
            }
        ]
        
        distance, date, velocity = processor.find_closest_approach(close_approach_data)
        
        assert distance is None
        assert date is None
        assert velocity is None

    def test_find_closest_approach_missing_velocity(self):
        processor = NEODataProcessor()
        close_approach_data = [
            {
                "close_approach_date": "2024-01-15",
                "miss_distance": {"kilometers": "15000000.0"},
                "relative_velocity": {"kilometers_per_second": None}
            }
        ]
        
        distance, date, velocity = processor.find_closest_approach(close_approach_data)
        
        assert distance == 15000000.0
        assert date == "2024-01-15"
        assert velocity is None

    def test_process_neo_complete_data(self, sample_neo_data):
        processor = NEODataProcessor()
        result = processor.process_neo(sample_neo_data)
        
        assert result["id"] == "2000433"
        assert result["closest_approach_distance_km"] == 15000000.0
        assert result["closest_approach_date"] == "2024-01-15"
        assert result["closest_approach_velocity_km_s"] == 15.5

    def test_process_neo_missing_close_approach(self, sample_neo_data_missing_fields):
        processor = NEODataProcessor()
        result = processor.process_neo(sample_neo_data_missing_fields)
        
        assert result["id"] == "2000001"
        assert result["closest_approach_distance_km"] is None
        assert result["closest_approach_date"] is None
        assert result["closest_approach_velocity_km_s"] is None

    def test_process_batch(self, sample_neo_data, sample_neo_data_missing_fields):
        processor = NEODataProcessor()
        neos = [sample_neo_data, sample_neo_data_missing_fields]
        
        df = processor.process_batch(neos)
        
        assert len(df) == 2
        assert "id" in df.columns
        assert "closest_approach_distance_km" in df.columns
        assert df.iloc[0]["id"] == "2000433"
        assert df.iloc[1]["id"] == "2000001"

    def test_calculate_aggregations_with_detailed_data(self, sample_processed_dataframe):
        processor = NEODataProcessor()
        detailed_data = [
            {
                "close_approach_data": [
                    {
                        "close_approach_date": "2024-01-15",
                        "miss_distance": {"astronomical": "0.1"}
                    },
                    {
                        "close_approach_date": "2024-06-15",
                        "miss_distance": {"astronomical": "0.3"}
                    }
                ]
            },
            {
                "close_approach_data": [
                    {
                        "close_approach_date": "2024-03-15",
                        "miss_distance": {"astronomical": "0.15"}
                    }
                ]
            }
        ]
        
        result = processor.calculate_aggregations(sample_processed_dataframe, detailed_data)
        
        assert result["close_approaches_under_02_au"] == 2
        assert 2024 in result["yearly_approaches"]
        assert result["yearly_approaches"][2024] == 3

    def test_calculate_aggregations_fallback_mode(self, sample_processed_dataframe):
        processor = NEODataProcessor()
        
        result = processor.calculate_aggregations(sample_processed_dataframe)
        
        assert result["close_approaches_under_02_au"] == 0
        assert 2024 in result["yearly_approaches"]
        assert result["yearly_approaches"][2024] == 2

    def test_calculate_aggregations_invalid_dates(self, sample_processed_dataframe):
        processor = NEODataProcessor()
        detailed_data = [
            {
                "close_approach_data": [
                    {
                        "close_approach_date": "invalid_date",
                        "miss_distance": {"astronomical": "0.1"}
                    }
                ]
            }
        ]
        
        result = processor.calculate_aggregations(sample_processed_dataframe, detailed_data)
        
        assert result["close_approaches_under_02_au"] == 1
        assert len(result["yearly_approaches"]) == 0

    def test_calculate_aggregations_missing_astronomical_distance(self, sample_processed_dataframe):
        processor = NEODataProcessor()
        detailed_data = [
            {
                "close_approach_data": [
                    {
                        "close_approach_date": "2024-01-15",
                        "miss_distance": {"astronomical": None}
                    }
                ]
            }
        ]
        
        result = processor.calculate_aggregations(sample_processed_dataframe, detailed_data)
        
        assert result["close_approaches_under_02_au"] == 0
        assert len(result["yearly_approaches"]) == 0

    def test_find_closest_approach_invalid_velocity(self):
        """Test handling of invalid velocity data."""
        processor = NEODataProcessor()
        close_approach_data = [
            {
                "close_approach_date": "2024-01-15",
                "miss_distance": {"kilometers": "15000000.0"},
                "relative_velocity": {"kilometers_per_second": "invalid_velocity"}
            }
        ]
        
        distance, date, velocity = processor.find_closest_approach(close_approach_data)
        
        assert distance == 15000000.0
        assert date == "2024-01-15"
        assert velocity is None

    def test_find_closest_approach_velocity_type_error(self):
        """Test handling of TypeError in velocity conversion."""
        processor = NEODataProcessor()
        close_approach_data = [
            {
                "close_approach_date": "2024-01-15",
                "miss_distance": {"kilometers": "15000000.0"},
                "relative_velocity": {"kilometers_per_second": None}
            }
        ]
        
        distance, date, velocity = processor.find_closest_approach(close_approach_data)
        
        assert distance == 15000000.0
        assert date == "2024-01-15"
        assert velocity is None

    def test_calculate_aggregations_fallback_invalid_date(self, sample_processed_dataframe):
        """Test fallback mode with invalid date handling."""
        processor = NEODataProcessor()
        
        # Create DataFrame with invalid date to trigger exception handling
        df_with_invalid_date = sample_processed_dataframe.copy()
        df_with_invalid_date.loc[0, 'closest_approach_date'] = 'invalid_date'
        
        result = processor.calculate_aggregations(df_with_invalid_date)
        
        # Should handle invalid date gracefully
        assert result["close_approaches_under_02_au"] == 0
        assert len(result["yearly_approaches"]) == 1  # Only the valid date
