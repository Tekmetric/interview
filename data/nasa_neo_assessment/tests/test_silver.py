from nasa_neo_pipeline.transform import SILVER_COLUMNS, build_silver


def test_build_silver_selects_minimum_miss_distance(
    bronze_objects_df,
    bronze_close_approaches_df,
):
    silver_df = build_silver(bronze_objects_df, bronze_close_approaches_df)

    assert len(silver_df) == 1
    assert silver_df.iloc[0]["closest_approach_miss_distance_kilometers"] == 100.0
    assert silver_df.iloc[0]["closest_approach_date"] == "2020-03-01"
    assert (
        silver_df.iloc[0]["closest_approach_relative_velocity_kilometers_per_second"]
        == 4.0
    )


def test_silver_has_required_columns(
    bronze_objects_df,
    bronze_close_approaches_df,
):
    silver_df = build_silver(bronze_objects_df, bronze_close_approaches_df)

    assert list(silver_df.columns) == SILVER_COLUMNS
