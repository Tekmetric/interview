import os
import logging
from contextlib import suppress


with suppress(ImportError):
    from dotenv import load_dotenv

    load_dotenv()


LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO")
logging.basicConfig(level=LOG_LEVEL)
logger = logging.getLogger()
