from abc import ABC, abstractmethod
import pandas as pd
import os


class Loader(ABC):
    @abstractmethod
    def write(self, data: pd.DataFrame, path: str, filename: str):
        pass

    @abstractmethod
    def read(self, path, filename: str) -> pd.DataFrame:
        pass


class ParquetFileSystem(Loader):
  EXT = ".parquet"

  def concat_file_path(self, path: str, filename: str) -> str:
    return f"{path}{os.sep}{filename}{self.EXT}"

  def write(self, data: pd.DataFrame, path: str, filename:str):
    os.makedirs(path, exist_ok=True)
    data.to_parquet(self.concat_file_path(path, filename))

  def read(self, path: str, filename: str) -> pd.DataFrame:
    return pd.read_parquet(self.concat_file_path(path, filename))
