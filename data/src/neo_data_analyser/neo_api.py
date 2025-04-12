from http import HTTPStatus
from types import TracebackType
from typing import cast

import aiohttp

from neo_data_analyser.config import get_settings


class NeoApi:
    def __init__(self) -> None:
        settings = get_settings()
        self._api_key: str = settings.neo_api_key
        self._api_url: str = settings.neo_api_url
        self._session: aiohttp.ClientSession

    async def __aenter__(self) -> "NeoApi":
        self._session = aiohttp.ClientSession(
            headers={"Content-Type": "application/json"}
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

    # TODO: Add proper type to the response
    async def browse(self, page: int = 0) -> dict[str, str]:
        url = self._build_url("browse")
        params = {"page": page}

        async with self._session.get(url, params=params) as response:
            if response.status != HTTPStatus.OK:
                # TODO: Raise custom exception
                exception_message = (
                    f"Failed to fetch data from Neo API: {response.status}"
                )
                raise RuntimeError(exception_message)
            data = await response.json()
            return cast("dict[str, str]", data)
