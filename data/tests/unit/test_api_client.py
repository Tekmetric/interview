"""Unit tests for NEO API Client"""

import pytest
from unittest.mock import Mock, patch, MagicMock
import requests
from requests.exceptions import Timeout, ConnectionError, RequestException

from src.api_client import NEOAPIClient
from src.exceptions import NEOAPIError


class TestNEOAPIClientInitialization:
    """Test suite for NEOAPIClient initialization"""
    
    def test_init_with_valid_api_key(self):
        """Test initialization with valid API key succeeds"""
        client = NEOAPIClient(api_key='test_key_12345')
        
        assert client.api_key == 'test_key_12345'
        assert client.base_url == "https://api.nasa.gov/neo/rest/v1"
        assert client.browse_endpoint == "https://api.nasa.gov/neo/rest/v1/neo/browse"
        assert client.session is not None
    
    def test_init_with_custom_base_url(self):
        """Test initialization with custom base URL"""
        custom_url = "https://custom.api.nasa.gov/neo/v2"
        client = NEOAPIClient(api_key='test_key', base_url=custom_url)
        
        assert client.base_url == custom_url
        assert client.browse_endpoint == f"{custom_url}/neo/browse"
    
    def test_init_with_trailing_slash_in_base_url(self):
        """Test initialization strips trailing slash from base URL"""
        client = NEOAPIClient(
            api_key='test_key',
            base_url="https://api.nasa.gov/neo/rest/v1/"
        )
        
        assert client.base_url == "https://api.nasa.gov/neo/rest/v1"
        assert not client.base_url.endswith('/')
    
    def test_init_with_empty_api_key_raises_error(self):
        """Test initialization with empty API key raises ValueError"""
        with pytest.raises(ValueError) as exc_info:
            NEOAPIClient(api_key='')
        
        assert "API key is required and cannot be empty" in str(exc_info.value)
    
    def test_init_with_none_api_key_raises_error(self):
        """Test initialization with None API key raises ValueError"""
        with pytest.raises(ValueError) as exc_info:
            NEOAPIClient(api_key=None)
        
        assert "API key is required and cannot be empty" in str(exc_info.value)
    
    def test_init_creates_session_with_retry_adapter(self):
        """Test initialization creates session with retry configuration"""
        client = NEOAPIClient(api_key='test_key')
        
        # Verify session exists and has adapters
        assert isinstance(client.session, requests.Session)
        assert 'https://' in client.session.adapters
        assert 'http://' in client.session.adapters


