SELECT
    id,
    neo_reference_id,
    name,
    name_limited,
    designation,
    nasa_jpl_url,
    absolute_magnitude_h,
    is_potentially_hazardous_asteroid,
    CAST(estimated_diameter.meters.estimated_diameter_min AS DOUBLE) AS minimum_diameter_meters,
    CAST(estimated_diameter.meters.estimated_diameter_max AS DOUBLE) AS maximum_diameter_meters,
    CAST(approach_data.miss_distance.kilometers AS DOUBLE) AS approach_miss_distance_km,
    approach_data.close_approach_date AS approach_date,
    year (cast(approach_data.close_approach_date as timestamp) ) as  approach_year, 
    CAST(approach_data.relative_velocity.kilometers_per_second AS DOUBLE) AS approach_velocity_kmps,
    CAST(approach_data.miss_distance.astronomical AS DOUBLE) AS approach_miss_distance_astronomical,
    orbital_data.first_observation_date AS first_observation_date,
    orbital_data.last_observation_date AS last_observation_date,
    orbital_data.observations_used AS observations_used,
    orbital_data.orbital_period AS orbital_period
FROM view