import asyncio
import sys

import structlog
from pydantic import ValidationError

from neo_data_analyser.neo_data_fetcher import NeoDataFetcher
from neo_data_analyser.settings import get_settings

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

    fetcher = NeoDataFetcher()
    async for _ in fetcher.fetch_near_earth_objects():
        pass
