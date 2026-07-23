from __future__ import annotations

import time

import pandas as pd

from .config import DATA_PATH, OBJECT_LIMIT, REQUEST_DELAY_SECONDS, snapshot_date
from .extract import fetch_page, flatten_close_approaches, flatten_neo_objects
from .load import reset_output_path, write_dataset, write_gold_aggregations
from .logging_utils import get_logger
from .transform import SILVER_COLUMNS, build_silver, compute_gold


BRONZE_NEO_OBJECTS_REQUIRED_COLUMNS = [
    "id",
    "neo_reference_id",
    "name",
]

BRONZE_CLOSE_APPROACHES_REQUIRED_COLUMNS = [
    "neo_reference_id",
    "close_approach_date",
    "miss_distance_astronomical",
    "miss_distance_kilometers",
    "relative_velocity_kilometers_per_second",
]

SILVER_REQUIRED_NON_NULL_COLUMNS = [
    "id",
    "neo_reference_id",
    "name",
]


def trim_to_object_limit(
    page_objects: list[dict],
    total_objects: int,
) -> list[dict]:
    remaining = OBJECT_LIMIT - total_objects
    return page_objects[:remaining]


def validate_required_columns(
    df: pd.DataFrame,
    required_columns: list[str],
    dataset_name: str,
) -> None:
    missing_columns = [column for column in required_columns if column not in df.columns]
    if missing_columns:
        raise ValueError(
            f"{dataset_name} validation failed: missing columns {missing_columns}"
        )


def validate_non_null_columns(
    df: pd.DataFrame,
    required_columns: list[str],
    dataset_name: str,
) -> None:
    for column in required_columns:
        if df[column].isna().any():
            raise ValueError(
                f"{dataset_name} validation failed: null values found in '{column}'"
            )


def validate_no_duplicates(
    df: pd.DataFrame,
    key_columns: list[str],
    dataset_name: str,
) -> None:
    duplicate_mask = df.duplicated(subset=key_columns, keep=False)
    if duplicate_mask.any():
        duplicate_rows = df.loc[duplicate_mask, key_columns].drop_duplicates()
        raise ValueError(
            f"{dataset_name} validation failed: duplicate keys found in {key_columns}. "
            f"Examples: {duplicate_rows.head(10).to_dict(orient='records')}"
        )


def validate_bronze_page(
    neo_objects_df: pd.DataFrame,
    close_approaches_df: pd.DataFrame,
    objects_to_process: list[dict],
    page: int,
) -> None:
    expected_object_count = len(objects_to_process)
    expected_close_approach_count = sum(
        len(obj.get("close_approach_data", []))
        for obj in objects_to_process
    )

    validate_required_columns(
        neo_objects_df,
        BRONZE_NEO_OBJECTS_REQUIRED_COLUMNS,
        "Bronze neo_objects",
    )
    validate_required_columns(
        close_approaches_df,
        BRONZE_CLOSE_APPROACHES_REQUIRED_COLUMNS,
        "Bronze close_approaches",
    )

    if len(neo_objects_df) != expected_object_count:
        raise ValueError(
            f"Bronze neo_objects validation failed on page {page}: "
            f"expected {expected_object_count} rows, got {len(neo_objects_df)}"
        )

    if len(close_approaches_df) != expected_close_approach_count:
        raise ValueError(
            f"Bronze close_approaches validation failed on page {page}: "
            f"expected {expected_close_approach_count} rows, got {len(close_approaches_df)}"
        )

    validate_non_null_columns(
        neo_objects_df,
        ["id", "neo_reference_id"],
        "Bronze neo_objects",
    )
    validate_non_null_columns(
        close_approaches_df,
        BRONZE_CLOSE_APPROACHES_REQUIRED_COLUMNS,
        "Bronze close_approaches",
    )
    validate_no_duplicates(
        neo_objects_df,
        ["neo_reference_id"],
        "Bronze neo_objects",
    )


def validate_silver(silver_df: pd.DataFrame) -> None:
    validate_required_columns(silver_df, SILVER_COLUMNS, "Silver")

    if len(silver_df) != OBJECT_LIMIT:
        raise ValueError(
            f"Silver validation failed: expected {OBJECT_LIMIT} rows, got {len(silver_df)}"
        )

    validate_non_null_columns(
        silver_df,
        SILVER_REQUIRED_NON_NULL_COLUMNS,
        "Silver",
    )
    validate_no_duplicates(
        silver_df,
        ["neo_reference_id"],
        "Silver",
    )


