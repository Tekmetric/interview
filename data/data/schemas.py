import pyarrow as pa

DECIMAL_TYPE = pa.decimal128(25, 15)

neo_schema = pa.schema(
    [
        pa.field("id", pa.string()),
        pa.field("neo_reference_id", pa.string()),
        pa.field("name", pa.string()),
        pa.field("name_limited", pa.string(), nullable=True),
        pa.field("designation", pa.string()),
        pa.field("nasa_jpl_url", pa.string()),
        pa.field("absolute_magnitude_h", pa.float32()),
        pa.field("is_potentially_hazardous_asteroid", pa.bool_()),
        pa.field("estimated_diameter_min_meters", pa.float64()),
        pa.field("estimated_diameter_max_meters", pa.float64()),
        pa.field(
            "closest_approach_miss_distance_kilometers", DECIMAL_TYPE, nullable=True
        ),
        pa.field("closest_approach_date", pa.string(), nullable=True),
        pa.field(
            "closest_approach_relative_velocity_kilometers_per_second",
            DECIMAL_TYPE,
            nullable=True,
        ),
        pa.field("first_observation_date", pa.string()),
        pa.field("last_observation_date", pa.string()),
        pa.field("observations_used", pa.int64()),
        pa.field("orbital_period", DECIMAL_TYPE),
    ]
)
yearly_count_schema = pa.schema(
    [pa.field("year", pa.int8()), pa.field("num_close_approaches", pa.int32())]
)
totals_schema = pa.schema([pa.field("num_close_approaches", pa.int32())])
