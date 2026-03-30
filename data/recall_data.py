"""recall_data.py — fetch Near Earth Objects from NASA NeoWS and save to Parquet.

Usage:
    python recall_data.py [--limit 200] [--concurrency 5] [--pages-per-file N]
                          [--output-dir data/neo] [--api-key KEY]

The NASA API key can be supplied via --api-key or the NASA_API_KEY environment variable.
"""

from __future__ import annotations

import argparse
import json
import logging
import math
import os
import queue
import sys
from datetime import date
from pathlib import Path

from tqdm import tqdm

from collections import defaultdict

from fetch import fetch_all
from transform import PageResult, transform_page
from writer import Writer, make_sentinel

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(message)s",
)
logger = logging.getLogger(__name__)

PAGE_SIZE = 20  # NeoWS Browse API hard-caps page size at 20


# ---------------------------------------------------------------------------
# Core batch processor
# ---------------------------------------------------------------------------

def process_batch(
    page_assignments: list[tuple[int, int]],
    output_dir: Path,
    api_key: str,
    concurrency: int,
    file_idx: int = 0,
) -> tuple[list[PageResult], int | None]:
    """Fetch a contiguous range of pages, transform, and write a single Parquet file.

    Returns (page_results, total_elements_from_api).

    On failure the daemon Writer is abandoned with nothing written to disk.

    At scale, this function maps 1:1 to an independent worker. Multiple batches can be processed in parallel by
    invoking it concurrently across separate processes, each writing to its own output partition.
    """
    # Bounded queue provides backpressure: if the writer falls behind, fetchers block.
    q: queue.Queue = queue.Queue(maxsize=concurrency * 2)

    writer = Writer(q, output_dir, file_idx)
    writer.start()

    page_results: list[PageResult] = []
    total_elements: int | None = None

    with tqdm(total=len(page_assignments), unit="page", desc="Fetching",
              position=1, leave=False) as progress:
        for raw_response in fetch_all(api_key, page_assignments, concurrency):
            if total_elements is None:
                total_elements = raw_response.get("page", {}).get("total_elements")

            raw_objects = raw_response.get("near_earth_objects", [])
            result = transform_page(raw_objects)
            page_results.append(result)
            q.put(result.records)  # blocks if queue is full (backpressure)
            progress.update(1)

    q.put(make_sentinel())
    writer.join()

    return page_results, total_elements


# ---------------------------------------------------------------------------
# Aggregation helpers
# ---------------------------------------------------------------------------

def combine_aggregations(page_results: list[PageResult]) -> dict:
    total_under_0_2 = sum(r.approaches_under_0_2_au for r in page_results)
    by_year: defaultdict[str, int] = defaultdict(int)
    for r in page_results:
        for year, count in r.approaches_by_year.items():
            by_year[year] += count

    return {
        "approaches_closer_than_0_2_au": total_under_0_2,
        "approaches_by_year": dict(sorted(by_year.items())),
    }


def write_aggregations(aggregations: dict, output_dir: Path, ingest_date: str) -> Path:
    agg_dir = output_dir / "aggregations" / f"ingest_date={ingest_date}"
    agg_dir.mkdir(parents=True, exist_ok=True)
    path = agg_dir / "aggregations.json"
    path.write_text(json.dumps(aggregations, indent=2))
    return path


# ---------------------------------------------------------------------------
# CLI
# ---------------------------------------------------------------------------

def build_page_assignments(limit: int) -> list[tuple[int, int]]:
    """Compute (page_num, page_size) for each page needed to fetch ``limit`` objects."""
    full_pages, remainder = divmod(limit, PAGE_SIZE)
    assignments = [(i, PAGE_SIZE) for i in range(full_pages)]
    if remainder:
        assignments.append((full_pages, remainder))
    return assignments


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--limit",
        type=int,
        default=200,
        help="Total number of NEOs to fetch (default: 200).",
    )
    parser.add_argument(
        "--concurrency",
        type=int,
        default=5,
        help="Max parallel HTTP requests per batch (default: 5).",
    )
    parser.add_argument(
        "--pages-per-file",
        type=int,
        default=None,
        help="Number of pages per output Parquet file. "
             "Defaults to all pages (single output file).",
    )
    parser.add_argument(
        "--output-dir",
        type=Path,
        default=Path(__file__).parent / "neo",
        help="Root output directory (default: <script_dir>/neo).",
    )
    parser.add_argument(
        "--api-key",
        default=os.environ.get("NASA_API_KEY"),
        help="NASA API key. Falls back to NASA_API_KEY env var.",
    )
    return parser.parse_args()


def main() -> None:
    args = parse_args()

    if not args.api_key:
        logger.error("NASA API key required. Pass --api-key or set the NASA_API_KEY environment variable.")
        sys.exit(1)

    ingest_date = date.today().isoformat()

    page_assignments = build_page_assignments(args.limit)
    num_pages = len(page_assignments)
    pages_per_file = args.pages_per_file or num_pages  # default: one file per run

    if pages_per_file < args.concurrency:
        logger.warning(
            "pages_per_file (%d) < concurrency (%d): each batch only has %d page(s) "
            "to fetch, so only %d of %d threads will be used.",
            pages_per_file, args.concurrency, pages_per_file,
            pages_per_file, args.concurrency,
        )

    logger.info(
        "Fetching %d NEOs across %d page(s) (concurrency=%d, pages_per_file=%d)",
        args.limit, num_pages, args.concurrency, pages_per_file,
    )

    # Split page_assignments into batches — one batch per output file.
    # At scale, each batch could be dispatched to a separate worker process, 
    # with a coordinator process responsible for reducing aggregations.
    num_batches = math.ceil(num_pages / pages_per_file)
    raw_path = args.output_dir / "raw" / f"ingest_date={ingest_date}"
    all_results: list[PageResult] = []
    total_elements: int = 0

    for batch_idx in tqdm(range(num_batches), unit="batch", desc="Batches", position=0):
        start = batch_idx * pages_per_file
        batch_pages = page_assignments[start : start + pages_per_file]

        results, total_elements_from_api = process_batch(
            batch_pages,
            raw_path,
            args.api_key,
            args.concurrency,
            file_idx=batch_idx,
        )
        all_results.extend(results)
        if total_elements == 0 and total_elements_from_api is not None:
            total_elements = total_elements_from_api

    # --- validation ---
    total_written = sum(len(r.records) for r in all_results)
    if total_written != args.limit:
        logger.warning("Expected %d records but wrote %d.", args.limit, total_written)
    if total_elements and args.limit > total_elements:
        logger.warning(
            "Requested --limit %d exceeds API total_elements=%d. "
            "Only %d objects are available.",
            args.limit, total_elements, total_elements,
        )

    # --- aggregations ---
    aggregations = combine_aggregations(all_results)
    agg_path = write_aggregations(aggregations, args.output_dir, ingest_date)

    logger.info("Done. %d records written.", total_written)
    logger.info("Aggregations -> %s", agg_path)
    logger.info(
        "  Approaches closer than 0.2 AU : %d",
        aggregations["approaches_closer_than_0_2_au"],
    )
    logger.info(
        "  Years with close approaches   : %d",
        len(aggregations["approaches_by_year"]),
    )


if __name__ == "__main__":
    main()
