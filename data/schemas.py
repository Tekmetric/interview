"""
Contains Pyspark schemas associated with the NASA NeoWs API: https://api.nasa.gov.
NEOWS_SCHEMA is the top-level schema for raw data. DecimalTypes used to preserve precision.
"""

from pyspark.sql.types import (
    ArrayType,
    BooleanType,
    DateType,
    DecimalType,
    FloatType,
    IntegerType,
    LongType,
    StringType,
    StructField,
    StructType,
    TimestampType,
)

RELATIVE_VELOCITY_SCHEMA = StructType(
    [
        StructField("kilometers_per_second", DecimalType(20, 10), False),
        StructField("kilometers_per_hour", DecimalType(20, 10), False),
        StructField("miles_per_hour", DecimalType(20, 10), False),
    ]
)

MISS_DISTANCE_SCHEMA = StructType(
    [
        StructField("astronomical", DecimalType(20, 10), False),
        StructField("lunar", DecimalType(20, 10), False),
        StructField("kilometers", DecimalType(20, 10), False),
        StructField("miles", DecimalType(20, 10), False),
    ]
)


CLOSE_APPROACH_SCHEMA = StructType(
    [
        StructField("close_approach_date", DateType(), False),
        StructField("close_approach_date_full", StringType(), False),
        StructField("epoch_date_close_approach", LongType(), False),
        StructField("relative_velocity", RELATIVE_VELOCITY_SCHEMA, False),
        StructField("miss_distance", MISS_DISTANCE_SCHEMA, False),
        StructField("orbiting_body", StringType(), False),
    ]
)

DIAMETER_SCHEMA = StructType(
    [
        StructField("estimated_diameter_min", DecimalType(20, 10), False),
        StructField("estimated_diameter_max", DecimalType(20, 10), False),
    ]
)

ESTIMATED_DIAMETER_SCHEMA = StructType(
    [
        StructField("kilometers", DIAMETER_SCHEMA, False),
        StructField("meters", DIAMETER_SCHEMA, False),
        StructField("miles", DIAMETER_SCHEMA, False),
        StructField("feet", DIAMETER_SCHEMA, False),
    ]
)

ORBIT_CLASS_SCHEMA = StructType(
    [
        StructField("orbit_class_type", StringType(), False),
        StructField("orbit_class_description", StringType(), False),
        StructField("orbit_class_range", StringType(), False),
    ]
)

ORBITAL_DATA_SCHEMA = StructType(
    [
        StructField("orbit_id", StringType(), False),
        StructField("orbit_determination_date", TimestampType(), False),
        StructField("first_observation_date", DateType(), False),
        StructField("last_observation_date", DateType(), False),
        StructField("data_arc_in_days", IntegerType(), False),
        StructField("observations_used", IntegerType(), False),
        StructField("orbit_uncertainty", StringType(), False),
        StructField("minimum_orbit_intersection", DecimalType(20, 10), False),
        StructField("jupiter_tisserand_invariant", DecimalType(20, 10), False),
        StructField("epoch_osculation", DecimalType(20, 10), False),
        StructField("eccentricity", DecimalType(20, 10), False),
        StructField("semi_major_axis", DecimalType(20, 10), False),
        StructField("inclination", DecimalType(20, 10), False),
        StructField("ascending_node_longitude", DecimalType(20, 10), False),
        StructField("orbital_period", DecimalType(20, 10), False),
        StructField("perihelion_distance", DecimalType(20, 10), False),
        StructField("aphelion_distance", DecimalType(20, 10), False),
        StructField("perihelion_time", DecimalType(20, 10), False),
        StructField("mean_anomaly", DecimalType(20, 10), False),
        StructField("mean_motion", DecimalType(20, 10), False),
        StructField("equinox", StringType(), False),
        StructField("orbit_class", ORBIT_CLASS_SCHEMA, False),
    ]
)

# Top-level schema for close earth objects from NeoWs API.
NEOWS_SCHEMA = StructType(
    [
        StructField("id", StringType(), False),
        StructField("neo_reference_id", StringType(), False),
        StructField("name", StringType(), False),
        StructField("name_limited", StringType(), False),
        StructField("designation", StringType(), False),
        StructField("nasa_jpl_url", StringType(), False),
        StructField("absolute_magnitude_h", FloatType(), False),
        StructField("estimated_diameter", ESTIMATED_DIAMETER_SCHEMA, False),
        StructField("is_potentially_hazardous_asteroid", BooleanType(), False),
        StructField("close_approach_data", ArrayType(CLOSE_APPROACH_SCHEMA), False),
        StructField("orbital_data", ORBITAL_DATA_SCHEMA, False),
        StructField("is_sentry_object", BooleanType(), False),
    ]
)

# Output schema for aggregated closest approach data. Used in unit tests.
AGG_CLOSEST_APPROACH_SCHEMA = StructType(
    [
        StructField("id", StringType(), False),
        StructField("neo_reference_id", StringType(), False),
        StructField("name", StringType(), False),
        StructField("name_limited", StringType(), False),
        StructField("designation", StringType(), False),
        StructField("nasa_jpl_url", StringType(), False),
        StructField("absolute_magnitude_h", FloatType(), False),
        StructField("is_potentially_hazardous_asteroid", BooleanType(), False),
        StructField("estimated_diameter_min_meters", DecimalType(20, 10), False),
        StructField("estimated_diameter_max_meters", DecimalType(20, 10), False),
        StructField("close_approach_date", DateType(), True),
        StructField(
            "close_approach_miss_distance_kilometers", DecimalType(20, 10), True
        ),
        StructField(
            "close_approach_velocity_kilometers_per_second", DecimalType(20, 10), True
        ),
        StructField("first_observation_date", DateType(), False),
        StructField("last_observation_date", DateType(), False),
        StructField("observations_used", IntegerType(), False),
        StructField("orbital_period", DecimalType(20, 10), False),
    ]
)
