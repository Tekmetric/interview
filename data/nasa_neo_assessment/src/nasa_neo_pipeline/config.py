from __future__ import annotations

import os
from datetime import datetime, timezone
from pathlib import Path

DATA_PATH = Path("data")
API_URL = "https://api.nasa.gov/neo/rest/v1/neo/browse"
NASA_API_KEY = os.getenv("NASA_API_KEY")

OBJECT_LIMIT = 200
REQUEST_TIMEOUT_SECONDS = 30
REQUEST_DELAY_SECONDS = 0.25
MAX_RETRIES = 3
RETRY_BACKOFF_SECONDS = 2.0


def snapshot_date() -> str:
    return datetime.now(timezone.utc).strftime("%Y-%m-%d")
