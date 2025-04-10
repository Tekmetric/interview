import pytest
from unittest.mock import patch, MagicMock
from neo.ingesters.sync_ingester import SyncDataIngester


@pytest.fixture
def mock_api_key():
    return "test_api_key"


@pytest.fixture
def mock_base_url():
    return "https://example.com/api"


@pytest.fixture
def mock_response_success():
    """
    Mock a successful API response with paginated data.
    """

    def _mock_response(page, total_pages, objects_per_page):
        response = MagicMock()
        response.status_code = 200
        response.json.return_value = {
            "near_earth_objects": [
                {"id": f"object_{i}"} for i in range(objects_per_page)
            ],
            "page": {"total_pages": total_pages},
        }
        return response

    return _mock_response


@pytest.fixture
def mock_response_failure():
    """
    Mock a failed API response.
    """
    response = MagicMock()
    response.status_code = 500
    response.json.return_value = {}
    return response


@patch("neo.ingesters.sync_ingester.requests.get")
def test_fetch_objects_success(mock_get, mock_api_key, mock_response_success):
    """
    Test successful data fetching with multiple pages.
    """
    # Mock the API responses for two pages
    mock_get.side_effect = [
        mock_response_success(page=0, total_pages=2, objects_per_page=5),
        mock_response_success(page=1, total_pages=2, objects_per_page=5),
    ]

    ingester = SyncDataIngester(api_key=mock_api_key, page_size=5)
    results = ingester.fetch_objects(limit=10)

    # Assertions
    assert len(results) == 10
    assert results[0]["id"] == "object_0"
    assert results[9]["id"] == "object_4"
    assert mock_get.call_count == 2


@patch("neo.ingesters.sync_ingester.requests.get")
def test_fetch_objects_partial_success(mock_get, mock_api_key, mock_response_success):
    """
    Test fetching data when the limit is reached before all pages are fetched.
    """
    # Mock the API responses for one page
    mock_get.side_effect = [
        mock_response_success(page=0, total_pages=2, objects_per_page=5),
    ]

    ingester = SyncDataIngester(api_key=mock_api_key, page_size=5)
    results = ingester.fetch_objects(limit=3)

    # Assertions
    assert len(results) == 3
    assert results[0]["id"] == "object_0"
    assert results[2]["id"] == "object_2"
    assert mock_get.call_count == 1


@patch("neo.ingesters.sync_ingester.requests.get")
def test_fetch_objects_failure(mock_get, mock_api_key, mock_response_failure):
    """
    Test handling of a failed API response.
    """
    # Mock the API response to fail
    mock_get.return_value = mock_response_failure

    ingester = SyncDataIngester(api_key=mock_api_key, page_size=5)
    results = ingester.fetch_objects(limit=10)

    # Assertions
    assert len(results) == 0
    assert mock_get.call_count == 1


@patch("neo.ingesters.sync_ingester.requests.get")
def test_fetch_objects_end_of_data(mock_get, mock_api_key, mock_response_success):
    """
    Test fetching data when the end of available data is reached before the limit.
    """
    # Mock the API response for one page with fewer objects than the limit
    mock_get.side_effect = [
        mock_response_success(page=0, total_pages=1, objects_per_page=3),
    ]

    ingester = SyncDataIngester(api_key=mock_api_key, page_size=5)
    results = ingester.fetch_objects(limit=10)

    # Assertions
    assert len(results) == 3
    assert results[0]["id"] == "object_0"
    assert results[2]["id"] == "object_2"
    assert mock_get.call_count == 1
