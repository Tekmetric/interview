from unittest.mock import patch

import pyarrow as pa
import pytest

from _neo.core import (
    NeosNotFoundError,
    _fetch_neo,
    count_close_approaches,
    count_close_approaches_per_year,
    fetch_neos,
    process_neos,
)


class StubResponse:
    """Mock HTTP response."""

    def __init__(self, data):
        """Initialize with mock data."""
        self._data = data

    async def __aenter__(self):
        """Enter context manager."""
        return self

    async def __aexit__(self, exc_type, exc_val, exc_tb):
        """Exit context manager."""
        pass

    def raise_for_status(self):
        """Mock raise_for_status method."""
        pass  # 200 OK

    async def json(self):
        """Return mock JSON data."""
        return self._data


class StubClientSession:
    """Mock ClientSession."""

    def __init__(self, pages):
        """Initialize with a list of pages."""
        super().__init__()
        self._pages = pages

    async def __aenter__(self):
        """Enter context manager."""
        return self

    async def __aexit__(self, exc_type, exc_val, exc_tb):
        """Exit context manager."""
        pass

    def get(self, url: str, params: dict) -> StubResponse:
        """Mock GET request."""
        page = params.get("page", 0)
        if page < len(self._pages):
            return StubResponse(self._pages[page])
        return StubResponse({"near_earth_objects": []})


@pytest.mark.asyncio
async def test_fetch_neo():
    """Test fetching a single page of NEO data."""
    session = StubClientSession(pages=[{"near_earth_objects": [{"id": "1", "name": "Stub NEO"}]}])
    result = await _fetch_neo(session, page=0)

    assert isinstance(result, list)
    assert result[0]["name"] == "Stub NEO"


@pytest.mark.asyncio
async def test_fetch_neos():
    """Test fetching multiple pages of NEO data."""
    stub_pages = [
        {"near_earth_objects": [{"id": "1", "name": "Stub NEO 1"}]},
        {"near_earth_objects": [{"id": "2", "name": "Stub NEO 2"}]},
        {"near_earth_objects": [{"id": "3", "name": "Stub NEO 3"}]},
    ]
    with (
        patch("_neo.core.aiohttp.ClientSession", return_value=StubClientSession(pages=stub_pages)),
        patch("_neo.core.PAGE_SIZE", 1),
    ):
        result = await fetch_neos(limit=2)

    assert len(result) == 2
    assert result[0]["name"] == "Stub NEO 1"
    assert result[1]["name"] == "Stub NEO 2"


@pytest.mark.asyncio
async def test_fetch_neos_raises_error():
    """Test fetching NEOs raises NeosNotFoundError when no NEOs are found."""
    with patch("_neo.core.aiohttp.ClientSession", return_value=StubClientSession(pages=[])):
        with pytest.raises(NeosNotFoundError):
            await fetch_neos()


@pytest.mark.asyncio
async def test_process_neos(neos: list[dict]):
    """Test processing NEO data into a PyArrow Table."""
    table = await process_neos(neos)

    assert isinstance(table, pa.Table)
    assert table.num_rows == 1
    assert table["id"][0].as_py() == "123"
    assert table["name"][0].as_py() == "Test NEO"
    assert table["estimated_diameter_min_m"][0].as_py() == 10.0
    assert table["closest_approach_miss_distance"][0].as_py() == "12345.6"


@pytest.mark.parametrize(
    "threshold,expected",
    [
        (0.2, 2),  # Only 0.1 and 0.05 are < 0.2
        (0.4, 4),  # All are < 0.4
        (0.01, 0),  # None are < 0.01
    ],
)
def test_count_close_approaches(threshold, expected):
    """Test counting close approaches below a given threshold."""
    neos = [
        {
            "close_approach_data": [
                {"miss_distance": {"astronomical": "0.1"}},
                {"miss_distance": {"astronomical": "0.3"}},
            ]
        },
        {
            "close_approach_data": [
                {"miss_distance": {"astronomical": "0.05"}},
            ]
        },
        {
            "close_approach_data": [
                {"miss_distance": {"astronomical": "0.25"}},
            ]
        },
    ]
    assert count_close_approaches(neos, threshold_au=threshold) == expected


def test_count_close_approaches_per_year():
    """Test counting close approaches per year."""
    neos = [
        {
            "close_approach_data": [
                {"close_approach_date": "2023-01-01"},
                {"close_approach_date": "2024-05-10"},
            ]
        },
        {
            "close_approach_data": [
                {"close_approach_date": "2024-07-15"},
            ]
        },
        {
            "close_approach_data": [
                {"close_approach_date": "2023-12-31"},
            ]
        },
    ]
    result = count_close_approaches_per_year(neos)
    assert result == {2023: 2, 2024: 2}
