import asyncio

import structlog

from neo_data_analyser.data_fetcher import NeoDataFetcher
from neo_data_analyser.processors import (
    CloseApproachesPerYearAggregator,
    CloserThan02AuAggregator,
    Ingester,
    ProcessorManager,
)
from neo_data_analyser.settings import check_settings
from neo_data_analyser.storage import LocalStorage

logger = structlog.get_logger()


def main() -> None:
    asyncio.run(_main())


async def _main() -> None:
    check_settings()

    storage = LocalStorage()
    fetcher = NeoDataFetcher()
    processor_manager = ProcessorManager(
        data_fetcher=fetcher,
        processors=[
            Ingester(storage=storage),
            CloserThan02AuAggregator(storage=storage),
            CloseApproachesPerYearAggregator(storage=storage),
        ],
        batch_size=10,
    )

    await processor_manager.process()
