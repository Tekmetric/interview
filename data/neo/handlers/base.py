from abc import ABC, abstractmethod
from typing import List, Dict
from neo.handlers.schema import Columns


class DataHandlerBase(ABC):
    """
    Abstract base class for processing NEO data.
    """

    @abstractmethod
    def to_dataframe(self, records: List[Dict]):
        """
        Convert raw records into a structured DataFrame-like object.
        """
        pass

    @abstractmethod
    def select_columns(self, data):
        """
        Select and rename the required columns from the data.
        """
        pass
