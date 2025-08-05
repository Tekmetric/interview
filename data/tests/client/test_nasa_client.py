import asyncio

import pytest

from _neo.client import NearEarthObject
from _neo.client.nasa_client import NeoClient, NeosNotFoundError, guard_semaphore


class StubResponse:
    """Mock HTTP response."""

    def __init__(self, data):
        self._data = data

    async def __aenter__(self):
        return self

    async def __aexit__(self, exc_type, exc_val, exc_tb):
        pass

    def raise_for_status(self):
        pass  # 200

    async def json(self):
        return self._data


class StubRetryClient:
    """Mock RetryClient."""

    def __init__(self, pages):
        self._pages = pages

    def get(self, url, params):
        page = params.get("page", 0)
        return StubResponse(self._pages[page] if page < len(self._pages) else {"near_earth_objects": []})

    async def close(self):
        pass


@pytest.mark.asyncio
async def test_guard_semaphore():
    """Test that the guard_semaphore function limits the number of concurrent tasks."""
    semaphore = asyncio.Semaphore(2)

    active_tasks = 0
    max_active_tasks = 0

    async def sample_task(identifier):
        nonlocal active_tasks, max_active_tasks

        active_tasks += 1
        max_active_tasks = max(max_active_tasks, active_tasks)
        assert active_tasks <= max_active_tasks

        await asyncio.sleep(0.1)

        active_tasks -= 1
        return identifier

    tasks = [sample_task(i) for i in range(10)]
    results = await asyncio.gather(*guard_semaphore(tasks, semaphore))

    assert sorted(results) == list(range(10))
    assert max_active_tasks <= 2


@pytest.mark.asyncio
async def test_list_entries(sample_neos):
    """Test list entries function returns correct NEO data."""
    client = NeoClient(api_key="dummy")
    client._client = StubRetryClient(sample_neos)

    result = await client.list_entries(limit=1, page_size=1)

    assert isinstance(result, list)
    assert len(result) == 1

    assert isinstance(result[0], NearEarthObject)
    assert result[0].name == "Test NEO"


@pytest.mark.asyncio
async def test_list_entries_raises_neos_not_found_error():
    """Test list entries function raises NeosNotFoundError."""
    client = NeoClient(api_key="dummy")
    client._client = StubRetryClient([])

    with pytest.raises(NeosNotFoundError):
        _ = await client.list_entries(limit=1, page_size=1)
