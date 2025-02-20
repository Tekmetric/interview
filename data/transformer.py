import pandas as pd
from data import NEOData

class Transformer:
	def process_estimated_diameter(self, estimated_diameter: dict) -> tuple[float, float]:
		estimated_diameter_meters = estimated_diameter.get("meters", {})
		estimated_diameter_min = estimated_diameter_meters.get("estimated_diameter_min", None)
		estimated_diameter_max = estimated_diameter_meters.get("estimated_diameter_max", None)

		return estimated_diameter_min, estimated_diameter_max
	
	def process_close_approach_data(self, close_approach_data: dict) -> tuple[float, float, str]:
		if len(close_approach_data) == 0:
			return None, None, None

		first_entry = close_approach_data[0]
		closest_approach_miss_distance_in_kilometers = first_entry.get("miss_distance", {}).get("kilometers", None)
		closest_approach_date = first_entry.get("close_approach_date", None)
		relative_velocity = first_entry.get("relative_velocity", {}).get("kilometers_per_second", None)

		return closest_approach_miss_distance_in_kilometers, relative_velocity, closest_approach_date
	
	def process_orbital_data(self, orbital_data: dict) -> tuple[str, str, int]:

		first_observation_date = orbital_data.get("first_observation_date", None)
		last_observation_date = orbital_data.get("last_observation_date", None)
		observations_used = orbital_data.get("observations_used", None)

		return first_observation_date, last_observation_date, observations_used

	def process_neo_entry(self, entry: dict) -> NEOData:
		estimated_diameter_min, estimated_diameter_max = self.process_estimated_diameter(entry.get("estimated_diameter", {}))
		closest_approach_miss_distance_in_kilometers, closest_approach_date, relative_velocity = self.process_close_approach_data(entry.get("close_approach_data", {}))
		first_observation_date, last_observation_date, observations_used = self.process_orbital_data(entry.get("orbital_data", {}))
		orbital_period = entry.get("orbital_data", {}).get("orbital_period", None)

		return NEOData(
			id=str(entry["id"]),
			neo_reference_id=str(entry["neo_reference_id"]),
      name=str(entry["name"]),
      name_limited=str(entry["name_limited"]),
      designation=str(entry["designation"]),
      nasa_jpl_url=str(entry["nasa_jpl_url"]),
      absolute_magnitude_h=float(entry["absolute_magnitude_h"]),   
      is_potentially_hazardous_asteroid=bool(entry["is_potentially_hazardous_asteroid"]),

			minimum_estimated_diameter_in_meters=float(estimated_diameter_min) if estimated_diameter_min is not None else None,
      maximum_estimated_diameter_in_meters=float(estimated_diameter_max) if estimated_diameter_max is not None else None,
            
      closest_approach_miss_distance_in_kilometers=closest_approach_miss_distance_in_kilometers,
      closest_approach_date=closest_approach_date,
      closest_approach_relative_velocity_in_kilometers_per_second=relative_velocity,

      first_observation_date=first_observation_date,
      last_observation_date=last_observation_date,
      observations_used=observations_used,
      orbital_period=orbital_period,
    )

	def process(self, raw_data: list[dict]) -> pd.DataFrame:
		processed_data = []
		for entry in raw_data:
			try:
				processed_data.append(self.process_neo_entry(entry))
			except Exception as e:
				print(f"Skipping entry: {entry} due to error: {e}")
				continue

		df = pd.DataFrame(processed_data)
		
		return df
