import asyncio
import sys

import structlog
from pydantic import ValidationError

from neo_data_analyser.data_fetcher import NeoDataFetcher
from neo_data_analyser.processors import (
    CloseApproachesPerYearAggregator,
    CloserThan02AuAggregator,
    Ingester,
    ProcessorManager,
)
from neo_data_analyser.settings import get_settings
from neo_data_analyser.storage import LocalStorage

logger = structlog.get_logger()


def main() -> None:
    asyncio.run(_main())


async def _main() -> None:
    try:
        get_settings()
    except ValidationError as exc:
        logger.error(  # noqa: TRY400
            "Environment is not configured properly.",
            reason=[(error["type"], error["loc"]) for error in exc.errors()],
        )
        sys.exit(1)

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
