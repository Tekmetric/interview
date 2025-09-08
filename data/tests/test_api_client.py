import pytest
import responses
from unittest.mock import Mock, patch
import requests
from utils.api_client import NASAAPIClient, APIConfig


class TestNASAAPIClient:
    
    def test_init_with_default_config(self):
        client = NASAAPIClient("test_key")
        assert client.config.api_key == "test_key"
        assert client.config.base_url == "https://api.nasa.gov/neo/rest/v1"
        assert client.config.rate_limit_delay == 0.2

    def test_init_with_custom_config(self):
        config = APIConfig(rate_limit_delay=0.5, max_retries=5)
        client = NASAAPIClient("test_key", config)
        assert client.config.rate_limit_delay == 0.5
        assert client.config.max_retries == 5

    @responses.activate
    def test_make_request_success(self):
        responses.add(
            responses.GET,
            "https://api.nasa.gov/neo/rest/v1/test",
            json={"status": "success"},
            status=200
        )
        
        client = NASAAPIClient("test_key")
        result = client._make_request("test", {})
        
        assert result["status"] == "success"
        assert len(responses.calls) == 1

    @responses.activate
    def test_make_request_with_retry(self):
        responses.add(
            responses.GET,
            "https://api.nasa.gov/neo/rest/v1/test",
            json={"error": "Server error"},
            status=500
        )
        responses.add(
            responses.GET,
            "https://api.nasa.gov/neo/rest/v1/test",
            json={"status": "success"},
            status=200
        )
        
        client = NASAAPIClient("test_key")
        result = client._make_request("test", {})
        
        assert result["status"] == "success"
        assert len(responses.calls) == 2

    @responses.activate
    def test_make_request_max_retries_exceeded(self):
        responses.add(
            responses.GET,
            "https://api.nasa.gov/neo/rest/v1/test",
            json={"error": "Server error"},
            status=500
        )
        
        client = NASAAPIClient("test_key")
        
        with pytest.raises(Exception, match="API request failed after 3 attempts"):
            client._make_request("test", {})

    @responses.activate
    def test_get_neo_browse_page(self):
        responses.add(
            responses.GET,
            "https://api.nasa.gov/neo/rest/v1/neo/browse",
            json={
                "near_earth_objects": [{"id": "1", "name": "Test"}],
                "page": {"size": 20, "total_elements": 100}
            },
            status=200
        )
        
        client = NASAAPIClient("test_key")
        result = client.get_neo_browse_page(page=0, size=20)
        
        assert "near_earth_objects" in result
        assert len(result["near_earth_objects"]) == 1
        assert "api_key" in responses.calls[0].request.params

    @responses.activate
    def test_fetch_neo_data_exact_count(self):
        responses.add(
            responses.GET,
            "https://api.nasa.gov/neo/rest/v1/neo/browse",
            json={
                "near_earth_objects": [{"id": str(i), "name": f"Test{i}"} for i in range(20)],
                "page": {"size": 20, "total_elements": 1000}
            },
            status=200
        )
        
        client = NASAAPIClient("test_key")
        neos = list(client.fetch_neo_data(target_count=5))
        
        assert len(neos) == 5
        assert neos[0]["id"] == "0"

    @responses.activate
    def test_fetch_neo_data_insufficient_data(self):
        responses.add(
            responses.GET,
            "https://api.nasa.gov/neo/rest/v1/neo/browse",
            json={
                "near_earth_objects": [{"id": "1", "name": "Test"}],
                "page": {"size": 20, "total_elements": 1}
            },
            status=200
        )
        
        client = NASAAPIClient("test_key")
        neos = list(client.fetch_neo_data(target_count=5))
        
        assert len(neos) == 1

    @responses.activate
    def test_fetch_neo_data_api_error(self):
        responses.add(
            responses.GET,
            "https://api.nasa.gov/neo/rest/v1/neo/browse",
            json={"error": "API error"},
            status=500
        )
        
        client = NASAAPIClient("test_key")
        neos = list(client.fetch_neo_data(target_count=5))
        
        assert len(neos) == 0

    @responses.activate
    def test_get_neo_details(self):
        responses.add(
            responses.GET,
            "https://api.nasa.gov/neo/rest/v1/neo/2000433",
            json={"id": "2000433", "name": "433 Eros"},
            status=200
        )
        
        client = NASAAPIClient("test_key")
        result = client.get_neo_details("2000433")
        
        assert result["id"] == "2000433"
        assert result["name"] == "433 Eros"

    def test_rate_limiting_delay(self):
        client = NASAAPIClient("test_key")
        
        with patch('time.sleep') as mock_sleep:
            with patch.object(client, '_make_request') as mock_request:
                # Return 20 NEOs for first call (full batch), then 1 for second call to trigger sleep
                first_batch = [{"id": str(i), "name": f"Test{i}"} for i in range(20)]
                mock_request.side_effect = [
                    {"near_earth_objects": first_batch},
                    {"near_earth_objects": [{"id": "21", "name": "Test21"}]}
                ]
                
                list(client.fetch_neo_data(target_count=21))
                
                mock_sleep.assert_called_with(0.2)

    def test_make_request_max_retries_exceeded(self):
        """Test the retry logic with max retries exceeded."""
        client = NASAAPIClient("test_key")
        
        with patch.object(client.session, 'get') as mock_get:
            mock_get.side_effect = requests.exceptions.RequestException("Test error")
            
            with pytest.raises(Exception, match="API request failed after 3 attempts"):
                client._make_request("test", {})

    def test_make_request_unexpected_error_path(self):
        """Test the unreachable error path by mocking max_retries to 0."""
        client = NASAAPIClient("test_key")
        client.config.max_retries = 0
        
        with patch.object(client.session, 'get') as mock_get:
            mock_get.side_effect = requests.exceptions.RequestException("Test error")
            
            with pytest.raises(Exception, match="Unexpected error in API request"):
                client._make_request("test", {})

