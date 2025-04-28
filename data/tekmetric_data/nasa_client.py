import logging
import os
from typing import Optional
from urllib.parse import urljoin

import requests

logger = logging.getLogger("tekmetric")


class NasaClient:
    """
    A client for the NASA API.
    """

    BASE_URL = "https://api.nasa.gov"
    DEMO_KEY = "DEMO_KEY"

    def __init__(self, url: str = BASE_URL, api_key: str = DEMO_KEY):
        self._url = url
        self._api_key = os.environ.get("TEK_NASA_API_KEY", api_key)
        self._session = requests.Session()

    def __del__(self):
        """
        Close the session.
        """
        self._session.close()

    def get(self, endpoint: str, params: Optional[dict] = None) -> dict:
        """
        Make a GET request to the NASA API.
        :param endpoint: The API endpoint to call.
        :param params: The query parameters to include in the request.
        :return: The JSON response from the API.
        """
        if params is None:
            params = {}
        params["api_key"] = self._api_key

        try:
            response = self._session.get(urljoin(self._url, endpoint), params=params)
            logger.debug("GET %s %s", response.url, response.status_code)
        except requests.exceptions.RequestException as exc:
            logger.error("Request failed: %s", exc)
            return {'error': {'message': str(exc)}}
        return response.json()


class NasaClientRegistry:
    """
    A factory for creating NASA API clients.
    """
    _registry = {}

    @classmethod
    def register(cls, client_type: str):
        def decorator(client_class):
            cls._registry[client_type] = client_class
            return client_class

        return decorator

    def __init__(self, client: Optional[NasaClient]) -> None:
        if client is None:
            client = NasaClient()
        self._client = client

    def get(self, client_type: str, **kwargs):
        client_cls = self._registry.get(client_type)
        if not client_cls:
            raise ValueError(f"Unknown client type: {client_type}")

        return client_cls(self._client, **kwargs)


@NasaClientRegistry.register("neo")
class NeoClient:
    """
    A client for the NASA NEO (Near Earth Object) API.
    """

    def __init__(self, client: NasaClient, page_size: int):
        self._base_path = "/neo/rest/v1/neo/browse"
        self._client = client
        self._page_size = page_size

    def browse(self, page: int = 0) -> dict:
        """
        Browse the NEO data.
        :param page: The page number to fetch.
        :return: The NEO data for the specified page.
        """
        params = {
            "page": page,
            "size": self._page_size
        }
        return self._client.get(self._base_path, params=params)
