from typing import Optional
import pandas as pd
from data import NEOData

class Transformer:
	def process_estimated_diameter(self, estimated_diameter: dict) -> tuple[Optional[float], Optional[float]]:
		estimated_diameter_meters = estimated_diameter.get("meters", {})
		estimated_diameter_min = estimated_diameter_meters.get("estimated_diameter_min", None)
		estimated_diameter_max = estimated_diameter_meters.get("estimated_diameter_max", None)

		return estimated_diameter_min, estimated_diameter_max
	
	def process_close_approach_data(self, close_approach_data: dict) -> tuple[Optional[float], Optional[float], Optional[str]]:
		if len(close_approach_data) == 0:
			return None, None, None

		first_entry = close_approach_data[0]
		closest_approach_miss_distance_in_kilometers = first_entry.get("miss_distance", {}).get("kilometers", None)
		closest_approach_date = first_entry.get("close_approach_date", None)
		relative_velocity = first_entry.get("relative_velocity", {}).get("kilometers_per_second", None)

		return closest_approach_miss_distance_in_kilometers, relative_velocity, closest_approach_date
	
	def process_orbital_data(self, orbital_data: dict) -> tuple[Optional[str], Optional[str], Optional[int]]:
		return (
			orbital_data.get("first_observation_date", None),
			orbital_data.get("last_observation_date", None),
			orbital_data.get("observations_used", None)
		)

	def process_neo_entry(self, entry: dict) -> NEOData:
		estimated_diameter_min, estimated_diameter_max = self.process_estimated_diameter(entry.get("estimated_diameter", {}))
		closest_approach_miss_distance_in_kilometers, closest_approach_date, relative_velocity = self.process_close_approach_data(entry.get("close_approach_data", {}))
		first_observation_date, last_observation_date, observations_used = self.process_orbital_data(entry.get("orbital_data", {}))
		orbital_period = entry.get("orbital_data", {}).get("orbital_period", None)

		return NEOData(
			id=str(entry["id"]),
			neo_reference_id=str(entry["neo_reference_id"]),
      name=str(entry["name"]),
      name_limited=entry.get("name_limited", None),
      designation=str(entry["designation"]),
      nasa_jpl_url=str(entry["nasa_jpl_url"]),
      absolute_magnitude_h=float(entry["absolute_magnitude_h"]),   
      is_potentially_hazardous_asteroid=bool(entry["is_potentially_hazardous_asteroid"]),
			minimum_estimated_diameter_in_meters=estimated_diameter_min,
      maximum_estimated_diameter_in_meters=estimated_diameter_max,
      closest_approach_miss_distance_in_kilometers=closest_approach_miss_distance_in_kilometers,
      closest_approach_date=closest_approach_date,
      closest_approach_relative_velocity_in_kilometers_per_second=relative_velocity,
      first_observation_date=first_observation_date,
      last_observation_date=last_observation_date,
      observations_used=observations_used,
      orbital_period=orbital_period,
    )

	def process(self, raw_data: list[list[dict]]) -> pd.DataFrame:
		processed_data = []
		for page_index, page in enumerate(raw_data):
			for entry in page:
				try:
					processed_data.append(self.process_neo_entry(entry))
				except Exception as e:
					log_entry = {
						"page": page_index,
						"id": entry.get("id"),
						"error": str(e)
					}
					print(f"Skipping entry: {log_entry}")
					continue

		df = pd.DataFrame(processed_data)
		
		return df
