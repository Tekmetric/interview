"""Data transformation module for NASA NEO Data Pipeline"""

from typing import Dict, Tuple
from datetime import datetime
from .exceptions import ValidationError


class NEODataTransformer:
    """Validates and transforms extracted NEO data before storage"""
    
    def transform(self, extracted_data: Dict, ingestion_date: Tuple[int, int, int]) -> Dict:
        """Main transformation pipeline: validate, normalize, and add partition fields
        
        Args:
            extracted_data: Raw extracted fields from API
            ingestion_date: (year, month, day) for raw layer partitioning
            
        Returns:
            Validated data with derived partition fields added
        """
        data = extracted_data.copy()
        
        self._validate_numeric_fields(data)
        self._validate_date_fields(data)
        data = self._normalize_units(data)
        data = self._add_derived_fields(data)
        data = self._add_ingestion_fields(data, ingestion_date)
        
        return data
    
    def _validate_numeric_fields(self, data: Dict) -> None:
        """Ensure numeric fields are correct type and within valid ranges (e.g., distances must be positive)"""
        numeric_fields = {
            'absolute_magnitude_h': {'type': (int, float), 'min': None, 'max': None},
            'diameter_min_meters': {'type': (int, float), 'min': 0, 'max': None},
            'diameter_max_meters': {'type': (int, float), 'min': 0, 'max': None},
            'closest_miss_distance_km': {'type': (int, float), 'min': 0, 'max': None},
            'closest_relative_velocity_kms': {'type': (int, float), 'min': 0, 'max': None},
            'observations_used': {'type': int, 'min': 0, 'max': None},
            'orbital_period_days': {'type': (int, float), 'min': 0, 'max': None}
        }
        
        for field, constraints in numeric_fields.items():
            value = data.get(field)
            
            if value is None:
                continue
            
            if not isinstance(value, constraints['type']):
                raise ValidationError(
                    f"Field '{field}' must be numeric, got {type(value).__name__}",
                    field=field,
                    value=value
                )
            
            if constraints['min'] is not None and value < constraints['min']:
                raise ValidationError(
                    f"Field '{field}' must be >= {constraints['min']}, got {value}",
                    field=field,
                    value=value
                )
            
            if constraints['max'] is not None and value > constraints['max']:
                raise ValidationError(
                    f"Field '{field}' must be <= {constraints['max']}, got {value}",
                    field=field,
                    value=value
                )
        
        # Validate diameter_max >= diameter_min
        if (data.get('diameter_min_meters') is not None and 
            data.get('diameter_max_meters') is not None):
            if data['diameter_max_meters'] < data['diameter_min_meters']:
                raise ValidationError(
                    f"diameter_max_meters ({data['diameter_max_meters']}) must be >= "
                    f"diameter_min_meters ({data['diameter_min_meters']})",
                    field='diameter_max_meters',
                    value=data['diameter_max_meters']
                )
    
    def _validate_date_fields(self, data: Dict) -> None:
        """Ensure date strings are in ISO 8601 format (YYYY-MM-DD) and represent valid dates"""
        date_fields = [
            'closest_approach_date',
            'first_observation_date',
            'last_observation_date'
        ]
        
        for field in date_fields:
            value = data.get(field)
            
            if value is None:
                continue
            
            if not isinstance(value, str):
                raise ValidationError(
                    f"Date field '{field}' must be a string, got {type(value).__name__}",
                    field=field,
                    value=value
                )
            
            try:
                datetime.strptime(value, '%Y-%m-%d')
            except ValueError as e:
                raise ValidationError(
                    f"Date field '{field}' must be in ISO 8601 format (YYYY-MM-DD), "
                    f"got '{value}': {str(e)}",
                    field=field,
                    value=value
                )
    
    def _normalize_units(self, data: Dict) -> Dict:
        """Ensure type consistency (e.g., convert string booleans to actual booleans)"""
        normalized = data.copy()
        
        # Ensure boolean fields are properly typed
        if 'is_potentially_hazardous' in normalized:
            value = normalized['is_potentially_hazardous']
            if value is not None and not isinstance(value, bool):
                if isinstance(value, str):
                    normalized['is_potentially_hazardous'] = value.lower() in ('true', '1', 'yes')
                else:
                    normalized['is_potentially_hazardous'] = bool(value)
        
        return normalized
    
    def _add_derived_fields(self, data: Dict) -> Dict:
        """Extract year/month/day from closest_approach_date for curated layer partitioning"""
        derived = data.copy()
        
        approach_date = data.get('closest_approach_date')
        
        if approach_date is not None:
            try:
                date_obj = datetime.strptime(approach_date, '%Y-%m-%d')
                derived['approach_year'] = date_obj.year
                derived['approach_month'] = date_obj.month
                derived['approach_day'] = date_obj.day
            except (ValueError, AttributeError):
                derived['approach_year'] = None
                derived['approach_month'] = None
                derived['approach_day'] = None
        else:
            derived['approach_year'] = None
            derived['approach_month'] = None
            derived['approach_day'] = None
        
        return derived
    
    def _add_ingestion_fields(self, data: Dict, ingestion_date: Tuple[int, int, int]) -> Dict:
        """Add ingestion timestamp fields for raw layer partitioning"""
        with_ingestion = data.copy()
        
        with_ingestion['ingestion_year'] = ingestion_date[0]
        with_ingestion['ingestion_month'] = ingestion_date[1]
        with_ingestion['ingestion_day'] = ingestion_date[2]
        
        return with_ingestion
