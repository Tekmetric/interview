import asyncio
from collections.abc import AsyncGenerator
from itertools import batched

import structlog

from neo_data_analyser.models import NearEarthObject
from neo_data_analyser.neo_api import BROWSE_ITEMS_PER_PAGE, NeoApi

from .data_fetcher import DataFetcher

logger = structlog.get_logger()

# To avoid using too much memory, or overloading the API, we limit the number of
# concurrent requests to the API.
MAX_PARALLEL_REQUESTS = 5


class NeoDataFetcher(DataFetcher):
    async def fetch_near_earth_objects(self, objects_count: int = 20) -> AsyncGenerator[list[NearEarthObject]]:
        pages_count_to_fetch = objects_count // BROWSE_ITEMS_PER_PAGE
        # If the number of objects is not divisible by the number of items per page,
        # we need to fetch one more page
        if objects_count % BROWSE_ITEMS_PER_PAGE != 0:
            pages_count_to_fetch += 1

        logger.info(
            "Fetching data from Neo API",
            pages_count=pages_count_to_fetch,
            objects_count=objects_count,
        )

        async with NeoApi() as api:
            for page_batch in batched(list(range(pages_count_to_fetch)), MAX_PARALLEL_REQUESTS):
                api_requests = [api.browse(page=page) for page in page_batch]
                for objects_batch in asyncio.as_completed(api_requests):
                    yield await objects_batch

        logger.info(
            "Finished fetching data from Neo API",
        )
