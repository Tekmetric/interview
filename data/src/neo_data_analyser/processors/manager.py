import structlog

from neo_data_analyser.data_fetcher import DataFetcher
from neo_data_analyser.models import NearEarthObject

from .processor import Processor

logger = structlog.get_logger()


class ProcessorManager:
    def __init__(
        self,
        data_fetcher: DataFetcher,
        processors: list[Processor],
        batch_size: int = 100,
    ) -> None:
        self._processors = processors
        self._data_fetcher = data_fetcher
        self._batch_size = batch_size

    def notify_processors(self, data: list[NearEarthObject]) -> None:
        for processor in self._processors:
            processor.process(data)

    async def process(self) -> None:
        batch = []

        async for near_earth_objects_batch in self._data_fetcher.fetch_near_earth_objects():
            logger.info(
                "Processing batch of objects",
                objects_count=len(near_earth_objects_batch),
            )

            # TODO: Implement a smarter batching using list slices
            for near_earth_object in near_earth_objects_batch:
                batch.append(near_earth_object)

                if len(batch) == self._batch_size:
                    self.notify_processors(batch)
                    batch = []

        if batch:
            self.notify_processors(batch)
