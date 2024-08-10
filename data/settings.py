import os

BASE_URL = os.getenv("BASE_URL", "https://catalog.data.gov")
STARTING_PATH = os.getenv("STARTING_PATH", "/dataset/recalls-data")
USE_CACHE = os.getenv("USE_CACHE", "false").lower() in ("true", "1")
S3_BUCKET_NAME = os.getenv("BUCKET_NAME", "recall-data")
