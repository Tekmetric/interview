import pyarrow as pa
import pytest

from _neo.process import count_close_approaches, count_close_approaches_per_year, process_neos


@pytest.mark.parametrize(
    "threshold,expected",
    [
        (0.2, 3),
        (0.4, 4),
        (0.01, 0),
    ],
)
def test_count_close_approaches(threshold, expected, sample_neos_models):
    """Test counting close approaches below a given threshold."""
    assert count_close_approaches(sample_neos_models, threshold_au=threshold) == expected


def test_count_close_approaches_per_year(sample_neos_models):
    """Test counting close approaches per year."""
    result = count_close_approaches_per_year(sample_neos_models)
    assert result == {2023: 2, 2024: 2}


@pytest.mark.asyncio
async def test_process_neos(sample_neos_models):
    """Test processing NEO data into a pyarrow table."""
    table = await process_neos(sample_neos_models)

    assert isinstance(table, pa.Table)
    assert table.num_rows == 3

    row = table.slice(0, 1).to_pydict()
    assert row["name"][0] == "Neo 1"
    assert row["estimated_diameter_min_m"][0] == 1.0
    assert row["estimated_diameter_max_m"][0] == 2.0
    assert float(row["closest_approach_miss_distance"][0]) > 0
    assert float(row["closest_relative_velocity_kps"][0]) > 0
