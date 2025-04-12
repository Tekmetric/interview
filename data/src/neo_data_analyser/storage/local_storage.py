import pandas as pd

from .storage import Storage


class LocalStorage(Storage):
    def store_dataframe_to_parquet_file(
        self,
        *,
        dataframe: pd.DataFrame,
        file_name: str,
    ) -> None:
        dataframe.to_parquet(self._add_directory_to_file_name(file_name), index=False)
