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


class MetricRegistry:
    """
    Factory class for creating metrics.
    """
    _registry = {}

    @classmethod
    def register(cls, client_type: str):
        def decorator(client_class):
            cls._registry[client_type] = client_class
            return client_class

        return decorator

    @classmethod
    def get(cls, metric_type: str, **kwargs):
        """
        Get a metric by type.
        :param metric_type: The type of metric to create (e.g., "close_approach").
        :return: The created metric.
        """
        metric_cls = cls._registry.get(metric_type)
        if not metric_cls:
            raise ValueError(f"Unknown metric type: {metric_type}")

        return metric_cls(**kwargs)


@MetricRegistry.register("close_approach")
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
