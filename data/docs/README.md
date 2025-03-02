### Documentation on the structure of the Scraping service.

# Requirements
- Create an account at [api.nasa.gov](https://api.nasa.gov) to get an API key
- Find the docs for the Near Earth Object Web Service (below the signup on the same page)
- Data should be saved in Parquet format
- Design the code such that the scraping and processing part could easily be scaled up GBs of data by swapping in and out various implementations.
- Use the Browse API to request data
    - There are over 1800 pages of near Earth objects, so we'll limit ourselves to gathering the first 200 near earth objects
- We want to save the following columns in our file(s):
    - id
    - neo_reference_id
    - name
    - name_limited
    - designation
    - nasa_jpl_url
    - absolute_magnitude_h
    - is_potentially_hazardous_asteroid
    - minimum estimated diameter in meters
    - maximum estimated diameter in meters
    - **closest** approach miss distance in kilometers
    - **closest** approach date
    - **closest** approach relative velocity in kilometers per second
    - first observation date
    - last observation date
    - observations used
    - orbital period
- Store the following aggregations:
    - The total number of times our 200 near earth objects approached closer than 0.2 astronomical units (found as miss_distance.astronomical)
    - The number of close approaches recorded in each year present in the data

# Installation and Running
In order to run the data scraping utility, you will need to have a Python environment where dependencies can be handled by [Poetry](https://python-poetry.org/).
Run the following commands within the data directory of the project

Commands:
```sh
poetry install
python recall_data.py scraper.request.api_key=<your NASA API key>
```

# Application components
In order to support the above requirements, some specific application components should be created.

1. Handling data requests to the NEO API.
This should be done via a DataHandler component. The NEO Service API is a REST API and using the browse API call we get paged results. The
RequestsDataHandler component will only request one page at a time and return the result. Currently we are using the [Requests](https://docs.python-requests.org/en/latest/) library as the particular implementation here. This can be replaced by something else, especially an async requests framework if we need to scale.

2. Handling the resulting data from the NEO API. 
The resulting JSON response containing the browse request data needs to be processed in order to save the required information about the asteroids and
required aggregations as well. There should be a component for this, called DataProcessor.

3. Putting it all together.
The NEO data scraping application is structured as a publisher-subscriber setup.
Currently a simple Python queue is used as the communication transport between the
publisher and subscriber. The publisher will be an implementation of the DataHandler interface while the subscriber will be an implementation of the DataProcessor interface. The scraper module contains the Scraper class which creates 2 threads, one for the subscriber, one for the producer, effectively decoupling them.
We could imagine this is a basic scaffold for a more scalable setup in which instead of using threads, one could use microservices that implement the DataHandler and DataProcessor contract while using a more scalable transport such as Rabbit MQ or Zero MQ.

# Example of NEO result
```json
{
    "links": {
        "self": "http://api.nasa.gov/neo/rest/v1/neo/2001981?api_key=DEMO_KEY"
    },
    "id": "2001981",
    "neo_reference_id": "2001981",
    "name": "1981 Midas (1973 EA)",
    "name_limited": "Midas",
    "designation": "1981",
    "nasa_jpl_url": "https://ssd.jpl.nasa.gov/tools/sbdb_lookup.html#/?sstr=2001981",
    "absolute_magnitude_h": 15.24,
    "estimated_diameter": {
        "kilometers": {
            "estimated_diameter_min": 2.3798795468,
            "estimated_diameter_max": 5.321572445
        },
        "meters": {
            "estimated_diameter_min": 2379.8795468308,
            "estimated_diameter_max": 5321.5724449751
        },
        "miles": {
            "estimated_diameter_min": 1.4787881339,
            "estimated_diameter_max": 3.3066707917
        },
        "feet": {
            "estimated_diameter_min": 7808.0040124244,
            "estimated_diameter_max": 17459.2277403721
        }
    },
    "is_potentially_hazardous_asteroid": true,
    "close_approach_data": [
        {
            "close_approach_date": "1916-09-24",
            "close_approach_date_full": "1916-Sep-24 16:24",
            "epoch_date_close_approach": -1681025760000,
            "relative_velocity": {
                "kilometers_per_second": "31.4029356404",
                "kilometers_per_hour": "113050.5683053748",
                "miles_per_hour": "70245.2267339703"
            },
            "miss_distance": {
                "astronomical": "0.1550818976",
                "lunar": "60.3268581664",
                "kilometers": "23199921.556518112",
                "miles": "14415762.7874174656"
            },
            "orbiting_body": "Earth"
        },
        {
            "close_approach_date": "1921-03-15",
            "close_approach_date_full": "1921-Mar-15 12:06",
            "epoch_date_close_approach": -1539950040000,
            "relative_velocity": {
                "kilometers_per_second": "29.6428801651",
                "kilometers_per_hour": "106714.3685944914",
                "miles_per_hour": "66308.1586413938"
            },
            "miss_distance": {
                "astronomical": "0.060169033",
                "lunar": "23.405753837",
                "kilometers": "9001159.17675971",
                "miles": "5593060.958755598"
            },
            "orbiting_body": "Earth"
        },
        {
            "close_approach_date": "1947-03-19",
            "close_approach_date_full": "1947-Mar-19 20:13",
            "epoch_date_close_approach": -719120820000,
            "relative_velocity": {
                "kilometers_per_second": "27.7329964762",
                "kilometers_per_hour": "99838.7873142275",
                "miles_per_hour": "62035.9398175543"
            },
            "miss_distance": {
                "astronomical": "0.0297912736",
                "lunar": "11.5888054304",
                "kilometers": "4456711.075147232",
                "miles": "2769271.8492545216"
            },
            "orbiting_body": "Earth"
        },
        {
            "close_approach_date": "1961-09-12",
            "close_approach_date_full": "1961-Sep-12 11:13",
            "epoch_date_close_approach": -262010820000,
            "relative_velocity": {
                "kilometers_per_second": "25.1210486554",
                "kilometers_per_hour": "90435.7751596031",
                "miles_per_hour": "56193.2737373656"
            },
            "miss_distance": {
                "astronomical": "0.1504010715",
                "lunar": "58.5060168135",
                "kilometers": "22499679.942117705",
                "miles": "13980652.824546129"
            },
            "orbiting_body": "Earth"
        },
        {
            "close_approach_date": "1987-09-21",
            "close_approach_date_full": "1987-Sep-21 09:16",
            "epoch_date_close_approach": 559214160000,
            "relative_velocity": {
                "kilometers_per_second": "29.3630471754",
                "kilometers_per_hour": "105706.9698312726",
                "miles_per_hour": "65682.2002265482"
            },
            "miss_distance": {
                "astronomical": "0.0692738718",
                "lunar": "26.9475361302",
                "kilometers": "10363223.667933066",
                "miles": "6439408.5879096708"
            },
            "orbiting_body": "Earth"
        },
        {
            "close_approach_date": "1992-03-11",
            "close_approach_date_full": "1992-Mar-11 16:49",
            "epoch_date_close_approach": 700332540000,
            "relative_velocity": {
                "kilometers_per_second": "31.3169626641",
                "kilometers_per_hour": "112741.0655908622",
                "miles_per_hour": "70052.9137833888"
            },
            "miss_distance": {
                "astronomical": "0.1333217795",
                "lunar": "51.8621722255",
                "kilometers": "19944654.237809665",
                "miles": "12393033.470776777"
            },
            "orbiting_body": "Earth"
        },
        {
            "close_approach_date": "2018-03-21",
            "close_approach_date_full": "2018-Mar-21 21:21",
            "epoch_date_close_approach": 1521667260000,
            "relative_velocity": {
                "kilometers_per_second": "26.5076663267",
                "kilometers_per_hour": "95427.5987762845",
                "miles_per_hour": "59294.9988062975"
            },
            "miss_distance": {
                "astronomical": "0.0895719502",
                "lunar": "34.8434886278",
                "kilometers": "13399772.961666074",
                "miles": "8326232.8258328612"
            },
            "orbiting_body": "Earth"
        },
        {
            "close_approach_date": "2032-09-14",
            "close_approach_date_full": "2032-Sep-14 14:57",
            "epoch_date_close_approach": 1978786620000,
            "relative_velocity": {
                "kilometers_per_second": "26.4179998614",
                "kilometers_per_hour": "95104.7995009954",
                "miles_per_hour": "59094.4238899379"
            },
            "miss_distance": {
                "astronomical": "0.0863501906",
                "lunar": "33.5902241434",
                "kilometers": "12917804.587854022",
                "miles": "8026751.5654766236"
            },
            "orbiting_body": "Earth"
        },
        {
            "close_approach_date": "2058-09-23",
            "close_approach_date_full": "2058-Sep-23 08:00",
            "epoch_date_close_approach": 2799993600000,
            "relative_velocity": {
                "kilometers_per_second": "30.6648255041",
                "kilometers_per_hour": "110393.3718148388",
                "miles_per_hour": "68594.1481701703"
            },
            "miss_distance": {
                "astronomical": "0.1199246485",
                "lunar": "46.6506882665",
                "kilometers": "17940471.976098695",
                "miles": "11147692.360584191"
            },
            "orbiting_body": "Earth"
        },
        {
            "close_approach_date": "2063-03-14",
            "close_approach_date_full": "2063-Mar-14 10:17",
            "epoch_date_close_approach": 2941093020000,
            "relative_velocity": {
                "kilometers_per_second": "30.1536943925",
                "kilometers_per_hour": "108553.2998128477",
                "miles_per_hour": "67450.7989864886"
            },
            "miss_distance": {
                "astronomical": "0.0824879129",
                "lunar": "32.0877981181",
                "kilometers": "12340016.070585523",
                "miles": "7667730.4288772974"
            },
            "orbiting_body": "Earth"
        },
        {
            "close_approach_date": "2089-03-20",
            "close_approach_date_full": "2089-Mar-20 14:00",
            "epoch_date_close_approach": 3762165600000,
            "relative_velocity": {
                "kilometers_per_second": "26.8464127336",
                "kilometers_per_hour": "96647.0858410306",
                "miles_per_hour": "60052.7406438337"
            },
            "miss_distance": {
                "astronomical": "0.0723277921",
                "lunar": "28.1355111269",
                "kilometers": "10820083.639962827",
                "miles": "6723288.2108559326"
            },
            "orbiting_body": "Earth"
        },
        {
            "close_approach_date": "2103-09-14",
            "close_approach_date_full": "2103-Sep-14 00:46",
            "epoch_date_close_approach": 4219173960000,
            "relative_velocity": {
                "kilometers_per_second": "25.4487105302",
                "kilometers_per_hour": "91615.3579086955",
                "miles_per_hour": "56926.2205849892"
            },
            "miss_distance": {
                "astronomical": "0.1337685144",
                "lunar": "52.0359521016",
                "kilometers": "20011484.827304328",
                "miles": "12434560.0734746064"
            },
            "orbiting_body": "Earth"
        },
        {
            "close_approach_date": "2129-09-20",
            "close_approach_date_full": "2129-Sep-20 14:31",
            "epoch_date_close_approach": 5040282660000,
            "relative_velocity": {
                "kilometers_per_second": "28.9668183869",
                "kilometers_per_hour": "104280.5461926823",
                "miles_per_hour": "64795.8760495586"
            },
            "miss_distance": {
                "astronomical": "0.0484668096",
                "lunar": "18.8535889344",
                "kilometers": "7250531.481855552",
                "miles": "4505271.3506165376"
            },
            "orbiting_body": "Earth"
        },
        {
            "close_approach_date": "2134-03-11",
            "close_approach_date_full": "2134-Mar-11 20:43",
            "epoch_date_close_approach": 5181396180000,
            "relative_velocity": {
                "kilometers_per_second": "31.7683709807",
                "kilometers_per_hour": "114366.135530372",
                "miles_per_hour": "71062.6690466359"
            },
            "miss_distance": {
                "astronomical": "0.1521728677",
                "lunar": "59.1952455353",
                "kilometers": "22764736.879711799",
                "miles": "14145351.5683848662"
            },
            "orbiting_body": "Earth"
        },
        {
            "close_approach_date": "2160-03-21",
            "close_approach_date_full": "2160-Mar-21 01:27",
            "epoch_date_close_approach": 6002731620000,
            "relative_velocity": {
                "kilometers_per_second": "26.8457383616",
                "kilometers_per_hour": "96644.6581019339",
                "miles_per_hour": "60051.2321411711"
            },
            "miss_distance": {
                "astronomical": "0.0721632732",
                "lunar": "28.0715132748",
                "kilometers": "10795471.962948084",
                "miles": "6707995.2239041992"
            },
            "orbiting_body": "Earth"
        },
        {
            "close_approach_date": "2174-09-15",
            "close_approach_date_full": "2174-Sep-15 06:22",
            "epoch_date_close_approach": 6459891720000,
            "relative_velocity": {
                "kilometers_per_second": "26.2694093542",
                "kilometers_per_hour": "94569.8736751866",
                "miles_per_hour": "58762.0417844514"
            },
            "miss_distance": {
                "astronomical": "0.0920473657",
                "lunar": "35.8064252573",
                "kilometers": "13770089.847831059",
                "miles": "8556337.0688202542"
            },
            "orbiting_body": "Earth"
        },
        {
            "close_approach_date": "2200-09-25",
            "close_approach_date_full": "2200-Sep-25 11:56",
            "epoch_date_close_approach": 7281230160000,
            "relative_velocity": {
                "kilometers_per_second": "31.2490714538",
                "kilometers_per_hour": "112496.6572335328",
                "miles_per_hour": "69901.0479349137"
            },
            "miss_distance": {
                "astronomical": "0.1430749481",
                "lunar": "55.6561548109",
                "kilometers": "21403707.486120547",
                "miles": "13299647.1190436686"
            },
            "orbiting_body": "Earth"
        }
    ],
    "orbital_data": {
        "orbit_id": "259",
        "orbit_determination_date": "2025-02-20 05:51:07",
        "first_observation_date": "1973-03-06",
        "last_observation_date": "2025-02-19",
        "data_arc_in_days": 18978,
        "observations_used": 1369,
        "orbit_uncertainty": "0",
        "minimum_orbit_intersection": ".00339308",
        "jupiter_tisserand_invariant": "3.611",
        "epoch_osculation": "2460800.5",
        "eccentricity": ".6505192163325029",
        "semi_major_axis": "1.776403059065212",
        "inclination": "39.82316254897223",
        "ascending_node_longitude": "356.7944707193426",
        "orbital_period": "864.7900725488627",
        "perihelion_distance": ".6208187331914495",
        "perihelion_argument": "267.8466237373545",
        "aphelion_distance": "2.931987384938975",
        "perihelion_time": "2460843.543563718180",
        "mean_anomaly": "342.0815670410355",
        "mean_motion": ".4162859998368669",
        "equinox": "J2000",
        "orbit_class": {
            "orbit_class_type": "APO",
            "orbit_class_description": "Near-Earth asteroid orbits which cross the Earth’s orbit similar to that of 1862 Apollo",
            "orbit_class_range": "a (semi-major axis) > 1.0 AU; q (perihelion) < 1.017 AU"
        }
    },
    "is_sentry_object": false
}
```
