from abc import ABC, abstractmethod
import pandas as pd

class Loader(ABC):
    @abstractmethod
    def write(self, data: pd.DataFrame):
        pass

    @abstractmethod
    def read(self) -> pd.DataFrame:
        pass
class Parquet(Loader):
	def __init__(self, path: str):
		self.path = path

	def write(self, data: pd.DataFrame):
		data.to_parquet(self.path)

	def read(self) -> pd.DataFrame:
		return pd.read_parquet(self.path)
