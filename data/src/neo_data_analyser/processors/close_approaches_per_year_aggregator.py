from collections import defaultdict

import pandas as pd
import structlog

from neo_data_analyser.models import NearEarthObject
from neo_data_analyser.storage import Storage

from .processor import Processor

logger = structlog.get_logger(__name__)


class CloseApproachesPerYearAggregator(Processor):
    """Processor that aggregates the closest approach data."""

    def __init__(self, storage: Storage) -> None:
        self._storage = storage
        self._close_approaches_per_year: dict[int, int] = defaultdict(lambda: 0)

    def process(self, data: list[NearEarthObject]) -> None:
        for neo in data:
            for close_approach in neo.close_approach_data:
                self._close_approaches_per_year[close_approach.close_approach_date.year] += 1

    def finalize(self) -> None:
        self._storage.store_dataframe_to_parquet_file(
            dataframe=pd.DataFrame(
                [
                    {
                        "year": year,
                        "close_approach_count": count,
                    }
                    for year, count in self._close_approaches_per_year.items()
                ]
            ),
            file_name="close_approaches_per_year.parquet",
        )
        logger.info(
            "Stored closest approach per year aggregation",
            close_approaches_per_year_count=len(self._close_approaches_per_year),
        )
