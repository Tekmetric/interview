"""
Test configuration management

This demonstrates how the modular structure makes testing easy
"""

import sys
from pathlib import Path

# Add current directory to path for testing
sys.path.insert(0, str(Path(__file__).parent.parent))

from src.config import Config, APIConfig, SparkConfig, StorageConfig


def test_api_config():
    """Test API configuration"""
    config = APIConfig(api_key="test-key")
    
    assert config.api_key == "test-key"
    assert config.base_url == "https://ssd-api.jpl.nasa.gov"
    assert config.close_approach_endpoint == "https://ssd-api.jpl.nasa.gov/cad.api"
    assert config.sbdb_endpoint == "https://ssd-api.jpl.nasa.gov/sbdb.api"
    print("✅ API config tests passed")


def test_spark_config():
    """Test Spark configuration"""
    config = SparkConfig()
    
    spark_configs = config.to_spark_configs()
    
    assert "spark.sql.adaptive.enabled" in spark_configs
    assert spark_configs["spark.sql.adaptive.enabled"] == "true"
    assert spark_configs["spark.serializer"] == "org.apache.spark.serializer.KryoSerializer"
    print("✅ Spark config tests passed")


def test_storage_config():
    """Test storage configuration"""
    config = StorageConfig()
    
    raw_path = config.get_raw_data_path(2025)
    processed_path = config.get_processed_data_path(2025)
    agg_path = config.get_aggregations_path(2025)
    
    assert "raw/neo/year=2025" in str(raw_path)
    assert "processed/neo/year=2025" in str(processed_path)
    assert "aggregations/neo/year=2025" in str(agg_path)
    print("✅ Storage config tests passed")


def test_main_config():
    """Test main configuration integration"""
    config = Config()
    
    assert hasattr(config, 'api')
    assert hasattr(config, 'spark')
    assert hasattr(config, 'storage')
    assert hasattr(config, 'processing')
    
    assert config.processing.object_limit == 200
    assert config.processing.close_approach_threshold_au == 0.2
    print("✅ Main config tests passed")


def run_tests():
    """Run all configuration tests"""
    print("🧪 Running configuration tests...")
    print("-" * 40)
    
    test_api_config()
    test_spark_config()
    test_storage_config()
    test_main_config()
    
    print("-" * 40)
    print("🎉 All configuration tests passed!")


if __name__ == "__main__":
    run_tests() 