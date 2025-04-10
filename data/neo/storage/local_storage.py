import pandas as pd
from pathlib import Path

from neo.storage.base import BaseStorage
from neo.logger import logger


class LocalStorage(BaseStorage):
    """
    A storage handler for saving and loading data from the local file system.
    Includes an optional root directory for storing files.
    """

    def __init__(self, data_directory: str = "data"):
        """
        Initialize the LocalStorage with an optional root data directory.
        """
        self.data_directory = Path(data_directory).resolve()
        self.data_directory.mkdir(parents=True, exist_ok=True)

    def _save_pandas_dataframe(self, df: pd.DataFrame, path: str) -> None:
        """
        Save a pandas DataFrame to local storage as a parquet file.
        """
        df.to_parquet(path, index=False, compression="snappy")

    def save(self, data, path: str) -> None:
        """
        Save data to local storage.
        """
        file_path = self.data_directory / Path(path)
        logger.info(f"Saving file to: {path}")

        file_path.parent.mkdir(parents=True, exist_ok=True)

        if isinstance(data, pd.DataFrame):
            # Can be extended to support other formats as well
            self._save_pandas_dataframe(data, file_path)
        else:
            logger.warning("Currently only Pandas DF is supported for saving.")
