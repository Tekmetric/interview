import os
from unittest.mock import MagicMock, patch

import pytest

from tekmetric_data.nasa_client import NasaClient, NeoClient, NasaClientFactory


def test_nasa_client_get():
    client = NasaClient()
    with patch.object(client._session, "get", return_value=MagicMock(json=lambda: {"result": "ok"})) as mock_get:
        result = client.get("/test", params={"foo": "bar"})
        mock_get.assert_called_once()
        args, kwargs = mock_get.call_args
        assert "api_key" in kwargs["params"]
        print(kwargs["params"]["api_key"])
        assert kwargs["params"]["api_key"] == "DEMO_KEY"
        assert result == {"result": "ok"}


def test_nasa_client_get_includes_api_key():
    with patch.dict(os.environ, {"TEK_NASA_API_KEY": "testkey"}):
        client = NasaClient()
        with patch.object(client._session, "get", return_value=MagicMock(json=lambda: {"result": "ok"})) as mock_get:
            result = client.get("/test", params={"foo": "bar"})
            mock_get.assert_called_once()
            args, kwargs = mock_get.call_args
            assert "api_key" in kwargs["params"]
            assert kwargs["params"]["api_key"] == "testkey"
            assert result == {"result": "ok"}


def test_neo_client_browse_calls_client_get():
    mock_client = MagicMock()
    neo = NeoClient(mock_client, page_size=42)
    neo.browse(page=3)
    mock_client.get.assert_called_once_with("/neo/rest/v1/neo/browse", params={"page": 3, "size": 42})


def test_nasa_client_factory_returns_same_instance():
    base_client = MagicMock()
    factory = NasaClientFactory(base_client)
    neo1 = factory.get_client("neo", page_size=10)
    neo2 = factory.get_client("neo", page_size=20)
    assert neo1._page_size == 10
    assert neo2._page_size == 20


def test_nasa_client_factory_invalid_type():
    base_client = MagicMock()
    factory = NasaClientFactory(base_client)
    with pytest.raises(ValueError):
        factory.get_client("invalid")
