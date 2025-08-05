import asyncio
import math
import os
from collections.abc import Iterable
from typing import Coroutine

from aiohttp_retry import ExponentialRetry, RetryClient

from _neo.client.models import NearEarthObject

NEO_BASE_URL = "https://api.nasa.gov/neo/rest/v1/neo/browse"
"""Base URL for NASA NEO API"""


class NeosNotFoundError(Exception):
    """Raised when no Near Earth Objects are found."""


def guard_semaphore(
    coroutines: Coroutine | Iterable[Coroutine], semaphore: asyncio.Semaphore
) -> Coroutine | Iterable[Coroutine]:
    """Guard each coroutine with the same semaphore."""

    async def guard_coroutine(coroutine):
        async with semaphore:
            return await coroutine

    return (
        [guard_coroutine(coroutine) for coroutine in coroutines]
        if isinstance(coroutines, Iterable)
        else guard_coroutine(coroutines)
    )


class NasaClient:
    """
    High-level NASA API client that provides access to various sub-APIs.

    Usage example:
        async with NasaClient() as client:
            neos = await client.neo.list_entries(limit=100)
    """

    def __init__(self, api_key: str | None = None, retries: int = 3):
        """
        Initialize NASA client.

        Args:
            api_key: NASA API key. If None, read from NASA_API_KEY env var.
            retries: Number of retry attempts for requests.
        """
        self.api_key = api_key or os.getenv("NASA_API_KEY")
        if not self.api_key:
            raise EnvironmentError("NASA_API_KEY must be set in environment or passed explicitly.")
        self.retries = retries
        self.neo: NeoClient | None = None

    async def __aenter__(self) -> "NasaClient":
        self.neo = await NeoClient(self.api_key, self.retries).__aenter__()
        return self

    async def __aexit__(self, exc_type, exc_val, exc_tb) -> None:
        if self.neo:
            await self.neo.__aexit__(exc_type, exc_val, exc_tb)


class NeoClient:
    """
    Async client for NASA's Near Earth Object (NEO) API.

    Usage example:
        async with NeoClient(api_key) as neo_client:
            neos = await neo_client.list_entries(limit=100, page_size=10)
    """

    def __init__(self, api_key: str, retries: int = 3):
        """Initialize the NeoClient.

        Args:
            api_key: NASA API key.
            retries: Number of retry attempts for requests.
        """
        self.api_key = api_key
        self._retries = retries
        self._client: RetryClient | None = None

    async def __aenter__(self) -> "NeoClient":
        self._client = RetryClient(raise_for_status=True, retry_options=ExponentialRetry(attempts=self._retries))
        return self

    async def __aexit__(self, exc_type, exc_val, exc_tb) -> None:
        if self._client:
            await self._client.close()

    async def _list_page(self, page: int, page_size: int) -> list[NearEarthObject]:
        """
        List a single page of NEO data.

        Args:
            page: Zero-based page number.
            page_size: Number of items per page.

        Returns:
            List of Near Earth Object(s).
        """
        if not self._client:
            raise RuntimeError("Client must be used within an async context manager.")

        params = {
            "api_key": self.api_key,
            "page": page,
            "size": page_size,
        }

        async with self._client.get(NEO_BASE_URL, params=params) as response:
            data = await response.json()
            return [NearEarthObject.model_validate(obj) for obj in data.get("near_earth_objects", [])]

    async def list_entries(
        self, limit: int = 200, page_size: int = 20, max_concurrency: int = 10
    ) -> list[NearEarthObject]:
        """
        List NEO entries across pages.

        Args:
            limit: Maximum number of NEO entries to fetch.
            page_size: Number of entries per page.
            max_concurrency: Maximum number of concurrent fetch tasks.

        Returns:
            List of Near Earth Object(s).

        Raises:
            NeosNotFoundError: If no NEOs are found.
        """
        semaphore = asyncio.Semaphore(max_concurrency)
        pages = await asyncio.gather(
            *guard_semaphore(
                [self._list_page(page, page_size) for page in range(math.ceil(limit / page_size))],
                semaphore,
            )
        )

        result = [neo for page in pages for neo in page][:limit]
        if not result:
            raise NeosNotFoundError("No Near Earth Objects found.")

        return result