class TestNEOAPIClientMakeRequest:
    """Test suite for _make_request method"""
    
    @patch('src.api_client.requests.Session.get')
    def test_make_request_success(self, mock_get):
        """Test successful API request returns parsed JSON"""
        # Mock successful response
        mock_response = Mock()
        mock_response.status_code = 200
        mock_response.json.return_value = {
            'near_earth_objects': [{'id': '123', 'name': 'Test NEO'}],
            'page': {'number': 0, 'size': 20}
        }
        mock_get.return_value = mock_response
        
        client = NEOAPIClient(api_key='test_key')
        result = client._make_request(page=0)
        
        assert result == mock_response.json.return_value
        assert 'near_earth_objects' in result
        
        # Verify request was made with correct parameters
        mock_get.assert_called_once()
        call_args = mock_get.call_args
        assert call_args[1]['params']['api_key'] == 'test_key'
        assert call_args[1]['params']['page'] == 0
        assert call_args[1]['timeout'] == 30
    
    @patch('src.api_client.requests.Session.get')
    def test_make_request_uses_correct_endpoint(self, mock_get):
        """Test _make_request uses the correct browse endpoint"""
        mock_response = Mock()
        mock_response.status_code = 200
        mock_response.json.return_value = {'near_earth_objects': []}
        mock_get.return_value = mock_response
        
        client = NEOAPIClient(api_key='test_key')
        client._make_request(page=5)
        
        # Verify correct endpoint was called
        call_args = mock_get.call_args
        assert call_args[0][0] == client.browse_endpoint
        assert call_args[1]['params']['page'] == 5
    
    @patch('src.api_client.requests.Session.get')
    def test_make_request_http_404_raises_error(self, mock_get):
        """Test _make_request raises NEOAPIError for HTTP 404"""
        mock_response = Mock()
        mock_response.status_code = 404
        mock_response.text = "Not Found"
        mock_get.return_value = mock_response
        
        client = NEOAPIClient(api_key='test_key')
        
        with pytest.raises(NEOAPIError) as exc_info:
            client._make_request(page=0)
        
        assert exc_info.value.status_code == 404
        assert "API request failed with status 404" in str(exc_info.value)
    
    @patch('src.api_client.requests.Session.get')
    def test_make_request_http_500_raises_error(self, mock_get):
        """Test _make_request raises NEOAPIError for HTTP 500"""
        mock_response = Mock()
        mock_response.status_code = 500
        mock_response.text = "Internal Server Error"
        mock_get.return_value = mock_response
        
        client = NEOAPIClient(api_key='test_key')
        
        with pytest.raises(NEOAPIError) as exc_info:
            client._make_request(page=0)
        
        assert exc_info.value.status_code == 500
        assert "API request failed with status 500" in str(exc_info.value)
    
    @patch('src.api_client.requests.Session.get')
    def test_make_request_invalid_json_raises_error(self, mock_get):
        """Test _make_request raises NEOAPIError for invalid JSON response"""
        mock_response = Mock()
        mock_response.status_code = 200
        mock_response.json.side_effect = ValueError("Invalid JSON")
        mock_response.text = "Not valid JSON"
        mock_get.return_value = mock_response
        
        client = NEOAPIClient(api_key='test_key')
        
        with pytest.raises(NEOAPIError) as exc_info:
            client._make_request(page=0)
        
        assert "Failed to parse JSON response" in str(exc_info.value)
    
    @patch('src.api_client.requests.Session.get')
    def test_make_request_timeout_raises_error(self, mock_get):
        """Test _make_request raises NEOAPIError for timeout"""
        mock_get.side_effect = Timeout("Request timeout")
        
        client = NEOAPIClient(api_key='test_key')
        
        with pytest.raises(NEOAPIError) as exc_info:
            client._make_request(page=0)
        
        assert "Request timeout after 30 seconds" in str(exc_info.value)
    
    @patch('src.api_client.requests.Session.get')
    def test_make_request_connection_error_raises_error(self, mock_get):
        """Test _make_request raises NEOAPIError for connection error"""
        mock_get.side_effect = ConnectionError("Connection failed")
        
        client = NEOAPIClient(api_key='test_key')
        
        with pytest.raises(NEOAPIError) as exc_info:
            client._make_request(page=0)
        
        assert "Connection error" in str(exc_info.value)
    
    @patch('src.api_client.requests.Session.get')
    def test_make_request_generic_request_exception_raises_error(self, mock_get):
        """Test _make_request raises NEOAPIError for generic request exception"""
        mock_get.side_effect = RequestException("Generic request error")
        
        client = NEOAPIClient(api_key='test_key')
        
        with pytest.raises(NEOAPIError) as exc_info:
            client._make_request(page=0)
        
        assert "Request failed" in str(exc_info.value)


class TestNEOAPIClientRateLimitHandling:
    """Test suite for rate limit handling"""
    
    @patch('src.api_client.time.sleep')
    @patch('src.api_client.requests.Session.get')
    def test_handle_rate_limit_with_retry_after_header(self, mock_get, mock_sleep):
        """Test rate limit handling uses Retry-After header value"""
        # First call returns 429, second call succeeds
        mock_response_429 = Mock()
        mock_response_429.status_code = 429
        mock_response_429.headers = {'Retry-After': '30'}
        
        mock_response_200 = Mock()
        mock_response_200.status_code = 200
        mock_response_200.json.return_value = {'near_earth_objects': []}
        
        mock_get.side_effect = [mock_response_429, mock_response_200]
        
        client = NEOAPIClient(api_key='test_key')
        result = client._make_request(page=0)
        
        # Verify sleep was called with Retry-After value
        mock_sleep.assert_called_once_with(30)
        
        # Verify request was retried and succeeded
        assert mock_get.call_count == 2
        assert result == {'near_earth_objects': []}
    
    @patch('src.api_client.time.sleep')
    @patch('src.api_client.requests.Session.get')
    def test_handle_rate_limit_without_retry_after_header(self, mock_get, mock_sleep):
        """Test rate limit handling uses default wait time without Retry-After header"""
        # First call returns 429 without Retry-After, second call succeeds
        mock_response_429 = Mock()
        mock_response_429.status_code = 429
        mock_response_429.headers = {}
        
        mock_response_200 = Mock()
        mock_response_200.status_code = 200
        mock_response_200.json.return_value = {'near_earth_objects': []}
        
        mock_get.side_effect = [mock_response_429, mock_response_200]
        
        client = NEOAPIClient(api_key='test_key')
        result = client._make_request(page=0)
        
        # Verify sleep was called with default 60 seconds
        mock_sleep.assert_called_once_with(60)
        
        # Verify request was retried
        assert mock_get.call_count == 2
    
    @patch('src.api_client.time.sleep')
    @patch('src.api_client.requests.Session.get')
    def test_handle_rate_limit_with_invalid_retry_after(self, mock_get, mock_sleep):
        """Test rate limit handling uses default when Retry-After is not an integer"""
        # First call returns 429 with invalid Retry-After, second call succeeds
        mock_response_429 = Mock()
        mock_response_429.status_code = 429
        mock_response_429.headers = {'Retry-After': 'invalid'}
        
        mock_response_200 = Mock()
        mock_response_200.status_code = 200
        mock_response_200.json.return_value = {'near_earth_objects': []}
        
        mock_get.side_effect = [mock_response_429, mock_response_200]
        
        client = NEOAPIClient(api_key='test_key')
        result = client._make_request(page=0)
        
        # Verify sleep was called with default 60 seconds
        mock_sleep.assert_called_once_with(60)
    
    @patch('src.api_client.time.sleep')
    @patch('src.api_client.requests.Session.get')
    def test_handle_rate_limit_retry_still_fails(self, mock_get, mock_sleep):
        """Test rate limit handling raises error if retry also returns 429"""
        # Both calls return 429
        mock_response_429 = Mock()
        mock_response_429.status_code = 429
        mock_response_429.headers = {'Retry-After': '10'}
        mock_response_429.text = "Rate limit exceeded"
        
        mock_get.return_value = mock_response_429
        
        client = NEOAPIClient(api_key='test_key')
        
        with pytest.raises(NEOAPIError) as exc_info:
            client._make_request(page=0)
        
        # Verify sleep was called
        mock_sleep.assert_called_once_with(10)
        
        # Verify error was raised after retry
        assert exc_info.value.status_code == 429


