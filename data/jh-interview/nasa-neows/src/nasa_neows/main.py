"""Pipeline orchestration — extract, load, flatten, aggregate."""

import logging

from nasa_neows.config import AGGREGATIONS_DIR, PROCESSED_DIR, RAW_DIR
from nasa_neows.load import extract_and_load
from nasa_neows.transform import aggregate, flatten

logger = logging.getLogger(__name__)


def main() -> None:
    """Run the full ELT pipeline."""
    logging.basicConfig(
        level=logging.INFO,
        format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
    )

    RAW_DIR.mkdir(parents=True, exist_ok=True)
    PROCESSED_DIR.mkdir(parents=True, exist_ok=True)
    AGGREGATIONS_DIR.mkdir(parents=True, exist_ok=True)

    extract_and_load()  # Bronze — raw API data
    flatten()  # Silver — flattened 17-column dataset
    aggregate()  # Gold  — aggregated summaries
    logger.info("Pipeline Completed.")


if __name__ == "__main__":
    main()
