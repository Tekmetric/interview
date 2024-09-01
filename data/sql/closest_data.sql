WITH ranks AS ( 
    SELECT 
        *,
        RANK() OVER (PARTITION BY id ORDER BY approach_miss_distance_km ASC) as distance_rank
    FROM view
)
SELECT 
    id, 
    neo_reference_id, 
    name,   
    name_limited, 
    designation, 
    nasa_jpl_url,
    absolute_magnitude_h,
    is_potentially_hazardous_asteroid,
    minimum_diameter_meters,
    maximum_diameter_meters,
    approach_miss_distance_km as closest_approach_miss_distance_km,
    approach_date as closest_approach_date,
    approach_velocity_kmps as closest_approach_velocity_kmps,
    first_observation_date,
    last_observation_date,
    observations_used,
    orbital_period
FROM ranks
WHERE distance_rank = 1 