import abc
from collections import defaultdict


class Metric(abc.ABC):
    """
    Base class for all metrics.
    """

    def __init__(self):
        pass

    @abc.abstractmethod
    def add(self, records: dict):
        """
        Add records to the metric.
        :param records: The records to add.
        """


class CloseApproachMetric(Metric):
    """
    Metric to track the number of near misses and the number of near misses per year.
    """

    def __init__(self, miss_limit: float = 0.2):
        super().__init__()
        self._miss_limit = miss_limit
        self._num_near_miss = 0
        self._per_year_miss = defaultdict(int)

    def add(self, records: dict) -> None:
        """
        Add records to the metric.
        :param records: The records to add.
        """
        for record in records['near_earth_objects']:
            for approach in record['close_approach_data']:
                if float(approach["miss_distance"]['astronomical']) < self._miss_limit:
                    self._num_near_miss += 1
                    year = approach["close_approach_date"].split("-")[0]
                    self._per_year_miss[year] += 1

    @property
    def number_of_near_misses(self) -> int:
        """
        Get the number of near misses.
        :return: The number of near misses.
        """
        return self._num_near_miss

    @property
    def per_year_miss(self) -> dict[str, int]:
        return self._per_year_miss

    def __repr__(self):
        return "Close Approach Metric"


class MetricFactory:
    """
    Factory class for creating metrics.
    """

    @staticmethod
    def get_metric(metric_type: str):
        """
        Get a metric by type.
        :param metric_type: The type of metric to create (e.g., "close_approach").
        :return: The created metric.
        """
        if metric_type == "close_approach":
            return CloseApproachMetric()
        else:
            raise ValueError(f"Unknown metric type: {metric_type}")
