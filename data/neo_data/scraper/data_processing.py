from abc import ABC, abstractmethod
import json
from omegaconf import DictConfig

class DataProcessor(ABC):
    """ 
    """

    def __init__(self, cfg: DictConfig):
        self.cfg = cfg

    @abstractmethod
    def process_json_data(self, json_data: dict):
        """
        Proocess the given JSON data dictionary according to it's implementation.
        Args:
            json_data (dict): A string containing JSON data to be persisted.
        """

        pass


class PrintProcessor(DataProcessor):
    """A class used to process NEO data by printing it to console."""
    
    def process_json_data(self, json_data: dict):
        print(f"data type: {type(json_data)}")
        print("-----------------------------")
        print(json_data)

class DataFrameProcessor(DataProcessor):
    """A class used to process NEO data. The data is passed as a JSON string and converted to a Pandas DataFrame before being written to the file."""
    
    def process_json_data(self, json_data: dict):
        pass