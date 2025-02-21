
import asyncio
import aiohttp
import requests

class Extractor:
    def __init__(self, browse_api_url: str, api_key: str):
        self.browse_api_url = browse_api_url
        self.api_key = api_key

    async def fetch_page(self, url: str) -> dict:
        async with aiohttp.ClientSession() as session:
            try:
                response = await session.get(url)
                response.raise_for_status()
                data = await response.json()
                return data.get("near_earth_objects", [])
            except requests.RequestException as e:
                raise Exception(f"failed to fetch neo data: {str(e)}") from e
            except ValueError as e:
                raise Exception(f"invalid neo data: {str(e)}") from e
            except Exception as e:
                raise Exception(f"unexpected error: {str(e)}") from e

    async def fetch_pages(self, page: int = 0, page_size: int = 20, limit: int = 1) -> list[list[dict]]:
        """ 
        Returns:
            list[list[dict]]: A list of pages, where each page contains a list of NEO dictionaries
        """
        print(f"fetching {limit} pages with size {page_size} starting from page {page}")
        urls = []
        for i in range(limit):
            urls.append(f"{self.browse_api_url}?api_key={self.api_key}&page={page + i}&size={page_size}")

        tasks = []
        for url in urls:
            print(f"appending task for page {url}")
            tasks.append(asyncio.create_task(self.fetch_page(url)))
        print(f"waiting for {len(tasks)} tasks to complete")
        pages = await asyncio.gather(*tasks)
        print(f"tasks completed")
        return pages
