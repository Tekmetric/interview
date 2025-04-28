import json
from abc import ABC, abstractmethod
from pathlib import Path
from typing import Any, Dict


class BaseAggregator(ABC):
    """
    Base class for aggregators that process raw objects and save them to disk.
    """

    def __init__(self, output_dir: str, output_file: str):
        """Initialize the aggregator with the output directory and file name.

        Args:
            output_dir (str): The directory where the output file will be saved.
            output_file (str): The name of the output file (without extension).
        """
        self.output_dir = Path(output_dir)
        self.output_file = output_file
        self.output_dir.mkdir(parents=True, exist_ok=True)

    @abstractmethod
    def process(self, obj: Dict[str, Any]) -> None:
        """Process a single raw object"""
        pass

    @abstractmethod
    def flush(self) -> None:
        """Save aggregated data to disk"""
        pass

    def _save_json(self, data: Dict[str, Any]) -> None:
        """Helper method to save JSON data"""
        output_path = self.output_dir / self.output_file
        with open(output_path, "w") as f:
            json.dump(data, f, indent=2)
