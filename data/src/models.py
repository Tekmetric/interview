"""Data models for NASA NEO data pipeline"""

from dataclasses import dataclass, asdict
from typing import Dict


@dataclass
class NEORecord:
    """Near Earth Object record with all extracted and derived fields
    
    Contains identification, physical properties, approach data, orbital data,
    and partition fields for data lake organization.
    """
    
    # Identification
    id: str
    neo_reference_id: str
    name: str
    name_limited: str
    designation: str
    nasa_jpl_url: str
    
    # Physical properties
    absolute_magnitude_h: float
    is_potentially_hazardous: bool
    diameter_min_meters: float
    diameter_max_meters: float
    
    # Closest approach data
    closest_approach_date: str
    closest_miss_distance_km: float
    closest_relative_velocity_kms: float
    
    # Orbital data
    first_observation_date: str
    last_observation_date: str
    observations_used: int
    orbital_period_days: float
    
    # Derived partition fields
    approach_year: int
    approach_month: int
    approach_day: int
    ingestion_year: int
    ingestion_month: int
    ingestion_day: int
    
    def to_dict(self) -> Dict:
        """Convert to dictionary for Parquet serialization"""
        return asdict(self)
    
    @classmethod
    def from_dict(cls, data: Dict) -> 'NEORecord':
        """Create instance from dictionary"""
        return cls(**data)
    
    def validate(self) -> None:
        """Validate field constraints for data quality
        
        Raises:
            AssertionError: If validation fails
        """
        assert self.diameter_min_meters > 0, \
            f"diameter_min_meters must be positive, got {self.diameter_min_meters}"
        assert self.diameter_max_meters >= self.diameter_min_meters, \
            f"diameter_max_meters ({self.diameter_max_meters}) must be >= diameter_min_meters ({self.diameter_min_meters})"
        assert self.closest_miss_distance_km > 0, \
            f"closest_miss_distance_km must be positive, got {self.closest_miss_distance_km}"
        assert self.closest_relative_velocity_kms > 0, \
            f"closest_relative_velocity_kms must be positive, got {self.closest_relative_velocity_kms}"
        assert self.observations_used > 0, \
            f"observations_used must be positive, got {self.observations_used}"
        assert self.orbital_period_days > 0, \
            f"orbital_period_days must be positive, got {self.orbital_period_days}"
