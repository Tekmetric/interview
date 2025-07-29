"""
NASA API client for fetching Near Earth Object data
"""

import json
import time
import logging
from typing import List, Dict, Any, Optional
import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

from .config import APIConfig
from .models import CloseApproachData, ObjectDetails
from .models import NASAAPIError, RateLimitError, APITimeoutError

logger = logging.getLogger(__name__)


class NASAAPIClient:
    """NASA API client with rate limiting and error handling"""
    
    def __init__(self, config: APIConfig):
        self.config = config
        self.session = self._create_session()
    
    def _create_session(self) -> requests.Session:
        """Create HTTP session with retry strategy"""
        session = requests.Session()
        
        # Configure retry strategy
        retry_strategy = Retry(
            total=self.config.max_retries,
            status_forcelist=[429, 500, 502, 503, 504],
            allowed_methods=["HEAD", "GET", "OPTIONS"],
            backoff_factor=1
        )
        
        adapter = HTTPAdapter(max_retries=retry_strategy)
        session.mount("http://", adapter)
        session.mount("https://", adapter)
        
        return session
    
    def fetch_close_approach_data(self, limit: int = 200) -> List[CloseApproachData]:
        """
        Fetch close approach data for NEO objects
        
        Args:
            limit: Maximum number of objects to fetch
            
        Returns:
            List of CloseApproachData objects
            
        Raises:
            NASAAPIError: If API request fails
        """
        logger.info(f"Fetching close approach data (limit: {limit})")
        
        params = {
            'neo': 'true',
            'limit': limit,
            'sort': 'object',
            'fullname': 'true',
            'diameter': 'true',
        }
        
        try:
            response = self._make_request(
                self.config.close_approach_endpoint,
                params=params
            )
            
            data = response.json()
            
            if 'data' not in data:
                raise NASAAPIError("Invalid response format: missing 'data' field")
            
            # Parse records into CloseApproachData objects
            close_approaches = []
            for record in data['data']:
                try:
                    approach = CloseApproachData.from_api_record(record)
                    close_approaches.append(approach)
                except Exception as e:
                    logger.warning(f"Failed to parse record {record}: {e}")
                    continue
            
            logger.info(f"Successfully fetched {len(close_approaches)} close approach records")
            return close_approaches
            
        except requests.RequestException as e:
            raise NASAAPIError(f"Close approach API request failed: {e}")
        except json.JSONDecodeError as e:
            raise NASAAPIError(f"Invalid JSON response: {e}")
    
    def fetch_object_details(self, designations: List[str]) -> List[ObjectDetails]:
        """
        Fetch detailed object data for given designations
        
        Args:
            designations: List of object designations
            
        Returns:
            List of ObjectDetails objects
        """
        logger.info(f"Fetching detailed data for {len(designations)} objects")
        
        object_details = []
        
        for i, designation in enumerate(designations):
            if i > 0 and i % self.config.request_timeout == 0:
                logger.info(f"Processed {i}/{len(designations)} objects...")
            
            # Rate limiting
            if i > 0:
                time.sleep(self.config.rate_limit_delay)
            
            try:
                details = self._fetch_single_object_details(designation)
                if details:
                    object_details.append(details)
                    
            except RateLimitError:
                logger.warning(f"Rate limit hit for {designation}, waiting...")
                time.sleep(self.config.rate_limit_delay * 2)
                # Retry once
                try:
                    details = self._fetch_single_object_details(designation)
                    if details:
                        object_details.append(details)
                except Exception as e:
                    logger.warning(f"Failed to fetch {designation} after retry: {e}")
                    
            except Exception as e:
                logger.warning(f"Failed to fetch data for {designation}: {e}")
                continue
        
        logger.info(f"Successfully fetched detailed data for {len(object_details)} objects")
        return object_details
    
    def _fetch_single_object_details(self, designation: str) -> Optional[ObjectDetails]:
        """Fetch details for a single object"""
        params = {
            'sstr': designation,
            'phys-par': 'true',
            'ca-data': 'true',
            'ca-body': 'Earth'
        }
        
        try:
            response = self._make_request(
                self.config.sbdb_endpoint,
                params=params
            )
            
            data = response.json()
            
            if 'object' not in data:
                logger.debug(f"No object data found for {designation}")
                return None
            
            return ObjectDetails.from_api_response(data)
            
        except requests.RequestException as e:
            if hasattr(e, 'response') and e.response.status_code == 429:
                raise RateLimitError(f"Rate limit exceeded for {designation}")
            raise NASAAPIError(f"SBDB API request failed for {designation}: {e}")
        except json.JSONDecodeError as e:
            raise NASAAPIError(f"Invalid JSON response for {designation}: {e}")
    
    def _make_request(self, url: str, params: Dict[str, Any]) -> requests.Response:
        """
        Make HTTP request with timeout and error handling
        
        Args:
            url: API endpoint URL
            params: Query parameters
            
        Returns:
            Response object
            
        Raises:
            APITimeoutError: If request times out
            NASAAPIError: If request fails
        """
        try:
            response = self.session.get(
                url,
                params=params,
                timeout=self.config.request_timeout
            )
            response.raise_for_status()
            return response
            
        except requests.Timeout:
            raise APITimeoutError(f"Request timeout for {url}")
        except requests.RequestException as e:
            if hasattr(e, 'response') and e.response is not None:
                if e.response.status_code == 429:
                    raise RateLimitError("Rate limit exceeded")
                elif e.response.status_code >= 500:
                    raise NASAAPIError(f"Server error ({e.response.status_code})")
            raise NASAAPIError(f"Request failed: {e}")
    
    def close(self):
        """Close the HTTP session"""
        if self.session:
            self.session.close() 