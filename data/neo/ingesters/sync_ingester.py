import requests
from typing import List, Dict

from config import BASE_URL
from neo.ingesters.base import DataIngesterBase
from neo.logger import logger


class SyncDataIngester(DataIngesterBase):
    """
    Synchronous implementation of the data ingester for NASA's NEO API.
    """

    def __init__(self, api_key: str, page_size: int = 20) -> None:
        self.api_key = api_key
        self.page_size = page_size

    def fetch_objects(self, limit: int = 200) -> List[Dict]:
        results: List[Dict] = []
        page: int = 0

        while len(results) < limit:
            logger.info(f"Fetching page {page} of NEO data.")
            response = requests.get(
                BASE_URL,
                params={"page": page, "size": self.page_size, "api_key": self.api_key},
            )

            if response.status_code != 200:
                logger.error(f"Failed to fetch page {page}: {response.status_code}")
                break

            data: Dict = response.json()
            page_data: List[Dict] = data.get("near_earth_objects", [])
            results.extend(page_data)

            logger.info(
                f"Fetched {len(page_data)} objects. Total so far: {len(results)}"
            )

            if not data.get("page") or data["page"].get("total_pages", 0) - 1 <= page:
                logger.warning("Reached end of available data before hitting limit.")
                break

            page += 1

        return results[:limit]
