from abc import ABC, abstractmethod
from typing import Any, Dict, Iterator, Tuple

from models.asteroid import AsteroidValidatedObject


class BaseScraper(ABC):
    @property
    @abstractmethod
    def objects(self) -> Iterator[Tuple[Dict[str, Any], AsteroidValidatedObject]]:
        """
        Yields tuples of (raw_data, processed_data) for each object
        raw_data: Dict containing the raw JSON response
        processed_data: Validated AsteroidValidatedObject
        """
        pass
