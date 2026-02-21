"""Unit tests for configuration management"""

import os
import pytest
from unittest.mock import patch
from src.config import PipelineConfig


class TestPipelineConfigFromEnv:
    """Test suite for PipelineConfig.from_env() method"""
    
    def test_from_env_with_all_variables(self):
        """Test from_env() loads all environment variables correctly"""
        env_vars = {
            'NASA_API_KEY': 'test_api_key_12345',
            'NASA_API_BASE_URL': 'https://custom.api.nasa.gov',
            'MAX_OBJECTS': '500',
            'BATCH_SIZE': '2000',
            'BASE_DATA_PATH': '/custom/data/path'
        }
        
        with patch.dict(os.environ, env_vars, clear=True):
            config = PipelineConfig.from_env()
            
            assert config.api_key == 'test_api_key_12345'
            assert config.api_base_url == 'https://custom.api.nasa.gov'
            assert config.max_objects == 500
            assert config.batch_size == 2000
            assert config.base_data_path == '/custom/data/path'
    
    def test_from_env_with_only_required_variables(self):
        """Test from_env() uses defaults when optional variables are missing"""
        env_vars = {
            'NASA_API_KEY': 'test_api_key_67890'
        }
        
        with patch.dict(os.environ, env_vars, clear=True):
            config = PipelineConfig.from_env()
            
            assert config.api_key == 'test_api_key_67890'
            # Check defaults are used
            assert config.api_base_url == "https://api.nasa.gov/neo/rest/v1"
            assert config.max_objects == 200
            assert config.batch_size == 1000
            assert config.base_data_path == "s3"
    
    def test_from_env_missing_api_key_raises_error(self):
        """Test from_env() raises ValueError when NASA_API_KEY is missing"""
        with patch.dict(os.environ, {}, clear=True):
            with pytest.raises(ValueError) as exc_info:
                PipelineConfig.from_env()
            
            assert "NASA_API_KEY environment variable is required" in str(exc_info.value)
    
    def test_from_env_empty_api_key_raises_error(self):
        """Test from_env() raises ValueError when NASA_API_KEY is empty string"""
        env_vars = {'NASA_API_KEY': ''}
        
        with patch.dict(os.environ, env_vars, clear=True):
            with pytest.raises(ValueError) as exc_info:
                PipelineConfig.from_env()
            
            assert "NASA_API_KEY environment variable is required" in str(exc_info.value)
    
    def test_from_env_numeric_conversion(self):
        """Test from_env() correctly converts string environment variables to integers"""
        env_vars = {
            'NASA_API_KEY': 'test_key',
            'MAX_OBJECTS': '999',
            'BATCH_SIZE': '5000'
        }
        
        with patch.dict(os.environ, env_vars, clear=True):
            config = PipelineConfig.from_env()
            
            assert isinstance(config.max_objects, int)
            assert isinstance(config.batch_size, int)
            assert config.max_objects == 999
            assert config.batch_size == 5000


class TestPipelineConfigPathGeneration:
    """Test suite for path generation methods"""
    
    def test_get_raw_path_basic(self):
        """Test get_raw_path() generates correct path with basic inputs"""
        config = PipelineConfig(api_key='test_key')
        
        path = config.get_raw_path(2024, 1, 15)
        
        assert path == "s3/raw/neo/year=2024/month=01/day=15"
    
    def test_get_raw_path_with_custom_base_path(self):
        """Test get_raw_path() respects custom base_data_path"""
        config = PipelineConfig(
            api_key='test_key',
            base_data_path='/custom/base'
        )
        
        path = config.get_raw_path(2024, 1, 15)
        
        assert path == "/custom/base/raw/neo/year=2024/month=01/day=15"
    
    def test_get_raw_path_zero_pads_month_and_day(self):
        """Test get_raw_path() zero-pads single-digit months and days"""
        config = PipelineConfig(api_key='test_key')
        
        # Test single-digit month and day
        path = config.get_raw_path(2024, 3, 7)
        assert path == "s3/raw/neo/year=2024/month=03/day=07"
        
        # Test double-digit month and day
        path = config.get_raw_path(2024, 12, 31)
        assert path == "s3/raw/neo/year=2024/month=12/day=31"
    
    def test_get_raw_path_various_years(self):
        """Test get_raw_path() handles various year values"""
        config = PipelineConfig(api_key='test_key')
        
        # Test different years
        path_2020 = config.get_raw_path(2020, 6, 15)
        path_2025 = config.get_raw_path(2025, 6, 15)
        
        assert "year=2020" in path_2020
        assert "year=2025" in path_2025
    
    def test_get_curated_path_basic(self):
        """Test get_curated_path() generates correct base path"""
        config = PipelineConfig(api_key='test_key')
        
        path = config.get_curated_path()
        
        assert path == "s3/curated/neo"
    
    def test_get_curated_path_with_custom_base_path(self):
        """Test get_curated_path() respects custom base_data_path"""
        config = PipelineConfig(
            api_key='test_key',
            base_data_path='/custom/base'
        )
        
        path = config.get_curated_path()
        
        assert path == "/custom/base/curated/neo"
    
    def test_get_curated_path_with_custom_layer_path(self):
        """Test get_curated_path() respects custom curated_layer_path"""
        config = PipelineConfig(
            api_key='test_key',
            curated_layer_path='processed/asteroids'
        )
        
        path = config.get_curated_path()
        
        assert path == "s3/processed/asteroids"
    
    def test_get_aggregates_path_basic(self):
        """Test get_aggregates_path() generates correct path for aggregate type"""
        config = PipelineConfig(api_key='test_key')
        
        path = config.get_aggregates_path('close_approaches')
        
        assert path == "s3/aggregates/neo/close_approaches"
    
    def test_get_aggregates_path_multiple_types(self):
        """Test get_aggregates_path() handles different aggregate types"""
        config = PipelineConfig(api_key='test_key')
        
        path1 = config.get_aggregates_path('close_approaches')
        path2 = config.get_aggregates_path('approaches_by_year')
        path3 = config.get_aggregates_path('custom_aggregate')
        
        assert path1 == "s3/aggregates/neo/close_approaches"
        assert path2 == "s3/aggregates/neo/approaches_by_year"
        assert path3 == "s3/aggregates/neo/custom_aggregate"
    
    def test_get_aggregates_path_with_custom_base_path(self):
        """Test get_aggregates_path() respects custom base_data_path"""
        config = PipelineConfig(
            api_key='test_key',
            base_data_path='/custom/base'
        )
        
        path = config.get_aggregates_path('close_approaches')
        
        assert path == "/custom/base/aggregates/neo/close_approaches"


