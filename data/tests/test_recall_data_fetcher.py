import pytest

from recall.errors import DataFetcherError
from recall.recall_data_fetcher import RecallDataFetcher


def test_extract_file_url_success(starting_page_content_correct):
    file_url = RecallDataFetcher._extract_file_url(starting_page_content_correct)
    assert file_url == "https://data.transportation.gov/api/views/6axg-epim/rows.csv?accessType=DOWNLOAD"


def test_extract_file_url_fail(starting_page_content_wrong):
    with pytest.raises(DataFetcherError):
        RecallDataFetcher._extract_file_url(starting_page_content_wrong)
