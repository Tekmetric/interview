from queue import Queue
import requests
import logging
from omegaconf import DictConfig
from abc import ABC, abstractmethod

class DataHandler(ABC):
    def __init__(self, cfg: DictConfig, queue: Queue):
        self.queue = queue
        self.cfg = cfg

    @abstractmethod
    def download_data(self):
        pass


class RequestsDataHandler(DataHandler):
    """ The RequestsDataHandler uses the http requests library in order to fetch data from the NASA NEO Web Service API.
    """

    def _build_paged_request(self, page: int):
        """
        Builds a dictionary representing a paged request with the given page number, for the NASA NEO Web Service API.
        Args:
            page (int): The page number to include in the request.
        Returns:
            dict: A dictionary containing the request parameters, including the API key, page number, and page size.
        """

        return {
            "api_key": self.cfg.scraper.request.api_key,
            "page": page,
            "size": self.cfg.scraper.request.page_size
        }
    
    def _fetch_paged_data(self, page: int) -> dict:
        """ Fetches one page of NEO data from the NASA NEO Web Service API.
        Args:
            page (int): The page number to fetch.
        Returns:
            dict: The JSON response from the endpoint.
        Raises:
            requests.exceptions.RequestException: If there is an error during the request.
        """

        try:
            response = requests.get(self.cfg.scraper.request.endpoint, params=self._build_paged_request(page))
            response.raise_for_status()
        except requests.exceptions.RequestException as e:
            logging.error(f"Error fetching NEO page: {e}")

        return response.json()

    def download_data(self):
        """ Downloads data from the NASA NEO Web Service API using the requests library as it is confgured in the configuration file.
        Resulting data is added to the queue for further processing.
        """

        start_page = self.cfg.scraper.request.start_page
        end_page = self.cfg.scraper.request.end_page

        for page in range(start_page, end_page):
            data = self._fetch_paged_data(page)
            if data:
                self.queue.put(data)
                logging.info(f"Data fetched for page {page}")
        self.queue.put(None)  # Signal the end of the data stream