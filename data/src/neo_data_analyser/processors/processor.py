from abc import ABC, abstractmethod

from neo_data_analyser.models import NearEarthObject


class Processor(ABC):
    """Base class for processors."""

    @abstractmethod
    def process(self, data: list[NearEarthObject]) -> None: ...
