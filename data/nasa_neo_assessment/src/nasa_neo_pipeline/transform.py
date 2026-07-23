from __future__ import annotations

import pandas as pd


SILVER_COLUMNS = [
    "id",
    "neo_reference_id",
    "name",
    "name_limited",
    "designation",
    "nasa_jpl_url",
    "absolute_magnitude_h",
    "is_potentially_hazardous_asteroid",
    "estimated_diameter_min_meters",
    "estimated_diameter_max_meters",
    "closest_approach_miss_distance_kilometers",
    "closest_approach_date",
    "closest_approach_relative_velocity_kilometers_per_second",
    "first_observation_date",
    "last_observation_date",
    "observations_used",
    "orbital_period",
]


def build_silver(
    bronze_objects_df: pd.DataFrame,
    bronze_close_approaches_df: pd.DataFrame,
) -> pd.DataFrame:
    objects_df = bronze_objects_df.copy().rename(
        columns={
            "estimated_diameter_meters_min": "estimated_diameter_min_meters",
            "estimated_diameter_meters_max": "estimated_diameter_max_meters",
        }
    )

    if bronze_close_approaches_df.empty:
        objects_df["closest_approach_miss_distance_kilometers"] = pd.NA
        objects_df["closest_approach_date"] = pd.NA
        objects_df[
            "closest_approach_relative_velocity_kilometers_per_second"
        ] = pd.NA
        return objects_df[SILVER_COLUMNS]

    # Silver keeps one row per object and uses the minimum miss distance
    # to define the closest approach fields.
    ranked_close_approaches = bronze_close_approaches_df.sort_values(
        by=[
            "neo_reference_id",
            "miss_distance_kilometers",
            "close_approach_date",
            "orbiting_body",
        ],
        ascending=[True, True, True, True],
        kind="mergesort",
    )

    closest_approach_df = (
        ranked_close_approaches.groupby("neo_reference_id", as_index=False)
        .first()
        .rename(
            columns={
                "miss_distance_kilometers": "closest_approach_miss_distance_kilometers",
                "close_approach_date": "closest_approach_date",
                "relative_velocity_kilometers_per_second": (
                    "closest_approach_relative_velocity_kilometers_per_second"
                ),
            }
        )[
            [
                "neo_reference_id",
                "closest_approach_miss_distance_kilometers",
                "closest_approach_date",
                "closest_approach_relative_velocity_kilometers_per_second",
            ]
        ]
    )

    silver_df = objects_df.merge(
        closest_approach_df,
        on="neo_reference_id",
        how="left",
    )

    return silver_df[SILVER_COLUMNS]


def compute_gold(
    bronze_close_approaches_df: pd.DataFrame,
) -> tuple[int, dict[str, int]]:
    # Gold metrics are based on all qualifying close approach events.
    filtered = bronze_close_approaches_df[
        bronze_close_approaches_df["miss_distance_astronomical"] < 0.2
    ].copy()

    total_close_approaches_lt_0_2_au = int(len(filtered))

    approaches_by_year = (
        filtered.assign(year=filtered["close_approach_date"].str[:4])
        .groupby("year")
        .size()
        .astype(int)
        .to_dict()
    )

    approaches_by_year = {
        str(year): int(count) for year, count in approaches_by_year.items()
    }

    return total_close_approaches_lt_0_2_au, approaches_by_year
