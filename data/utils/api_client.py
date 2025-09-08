import requests
import time
from typing import Dict, List, Optional, Generator
from dataclasses import dataclass


@dataclass
class APIConfig:
    base_url: str = "https://api.nasa.gov/neo/rest/v1"
    api_key: str = ""
    rate_limit_delay: float = 0.2
    max_retries: int = 3
    timeout: int = 30


class NASAAPIClient:
    def __init__(self, api_key: str, config: Optional[APIConfig] = None):
        self.config = config or APIConfig()
        self.config.api_key = api_key
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'NASA-NEO-Data-Collector/1.0'
        })

    def _make_request(self, endpoint: str, params: Dict) -> Dict:
        """Make API request with retry logic and rate limiting."""
        params['api_key'] = self.config.api_key
        
        for attempt in range(self.config.max_retries):
            try:
                response = self.session.get(
                    f"{self.config.base_url}/{endpoint}",
                    params=params,
                    timeout=self.config.timeout
                )
                response.raise_for_status()
                return response.json()
            except requests.exceptions.RequestException as e:
                if attempt == self.config.max_retries - 1:
                    raise Exception(f"API request failed after {self.config.max_retries} attempts: {e}")
                time.sleep(2 ** attempt)
        
        raise Exception("Unexpected error in API request")

    def get_neo_browse_page(self, page: int = 0, size: int = 20) -> Dict:
        """Fetch a single page of NEO browse data."""
        params = {'page': page, 'size': size}
        return self._make_request('neo/browse', params)

    def fetch_neo_data(self, target_count: int = 200) -> Generator[Dict, None, None]:
        """Fetch NEO data in batches, yielding individual NEO objects."""
        page = 0
        size = 20
        fetched_count = 0
        
        while fetched_count < target_count:
            batch_size = min(size, target_count - fetched_count)
            
            try:
                data = self.get_neo_browse_page(page, batch_size)
                neos = data.get('near_earth_objects', [])
                
                for neo in neos:
                    if fetched_count >= target_count:
                        break
                    yield neo
                    fetched_count += 1
                
                if not neos or len(neos) < batch_size:
                    break
                    
                page += 1
                time.sleep(self.config.rate_limit_delay)
                
            except Exception as e:
                print(f"Error fetching page {page}: {e}")
                break

    def get_neo_details(self, neo_id: str) -> Dict:
        """Fetch detailed NEO data including close approach information."""
        return self._make_request(f'neo/{neo_id}', {})