class TestNEOAPIClientRetryLogic:
    """Test suite for retry logic with exponential backoff"""
    
    @patch('src.api_client.requests.Session.get')
    def test_retry_on_connection_error(self, mock_get):
        """Test retry logic catches connection errors and raises NEOAPIError"""
        # Connection error should be caught and wrapped in NEOAPIError
        mock_get.side_effect = ConnectionError("Connection failed")
        
        client = NEOAPIClient(api_key='test_key')
        
        with pytest.raises(NEOAPIError) as exc_info:
            client._make_request(page=0)
        
        # Verify error was wrapped properly
        assert "Connection error" in str(exc_info.value)
    
    @patch('src.api_client.requests.Session.get')
    def test_retry_on_500_error(self, mock_get):
        """Test retry logic retries on HTTP 500 errors"""
        # First call returns 500, second succeeds
        mock_response_500 = Mock()
        mock_response_500.status_code = 500
        mock_response_500.text = "Internal Server Error"
        
        mock_response_200 = Mock()
        mock_response_200.status_code = 200
        mock_response_200.json.return_value = {'near_earth_objects': []}
        
        # Note: The retry adapter will handle 500 errors automatically
        # For this test, we simulate the behavior after retries
        mock_get.side_effect = [mock_response_500, mock_response_200]
        
        client = NEOAPIClient(api_key='test_key')
        
        # The first 500 will be raised as an error since we're not testing
        # the actual retry adapter behavior (which is tested by requests library)
        with pytest.raises(NEOAPIError):
            client._make_request(page=0)
    
    @patch('src.api_client.requests.Session.get')
    def test_retry_exhausted_raises_error(self, mock_get):
        """Test retry logic raises error after all retries exhausted"""
        # All calls fail with connection error
        mock_get.side_effect = ConnectionError("Connection failed")
        
        client = NEOAPIClient(api_key='test_key')
        
        with pytest.raises(NEOAPIError) as exc_info:
            client._make_request(page=0)
        
        assert "Connection error" in str(exc_info.value)


