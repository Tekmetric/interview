from __future__ import annotations

import threading
from concurrent.futures import ThreadPoolExecutor, as_completed
from typing import Iterator

import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

NEO_BROWSE_URL = "https://api.nasa.gov/neo/rest/v1/neo/browse"

_RETRY = Retry(
    total=3,
    backoff_factor=1,
    status_forcelist=[429, 500, 502, 503],
    respect_retry_after_header=False,
)
_thread_local = threading.local()


def _get_session() -> requests.Session:
    """Return a thread-local Session with automatic retry."""
    if not hasattr(_thread_local, "session"):
        session = requests.Session()
        session.mount("https://", HTTPAdapter(max_retries=_RETRY))
        _thread_local.session = session
    return _thread_local.session


def fetch_page(page_num: int, page_size: int, api_key: str) -> dict:
    """Fetch a single page from the NeoWS Browse API.

    Uses a thread-local Session for connection reuse and automatic retry.
    Raises requests.HTTPError on non-2xx responses after retries are exhausted.
    """
    session = _get_session()
    response = session.get(
        NEO_BROWSE_URL,
        params={"page": page_num, "size": page_size, "api_key": api_key},
        timeout=5,
    )
    response.raise_for_status()
    return response.json()


def fetch_all(
    api_key: str,
    page_assignments: list[tuple[int, int]],
    concurrency: int,
) -> Iterator[dict]:
    """Fetch all pages concurrently.

    Args:
        api_key: NASA API key.
        page_assignments: List of (page_num, page_size) tuples.
        concurrency: Max number of simultaneous HTTP requests.

    Yields:
        Raw API responses as each future completes. Order is not guaranteed.
    """
    with ThreadPoolExecutor(max_workers=concurrency) as executor:
        futures = [
            executor.submit(fetch_page, page_num, page_size, api_key)
            for page_num, page_size in page_assignments
        ]
        for future in as_completed(futures):
            yield future.result()
