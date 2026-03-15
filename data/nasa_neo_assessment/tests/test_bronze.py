from nasa_neo_pipeline.extract import flatten_close_approaches, flatten_neo_objects


def test_flatten_functions_preserve_expected_row_counts():
    objects = [
        {
            "links": {"self": "self"},
            "id": "1",
            "neo_reference_id": "1",
            "name": "A",
            "name_limited": "A",
            "designation": "A",
            "nasa_jpl_url": "url",
            "absolute_magnitude_h": 10.5,
            "is_potentially_hazardous_asteroid": False,
            "is_sentry_object": False,
            "estimated_diameter": {
                "kilometers": {
                    "estimated_diameter_min": 0.1,
                    "estimated_diameter_max": 0.2,
                },
                "meters": {
                    "estimated_diameter_min": 100.0,
                    "estimated_diameter_max": 200.0,
                },
                "miles": {
                    "estimated_diameter_min": 0.06,
                    "estimated_diameter_max": 0.12,
                },
                "feet": {
                    "estimated_diameter_min": 328.0,
                    "estimated_diameter_max": 656.0,
                },
            },
            "orbital_data": {
                "orbit_id": "1",
                "orbit_determination_date": "2020-01-01",
                "first_observation_date": "2010-01-01",
                "last_observation_date": "2020-01-01",
                "data_arc_in_days": 100,
                "observations_used": 50,
                "orbit_uncertainty": "0",
                "minimum_orbit_intersection": "0.1",
                "jupiter_tisserand_invariant": "3.5",
                "epoch_osculation": "2459000.5",
                "eccentricity": "0.1",
                "semi_major_axis": "1.2",
                "inclination": "5.1",
                "ascending_node_longitude": "10.2",
                "orbital_period": "400.0",
                "perihelion_distance": "0.9",
                "perihelion_argument": "20.1",
                "aphelion_distance": "1.5",
                "perihelion_time": "2459100.5",
                "mean_anomaly": "30.1",
                "mean_motion": "0.9",
                "equinox": "J2000",
                "orbit_class": {
                    "orbit_class_type": "APO",
                    "orbit_class_description": "Apollo",
                    "orbit_class_range": "a > 1.0 AU; q < 1.017 AU",
                },
            },
            "close_approach_data": [
                {
                    "close_approach_date": "2020-01-01",
                    "close_approach_date_full": "2020-Jan-01 00:00",
                    "epoch_date_close_approach": 1577836800000,
                    "relative_velocity": {
                        "kilometers_per_second": "5.5",
                        "kilometers_per_hour": "19800",
                        "miles_per_hour": "12303",
                    },
                    "miss_distance": {
                        "astronomical": "0.1",
                        "lunar": "38.9",
                        "kilometers": "14959787",
                        "miles": "9295588",
                    },
                    "orbiting_body": "Earth",
                }
            ],
        }
    ]

    neo_objects_df = flatten_neo_objects(objects)
    close_approaches_df = flatten_close_approaches(objects)

    assert len(neo_objects_df) == 1
    assert len(close_approaches_df) == 1
    assert neo_objects_df.iloc[0]["is_sentry_object"] == False
    assert close_approaches_df.iloc[0]["miss_distance_astronomical"] == 0.1
