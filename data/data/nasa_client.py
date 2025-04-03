from urllib.parse import urljoin

import requests
from requests import RequestException

from data import config


class NasaApiException(Exception):
    pass


class InvalidRequest(NasaApiException):
    pass


class NeoApi:
    """Near Earth Object API"""

    DEFAULT_PAGE = 0
    DEFAULT_PAGE_SIZE = 20
    MAX_PAGE_SIZE = 20

    def __init__(self, client: "NASAClient"):
        self._client = client
        self._base_path = "/neo/rest/v1/neo"

    def browse(
        self, page: int = DEFAULT_PAGE, page_size: int = DEFAULT_PAGE_SIZE
    ) -> dict:
        if page < 0 or page_size < 0 or page_size > self.MAX_PAGE_SIZE:
            raise InvalidRequest

        path = f"{self._base_path}/browse"
        params = {
            "page": page,
            "size": page_size,
        }
        return self._client.get(path, params)


class NASAClient:
    """Thin client over NASA apis.

    Supported APIS
    * NEO -Near Earth Object API

    TODO: Add data structures instead of plain dictionaries in responses
    TODO: Handle rate limiting
    """

    DEFAULT_BASE_URL = "https://api.nasa.gov"
    DEFAULT_API_KEY = "DEMO_KEY"

    def __init__(self, url: str = DEFAULT_BASE_URL, api_key: str = DEFAULT_API_KEY):
        self._base_url = url.rstrip("/")
        self._api_key = api_key
        self._neo_api = NeoApi(self)

    @classmethod
    def from_env(cls):
        return cls(
            config.NASA_API_URL,
            config.NASA_API_KEY,
        )

    @property
    def neo(self) -> NeoApi:
        return self._neo_api

    def _prepare_request(self, endpoint: str, params: dict) -> dict:
        return {
            "url": urljoin(self._base_url, endpoint),
            "params": {**params, "api_key": self._api_key},
        }

    def get(self, endpoint: str, params: dict) -> dict:
        get_args = self._prepare_request(endpoint, params)
        response = requests.get(**get_args)
        try:
            response.raise_for_status()
        except RequestException as e:
            raise NasaApiException from e
        return response.json()
