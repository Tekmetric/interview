import json
from enum import Enum
from hashlib import sha256
from urllib.parse import urljoin
import os
from pathlib import Path
import requests

import pandas as pd


class DownloadError(requests.HTTPError):

    pass


class DataDotGovClient:

    DEFAULT_BASE_URL  = 'https://data.transportation.gov/api/views'

    class Resource(Enum):
        RECALLS = '6axg-epim'

    def __init__(self, api_key: str, base_url: str = DEFAULT_BASE_URL, cache_dir: str | None='/tmp'):
        self._api_key = api_key
        self.base_url = base_url
        self.cache_dir = cache_dir

        if not self.base_url.endswith('/'):
            self.base_url += '/'

    def get_resource_url(self, resource: Resource):
        url = urljoin(self.base_url, f'{resource.value}/rows.json')
        return url

    def get_cache_path(self, url: str) -> Path:
        url_hash_obj = sha256()
        url_hash_obj.update(url.encode())
        url_hash = url_hash_obj.hexdigest()

        file_path = Path(os.path.join(self.cache_dir, url_hash))
        return file_path


    def get_from_cache(self, url):
        file_path = self.get_cache_path(url)
        file_content = None
        if file_path.exists():
            file_content = json.loads(file_path.read_text())

        return file_content

    def write_to_cache(self, url, content):
        file_path = self.get_cache_path(url)
        file_path.write_bytes(content)

    def download_resource(self, resource: Resource, use_cache=False):
        url = self.get_resource_url(resource)
        data = None

        if use_cache:
            data = self.get_from_cache(url)

        if data is None:
            response = requests.get(url)
            try:
                response.raise_for_status()
            except requests.HTTPError:
                raise DownloadError(f'Failed to download resource {resource.name}')

            if use_cache:
                self.write_to_cache(url, response.content)

            data = response.json()

        return data

    def get_resource(self, resource: Resource, use_cache=False) -> pd.DataFrame:
        resource_data = self.download_resource(resource, use_cache=use_cache)

        column_names = [column['fieldName'].lstrip(':') for column in resource_data['meta']['view']['columns']]
        df = pd.DataFrame(resource_data['data'], columns=column_names)

        return df


