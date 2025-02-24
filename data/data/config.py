import os
import logging
from contextlib import suppress


with suppress(ImportError):
    from dotenv import load_dotenv

    load_dotenv()


LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO")
logging.basicConfig(level=LOG_LEVEL)
logger = logging.getLogger()

NASA_API_URL = os.getenv("NASA_API_URL", "https://api.nasa.gov/")
NASA_API_KEY = os.getenv("NEO_API_KEY", "DEMO_KEY")
