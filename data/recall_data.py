from src.config import settings
from src.data_collector import DataCollector

if __name__ == "__main__":
    DataCollector(
        api_base_url=settings.api_base_url,
        api_key=settings.api_key,
        output_bucket=settings.output_bucket,
        use_requests_cache=settings.requests_cache,
    ).run()
