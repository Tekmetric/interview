"""Fetch Near Earth Objects from the NASA NeoWs Browse API with pagination and retry."""

import logging
import math
import time
from collections.abc import Iterator

import requests

from nasa_neows.config import settings
from nasa_neows.models import BrowseResponse

logger = logging.getLogger(__name__)

API_URL = "https://api.nasa.gov/neo/rest/v1/neo/browse"
MAX_RETRIES = 5
RETRY_DELAY = 10  # seconds


def _get_with_retry(url: str, params: dict) -> requests.Response:
    """Make a GET request with exponential backoff on rate limiting (429)."""
    for attempt in range(MAX_RETRIES):
        response = requests.get(url, params=params, timeout=30)

        if response.status_code == 429:
            wait = RETRY_DELAY * 2**attempt
            logger.warning("Rate limited, waiting %ds before retry...", wait)
            time.sleep(wait)
            continue

        response.raise_for_status()
        return response

    raise requests.HTTPError(f"Still rate limited after {MAX_RETRIES} retries")


def fetch_neos(
    total: int = 200, page_size: int = 20
) -> Iterator[list[dict]]:
    """Yield batches of raw NEO dicts from the Browse API.

    Validates the page-level response structure with Pydantic to fail fast
    if the API changes, then yields the raw dicts for loading.
    """
    total_pages = math.ceil(total / page_size)
    fetched = 0

    for page in range(total_pages):
        logger.info("Fetching page %d/%d...", page + 1, total_pages)
        response = _get_with_retry(
            API_URL,
            params={"api_key": settings.nasa_api_key, "page": page, "size": page_size},
        )

        data = response.json()
        browse_response = BrowseResponse.model_validate(data)

        remaining = total - fetched
        raw_neos = [neo.model_dump() for neo in browse_response.near_earth_objects[:remaining]]

        fetched += len(raw_neos)
        yield raw_neos

        if fetched >= total:
            break
