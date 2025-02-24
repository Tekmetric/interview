"""
Contains implementation of an API scraper using an asyncio queue + tasks.
"""

import asyncio
from typing import List

import aiohttp

NUM_TASKS = 5  # Number of async tasks polling url queue.
MAX_CONNECTIONS = 5  # Max number of TCP connections.
MAX_RETRIES = 2


async def scrape_urls(urls: List[str]):
    """Query provided urls. Return list of JSON responses."""
    queue = asyncio.Queue()
    for url in urls:
        await queue.put(url)
    conn = aiohttp.TCPConnector(limit=MAX_CONNECTIONS)
    async with aiohttp.ClientSession(connector=conn) as client:
        tasks = [_worker(client, queue) for i in range(NUM_TASKS)]
        results = await asyncio.gather(*tasks)
        # Flatten lists of responses from workers.
        return [ele for sub in results for ele in sub]


async def _worker(client: aiohttp.ClientSession, queue: asyncio.Queue):
    """Continuously polls queue for URLs to query"""
    results = []
    while not queue.empty():
        url = await queue.get()
        response = await _get_url(client, url)
        results.append(response)
    return results


async def _get_url(client: aiohttp.ClientSession, url: str):
    """Perform GET request with basic retry logic"""
    attempts = 0
    while attempts < MAX_RETRIES:
        try:
            print(f"Requesting data from: {url}...")
            async with client.get(url, raise_for_status=True) as response:
                data = await response.json()
                return data
        except aiohttp.client_exceptions.ClientResponseError as err:
            attempts += 1
            if attempts == MAX_RETRIES:
                raise err
            await asyncio.sleep((0.5 * attempts))