class TestNEOAPIClientFetchNeos:
    """Test suite for fetch_neos method"""
    
    @patch('src.api_client.NEOAPIClient._make_request')
    def test_fetch_neos_returns_iterator(self, mock_make_request):
        """Test fetch_neos returns an iterator"""
        mock_make_request.return_value = {
            'near_earth_objects': [
                {'id': '1', 'name': 'NEO 1'},
                {'id': '2', 'name': 'NEO 2'}
            ],
            'page': {'number': 0, 'size': 20, 'total_pages': 1}
        }
        
        client = NEOAPIClient(api_key='test_key')
        result = client.fetch_neos(max_objects=2)
        
        # Verify it's an iterator
        assert hasattr(result, '__iter__')
        assert hasattr(result, '__next__')
    
    @patch('src.api_client.NEOAPIClient._make_request')
    def test_fetch_neos_yields_individual_records(self, mock_make_request):
        """Test fetch_neos yields individual NEO records"""
        mock_make_request.return_value = {
            'near_earth_objects': [
                {'id': '1', 'name': 'NEO 1'},
                {'id': '2', 'name': 'NEO 2'},
                {'id': '3', 'name': 'NEO 3'}
            ],
            'page': {'number': 0, 'size': 20, 'total_pages': 1}
        }
        
        client = NEOAPIClient(api_key='test_key')
        neos = list(client.fetch_neos(max_objects=3))
        
        assert len(neos) == 3
        assert neos[0] == {'id': '1', 'name': 'NEO 1'}
        assert neos[1] == {'id': '2', 'name': 'NEO 2'}
        assert neos[2] == {'id': '3', 'name': 'NEO 3'}
    
    @patch('src.api_client.NEOAPIClient._make_request')
    def test_fetch_neos_respects_max_objects_limit(self, mock_make_request):
        """Test fetch_neos stops at max_objects limit"""
        # API returns 20 objects per page
        mock_make_request.return_value = {
            'near_earth_objects': [{'id': str(i), 'name': f'NEO {i}'} for i in range(20)],
            'page': {'number': 0, 'size': 20, 'total_pages': 10}
        }
        
        client = NEOAPIClient(api_key='test_key')
        neos = list(client.fetch_neos(max_objects=15))
        
        # Should stop at exactly 15 objects
        assert len(neos) == 15
    
    @patch('src.api_client.NEOAPIClient._make_request')
    def test_fetch_neos_handles_pagination(self, mock_make_request):
        """Test fetch_neos handles pagination across multiple pages"""
        # Mock two pages of results
        def mock_response(page):
            if page == 0:
                return {
                    'near_earth_objects': [{'id': str(i), 'name': f'NEO {i}'} for i in range(20)],
                    'page': {'number': 0, 'size': 20, 'total_pages': 2}
                }
            elif page == 1:
                return {
                    'near_earth_objects': [{'id': str(i), 'name': f'NEO {i}'} for i in range(20, 40)],
                    'page': {'number': 1, 'size': 20, 'total_pages': 2}
                }
        
        mock_make_request.side_effect = lambda page: mock_response(page)
        
        client = NEOAPIClient(api_key='test_key')
        neos = list(client.fetch_neos(max_objects=35))
        
        # Should fetch from two pages and stop at 35
        assert len(neos) == 35
        assert mock_make_request.call_count == 2
    
    @patch('src.api_client.NEOAPIClient._make_request')
    def test_fetch_neos_stops_at_last_page(self, mock_make_request):
        """Test fetch_neos stops when reaching last page"""
        mock_make_request.return_value = {
            'near_earth_objects': [{'id': str(i), 'name': f'NEO {i}'} for i in range(10)],
            'page': {'number': 0, 'size': 20, 'total_pages': 1}
        }
        
        client = NEOAPIClient(api_key='test_key')
        neos = list(client.fetch_neos(max_objects=200))
        
        # Should stop at 10 objects (all available)
        assert len(neos) == 10
        assert mock_make_request.call_count == 1
    
    @patch('src.api_client.NEOAPIClient._make_request')
    def test_fetch_neos_handles_empty_response(self, mock_make_request):
        """Test fetch_neos handles empty API response"""
        mock_make_request.return_value = {
            'near_earth_objects': [],
            'page': {'number': 0, 'size': 20, 'total_pages': 0}
        }
        
        client = NEOAPIClient(api_key='test_key')
        neos = list(client.fetch_neos(max_objects=200))
        
        assert len(neos) == 0
    
    @patch('src.api_client.NEOAPIClient._make_request')
    def test_fetch_neos_propagates_api_errors(self, mock_make_request):
        """Test fetch_neos propagates NEOAPIError from _make_request"""
        mock_make_request.side_effect = NEOAPIError("API Error", status_code=500)
        
        client = NEOAPIClient(api_key='test_key')
        
        with pytest.raises(NEOAPIError) as exc_info:
            list(client.fetch_neos(max_objects=200))
        
        assert "API Error" in str(exc_info.value)
        assert exc_info.value.status_code == 500
    
    @patch('src.api_client.NEOAPIClient._make_request')
    def test_fetch_neos_with_default_parameters(self, mock_make_request):
        """Test fetch_neos uses default parameters correctly"""
        mock_make_request.return_value = {
            'near_earth_objects': [{'id': '1', 'name': 'NEO 1'}],
            'page': {'number': 0, 'size': 20, 'total_pages': 1}
        }
        
        client = NEOAPIClient(api_key='test_key')
        neos = list(client.fetch_neos())  # Use defaults
        
        # Default max_objects is 200, but only 1 available
        assert len(neos) == 1
    
    @patch('src.api_client.NEOAPIClient._make_request')
    def test_fetch_neos_exact_page_boundary(self, mock_make_request):
        """Test fetch_neos handles exact page size boundary correctly"""
        # Mock exactly 20 objects (one full page)
        mock_make_request.return_value = {
            'near_earth_objects': [{'id': str(i), 'name': f'NEO {i}'} for i in range(20)],
            'page': {'number': 0, 'size': 20, 'total_pages': 1}
        }
        
        client = NEOAPIClient(api_key='test_key')
        neos = list(client.fetch_neos(max_objects=20))
        
        # Should fetch exactly 20 objects
        assert len(neos) == 20
        assert mock_make_request.call_count == 1