class TestPipelineConfigDefaults:
    """Test suite for default configuration values"""
    
    def test_default_api_configuration(self):
        """Test default API configuration values"""
        config = PipelineConfig(api_key='test_key')
        
        assert config.api_base_url == "https://api.nasa.gov/neo/rest/v1"
        assert config.max_objects == 200
        assert config.page_size == 20
    
    def test_default_storage_configuration(self):
        """Test default storage configuration values"""
        config = PipelineConfig(api_key='test_key')
        
        assert config.base_data_path == "s3"
        assert config.raw_layer_path == "raw/neo"
        assert config.curated_layer_path == "curated/neo"
        assert config.aggregates_layer_path == "aggregates/neo"
    
    def test_default_partitioning_configuration(self):
        """Test default partitioning configuration values"""
        config = PipelineConfig(api_key='test_key')
        
        assert config.raw_partition_columns == ['ingestion_year', 'ingestion_month', 'ingestion_day']
        assert config.curated_partition_columns == ['approach_year', 'approach_month', 'approach_day']
        assert config.aggregates_partition_columns == ['year', 'month', 'day']
        assert config.compression == "snappy"
    
    def test_default_processing_configuration(self):
        """Test default processing configuration values"""
        config = PipelineConfig(api_key='test_key')
        
        assert config.batch_size == 1000
        assert config.streaming_threshold == 10000
    
    def test_default_aggregation_configuration(self):
        """Test default aggregation configuration values"""
        config = PipelineConfig(api_key='test_key')
        
        assert config.close_approach_threshold_au == 0.2


class TestPipelineConfigCustomization:
    """Test suite for custom configuration values"""
    
    def test_custom_api_configuration(self):
        """Test custom API configuration values"""
        config = PipelineConfig(
            api_key='custom_key',
            api_base_url='https://custom.api.com',
            max_objects=500,
            page_size=50
        )
        
        assert config.api_key == 'custom_key'
        assert config.api_base_url == 'https://custom.api.com'
        assert config.max_objects == 500
        assert config.page_size == 50
    
    def test_custom_storage_paths(self):
        """Test custom storage path configuration"""
        config = PipelineConfig(
            api_key='test_key',
            base_data_path='/data',
            raw_layer_path='staging/neo',
            curated_layer_path='processed/neo',
            aggregates_layer_path='analytics/neo'
        )
        
        assert config.base_data_path == '/data'
        assert config.raw_layer_path == 'staging/neo'
        assert config.curated_layer_path == 'processed/neo'
        assert config.aggregates_layer_path == 'analytics/neo'
    
    def test_custom_partitioning_columns(self):
        """Test custom partitioning column configuration"""
        config = PipelineConfig(
            api_key='test_key',
            raw_partition_columns=['year', 'month'],
            curated_partition_columns=['event_year'],
            aggregates_partition_columns=['agg_year']
        )
        
        assert config.raw_partition_columns == ['year', 'month']
        assert config.curated_partition_columns == ['event_year']
        assert config.aggregates_partition_columns == ['agg_year']
    
    def test_custom_processing_parameters(self):
        """Test custom processing parameter configuration"""
        config = PipelineConfig(
            api_key='test_key',
            batch_size=5000,
            streaming_threshold=50000,
            compression='gzip'
        )
        
        assert config.batch_size == 5000
        assert config.streaming_threshold == 50000
        assert config.compression == 'gzip'
    
    def test_custom_aggregation_threshold(self):
        """Test custom aggregation threshold configuration"""
        config = PipelineConfig(
            api_key='test_key',
            close_approach_threshold_au=0.5
        )
        
        assert config.close_approach_threshold_au == 0.5
