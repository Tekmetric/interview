import pandas as pd
from typing import Dict, List, Tuple, Optional
from datetime import datetime


class NEODataProcessor:
    def __init__(self):
        self.required_fields = [
            'id', 'neo_reference_id', 'name', 'name_limited', 'designation',
            'nasa_jpl_url', 'absolute_magnitude_h', 'is_potentially_hazardous_asteroid'
        ]

    def extract_basic_info(self, neo: Dict) -> Dict:
        """Extract basic NEO information from API response."""
        result = {}
        
        for field in self.required_fields:
            result[field] = neo.get(field)
        
        # Extract diameter information
        estimated_diameter = neo.get('estimated_diameter', {}).get('meters', {})
        result['min_diameter_meters'] = estimated_diameter.get('estimated_diameter_min')
        result['max_diameter_meters'] = estimated_diameter.get('estimated_diameter_max')
        
        # Extract orbital data
        orbital_data = neo.get('orbital_data', {})
        result['first_observation_date'] = orbital_data.get('first_observation_date')
        result['last_observation_date'] = orbital_data.get('last_observation_date')
        result['observations_used'] = orbital_data.get('observations_used')
        result['orbital_period'] = orbital_data.get('orbital_period')
        
        return result

    def find_closest_approach(self, close_approach_data: List[Dict]) -> Tuple[Optional[float], Optional[str], Optional[float]]:
        """Find the closest approach from close approach data."""
        if not close_approach_data:
            return None, None, None
        
        closest_distance = float('inf')
        closest_date = None
        closest_velocity = None
        
        for approach in close_approach_data:
            miss_distance = approach.get('miss_distance', {})
            distance_km = miss_distance.get('kilometers')
            
            if distance_km is not None:
                try:
                    distance_km = float(distance_km)
                    if distance_km < closest_distance:
                        closest_distance = distance_km
                        closest_date = approach.get('close_approach_date')
                        relative_velocity = approach.get('relative_velocity', {})
                        closest_velocity = relative_velocity.get('kilometers_per_second')
                        if closest_velocity is not None:
                            try:
                                closest_velocity = float(closest_velocity)
                            except (ValueError, TypeError):
                                closest_velocity = None
                except (ValueError, TypeError):
                    continue
        
        return closest_distance if closest_distance != float('inf') else None, closest_date, closest_velocity

    def process_neo(self, neo: Dict) -> Dict:
        """Process a single NEO record into the required format."""
        basic_info = self.extract_basic_info(neo)
        
        # Get close approach data
        close_approach_data = neo.get('close_approach_data', [])
        closest_distance, closest_date, closest_velocity = self.find_closest_approach(close_approach_data)
        
        basic_info.update({
            'closest_approach_distance_km': closest_distance,
            'closest_approach_date': closest_date,
            'closest_approach_velocity_km_s': closest_velocity
        })
        
        return basic_info

    def process_batch(self, neos: List[Dict]) -> pd.DataFrame:
        """Process a batch of NEO records into a DataFrame."""
        processed_data = [self.process_neo(neo) for neo in neos]
        return pd.DataFrame(processed_data)

    def calculate_aggregations(self, df: pd.DataFrame, detailed_neo_data: List[Dict] = None) -> Dict:
        """Calculate required aggregations from the processed data."""
        close_approaches_under_02_au = 0
        yearly_approaches = {}
        
        # Process detailed NEO data if available for accurate close approach counting
        if detailed_neo_data:
            for neo in detailed_neo_data:
                close_approach_data = neo.get('close_approach_data', [])
                for approach in close_approach_data:
                    miss_distance = approach.get('miss_distance', {})
                    distance_au = miss_distance.get('astronomical')
                    
                    if distance_au is not None:
                        distance_au = float(distance_au)
                        if distance_au < 0.2:
                            close_approaches_under_02_au += 1
                        
                        # Extract year for yearly breakdown
                        approach_date = approach.get('close_approach_date')
                        if approach_date:
                            try:
                                year = pd.to_datetime(approach_date).year
                                yearly_approaches[year] = yearly_approaches.get(year, 0) + 1
                            except:
                                continue
        else:
            # Fallback: use closest approach data from processed DataFrame
            for _, row in df.iterrows():
                if pd.notna(row['closest_approach_date']):
                    try:
                        year = pd.to_datetime(row['closest_approach_date']).year
                        yearly_approaches[year] = yearly_approaches.get(year, 0) + 1
                    except:
                        continue
        
        return {
            'close_approaches_under_02_au': close_approaches_under_02_au,
            'yearly_approaches': yearly_approaches
        }
