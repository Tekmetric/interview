import os
import logging
from contextlib import suppress
from pathlib import Path

import requests_cache
from requests_cache import FileCache

with suppress(ImportError):
    from dotenv import load_dotenv

    load_dotenv()


LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO")
logging.basicConfig(level=LOG_LEVEL)
logger = logging.getLogger()

OUTPUT_DIR = Path(os.getenv("OUTPUT_DIR", Path(__file__).parent.parent / "output"))
RAW_OUTPUT_DIR = OUTPUT_DIR / "raw"
RAW_OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

# setup request caching globally
CACHE_REQUESTS = os.getenv("CACHE_REQUESTS", "true") == "true"
if CACHE_REQUESTS:
    requests_cache.install_cache(backend=FileCache(cache_name=RAW_OUTPUT_DIR))


NASA_API_URL = os.getenv("NASA_API_URL", "https://api.nasa.gov/")
NASA_API_KEY = os.getenv("NEO_API_KEY", "DEMO_KEY")

MISS_THRESHOLD_ASTR = float(os.getenv("MISS_THRESHOLD_ASTR", 0.2))
START_PAGE = int(os.getenv("START_PAGE", 0))
END_PAGE = int(os.getenv("END_PAGE", 9))
PAGES_PER_BATCH = int(os.getenv("PAGES_PER_BATCH", 3))
