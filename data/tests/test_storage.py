import pytest
import pandas as pd
from neo.storage.local_storage import LocalStorage


@pytest.fixture
def sample_dataframe():
    """
    Fixture to provide a sample Pandas DataFrame for testing.
    """
    return pd.DataFrame(
        {
            "id": [1, 2, 3],
            "name": ["Asteroid 1", "Asteroid 2", "Asteroid 3"],
            "distance": [0.15, 0.25, 0.35],
        }
    )


@pytest.fixture
def storage(tmp_path):
    """
    Fixture to provide a LocalStorage instance with a temporary directory.
    """
    return LocalStorage(data_directory=tmp_path)


def test_save_pandas_dataframe(storage, sample_dataframe, tmp_path):
    """
    Test saving a Pandas DataFrame to local storage.
    """
    file_name = "test_data.parquet"
    storage.save(sample_dataframe, file_name)

    # Verify the file exists
    saved_file = tmp_path / file_name
    assert saved_file.exists()

    # Verify the contents of the saved file
    loaded_df = pd.read_parquet(saved_file)
    pd.testing.assert_frame_equal(loaded_df, sample_dataframe)


def test_save_creates_nested_directories(storage, sample_dataframe, tmp_path):
    """
    Test saving a Pandas DataFrame to a nested directory.
    """
    nested_path = "nested/directory/test_data.parquet"
    storage.save(sample_dataframe, nested_path)

    # Verify the file exists
    saved_file = tmp_path / nested_path
    assert saved_file.exists()

    # Verify the contents of the saved file
    loaded_df = pd.read_parquet(saved_file)
    pd.testing.assert_frame_equal(loaded_df, sample_dataframe)


def test_save_non_dataframe(storage, tmp_path, caplog):
    """
    Test saving non-DataFrame data raises a warning.
    """
    invalid_data = {"key": "value"}
    file_name = "invalid_data.parquet"

    with caplog.at_level("WARNING"):
        storage.save(invalid_data, file_name)

    assert "Currently only Pandas DF is supported for saving." in caplog.text

    # Verify the file was not created
    saved_file = tmp_path / file_name
    assert not saved_file.exists()
