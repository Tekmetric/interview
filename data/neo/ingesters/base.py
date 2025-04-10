from abc import ABC, abstractmethod
from typing import List, Dict


class DataIngesterBase(ABC):
    """
    Abstract base class for data ingesters that retrieve data.
    Easily extendable for different data sources (e.g., APIs, databases).
    """

    @abstractmethod
    def fetch_objects(self, limit: int) -> List[Dict]:
        """
        Fetch objects from a source and return them as a list of dicts.
        """
        pass
