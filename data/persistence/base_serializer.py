from abc import ABC, abstractmethod
from datetime import datetime
from pathlib import Path

from pydantic import BaseModel


class BaseSerializer(ABC):
    """
    Base class for serializers that store validated objects to disk.
    """

    def __init__(self, output_dir: str, object_count: int):
        """Initialize the serializer with the output directory and object count.

        Args:
            output_dir (str): The directory where the output files will be saved.
            object_count (int): The number of objects to be stored in each output file.
        """
        self.output_dir = Path(output_dir)
        self.object_count = object_count
        self.current_count = 0
        self.output_dir.mkdir(parents=True, exist_ok=True)

    @property
    @abstractmethod
    def extension(self) -> str:
        """File extension for the output file (without dot)"""
        pass

    def _get_unique_output_file(self) -> Path:
        """
        Generate a unique output file path using the class's extension.
        """
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        counter = 1
        output_file = self.output_dir / f"asteroids_{timestamp}_{counter}.{self.extension}"
        while output_file.exists():
            counter += 1
            output_file = self.output_dir / f"asteroids_{timestamp}_{counter}.{self.extension}"
        return output_file

    @abstractmethod
    def add(self, obj: BaseModel) -> None:
        """Add a single object to the serializer"""
        pass

    @abstractmethod
    def flush(self) -> None:
        """Flush all pending objects to disk"""
        pass
