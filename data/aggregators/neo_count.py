from typing import Any, Dict

from .base_aggregator import BaseAggregator


class NeoCountAggregator(BaseAggregator):
    """
    Aggregator to count the total number of times the near earth objects
    approached closer than 0.2 astronomical units (found as miss_distance.astronomical).
    """

    def __init__(self, output_dir: str, output_file: str):
        """
        Initialize the aggregator with the output directory and file name.

        Args:
            output_dir (str): The directory where the output file will be saved.
            output_file (str): The name of the output file (without extension).
        """
        super().__init__(output_dir, output_file)
        self.total_count = 0

    def process(self, obj: Dict[str, Any]) -> None:
        """
        Process a single raw object to update the count.

        Args:
            obj (Dict[str, Any]): The raw object containing the miss distance information.
        """
        if "close_approach_data" not in obj:
            return

        for approach in obj["close_approach_data"]:
            if "miss_distance" not in approach or "astronomical" not in approach["miss_distance"]:
                continue

            if float(approach["miss_distance"]["astronomical"]) < 0.2:
                self.total_count += 1

    def flush(self) -> None:
        """
        Save the aggregated count to disk in JSON format.
        """
        data = {
            "asteroids_close_encounters": self.total_count,
        }
        self._save_json(data)
