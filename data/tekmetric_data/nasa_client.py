import logging
import os

import requests

logger = logging.getLogger("tekmetric")


class NeoClient:
    """
    A client for the NASA NEO (Near Earth Object) API.
    """

    def __init__(self, client, page_size):
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

    def get(self, endpoint: str, params: dict = None) -> dict:
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
            response = self._session.get(f"{self._url}{endpoint}", params=params)
            logger.debug("GET %s %s", response.url, response.status_code)
        except requests.exceptions.RequestException as exc:
            logger.error("Request failed: %s", exc)
            return {'error': {'message': str(exc)}}
        return response.json()


class NasaClientFactory:
    """
    A factory for creating NASA API clients.
    """

    def __init__(self, client: NasaClient) -> None:
        self._client = client

    def get_client(self, client_type: str, **kwargs):
        """
        Get a specific type of client.
        :param client_type: The type of client to create (e.g., "neo").
        :param kwargs: Additional arguments to pass to the client constructor.
        :return: The created client.
        """

        if client_type == "neo":
            client = NeoClient(self._client, **kwargs)
        else:
            raise ValueError(f"Unknown client type: {client_type}")

        return client
