import asyncio
import sys

import structlog
from pydantic import ValidationError

from neo_data_analyser.config import get_settings
from neo_data_analyser.neo_api import NeoApi

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

    async with NeoApi() as api:
        await api.browse()
