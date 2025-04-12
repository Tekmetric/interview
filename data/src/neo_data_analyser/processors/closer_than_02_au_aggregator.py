import pandas as pd
import structlog

from neo_data_analyser.models import NearEarthObject
from neo_data_analyser.storage import Storage

from .processor import Processor

logger = structlog.get_logger(__name__)


class CloserThan02AuAggregator(Processor):
    """Processor that aggregates the closest approach data for Near Earth Objects
    that are closer than 0.2 AU.
    """

    def __init__(self, storage: Storage) -> None:
        self._storage = storage
        self._closest_approach_counter = 0
        self._closer_limit = 0.2  # AU

    def process(self, data: list[NearEarthObject]) -> None:
        for neo in data:
            if neo.closest_approach and neo.closest_approach.miss_distance.astronomical < self._closer_limit:
                self._closest_approach_counter += 1

    def finalize(self) -> None:
        self._storage.store_dataframe_to_parquet_file(
            dataframe=pd.DataFrame(
                {
                    "closest_approach_count": [self._closest_approach_counter],
                }
            ),
            file_name="closer_than_02_au_aggregator.parquet",
        )
        logger.info(
            "Stored closest approach count",
            closest_approach_count=self._closest_approach_counter,
        )
