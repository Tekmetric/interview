from nasa_neo_pipeline.transform import compute_gold


def test_compute_gold_returns_expected_outputs(bronze_close_approaches_df):
    total, approaches_by_year = compute_gold(bronze_close_approaches_df)

    assert total == 1
    assert approaches_by_year == {"2020": 1}
