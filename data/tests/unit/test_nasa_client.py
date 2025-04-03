import pytest

from data.nasa_client import NASAClient, NasaApiException

NASA_URL = "http://localhost:8000"
NEO_URL = f"{NASA_URL}/neo/rest/v1/neo"
API_KEY = "secretkey"


@pytest.fixture
def get_mock(mocker):
    mock = mocker.patch("data.nasa_client.requests.get")
    return mock


@pytest.fixture
def client():
    return NASAClient(url=NASA_URL, api_key=API_KEY)


@pytest.mark.parametrize("url", ["http://localhost/", "http://localhost"])
def test_base_url_is_normalized(url):
    client = NASAClient(url=url)
    assert client._base_url == "http://localhost"


class TestNeoApi:
    @pytest.mark.parametrize(
        "input_args, expected",
        [
            (
                {},
                {
                    "url": f"{NEO_URL}/browse",
                    "params": {"page": 0, "size": 20, "api_key": API_KEY},
                },
            ),
            (
                {"page": 5, "page_size": 2},
                {
                    "url": f"{NEO_URL}/browse",
                    "params": {"page": 5, "size": 2, "api_key": API_KEY},
                },
            ),
        ],
    )
    def test_browse__requests_the_correct_url(
        self, client, get_mock, input_args, expected
    ):
        client.neo.browse(**input_args)

        get_mock.assert_called_once_with(**expected)

    @pytest.mark.parametrize("input_args", [{"page": -2}, {"page_size": 50}])
    def test_browse__raises_exception_for_invalid_input(self, client, input_args):
        with pytest.raises(NasaApiException):
            client.neo.browse(**input_args)
