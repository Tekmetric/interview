from decimal import Decimal

import pytest

from data.process import map_neo_api_entry, _get_closest_approach

CLOSE_APPROACH_DATA = [
    {
        "close_approach_date": "2011-12-27",
        "close_approach_date_full": "2011-Dec-27 01:30",
        "epoch_date_close_approach": -2177879400000,
        "relative_velocity": {
            "kilometers_per_second": "5.5786191875",
            "kilometers_per_hour": "20083.0290749201",
            "miles_per_hour": "12478.8132604691",
        },
        "miss_distance": {
            "astronomical": "0.3149291693",
            "lunar": "122.5074468577",
            "kilometers": "47112732.928149391",
            "miles": "29274494.7651919558",
        },
        "orbiting_body": "Earth",
    },
    {
        "close_approach_date": "2007-11-05",
        "close_approach_date_full": "2007-Nov-05 03:31",
        "epoch_date_close_approach": -1961526540000,
        "relative_velocity": {
            "kilometers_per_second": "4.3944908885",
            "kilometers_per_hour": "15820.1671985367",
            "miles_per_hour": "9830.0366684463",
        },
        "miss_distance": {
            "astronomical": "0.1714855425",
            "lunar": "183.4078760325",
            "kilometers": "70533232.893794475",
            "miles": "43827318.620434755",
        },
        "orbiting_body": "Earth",
    },
]


def make_api_entry(close_approach_data: list[dict] = CLOSE_APPROACH_DATA):
    return {
        "links": {
            "self": "http://api.nasa.gov/neo/rest/v1/neo/2000433?api_key=Rz28he0ZUNcSgB9r9zKB88J5i5Bu3JQZdmOFsL2I"
        },
        "id": "2000433",
        "neo_reference_id": "2000433",
        "name": "433 Eros (A898 PA)",
        "name_limited": "Eros",
        "designation": "433",
        "nasa_jpl_url": "https://ssd.jpl.nasa.gov/tools/sbdb_lookup.html#/?sstr=2000433",
        "absolute_magnitude_h": 10.41,
        "estimated_diameter": {
            "kilometers": {
                "estimated_diameter_min": 22.0067027115,
                "estimated_diameter_max": 49.2084832235,
            },
            "meters": {
                "estimated_diameter_min": 22006.7027114738,
                "estimated_diameter_max": 49208.4832234845,
            },
            "miles": {
                "estimated_diameter_min": 13.6743268705,
                "estimated_diameter_max": 30.5767244291,
            },
            "feet": {
                "estimated_diameter_min": 72200.4705239119,
                "estimated_diameter_max": 161445.1600989368,
            },
        },
        "is_potentially_hazardous_asteroid": False,
        "close_approach_data": close_approach_data,
        "orbital_data": {
            "orbit_id": "659",
            "orbit_determination_date": "2021-05-24 17:55:05",
            "first_observation_date": "1893-10-29",
            "last_observation_date": "2021-05-13",
            "data_arc_in_days": 46582,
            "observations_used": 9130,
            "orbit_uncertainty": "0",
            "minimum_orbit_intersection": ".148588",
            "jupiter_tisserand_invariant": "4.582",
            "epoch_osculation": "2460800.5",
            "eccentricity": ".2227480169011467",
            "semi_major_axis": "1.45815896084448",
            "inclination": "10.82830761253864",
            "ascending_node_longitude": "304.2718959654088",
            "orbital_period": "643.1403141999031",
            "perihelion_distance": "1.133356943989735",
            "perihelion_argument": "178.9225697371719",
            "aphelion_distance": "1.782960977699224",
            "perihelion_time": "2461088.844738645026",
            "mean_anomaly": "198.5980421063379",
            "mean_motion": ".5597534349061246",
            "equinox": "J2000",
            "orbit_class": {
                "orbit_class_type": "AMO",
                "orbit_class_description": "Near-Earth asteroid orbits similar to that of 1221 Amor",
                "orbit_class_range": "1.017 AU < q (perihelion) < 1.3 AU",
            },
        },
        "is_sentry_object": False,
    }


def _make_expected_record(has_closest: bool = True):
    return {
        "absolute_magnitude_h": 10.41,
        "closest_approach_date": "2007-11-05" if has_closest else None,
        "closest_approach_miss_distance_kilometers": (
            Decimal("70533232.893794475") if has_closest else None
        ),
        "closest_approach_relative_velocity_kilometers_per_second": (
            Decimal("4.3944908885") if has_closest else None
        ),
        "designation": "433",
        "estimated_diameter_max_meters": 49208.4832234845,
        "estimated_diameter_min_meters": 22006.7027114738,
        "first_observation_date": "1893-10-29",
        "id": "2000433",
        "is_potentially_hazardous_asteroid": False,
        "last_observation_date": "2021-05-13",
        "name": "433 Eros (A898 PA)",
        "name_limited": "Eros",
        "nasa_jpl_url": "https://ssd.jpl.nasa.gov/tools/sbdb_lookup.html#/?sstr=2000433",
        "neo_reference_id": "2000433",
        "observations_used": 9130,
        "orbital_period": Decimal("643.1403141999031"),
    }


API_ENTRY = make_api_entry()
MISSING_CLOSE_APPROACH_API_ENTRY = make_api_entry(close_approach_data=[])


TEST_DATA = [
    ("full_data", API_ENTRY, _make_expected_record()),
    (
        "missing_close_approaches",
        MISSING_CLOSE_APPROACH_API_ENTRY,
        _make_expected_record(has_closest=False),
    ),
]


@pytest.mark.parametrize("label, api_entry, expected", TEST_DATA)
def test_map_neo_api_entry(label, api_entry, expected):
    assert map_neo_api_entry(api_entry) == expected


@pytest.mark.parametrize(
    "approach, expected",
    [
        ([(0.453, "2020-01-02")], (0.453, "2020-01-02")),
        ([(0.23, "2019-01-07"), (0.453, "2020-01-02")], (0.23, "2019-01-07")),
        (
            [(0.453, "2020-01-02"), (0.23, "2019-01-07"), (0.23, "2022-01-02")],
            (0.23, "2022-01-02"),
        ),
    ],
)
def test_get_closest_approach__chooses_first_with_min_distance(approach, expected):
    def _make_approach(distance: Decimal, date: str):
        return {
            "miss_distance": {"astronomical": distance},
            "close_approach_date": date,
        }

    approaches = [_make_approach(distance, date) for distance, date in approach]

    closest = _get_closest_approach(approaches)

    assert closest == _make_approach(*expected)
