from abc import ABC, abstractmethod
import pandas as pd
import os


class Loader(ABC):
    @abstractmethod
    def write(self, data: bytes, path: str, filename: str):
        pass

    @abstractmethod
    def read(self, path, filename: str) -> pd.DataFrame:
        pass
    
    @property
    @abstractmethod
    def file_extension(self) -> str:
        pass
  
    def concat_file_path(self, path: str, filename: str) -> str:
        return f"{path}{os.sep}{filename}{self.file_extension}"


class LoaderJSON(Loader):

    @property
    def file_extension(self) -> str:
        return ".json"

    def write(self, data: bytes, path: str, filename:str):
        os.makedirs(path, exist_ok=True)
        with open(self.concat_file_path(path, filename), 'wb') as f:
            f.write(data)

    def read(self, path: str, filename: str) -> pd.DataFrame:
        return pd.read_json(self.concat_file_path(path, filename))


class LoaderParquet(Loader):
    @property
    def file_extension(self) -> str:
        return ".parquet"

    def write(self, data: bytes, path: str, filename:str):
        os.makedirs(path, exist_ok=True)
        with open(self.concat_file_path(path, filename), 'wb') as f:
            f.write(data)

    def read(self, path: str, filename: str) -> pd.DataFrame:
        return pd.read_parquet(self.concat_file_path(path, filename))
