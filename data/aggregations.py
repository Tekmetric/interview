"""
Contains PySpark functions for aggregating and transforming NASA NeoWs API data.
See schemas.py for expected DataFrame input formats.
"""

import pyspark.sql.functions as F
from pyspark.sql import DataFrame
from pyspark.sql.window import Window


def agg_near_misses(df: DataFrame) -> DataFrame:
    """
    Aggregate raw NeoWs data to determine count of approaches within .2 AU.
    Args:
        df: DataFrame using the NeoWs CLOSE_APPROACH_SCHEMA.
    """
    cond = F.when(F.col("miss_distance.astronomical") < 0.2, 1).otherwise(0)
    return df.agg(F.sum(cond).alias("approach_count"))


def agg_approaches_by_year(df: DataFrame) -> DataFrame:
    """
    Aggregate raw NeoWs data to determine number of close approaches by year.
    Args:
        df: DataFrame using the NeoWs CLOSE_APPROACH_SCHEMA.
    """
    agg_df = df.groupBy(F.year("close_approach_date").alias("year")).agg(
        F.count("*").alias("approach_count")
    )
    return agg_df


def agg_closest_approach(df: DataFrame) -> DataFrame:
    """
    Aggregate raw NeoWs data to find an object's closest approach data.
    If an object has no close approach data, new derived columns will be null.
    Args:
        df: DataFrame using the NEOWS_SCHEMA.
    """
    exp_df = df.select(
        "id", F.explode_outer("close_approach_data").alias("approach_data")
    )
    w = Window.partitionBy("id").orderBy("approach_data.miss_distance.kilometers")
    ranked_df = exp_df.withColumn("rank", F.rank().over(w)).filter(F.col("rank") == 1)
    joined_df = df.join(ranked_df, on=["id"])
    result_df = joined_df.select(
        "id",
        "neo_reference_id",
        "name",
        "name_limited",
        "designation",
        "nasa_jpl_url",
        "absolute_magnitude_h",
        "is_potentially_hazardous_asteroid",
        "estimated_diameter.meters.estimated_diameter_min",
        "estimated_diameter.meters.estimated_diameter_max",
        "approach_data.close_approach_date",
        "approach_data.miss_distance.kilometers",
        "approach_data.relative_velocity.kilometers_per_second",
        "orbital_data.first_observation_date",
        "orbital_data.last_observation_date",
        "orbital_data.observations_used",
        "orbital_data.orbital_period",
    )

    return result_df.withColumnsRenamed(
        {
            "estimated_diameter_min": "estimated_diameter_min_meters",
            "estimated_diameter_max": "estimated_diameter_max_meters",
            "kilometers": "close_approach_miss_distance_kilometers",
            "kilometers_per_second": "close_approach_velocity_kilometers_per_second",
        }
    )
