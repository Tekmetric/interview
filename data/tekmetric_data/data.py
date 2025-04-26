import pyarrow as pa

data_schema = pa.schema(
    [
        pa.field("id", pa.string()),
        pa.field("neo_reference_id", pa.string()),
        pa.field("name", pa.string()),
        pa.field("name_limited", pa.string(), nullable=True),
        pa.field("designation", pa.string()),
        pa.field("nasa_jpl_url", pa.string()),
        pa.field("absolute_magnitude_h", pa.float32()),
        pa.field("is_potentially_hazardous_asteroid", pa.bool_()),
        pa.field("estimated_diameter_min_m", pa.float64()),
        pa.field("estimated_diameter_max_m", pa.float64()),
        pa.field("closest_approach_miss_distance_km", pa.float64(), nullable=True),  # precision loss here
        pa.field("closest_approach_date", pa.string(), nullable=True),
        pa.field("closest_approach_relative_velocity_kmh", pa.float64(), nullable=True),  # precision loss here
        pa.field("first_observation_date", pa.string()),
        pa.field("last_observation_date", pa.string()),
        pa.field("observations_used", pa.int32()),
        pa.field("orbital_period", pa.float64()),  # precision loss here
    ]
)

metric_schema = pa.schema(
    [
        pa.field("number_of_near_misses", pa.int32()),
        pa.field("per_year_miss", pa.map_(pa.int32(), pa.int32()))
    ]
)


def data_to_record_dict(data: dict) -> dict:
    """
    Convert a dictionary to a dictionary compatible with the data schema.
    :param data: The data to convert.
    :return: A pyarrow RecordBatch.
    """

    # Extract the closest approach data
    closest_approach = min(
        data.get('close_approach_data', []),
        key=lambda x: float(x['miss_distance']['lunar']),
        default=None
    )

    # If some oject has no close approach data, we set the values to None
    if closest_approach is not None:
        close_approach_dict = {
            "closest_approach_miss_distance_km": float(closest_approach['miss_distance']['kilometers']),
            "closest_approach_date": closest_approach['close_approach_date_full'],
            "closest_approach_relative_velocity_kmh": float(
                closest_approach['relative_velocity']['kilometers_per_hour']),
        }
    else:
        close_approach_dict = {
            "closest_approach_miss_distance_km": None,
            "closest_approach_date": None,
            "closest_approach_relative_velocity_kmh": None,
        }

    record = {
        "id": data["id"],
        "neo_reference_id": data["neo_reference_id"],
        "name": data["name"],
        "name_limited": data.get("name_limited"),
        "designation": data["designation"],
        "nasa_jpl_url": data["nasa_jpl_url"],
        "absolute_magnitude_h": data["absolute_magnitude_h"],
        "is_potentially_hazardous_asteroid": data["is_potentially_hazardous_asteroid"],
        "estimated_diameter_min_m": data['estimated_diameter']['meters']['estimated_diameter_min'],
        "estimated_diameter_max_m": data['estimated_diameter']['meters']['estimated_diameter_max'],
        "first_observation_date": data["orbital_data"]["first_observation_date"],
        "last_observation_date": data["orbital_data"]["last_observation_date"],
        "observations_used": data["orbital_data"]["observations_used"],
        "orbital_period": float(data["orbital_data"]["orbital_period"]),
    }
    record.update(close_approach_dict)

    return record
