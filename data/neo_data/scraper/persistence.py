from abc import ABC, abstractmethod
import json

class DataPersister(ABC):
    """ Abstract base class for persisting JSON data.
    This class defines the interface for persisting JSON data. Any subclass
    must implement the `persist` method to handle the actual persistence logic.
    Methods:
        persist(json_data: str):
            Persists the given JSON data. This method must be implemented by
            subclasses.
    """

    @abstractmethod
    def persist(self, json_data: str):
        """
        Persists the given JSON data.
        Args:
            json_data (str): A string containing JSON data to be persisted.
        """

        pass


class PrintPersister(DataPersister):
    """A class used to persist data by printing it to the console."""
    
    def persist(self, json_data: str):
        print(json_data)
