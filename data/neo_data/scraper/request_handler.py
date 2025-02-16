import requests
import logging
from omegaconf import DictConfig

class RequestHandler:

    def __init__(self, cfg: DictConfig):
        self.cfg = cfg

    def _build_paged_request(self, page: int):
        """
        Builds a dictionary representing a paged request with the given page number.
        Args:
            page (int): The page number to include in the request.
        Returns:
            dict: A dictionary containing the request parameters, including the API key, page number, and page size.
        """

        return {
            "api_key": self.cfg.scraper.api_key,
            "page": page,
            "size": self.cfg.scraper.page_size
        }
        

    def fetch_page(self, page: int):
        """
        Fetches a specific page of data from the configured scraper endpoint.
        Args:
            page (int): The page number to fetch.
        Returns:
            dict: The JSON response from the endpoint.
        Raises:
            requests.exceptions.RequestException: If there is an error during the request.
        """

        try:
            response = requests.get(self.cfg.scraper.endpoint, params=self._build_paged_request(page))
            response.raise_for_status()
        except requests.exceptions.RequestException as e:
            logging.error(f"Error fetching NEO page: {e}")

        return response.json()
