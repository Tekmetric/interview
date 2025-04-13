from http import HTTPStatus
from types import TracebackType

import aiohttp
import structlog
from aiohttp_retry import RetryClient, ExponentialRetry

from neo_data_analyser.models import NearEarthObject
from neo_data_analyser.settings import get_settings

# The number of items per page for the Neo API. It is a constant and cannot be changed.
BROWSE_ITEMS_PER_PAGE = 20

logger = structlog.get_logger()


class NeoApi:
    def __init__(self) -> None:
        settings = get_settings()
        self._api_key: str = settings.neo_api_key
        self._api_url: str = settings.neo_api_url
        self._session: aiohttp.ClientSession
        self._retry_client: RetryClient

    async def __aenter__(self) -> "NeoApi":
        self._session = aiohttp.ClientSession(
            headers={"Content-Type": "application/json"}
        )
        self._retry_client = RetryClient(
            self._session,
            retry_options=ExponentialRetry(
                attempts=5,
            ),
        )
        return self

    async def __aexit__(
        self,
        exc_type: type[BaseException] | None,
        exc_value: BaseException | None,
        traceback: TracebackType | None,
    ) -> bool | None:
        if self._session:
            await self._session.close()

        return False

    def _build_url(self, endpoint: str) -> str:
        return f"{self._api_url}/{endpoint}?api_key={self._api_key}"

    async def browse(self, page: int = 0) -> list[NearEarthObject]:
        logger.info(
            "Fetching data from Neo API",
            endpoint="browse",
            page=page,
        )
        url = self._build_url("browse")
        params = {"page": page}

        async with self._retry_client.get(url, params=params) as response:
            if response.status != HTTPStatus.OK:
                # TODO: Raise custom exception
                exception_message = (
                    f"Failed to fetch data from Neo API: {response.status}"
                )
                raise RuntimeError(exception_message)

            data = await response.json()

            logger.info(
                "Data fetched from Neo API",
                endpoint="browse",
                page=page,
            )

            return [
                NearEarthObject.model_validate(item)
                for item in data["near_earth_objects"]
            ]
