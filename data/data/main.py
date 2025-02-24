import pyarrow as pa
import pyarrow.parquet as pq
import requests

from data.config import logger

if __name__ == "__main__":
    URL = "https://api.nasa.gov/neo/rest/v1/neo/browse?api_key=DEMO_KEY"
    response = requests.get(URL)
    data = response.json()

    near_earth_objects = data["near_earth_objects"]
    records = []
    for obj in near_earth_objects:
        obj_id = obj["id"]
        est_diameter = obj["estimated_diameter"]["meters"]
        orbital_data = obj["orbital_data"]

        # map closest approach data if found
        closest_approach_miss_distance_kilometers = None
        closest_approach_date = None
        closest_approach_relative_velocity_kilometers_per_second = None
        close_approaches = obj["close_approach_data"]
        if close_approaches:
            closest = min(
                close_approaches,
                key=lambda x: float(x["miss_distance"]["astronomical"]),
            )
            closest_approach_miss_distance_kilometers = float(
                closest["miss_distance"]["kilometers"]
            )
            closest_approach_date = closest["close_approach_date"]
            closest_approach_relative_velocity_kilometers_per_second = float(
                closest["relative_velocity"]["kilometers_per_second"]
            )
        else:
            logger.debug(f"missing_close_data id={obj_id}")

        record = {
            "id": obj_id,
            "neo_reference_id": obj["neo_reference_id"],
            "name": obj["name"],
            "name_limited": obj["name_limited"],
            "designation": obj["designation"],
            "nasa_jpl_url": obj["nasa_jpl_url"],
            "absolute_magnitude_h": obj["absolute_magnitude_h"],
            "is_potentially_hazardous_asteroid": obj[
                "is_potentially_hazardous_asteroid"
            ],
            "estimated_diameter_min_meters": est_diameter["estimated_diameter_min"],
            "estimated_diameter_max_meters": est_diameter["estimated_diameter_max"],
            # closest approach
            "closest_approach_miss_distance_kilometers": closest_approach_miss_distance_kilometers,
            "closest_approach_date": closest_approach_date,
            "closest_approach_relative_velocity_kilometers_per_second": closest_approach_relative_velocity_kilometers_per_second,
            # orbital data
            "first_observation_date": orbital_data["first_observation_date"],
            "last_observation_date": orbital_data["last_observation_date"],
            "observations_used": int(orbital_data["observations_used"]),
            "orbital_period": float(orbital_data["orbital_period"]),
        }
        logger.debug(f"mapped_record record={record}")
        records.append(record)
    table = pa.Table.from_pylist(records)
    output_path = "output.parquet"
    logger.info(f"writing_output path={output_path}")
    pq.write_table(table, "output.parquet")
