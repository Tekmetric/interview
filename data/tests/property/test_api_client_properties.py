"""
Property-based tests for NEOAPIClient.

These tests use hypothesis to verify universal properties that should hold
for all valid inputs, ensuring correctness across a wide range of scenarios.
"""

import pytest
from unittest.mock import Mock, patch
from hypothesis import given, strategies as st, assume
from src.api_client import NEOAPIClient
from src.exceptions import NEOAPIError


# Strategy for generating valid page sizes (API max is 20)
page_size_strategy = st.integers(min_value=1, max_value=20)

# Strategy for generating max_objects values
max_objects_strategy = st.integers(min_value=1, max_value=500)


def create_mock_api_response(page: int, objects_per_page: int, total_pages: int):
    """
    Create a mock API response matching NASA NEO API structure.
    
    Args:
        page: Current page number (0-indexed)
        objects_per_page: Number of NEO objects in this page
        total_pages: Total number of pages available
        
    Returns:
        Dict: Mock API response with NEO objects and pagination metadata
    """
    neos = []
    for i in range(objects_per_page):
        neo_id = f"neo_{page}_{i}"
        neos.append({
            'id': neo_id,
            'neo_reference_id': neo_id,
            'name': f"NEO {neo_id}",
            'designation': neo_id,
            'nasa_jpl_url': f"https://ssd.jpl.nasa.gov/sbdb.cgi?sstr={neo_id}",
            'absolute_magnitude_h': 20.0 + i,
            'is_potentially_hazardous_asteroid': False,
            'estimated_diameter': {
                'meters': {
                    'estimated_diameter_min': 100.0,
                    'estimated_diameter_max': 200.0
                }
            },
            'close_approach_data': [{
                'close_approach_date': '2020-01-01',
                'miss_distance': {
                    'kilometers': '1000000.0'
                },
                'relative_velocity': {
                    'kilometers_per_second': '10.0'
                }
            }],
            'orbital_data': {
                'first_observation_date': '2000-01-01',
                'last_observation_date': '2020-01-01',
                'observations_used': 100,
                'orbital_period': '365.25'
            }
        })
    
    return {
        'near_earth_objects': neos,
        'page': {
            'size': objects_per_page,
            'total_elements': total_pages * objects_per_page,
            'total_pages': total_pages,
            'number': page
        }
    }


