"""API Client for NASA NEO (Near Earth Object) API"""

import time
import logging
from typing import Dict, Iterator, Optional
import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

from .exceptions import NEOAPIError


logger = logging.getLogger(__name__)


class NEOAPIClient:
    """Client for NASA's NEO Browse API with pagination, rate limiting, and retry logic"""
    
    def __init__(self, api_key: str, base_url: str = "https://api.nasa.gov/neo/rest/v1"):
        if not api_key:
            raise ValueError("API key is required and cannot be empty")
        
        self.api_key = api_key
        self.base_url = base_url.rstrip('/')
        self.browse_endpoint = f"{self.base_url}/neo/browse"
        self.session = self._create_session()
        
        logger.info("NEOAPIClient initialized with base URL: %s", self.base_url)
    
    def _create_session(self) -> requests.Session:
        """Configure session with automatic retry for transient failures (500s, connection errors)"""
        session = requests.Session()
        
        retry_strategy = Retry(
            total=3,
            backoff_factor=1,  # Exponential backoff: 1s, 2s, 4s
            status_forcelist=[500, 502, 503, 504],
            allowed_methods=["GET"],
        )
        
        adapter = HTTPAdapter(max_retries=retry_strategy)
        session.mount("http://", adapter)
        session.mount("https://", adapter)
        
        return session

    
    def _make_request(self, page: int = 0) -> Dict:
        """Make HTTP GET request with error handling and rate limit support
        
        Args:
            page: Page number (0-indexed)
            
        Returns:
            Parsed JSON response
        """
        params = {
            'api_key': self.api_key,
            'page': page
        }
        
        try:
            logger.debug("Making API request to %s with page=%d", self.browse_endpoint, page)
            
            response = self.session.get(
                self.browse_endpoint,
                params=params,
                timeout=30
            )
            
            if response.status_code == 429:
                self._handle_rate_limit(response)
                response = self.session.get(
                    self.browse_endpoint,
                    params=params,
                    timeout=30
                )
            
            if response.status_code != 200:
                error_msg = f"API request failed with status {response.status_code}"
                logger.error("%s: %s", error_msg, response.text[:200])
                raise NEOAPIError(
                    error_msg,
                    status_code=response.status_code,
                    response=response.text[:500]
                )
            
            try:
                data = response.json()
                logger.debug("Successfully fetched page %d", page)
                return data
            except ValueError as e:
                error_msg = f"Failed to parse JSON response: {str(e)}"
                logger.error(error_msg)
                raise NEOAPIError(
                    error_msg,
                    status_code=response.status_code,
                    response=response.text[:500]
                )
        
        except requests.exceptions.Timeout as e:
            error_msg = f"Request timeout after 30 seconds: {str(e)}"
            logger.error(error_msg)
            raise NEOAPIError(error_msg)
        
        except requests.exceptions.ConnectionError as e:
            error_msg = f"Connection error: {str(e)}"
            logger.error(error_msg)
            raise NEOAPIError(error_msg)
        
        except requests.exceptions.RequestException as e:
            error_msg = f"Request failed: {str(e)}"
            logger.error(error_msg)
            raise NEOAPIError(error_msg)
    
    def _handle_rate_limit(self, response: requests.Response) -> None:
        """Wait for rate limit to clear using Retry-After header or default 60s"""
        retry_after = response.headers.get('Retry-After')
        
        if retry_after:
            try:
                wait_time = int(retry_after)
            except ValueError:
                wait_time = 60
        else:
            wait_time = 60
        
        logger.warning(
            "Rate limit exceeded (HTTP 429). Waiting %d seconds before retry...",
            wait_time
        )
        
        time.sleep(wait_time)

    
    def fetch_neos(self, page_size: int = 20, max_objects: int = 200) -> Iterator[Dict]:
        """Fetch NEO data with pagination, yielding individual records for streaming processing
        
        Args:
            page_size: Objects per page (API max is 20)
            max_objects: Total objects to fetch
            
        Yields:
            Individual NEO records as dictionaries
        """
        fetched = 0
        page = 0
        
        logger.info("Starting to fetch NEOs (max_objects=%d, page_size=%d)", 
                   max_objects, page_size)
        
        while fetched < max_objects:
            try:
                response = self._make_request(page=page)
                neos = response.get('near_earth_objects', [])
                
                if not neos:
                    logger.info("No more NEO data available. Fetched %d objects total.", fetched)
                    break
                
                for neo in neos:
                    if fetched >= max_objects:
                        logger.info("Reached max_objects limit (%d). Stopping fetch.", max_objects)
                        return
                    
                    yield neo
                    fetched += 1
                
                logger.debug("Fetched page %d: %d objects (total: %d)", 
                           page, len(neos), fetched)
                
                page_info = response.get('page', {})
                total_pages = page_info.get('total_pages', 0)
                
                if page >= total_pages - 1:
                    logger.info("Reached last page of API results. Fetched %d objects total.", 
                              fetched)
                    break
                
                page += 1
                
            except NEOAPIError as e:
                logger.error("Error fetching page %d: %s", page, str(e))
                raise
        
        logger.info("Completed fetching NEOs. Total objects fetched: %d", fetched)
