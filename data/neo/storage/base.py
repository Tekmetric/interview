from abc import ABC, abstractmethod


class BaseStorage(ABC):
    """
    Base class for storage systems (Local Storage, S3, etc.).
    Should be extended to provide save/load functionalities for specific backends.
    """

    @abstractmethod
    def save(self, data, path: str) -> None:
        """
        Save the data to the storage medium (Local or S3).
        """
        pass
