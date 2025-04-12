from abc import ABC, abstractmethod
from collections.abc import AsyncGenerator

from neo_data_analyser.models import NearEarthObject


class DataFetcher(ABC):
    """Abstract base class for data fetching."""

    @abstractmethod
    def fetch_near_earth_objects(self, objects_count: int = 20) -> AsyncGenerator[list[NearEarthObject]]:
        pass
