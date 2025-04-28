from collections import defaultdict
from datetime import datetime
from typing import Any, Dict

from .base_aggregator import BaseAggregator


class NeoCountPerYearAggregator(BaseAggregator):
    """
    Aggregator to count the number of close approaches recorded in each year present in the data.
    """

    def __init__(self, output_dir: str, output_file: str):
        """
        Initialize the aggregator with the output directory and file name.

        Args:
            output_dir (str): The directory where the output file will be saved.
            output_file (str): The name of the output file (without extension).
        """
        super().__init__(output_dir, output_file)
        self.counts_by_year = defaultdict(int)

    def process(self, obj: Dict[str, Any]) -> None:
        """
        Process a single raw object to update the count of close approaches by year.

        Args:
            obj (Dict[str, Any]): The raw object containing the asteroid data.
        """
        if "close_approach_data" not in obj:
            return

        for approach in obj["close_approach_data"]:
            if "close_approach_date" not in approach:
                continue

            if "miss_distance" not in approach or "astronomical" not in approach["miss_distance"]:
                continue

            if float(approach["miss_distance"]["astronomical"]) < 0.2:
                try:
                    year = str(datetime.strptime(approach["close_approach_date"], "%Y-%m-%d").year)
                    self.counts_by_year[year] += 1
                except ValueError:
                    continue  # Skip if date parsing fails

    def flush(self) -> None:
        """
        Save the aggregated counts by year to disk in JSON format.
        """
        data = dict(sorted(self.counts_by_year.items()))
        self._save_json(data)
