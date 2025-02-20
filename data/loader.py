import pandas as pd

class Loader:
	def __init__(self, path: str):
		self.path = path

	def write(self, data: pd.DataFrame):
		data.to_parquet(self.path)

	def read(self) -> pd.DataFrame:
		return pd.read_parquet(self.path)
