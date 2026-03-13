"""Data extraction module for NASA NEO Data Pipeline"""

from typing import Dict, Optional, List
from .exceptions import DataExtractionError


class NEODataExtractor:
    """Extracts structured data from NASA NEO API JSON responses"""
    
    def extract_neo_data(self, neo_json: Dict) -> Dict:
        """Extract all relevant fields from a NEO API response
        
        Args:
            neo_json: Raw JSON response for a single NEO
            
        Returns:
            Dictionary with all extracted fields
        """
        extracted = {}
        extracted.update(self._extract_basic_fields(neo_json))
        extracted.update(self._extract_diameter_data(neo_json))
        extracted.update(self._extract_closest_approach(neo_json))
        extracted.update(self._extract_orbital_data(neo_json))
        return extracted
    
    def _extract_basic_fields(self, neo_json: Dict) -> Dict:
        try:
            return {
                'id': neo_json['id'],
                'neo_reference_id': neo_json['neo_reference_id'],
                'name': neo_json['name'],
                'name_limited': neo_json.get('name_limited'),
                'designation': neo_json['designation'],
                'nasa_jpl_url': neo_json['nasa_jpl_url'],
                'absolute_magnitude_h': neo_json['absolute_magnitude_h'],
                'is_potentially_hazardous': neo_json['is_potentially_hazardous_asteroid']
            }
        except KeyError as e:
            raise DataExtractionError(
                f"Missing required field in basic data: {e}",
                field=str(e),
                raw_data=neo_json
            )
    
    def _extract_diameter_data(self, neo_json: Dict) -> Dict:
        try:
            diameter_data = neo_json.get('estimated_diameter', {}).get('meters', {})
            return {
                'diameter_min_meters': diameter_data.get('estimated_diameter_min'),
                'diameter_max_meters': diameter_data.get('estimated_diameter_max')
            }
        except (KeyError, AttributeError):
            return {
                'diameter_min_meters': None,
                'diameter_max_meters': None
            }
    
    def _extract_closest_approach(self, neo_json: Dict) -> Dict:
        """Find the approach with minimum miss distance across all close approach records"""
        close_approach_data = neo_json.get('close_approach_data', [])
        
        if not close_approach_data:
            return {
                'closest_approach_date': None,
                'closest_miss_distance_km': None,
                'closest_relative_velocity_kms': None
            }
        
        closest_approach = min(
            close_approach_data,
            key=lambda x: float(x.get('miss_distance', {}).get('kilometers', float('inf')))
        )
        
        try:
            return {
                'closest_approach_date': closest_approach['close_approach_date'],
                'closest_miss_distance_km': float(
                    closest_approach['miss_distance']['kilometers']
                ),
                'closest_relative_velocity_kms': float(
                    closest_approach['relative_velocity']['kilometers_per_second']
                )
            }
        except (KeyError, ValueError, TypeError) as e:
            return {
                'closest_approach_date': None,
                'closest_miss_distance_km': None,
                'closest_relative_velocity_kms': None
            }
    
    def _extract_orbital_data(self, neo_json: Dict) -> Dict:
        orbital_data = neo_json.get('orbital_data', {})
        
        if not orbital_data:
            return {
                'first_observation_date': None,
                'last_observation_date': None,
                'observations_used': None,
                'orbital_period_days': None
            }
        
        try:
            orbital_period = orbital_data.get('orbital_period')
            # API sometimes returns orbital_period as string, convert to float
            if orbital_period is not None and isinstance(orbital_period, str):
                orbital_period = float(orbital_period)
            
            return {
                'first_observation_date': orbital_data.get('first_observation_date'),
                'last_observation_date': orbital_data.get('last_observation_date'),
                'observations_used': orbital_data.get('observations_used'),
                'orbital_period_days': orbital_period
            }
        except (KeyError, AttributeError, ValueError):
            return {
                'first_observation_date': None,
                'last_observation_date': None,
                'observations_used': None,
                'orbital_period_days': None
            }
