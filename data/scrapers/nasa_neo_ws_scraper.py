import logging
from typing import Any, Dict, Iterator, Tuple

import requests

from models.asteroid import AsteroidValidatedObject

from .base_scraper import BaseScraper

LOGGER = logging.getLogger(__name__)


class NasaNeoWsScraper(BaseScraper):
    """
    Scraper for NASA's Near Earth Object Web Service (NeoWs) API.
    """

    def __init__(self, base_url: str, object_count: int, api_key: str):
        """
        Initialize the NasaNeoWsScraper with the base URL, object count, and API key.

        Args:
            base_url (str): The base URL for the NASA NeoWs API.
            object_count (int): The number of objects to scrape.
            api_key (str): The API key for accessing the NASA NeoWs API.

        Raises:
            ValueError: If the API key is not found'.
        """
        self.base_url = base_url
        self.max_objects = object_count
        self.api_key = api_key

    @property
    def objects(self) -> Iterator[Tuple[Dict[str, Any], AsteroidValidatedObject]]:
        """Fetches and yields objects from the NASA NeoWs API.
        This method handles pagination and yields both the raw object and the validated object.

        Yields:
            Iterator[Tuple[Dict[str, Any], AsteroidValidatedObject]]:
                A tuple containing the raw object and the validated object.
        """
        objects_yielded = 0
        current_url = f"{self.base_url}?api_key={self.api_key}"

        while current_url and objects_yielded < self.max_objects:
            response = requests.get(current_url)
            try:
                response.raise_for_status()
                data = response.json()
            except Exception as e:
                LOGGER.error(f"Error fetching data from {current_url}: {e}")
                raise

            for neo in data.get("near_earth_objects", []):
                if self.max_objects > 0 and objects_yielded >= self.max_objects:
                    break

                validated_obj = AsteroidValidatedObject(**neo)

                yield neo, validated_obj
                objects_yielded += 1

            # Get next page URL if available and needed
            current_url = data.get("links", {}).get("next")
            LOGGER.debug(f"Next URL: {current_url}")
