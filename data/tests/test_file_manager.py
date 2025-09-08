import pytest
import pandas as pd
import os
import tempfile
import shutil
from pathlib import Path
from datetime import datetime
from utils.file_manager import FileManager


class TestFileManager:
    
    @pytest.fixture
    def temp_file_manager(self):
        temp_dir = tempfile.mkdtemp()
        manager = FileManager(base_path=temp_dir)
        yield manager
        shutil.rmtree(temp_dir)

    def test_init_creates_directories(self, temp_file_manager):
        assert temp_file_manager.raw_path.exists()
        assert temp_file_manager.aggregated_path.exists()

    def test_get_partitioned_path_default_timestamp(self, temp_file_manager):
        path = temp_file_manager._get_partitioned_path()
        current_year = datetime.now().strftime("%Y")
        current_month = datetime.now().strftime("%m")
        
        assert f"year={current_year}" in str(path)
        assert f"month={current_month}" in str(path)

    def test_get_partitioned_path_custom_timestamp(self, temp_file_manager):
        custom_date = datetime(2023, 6, 15)
        path = temp_file_manager._get_partitioned_path(custom_date)
        
        assert "year=2023" in str(path)
        assert "month=06" in str(path)

    def test_save_raw_data(self, temp_file_manager, sample_processed_dataframe):
        file_path = temp_file_manager.save_raw_data(sample_processed_dataframe)
        
        assert os.path.exists(file_path)
        assert file_path.endswith(".parquet")
        
        loaded_df = pd.read_parquet(file_path)
        assert len(loaded_df) == 2
        assert "id" in loaded_df.columns

    def test_save_raw_data_custom_filename(self, temp_file_manager, sample_processed_dataframe):
        file_path = temp_file_manager.save_raw_data(sample_processed_dataframe, "custom.parquet")
        
        assert file_path.endswith("custom.parquet")
        assert os.path.exists(file_path)

    def test_save_aggregations(self, temp_file_manager):
        aggregations = {
            "close_approaches_under_02_au": 5,
            "yearly_approaches": {2024: 3, 2023: 2},
            "processed_objects": []
        }
        
        file_path = temp_file_manager.save_aggregations(aggregations)
        
        assert os.path.exists(file_path)
        assert file_path.endswith("summary_stats.parquet")
        
        yearly_file = temp_file_manager.aggregated_path / "yearly_approaches.parquet"
        assert yearly_file.exists()
        
        yearly_df = pd.read_parquet(yearly_file)
        assert len(yearly_df) == 2
        assert set(yearly_df["year"]) == {2024, 2023}

    def test_save_aggregations_empty_yearly_data(self, temp_file_manager):
        aggregations = {
            "close_approaches_under_02_au": 0,
            "yearly_approaches": {},
            "processed_objects": []
        }
        
        file_path = temp_file_manager.save_aggregations(aggregations)
        
        assert os.path.exists(file_path)
        
        yearly_file = temp_file_manager.aggregated_path / "yearly_approaches.parquet"
        assert yearly_file.exists()
        
        yearly_df = pd.read_parquet(yearly_file)
        assert len(yearly_df) == 0

    def test_load_latest_data_no_files(self, temp_file_manager):
        df = temp_file_manager.load_latest_data()
        assert df.empty

    def test_load_latest_data_with_files(self, temp_file_manager, sample_processed_dataframe):
        temp_file_manager.save_raw_data(sample_processed_dataframe, "test1.parquet")
        
        loaded_df = temp_file_manager.load_latest_data()
        assert len(loaded_df) == 2
        assert "id" in loaded_df.columns

    def test_load_latest_data_multiple_files(self, temp_file_manager, sample_processed_dataframe):
        temp_file_manager.save_raw_data(sample_processed_dataframe, "test1.parquet")
        
        modified_df = sample_processed_dataframe.copy()
        modified_df.loc[0, "name"] = "Modified"
        temp_file_manager.save_raw_data(modified_df, "test2.parquet")
        
        loaded_df = temp_file_manager.load_latest_data()
        assert loaded_df.iloc[0]["name"] == "Modified"

    def test_get_data_summary_no_files(self, temp_file_manager):
        summary = temp_file_manager.get_data_summary()
        
        assert summary["raw_files_count"] == 0
        assert summary["aggregated_files_count"] == 0
        assert summary["latest_raw_file"] is None
        assert summary["total_size_mb"] == 0

    def test_get_data_summary_with_files(self, temp_file_manager, sample_processed_dataframe):
        temp_file_manager.save_raw_data(sample_processed_dataframe)
        temp_file_manager.save_aggregations({
            "close_approaches_under_02_au": 1,
            "yearly_approaches": {2024: 1},
            "processed_objects": []
        })
        
        summary = temp_file_manager.get_data_summary()
        
        assert summary["raw_files_count"] == 1
        assert summary["aggregated_files_count"] == 2
        assert summary["latest_raw_file"] is not None
        assert summary["total_size_mb"] > 0

    def test_save_raw_data_creates_partitioned_structure(self, temp_file_manager, sample_processed_dataframe):
        file_path = temp_file_manager.save_raw_data(sample_processed_dataframe)
        
        path_parts = Path(file_path).parts
        assert "year=" in path_parts[-3]
        assert "month=" in path_parts[-2]

    def test_save_raw_data_handles_empty_dataframe(self, temp_file_manager):
        empty_df = pd.DataFrame()
        file_path = temp_file_manager.save_raw_data(empty_df)
        
        assert os.path.exists(file_path)
        
        loaded_df = pd.read_parquet(file_path)
        assert len(loaded_df) == 0

    def test_save_aggregations_handles_missing_processed_objects(self, temp_file_manager):
        aggregations = {
            "close_approaches_under_02_au": 2,
            "yearly_approaches": {2024: 2}
        }
        
        file_path = temp_file_manager.save_aggregations(aggregations)
        
        assert os.path.exists(file_path)
        
        summary_df = pd.read_parquet(file_path)
        assert summary_df.iloc[0]["total_objects"] == 0
