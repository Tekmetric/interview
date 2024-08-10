import os
from urllib.parse import urljoin

import requests
import requests_cache
from lxml import html

from recall.errors import DataFetcherError

CSV_PATH = "/tmp/recall-data/recall-data.csv"


class RecallDataFetcher:
    def __init__(self, base_url: str, use_cache: bool = False):
        os.makedirs("/tmp/recall-data", exist_ok=True)
        self.base_url = base_url
        if use_cache:
            requests_cache.install_cache("recall_cache")

    def download_vehicle_recall_csv(self, starting_path) -> str:
        starting_url = urljoin(self.base_url, starting_path)
        starting_page = self._fetch_online_data(starting_url)

        file_url = self._extract_file_url(starting_page)
        file_content = self._fetch_online_data(file_url)
        with open(CSV_PATH, "wb") as file:
            file.write(file_content)
        return CSV_PATH

    @staticmethod
    def _fetch_online_data(url: str) -> bytes:
        response = requests.get(url)
        response.raise_for_status()
        return response.content

    @staticmethod
    def _extract_file_url(page_content: bytes) -> str:
        tree = html.fromstring(page_content)
        link = tree.xpath('//section[@id="dataset-resources"]//li[@class="resource-item"]//a[@data-format="csv"]/@href')
        if not link:
            raise DataFetcherError("No file link found")
        return link[0]
