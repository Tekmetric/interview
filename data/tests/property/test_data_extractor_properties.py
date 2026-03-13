"""
Property-based tests for NEODataExtractor.

These tests use hypothesis to verify universal properties that should hold
for all valid inputs, ensuring correctness across a wide range of scenarios.
"""

import pytest
from datetime import date
from hypothesis import given, strategies as st
from src.data_extractor import NEODataExtractor
from src.exceptions import DataExtractionError


# Hypothesis strategies for generating valid NEO API response data

# Basic field strategies
neo_id_strategy = st.text(min_size=1, max_size=20, alphabet=st.characters(min_codepoint=48, max_codepoint=122))
neo_name_strategy = st.text(min_size=1, max_size=100)
magnitude_strategy = st.floats(min_value=0, max_value=35, allow_nan=False, allow_infinity=False)
url_strategy = st.text(min_size=10, max_size=200).map(lambda x: f"https://ssd.jpl.nasa.gov/sbdb.cgi?sstr={x}")
boolean_strategy = st.booleans()

# Diameter strategies
diameter_strategy = st.floats(min_value=0.001, max_value=100000, allow_nan=False, allow_infinity=False)

# Distance and velocity strategies
distance_km_strategy = st.floats(min_value=0, max_value=1e9, allow_nan=False, allow_infinity=False)
velocity_kms_strategy = st.floats(min_value=0, max_value=100, allow_nan=False, allow_infinity=False)

# Date strategies (ISO format YYYY-MM-DD)
date_strategy = st.dates(min_value=date(1900, 1, 1), max_value=date(2100, 12, 31)).map(
    lambda d: d.isoformat()
)

# Observation count strategy
observations_strategy = st.integers(min_value=1, max_value=10000)

# Orbital period strategy (in days)
orbital_period_strategy = st.floats(min_value=0.1, max_value=100000, allow_nan=False, allow_infinity=False)


def generate_diameter_data(min_diameter, max_diameter):
    """Generate diameter data structure."""
    return {
        'meters': {
            'estimated_diameter_min': min_diameter,
            'estimated_diameter_max': max_diameter
        }
    }


def generate_close_approach_data(approach_date, miss_distance_km, velocity_kms):
    """Generate close approach data structure."""
    return {
        'close_approach_date': approach_date,
        'miss_distance': {
            'kilometers': str(miss_distance_km)
        },
        'relative_velocity': {
            'kilometers_per_second': str(velocity_kms)
        }
    }


def generate_orbital_data(first_obs, last_obs, obs_used, orbital_period):
    """Generate orbital data structure."""
    return {
        'first_observation_date': first_obs,
        'last_observation_date': last_obs,
        'observations_used': obs_used,
        'orbital_period': orbital_period
    }


# Composite strategy for generating complete NEO API responses
@st.composite
def neo_api_response_strategy(draw):
    """
    Generate a complete, valid NEO API response.
    
    This strategy generates all required fields with valid data types
    and structures matching the NASA NEO API format.
    """
    neo_id = draw(neo_id_strategy)
    
    # Generate diameter data (ensure max >= min)
    diameter_min = draw(diameter_strategy)
    diameter_max = draw(st.floats(min_value=diameter_min, max_value=diameter_min * 10, allow_nan=False, allow_infinity=False))
    
    # Generate close approach data (1-5 approaches)
    num_approaches = draw(st.integers(min_value=1, max_value=5))
    close_approaches = []
    for _ in range(num_approaches):
        approach_date = draw(date_strategy)
        miss_distance = draw(distance_km_strategy)
        velocity = draw(velocity_kms_strategy)
        close_approaches.append(generate_close_approach_data(approach_date, miss_distance, velocity))
    
    # Generate orbital data
    first_obs = draw(date_strategy)
    last_obs = draw(date_strategy)
    obs_used = draw(observations_strategy)
    orbital_period = draw(orbital_period_strategy)
    
    return {
        'id': neo_id,
        'neo_reference_id': neo_id,
        'name': draw(neo_name_strategy),
        'name_limited': draw(st.one_of(neo_name_strategy, st.none())),
        'designation': draw(neo_name_strategy),
        'nasa_jpl_url': draw(url_strategy),
        'absolute_magnitude_h': draw(magnitude_strategy),
        'is_potentially_hazardous_asteroid': draw(boolean_strategy),
        'estimated_diameter': generate_diameter_data(diameter_min, diameter_max),
        'close_approach_data': close_approaches,
        'orbital_data': generate_orbital_data(first_obs, last_obs, obs_used, orbital_period)
    }


