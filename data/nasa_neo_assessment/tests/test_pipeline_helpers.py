import pandas as pd
import pytest

from nasa_neo_pipeline.pipeline import (
    trim_to_object_limit,
    validate_gold,
    validate_no_duplicates,
    validate_non_null_columns,
)


# The exercise requires fetching full pages until the first 200 objects are collected,
# then processing only the first 200 objects.
def test_trim_to_object_limit_trims_last_page():
    page_objects = [{"id": str(i)} for i in range(20)]
    trimmed = trim_to_object_limit(page_objects=page_objects, total_objects=195)

    assert len(trimmed) == 5


def test_duplicate_detection_raises():
    df = pd.DataFrame(
        [
            {"neo_reference_id": "1"},
            {"neo_reference_id": "1"},
        ]
    )

    with pytest.raises(ValueError):
        validate_no_duplicates(df, ["neo_reference_id"], "test_dataset")


def test_non_null_validation_raises():
    df = pd.DataFrame(
        [
            {"id": "1", "neo_reference_id": None},
        ]
    )

    with pytest.raises(ValueError):
        validate_non_null_columns(df, ["id", "neo_reference_id"], "test_dataset")


def test_gold_validation_accepts_valid_types():
    validate_gold(
        total_close_approaches_lt_0_2_au=3,
        approaches_by_year={"2020": 2, "2021": 1},
    )