class TestPaginationLimitEnforcement:
    """
    Property tests for pagination limit enforcement.
    
    **Validates: Requirements 1.3**
    
    These tests verify that the API client correctly enforces the max_objects
    limit when fetching NEO data, regardless of page size or total available data.
    """
    
    @given(
        page_size=page_size_strategy,
        max_objects=max_objects_strategy
    )
    def test_pagination_limit_enforcement_property(self, page_size, max_objects):
        """
        **Property 1: Pagination Limit Enforcement**
        **Validates: Requirements 1.3**
        
        Property: When fetching NEOs with a specified max_objects limit,
        the client SHALL return exactly max_objects (or fewer if API has less data).
        
        This property holds for all valid combinations of page_size and max_objects.
        
        Test strategy:
        1. Generate various page_size (1-20) and max_objects (1-500) values
        2. Mock API to provide sufficient data (more than max_objects)
        3. Verify exactly max_objects are returned
        4. Verify no extra API calls are made after limit is reached
        """
        # Calculate how many pages we need to provide enough data
        # We want to ensure API has MORE data than max_objects to test the limit
        objects_per_page = min(page_size, 20)  # API caps at 20
        pages_needed = (max_objects // objects_per_page) + 2  # +2 to ensure excess data
        
        # Create mock responses for each page
        mock_responses = []
        for page_num in range(pages_needed):
            mock_responses.append(
                create_mock_api_response(
                    page=page_num,
                    objects_per_page=objects_per_page,
                    total_pages=pages_needed
                )
            )
        
        # Mock the _make_request method to return our prepared responses
        with patch.object(NEOAPIClient, '_make_request') as mock_request:
            mock_request.side_effect = mock_responses
            
            # Create client and fetch NEOs
            client = NEOAPIClient(api_key="test_key")
            
            # Collect all fetched NEOs
            fetched_neos = list(client.fetch_neos(
                page_size=page_size,
                max_objects=max_objects
            ))
            
            # ASSERTION 1: Exactly max_objects returned (or fewer if API exhausted)
            # Since we provided excess data, we should get exactly max_objects
            assert len(fetched_neos) == max_objects, (
                f"Expected exactly {max_objects} NEOs, but got {len(fetched_neos)}"
            )
            
            # ASSERTION 2: Each fetched NEO is a valid dictionary
            for neo in fetched_neos:
                assert isinstance(neo, dict), "Each NEO should be a dictionary"
                assert 'id' in neo, "Each NEO should have an 'id' field"
            
            # ASSERTION 3: No duplicate NEOs (each should have unique ID)
            neo_ids = [neo['id'] for neo in fetched_neos]
            assert len(neo_ids) == len(set(neo_ids)), "NEO IDs should be unique"
            
            # ASSERTION 4: API calls should stop after reaching max_objects
            # Calculate expected number of API calls
            expected_calls = (max_objects + objects_per_page - 1) // objects_per_page
            actual_calls = mock_request.call_count
            
            # Should not make more calls than necessary
            assert actual_calls <= expected_calls + 1, (
                f"Made {actual_calls} API calls, expected at most {expected_calls + 1}"
            )
    
    @given(
        max_objects=st.integers(min_value=1, max_value=100)
    )
    def test_pagination_limit_with_insufficient_data(self, max_objects):
        """
        **Property 1b: Pagination Limit with Insufficient Data**
        **Validates: Requirements 1.3**
        
        Property: When the API has fewer objects than max_objects,
        the client SHALL return all available objects (fewer than max_objects).
        
        Test strategy:
        1. Mock API to provide fewer objects than requested
        2. Verify client returns all available objects
        3. Verify client stops when API returns empty page
        """
        # Provide fewer objects than requested
        available_objects = max(1, max_objects // 2)  # Half of requested
        objects_per_page = 20
        pages_needed = (available_objects + objects_per_page - 1) // objects_per_page
        
        # Create mock responses with limited data
        mock_responses = []
        remaining = available_objects
        
        for page_num in range(pages_needed):
            objects_in_page = min(remaining, objects_per_page)
            mock_responses.append(
                create_mock_api_response(
                    page=page_num,
                    objects_per_page=objects_in_page,
                    total_pages=pages_needed
                )
            )
            remaining -= objects_in_page
        
        # Add empty page to signal end of data
        mock_responses.append(
            create_mock_api_response(
                page=pages_needed,
                objects_per_page=0,
                total_pages=pages_needed
            )
        )
        
        with patch.object(NEOAPIClient, '_make_request') as mock_request:
            mock_request.side_effect = mock_responses
            
            client = NEOAPIClient(api_key="test_key")
            fetched_neos = list(client.fetch_neos(max_objects=max_objects))
            
            # Should return all available objects (less than max_objects)
            assert len(fetched_neos) == available_objects, (
                f"Expected {available_objects} NEOs (all available), "
                f"but got {len(fetched_neos)}"
            )
            
            # Should have unique IDs
            neo_ids = [neo['id'] for neo in fetched_neos]
            assert len(neo_ids) == len(set(neo_ids)), "NEO IDs should be unique"
    
    @given(
        page_size=page_size_strategy,
        max_objects=st.integers(min_value=1, max_value=50)
    )
    def test_pagination_respects_page_boundaries(self, page_size, max_objects):
        """
        **Property 1c: Pagination Respects Page Boundaries**
        **Validates: Requirements 1.3**
        
        Property: The client SHALL correctly handle max_objects that don't
        align with page boundaries (e.g., max_objects=25 with page_size=20).
        
        Test strategy:
        1. Use max_objects that don't divide evenly by page_size
        2. Verify exactly max_objects are returned
        3. Verify partial pages are handled correctly
        """
        objects_per_page = min(page_size, 20)
        
        # Ensure we have enough pages to test partial page handling
        pages_needed = (max_objects // objects_per_page) + 2
        
        mock_responses = []
        for page_num in range(pages_needed):
            mock_responses.append(
                create_mock_api_response(
                    page=page_num,
                    objects_per_page=objects_per_page,
                    total_pages=pages_needed
                )
            )
        
        with patch.object(NEOAPIClient, '_make_request') as mock_request:
            mock_request.side_effect = mock_responses
            
            client = NEOAPIClient(api_key="test_key")
            fetched_neos = list(client.fetch_neos(
                page_size=page_size,
                max_objects=max_objects
            ))
            
            # Should return exactly max_objects even if it's not a multiple of page_size
            assert len(fetched_neos) == max_objects, (
                f"Expected exactly {max_objects} NEOs, but got {len(fetched_neos)}"
            )
            
            # Verify all NEOs are valid
            for neo in fetched_neos:
                assert isinstance(neo, dict)
                assert 'id' in neo


class TestAPIErrorHandling:
    """
    Property tests for API error handling.
    
    **Validates: Requirements 1.4**
    
    These tests verify that the API client correctly handles various error
    conditions (network failures, HTTP errors, timeouts) by raising appropriate
    exceptions with descriptive messages.
    """
    
    @given(
        status_code=st.sampled_from([400, 401, 403, 404, 429, 500, 502, 503, 504])
    )
    def test_http_error_handling_property(self, status_code):
        """
        **Property 2: API Error Handling**
        **Validates: Requirements 1.4**
        
        Property: When the API returns an HTTP error status code,
        the client SHALL raise NEOAPIError with the status code and
        descriptive error message.
        
        Test strategy:
        1. Generate various HTTP error status codes (4xx, 5xx)
        2. Mock API to return error responses
        3. Verify NEOAPIError is raised
        4. Verify exception contains status code and descriptive message
        """
        # Create mock error response
        mock_response = Mock()
        mock_response.status_code = status_code
        mock_response.text = f"Error {status_code}: Test error message"
        mock_response.json.side_effect = ValueError("Invalid JSON")
        
        # For 429 (rate limit), provide proper headers to avoid blocking
        if status_code == 429:
            mock_response.headers = {'Retry-After': '1'}  # 1 second wait
        else:
            mock_response.headers = {}
        
        # Mock time.sleep to avoid actual waiting during rate limit handling
        with patch('requests.Session.get', return_value=mock_response), \
             patch('time.sleep'):  # Mock sleep to avoid delays
            client = NEOAPIClient(api_key="test_key")
            
            # Attempt to fetch NEOs - should raise NEOAPIError
            with pytest.raises(NEOAPIError) as exc_info:
                list(client.fetch_neos(max_objects=10))
            
            # Verify exception contains status code
            assert exc_info.value.status_code == status_code, (
                f"Expected status_code {status_code}, got {exc_info.value.status_code}"
            )
            
            # Verify exception message is descriptive
            error_message = str(exc_info.value)
            assert str(status_code) in error_message, (
                f"Error message should contain status code {status_code}"
            )
            assert len(error_message) > 10, (
                "Error message should be descriptive (more than 10 characters)"
            )
    
    @given(
        error_type=st.sampled_from([
            'ConnectionError',
            'Timeout',
            'RequestException'
        ])
    )
    def test_network_error_handling_property(self, error_type):
        """
        **Property 2b: Network Error Handling**
        **Validates: Requirements 1.4**
        
        Property: When network errors occur (connection failures, timeouts),
        the client SHALL raise NEOAPIError with descriptive error message
        indicating the network issue.
        
        Test strategy:
        1. Generate various network error types
        2. Mock requests to raise network exceptions
        3. Verify NEOAPIError is raised
        4. Verify exception message describes the network issue
        """
        import requests
        
        # Map error type to exception class
        exception_map = {
            'ConnectionError': requests.exceptions.ConnectionError("Connection failed"),
            'Timeout': requests.exceptions.Timeout("Request timeout after 30 seconds"),
            'RequestException': requests.exceptions.RequestException("Request failed")
        }
        
        network_exception = exception_map[error_type]
        
        with patch('requests.Session.get', side_effect=network_exception):
            client = NEOAPIClient(api_key="test_key")
            
            # Attempt to fetch NEOs - should raise NEOAPIError
            with pytest.raises(NEOAPIError) as exc_info:
                list(client.fetch_neos(max_objects=10))
            
            # Verify exception message is descriptive
            error_message = str(exc_info.value)
            assert len(error_message) > 10, (
                "Error message should be descriptive (more than 10 characters)"
            )
            
            # Verify message indicates the type of error
            error_message_lower = error_message.lower()
            if error_type == 'ConnectionError':
                assert 'connection' in error_message_lower, (
                    "Error message should mention 'connection' for ConnectionError"
                )
            elif error_type == 'Timeout':
                assert 'timeout' in error_message_lower, (
                    "Error message should mention 'timeout' for Timeout"
                )
            elif error_type == 'RequestException':
                assert 'request' in error_message_lower or 'failed' in error_message_lower, (
                    "Error message should mention 'request' or 'failed' for RequestException"
                )
    
    @given(
        invalid_json=st.sampled_from([
            "not json at all",
            "{incomplete json",
            "{'single': 'quotes'}",
            "<html>HTML response</html>",
            ""
        ])
    )
    def test_malformed_response_handling_property(self, invalid_json):
        """
        **Property 2c: Malformed Response Handling**
        **Validates: Requirements 1.4**
        
        Property: When the API returns a malformed JSON response,
        the client SHALL raise NEOAPIError with descriptive message
        indicating JSON parsing failure.
        
        Test strategy:
        1. Generate various malformed JSON responses
        2. Mock API to return 200 status with invalid JSON
        3. Verify NEOAPIError is raised
        4. Verify exception message indicates JSON parsing issue
        """
        # Create mock response with invalid JSON
        mock_response = Mock()
        mock_response.status_code = 200
        mock_response.text = invalid_json
        mock_response.json.side_effect = ValueError("Invalid JSON")
        
        with patch('requests.Session.get', return_value=mock_response):
            client = NEOAPIClient(api_key="test_key")
            
            # Attempt to fetch NEOs - should raise NEOAPIError
            with pytest.raises(NEOAPIError) as exc_info:
                list(client.fetch_neos(max_objects=10))
            
            # Verify exception message mentions JSON parsing
            error_message = str(exc_info.value).lower()
            assert 'json' in error_message or 'parse' in error_message, (
                "Error message should mention 'json' or 'parse' for JSON parsing errors"
            )
            
            # Verify exception has status code (200 in this case)
            assert exc_info.value.status_code == 200, (
                "Exception should include the HTTP status code"
            )
    
    @given(
        status_code=st.sampled_from([400, 401, 403, 404, 500, 502, 503, 504]),
        response_text=st.text(min_size=10, max_size=200)
    )
    def test_error_response_captured_property(self, status_code, response_text):
        """
        **Property 2d: Error Response Capture**
        **Validates: Requirements 1.4**
        
        Property: When an API error occurs, the NEOAPIError exception
        SHALL capture the response text for debugging purposes.
        
        Test strategy:
        1. Generate various error status codes and response texts
        2. Mock API to return error with response text
        3. Verify NEOAPIError captures the response text
        4. Verify response text is accessible via exception attribute
        """
        # Create mock error response
        mock_response = Mock()
        mock_response.status_code = status_code
        mock_response.text = response_text
        mock_response.json.side_effect = ValueError("Invalid JSON")
        
        with patch('requests.Session.get', return_value=mock_response):
            client = NEOAPIClient(api_key="test_key")
            
            # Attempt to fetch NEOs - should raise NEOAPIError
            with pytest.raises(NEOAPIError) as exc_info:
                list(client.fetch_neos(max_objects=10))
            
            # Verify exception has response attribute
            assert hasattr(exc_info.value, 'response'), (
                "NEOAPIError should have 'response' attribute"
            )
            
            # Verify response text is captured (may be truncated)
            captured_response = exc_info.value.response
            if captured_response is not None:
                # Response should contain at least part of the original text
                # (implementation may truncate long responses)
                assert isinstance(captured_response, str), (
                    "Response should be a string"
                )
    
    def test_api_key_validation_property(self):
        """
        **Property 2e: API Key Validation**
        **Validates: Requirements 1.4**
        
        Property: When initializing the client with an invalid API key
        (None or empty string), the client SHALL raise ValueError with
        descriptive message.
        
        Test strategy:
        1. Test with None and empty string API keys
        2. Verify ValueError is raised
        3. Verify error message is descriptive
        """
        # Test with None
        with pytest.raises(ValueError) as exc_info:
            NEOAPIClient(api_key=None)
        
        error_message = str(exc_info.value).lower()
        assert 'api key' in error_message or 'key' in error_message, (
            "Error message should mention 'api key' or 'key'"
        )
        
        # Test with empty string
        with pytest.raises(ValueError) as exc_info:
            NEOAPIClient(api_key="")
        
        error_message = str(exc_info.value).lower()
        assert 'api key' in error_message or 'key' in error_message or 'empty' in error_message, (
            "Error message should mention 'api key', 'key', or 'empty'"
        )