class TestCompleteFieldExtraction:
    """
    Property tests for complete field extraction.
    
    **Validates: Requirements 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8**
    
    These tests verify that the data extractor successfully extracts all
    required fields from valid NEO API responses.
    """
    
    @given(neo_response=neo_api_response_strategy())
    def test_complete_field_extraction_property(self, neo_response):
        """
        **Property 3: Complete Field Extraction**
        **Validates: Requirements 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8**
        
        Property: For any valid NEO API response, the data extractor SHALL
        successfully extract all required fields and return a complete record
        with all fields present.
        
        Required fields:
        - Basic: id, neo_reference_id, name, name_limited, designation, 
                 nasa_jpl_url, absolute_magnitude_h, is_potentially_hazardous
        - Diameter: diameter_min_meters, diameter_max_meters
        - Closest approach: closest_approach_date, closest_miss_distance_km,
                           closest_relative_velocity_kms
        - Orbital: first_observation_date, last_observation_date,
                   observations_used, orbital_period_days
        
        Test strategy:
        1. Generate valid NEO API responses with all required fields
        2. Extract data using NEODataExtractor
        3. Verify all required fields are present in output
        4. Verify field values match input data
        5. Verify data types are correct
        """
        extractor = NEODataExtractor()
        
        # Extract data from generated response
        extracted = extractor.extract_neo_data(neo_response)
        
        # ASSERTION 1: All required fields are present
        required_fields = [
            # Basic fields (Requirement 2.1, 2.2, 2.3, 2.4)
            'id',
            'neo_reference_id',
            'name',
            'name_limited',
            'designation',
            'nasa_jpl_url',
            'absolute_magnitude_h',
            'is_potentially_hazardous',
            # Diameter fields (Requirement 2.5)
            'diameter_min_meters',
            'diameter_max_meters',
            # Closest approach fields (Requirement 2.6, 2.7)
            'closest_approach_date',
            'closest_miss_distance_km',
            'closest_relative_velocity_kms',
            # Orbital fields (Requirement 2.8)
            'first_observation_date',
            'last_observation_date',
            'observations_used',
            'orbital_period_days'
        ]
        
        for field in required_fields:
            assert field in extracted, (
                f"Required field '{field}' is missing from extracted data"
            )
        
        # ASSERTION 2: Basic fields match input (Requirement 2.1, 2.2, 2.3, 2.4)
        assert extracted['id'] == neo_response['id']
        assert extracted['neo_reference_id'] == neo_response['neo_reference_id']
        assert extracted['name'] == neo_response['name']
        assert extracted['name_limited'] == neo_response.get('name_limited')
        assert extracted['designation'] == neo_response['designation']
        assert extracted['nasa_jpl_url'] == neo_response['nasa_jpl_url']
        assert extracted['absolute_magnitude_h'] == neo_response['absolute_magnitude_h']
        assert extracted['is_potentially_hazardous'] == neo_response['is_potentially_hazardous_asteroid']
        
        # ASSERTION 3: Diameter fields match input (Requirement 2.5)
        expected_diameter_min = neo_response['estimated_diameter']['meters']['estimated_diameter_min']
        expected_diameter_max = neo_response['estimated_diameter']['meters']['estimated_diameter_max']
        
        assert extracted['diameter_min_meters'] == expected_diameter_min
        assert extracted['diameter_max_meters'] == expected_diameter_max
        
        # ASSERTION 4: Closest approach is correctly identified (Requirement 2.6, 2.7)
        # Find the approach with minimum miss distance
        close_approaches = neo_response['close_approach_data']
        closest = min(close_approaches, key=lambda x: float(x['miss_distance']['kilometers']))
        
        assert extracted['closest_approach_date'] == closest['close_approach_date']
        assert extracted['closest_miss_distance_km'] == float(closest['miss_distance']['kilometers'])
        assert extracted['closest_relative_velocity_kms'] == float(closest['relative_velocity']['kilometers_per_second'])
        
        # ASSERTION 5: Orbital fields match input (Requirement 2.8)
        orbital_data = neo_response['orbital_data']
        assert extracted['first_observation_date'] == orbital_data['first_observation_date']
        assert extracted['last_observation_date'] == orbital_data['last_observation_date']
        assert extracted['observations_used'] == orbital_data['observations_used']
        assert extracted['orbital_period_days'] == orbital_data['orbital_period']
        
        # ASSERTION 6: Data types are correct
        assert isinstance(extracted['id'], str)
        assert isinstance(extracted['neo_reference_id'], str)
        assert isinstance(extracted['name'], str)
        assert isinstance(extracted['designation'], str)
        assert isinstance(extracted['nasa_jpl_url'], str)
        assert isinstance(extracted['absolute_magnitude_h'], (int, float))
        assert isinstance(extracted['is_potentially_hazardous'], bool)
        assert isinstance(extracted['diameter_min_meters'], (int, float))
        assert isinstance(extracted['diameter_max_meters'], (int, float))
        assert isinstance(extracted['closest_approach_date'], str)
        assert isinstance(extracted['closest_miss_distance_km'], (int, float))
        assert isinstance(extracted['closest_relative_velocity_kms'], (int, float))
        assert isinstance(extracted['first_observation_date'], str)
        assert isinstance(extracted['last_observation_date'], str)
        assert isinstance(extracted['observations_used'], int)
        assert isinstance(extracted['orbital_period_days'], (int, float))
    
    @given(neo_response=neo_api_response_strategy())
    def test_closest_approach_selection_property(self, neo_response):
        """
        **Property 3b: Closest Approach Selection**
        **Validates: Requirements 2.6, 2.7**
        
        Property: When multiple close approach records exist, the extractor
        SHALL select the one with the minimum miss distance.
        
        Test strategy:
        1. Generate NEO responses with multiple close approaches
        2. Extract data
        3. Verify the selected approach has the minimum miss distance
        4. Verify no other approach has a smaller miss distance
        """
        extractor = NEODataExtractor()
        extracted = extractor.extract_neo_data(neo_response)
        
        # Get all miss distances from the response
        close_approaches = neo_response['close_approach_data']
        all_distances = [float(approach['miss_distance']['kilometers']) for approach in close_approaches]
        
        # The extracted distance should be the minimum
        min_distance = min(all_distances)
        
        assert extracted['closest_miss_distance_km'] == min_distance, (
            f"Extracted distance {extracted['closest_miss_distance_km']} "
            f"is not the minimum distance {min_distance}"
        )
        
        # Verify no other approach has a smaller distance
        for distance in all_distances:
            assert extracted['closest_miss_distance_km'] <= distance, (
                f"Found approach with smaller distance {distance} than "
                f"extracted distance {extracted['closest_miss_distance_km']}"
            )
    
    @given(neo_response=neo_api_response_strategy())
    def test_diameter_consistency_property(self, neo_response):
        """
        **Property 3c: Diameter Consistency**
        **Validates: Requirements 2.5**
        
        Property: The extracted maximum diameter SHALL always be greater than
        or equal to the minimum diameter.
        
        Test strategy:
        1. Generate NEO responses with diameter data
        2. Extract data
        3. Verify diameter_max_meters >= diameter_min_meters
        """
        extractor = NEODataExtractor()
        extracted = extractor.extract_neo_data(neo_response)
        
        diameter_min = extracted['diameter_min_meters']
        diameter_max = extracted['diameter_max_meters']
        
        assert diameter_max >= diameter_min, (
            f"Maximum diameter {diameter_max} is less than "
            f"minimum diameter {diameter_min}"
        )
    
    @given(neo_response=neo_api_response_strategy())
    def test_no_data_loss_property(self, neo_response):
        """
        **Property 3d: No Data Loss**
        **Validates: Requirements 2.1-2.8**
        
        Property: The extraction process SHALL not lose any data from
        required fields. All non-null input values SHALL be preserved
        in the output.
        
        Test strategy:
        1. Generate NEO responses with all fields populated
        2. Extract data
        3. Verify no required field is None when input had a value
        4. Verify extracted values match input values
        """
        extractor = NEODataExtractor()
        extracted = extractor.extract_neo_data(neo_response)
        
        # Check that required fields from input are not None in output
        assert extracted['id'] is not None
        assert extracted['neo_reference_id'] is not None
        assert extracted['name'] is not None
        assert extracted['designation'] is not None
        assert extracted['nasa_jpl_url'] is not None
        assert extracted['absolute_magnitude_h'] is not None
        assert extracted['is_potentially_hazardous'] is not None
        
        # Diameter fields should not be None if present in input
        if 'estimated_diameter' in neo_response and 'meters' in neo_response['estimated_diameter']:
            assert extracted['diameter_min_meters'] is not None
            assert extracted['diameter_max_meters'] is not None
        
        # Close approach fields should not be None if present in input
        if neo_response.get('close_approach_data'):
            assert extracted['closest_approach_date'] is not None
            assert extracted['closest_miss_distance_km'] is not None
            assert extracted['closest_relative_velocity_kms'] is not None
        
        # Orbital fields should not be None if present in input
        if neo_response.get('orbital_data'):
            assert extracted['first_observation_date'] is not None
            assert extracted['last_observation_date'] is not None
            assert extracted['observations_used'] is not None
            assert extracted['orbital_period_days'] is not None