def validate_gold(
    total_close_approaches_lt_0_2_au: int,
    approaches_by_year: dict[str, int],
) -> None:
    if not isinstance(total_close_approaches_lt_0_2_au, int):
        raise ValueError(
            "Gold validation failed: total_close_approaches_lt_0_2_au must be an integer"
        )

    if not isinstance(approaches_by_year, dict):
        raise ValueError(
            "Gold validation failed: approaches_by_year must be a dictionary"
        )

    if not all(isinstance(year, str) for year in approaches_by_year.keys()):
        raise ValueError(
            "Gold validation failed: approaches_by_year keys must be strings"
        )

    if not all(isinstance(count, int) for count in approaches_by_year.values()):
        raise ValueError(
            "Gold validation failed: approaches_by_year values must be integers"
        )


def run() -> None:
    logger = get_logger()
    current_snapshot_date = snapshot_date()

    logger.info("Pipeline started for snapshot date %s", current_snapshot_date)
    logger.info("Target object limit: %s", OBJECT_LIMIT)

    bronze_objects_path = DATA_PATH / "bronze" / "neo_objects" / current_snapshot_date
    bronze_close_approaches_path = (
        DATA_PATH / "bronze" / "close_approaches" / current_snapshot_date
    )

    reset_output_path(bronze_objects_path)
    reset_output_path(bronze_close_approaches_path)

    page = 0
    total_objects = 0
    total_close_approaches = 0

    while total_objects < OBJECT_LIMIT:
        response = fetch_page(page)
        page_objects = response["near_earth_objects"]

        # The API is paginated, but the exercise wants exactly OBJECT_LIMIT objects.
        # We fetch full pages and trim only the final page.
        objects_to_process = trim_to_object_limit(
            page_objects=page_objects,
            total_objects=total_objects,
        )

        # Bronze keeps the raw source split into object-level and event-level datasets.
        neo_objects_df = flatten_neo_objects(objects_to_process)
        close_approaches_df = flatten_close_approaches(objects_to_process)

        # Fail fast if the raw response and flattened Bronze rows do not match.
        validate_bronze_page(
            neo_objects_df=neo_objects_df,
            close_approaches_df=close_approaches_df,
            objects_to_process=objects_to_process,
            page=page,
        )

        neo_objects_df.to_parquet(
            bronze_objects_path / f"page_{page:03d}.parquet",
            index=False,
        )
        close_approaches_df.to_parquet(
            bronze_close_approaches_path / f"page_{page:03d}.parquet",
            index=False,
        )

        total_objects += len(objects_to_process)
        total_close_approaches += len(close_approaches_df)

        logger.info(
            "Bronze page %s complete | objects=%s | close_approaches=%s | running_total_objects=%s",
            page,
            len(neo_objects_df),
            len(close_approaches_df),
            total_objects,
        )

        page += 1
        time.sleep(REQUEST_DELAY_SECONDS)

    logger.info(
        "Bronze layer complete | total_objects=%s | total_close_approaches=%s",
        total_objects,
        total_close_approaches,
    )

    bronze_objects_df = pd.read_parquet(bronze_objects_path)
    bronze_close_approaches_df = pd.read_parquet(bronze_close_approaches_path)

    validate_no_duplicates(
        bronze_objects_df,
        ["neo_reference_id"],
        "Bronze neo_objects",
    )

    # Silver is the curated one-row-per-object dataset required by the exercise.
    silver_df = build_silver(
        bronze_objects_df=bronze_objects_df,
        bronze_close_approaches_df=bronze_close_approaches_df,
    )
    validate_silver(silver_df)

    write_dataset(
        df=silver_df,
        base_path=DATA_PATH,
        layer="silver",
        dataset="neo_objects",
        snapshot_date=current_snapshot_date,
        filename="neo_objects.parquet",
    )

    logger.info("Silver layer complete | rows=%s", len(silver_df))

    # Gold is built from all close approach events, not from Silver,
    # because the required metrics are event-based.
    total_close_approaches_lt_0_2_au, approaches_by_year = compute_gold(
        bronze_close_approaches_df=bronze_close_approaches_df
    )
    validate_gold(
        total_close_approaches_lt_0_2_au=total_close_approaches_lt_0_2_au,
        approaches_by_year=approaches_by_year,
    )

    write_gold_aggregations(
        total_close_approaches_lt_0_2_au=total_close_approaches_lt_0_2_au,
        approaches_by_year=approaches_by_year,
        base_path=DATA_PATH,
        snapshot_date=current_snapshot_date,
    )

    logger.info(
        "Gold layer complete | total_lt_0_2_au=%s | years=%s",
        total_close_approaches_lt_0_2_au,
        len(approaches_by_year),
    )
    logger.info("Pipeline completed successfully")
