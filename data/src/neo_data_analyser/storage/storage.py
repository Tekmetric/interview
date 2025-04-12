from abc import abstractmethod
from pathlib import Path

import pandas as pd


class Storage:
    def __init__(self) -> None:
        self._create_files_directory()

    def _create_files_directory(self) -> None:
        # Set "exist_ok" to True to avoid raising an error if the directory already exists
        Path("files").mkdir(exist_ok=True)

    def _add_directory_to_file_name(self, file_name: str) -> str:
        """Add the directory to the file name."""
        return str(Path("files") / file_name)

    @abstractmethod
    def store_dataframe_to_parquet_file(
        self,
        *,
        dataframe: pd.DataFrame,
        file_name: str,
    ) -> None:
        """Store the given dataframe to a file."""
