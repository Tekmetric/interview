"""
Contains tests for Spark transformations and aggregations on NeoWs data.
"""

import os

import pytest
from pyspark.sql import SparkSession
from pyspark.sql.types import IntegerType, LongType, StructField, StructType
from pyspark.testing import assertDataFrameEqual

from data.aggregations import (
    agg_approaches_by_year,
    agg_closest_approach,
    agg_near_misses,
)
from data.schemas import (
    AGG_CLOSEST_APPROACH_SCHEMA,
    CLOSE_APPROACH_SCHEMA,
    NEOWS_SCHEMA,
)

DATA_DIR = f"{os.path.dirname(os.path.realpath(__file__))}/data"


@pytest.fixture
def spark_session() -> SparkSession:
    return SparkSession.Builder().master("local[*]").getOrCreate()


@pytest.mark.parametrize(
    "input_data,expected_data",
    [
        (
            f"{DATA_DIR}/input/agg_near_misses.json",
            f"{DATA_DIR}/expected/agg_near_misses.json",
        )
    ],
)
def test_agg_near_misses(spark_session, input_data, expected_data):
    df = spark_session.read.json(
        input_data, multiLine=True, schema=CLOSE_APPROACH_SCHEMA
    )
    actual_df = agg_near_misses(df)
    expected_df = df = spark_session.read.json(expected_data, multiLine=True)
    assertDataFrameEqual(actual_df, expected_df)


@pytest.mark.parametrize(
    "input_data,expected_data",
    [
        (
            f"{DATA_DIR}/input/agg_approaches_by_year.json",
            f"{DATA_DIR}/expected/agg_approaches_by_year.json",
        )
    ],
)
def test_agg_approaches_by_year(spark_session, input_data, expected_data):
    df = spark_session.read.json(
        input_data, multiLine=True, schema=CLOSE_APPROACH_SCHEMA
    )
    actual_df = agg_approaches_by_year(df)
    schema = StructType(
        [StructField("year", IntegerType()), StructField("approach_count", LongType())]
    )
    expected_df = spark_session.read.json(expected_data, multiLine=True, schema=schema)
    assertDataFrameEqual(actual_df, expected_df)


@pytest.mark.parametrize(
    "input_data,expected_data",
    [
        # Scenario includes an object with no close approach data.
        (
            f"{DATA_DIR}/input/agg_closest_approach.json",
            f"{DATA_DIR}/expected/agg_closest_approach.json",
        )
    ],
)
def test_agg_closest_approach(spark_session, input_data, expected_data):
    df = spark_session.read.json(input_data, multiLine=True, schema=NEOWS_SCHEMA)
    actual_df = agg_closest_approach(df)
    expected_df = spark_session.read.json(
        expected_data, multiLine=True, schema=AGG_CLOSEST_APPROACH_SCHEMA
    )
    assertDataFrameEqual(actual_df, expected_df)
