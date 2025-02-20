
import requests

class Extractor:
    def __init__(self, browse_api_url: str, api_key: str):
        self.browse_api_url = browse_api_url
        self.api_key = api_key

    def fetch_page(self, page: int = 0, page_size: int = 20) -> dict:
        params = {
            "page": page,
            "size": page_size,
            "api_key": self.api_key
        }

        try:
            response = requests.get(self.browse_api_url, params=params)
            response.raise_for_status()
            data = response.json()

            return data.get("near_earth_objects", [])
        except requests.RequestException as e:
            raise Exception(f"failed to fetch neo data: {str(e)}") from e
        except ValueError as e:
            raise Exception(f"invalid neo data: {str(e)}") from e
        except Exception as e:
            raise Exception(f"unexpected error: {str(e)}") from e

    def fetch_pages(self, page: int = 0, page_size: int = 20, limit: int = 1) -> list[dict]:
        pages = []
        while True:
            page_data = self.fetch_page(page, page_size)
            if not page_data:
                break
            pages.extend(page_data)
            page += 1
            if page >= limit:
                break
        return pages