COPY
(
    SELECT
    n.id,
    n.neo_reference_id,
    n.name,
    n.name_limited,
    n.designation,
    n.nasa_jpl_url,
    n.absolute_magnitude_h,
    n.is_potentially_hazardous_asteroid,
    n.estimated_diameter.meters.estimated_diameter_min AS estimated_diameter_min_meters,
    n.estimated_diameter.meters.estimated_diameter_max AS estimated_diameter_max_meters,
    CAST(unnest.miss_distance.kilometers AS DOUBLE) AS miss_distance_km,
    CAST(unnest.miss_distance.astronomical AS DOUBLE) AS miss_distance_au,
    unnest.close_approach_date AS close_approach_date,
    CAST(unnest.relative_velocity.kilometers_per_second AS DOUBLE) AS relative_velocity_kps,
    n.orbital_data.first_observation_date AS first_observation_date,
    n.orbital_data.last_observation_date AS last_observation_date,
    n.orbital_data.observations_used AS observations_used,
    TRY_CAST(n.orbital_data.orbital_period AS DOUBLE) AS orbital_period
FROM '{source}' n,
    UNNEST(close_approach_data) AS ca
)
TO '{output}'
(FORMAT PARQUET)
