import pandas as pd


def get_closest_approach(datum):
    closest = 100000000000000000.0 # 10^18 km, ~diameter of the Milky Way.
    day = ''
    velocity = ''
    for approach in datum['close_approach_data']:
        if float(approach['miss_distance']['kilometers']) < closest:
            closest = approach['miss_distance']['kilometers']
            day = approach['close_approach_date']
            velocity = approach['relative_velocity']['kilometers_per_second']
    
    return closest, day, velocity
    

def list_of_lists_to_df(list_of_lists):

    columns = [
            'id',
            'neo_reference_id',
            'name',
            'name_limited',
            'designation',
            'nasa_jpl_url',
            'absolute_magnitude_h',
            'is_potentially_hazardous_asteroid',
            'diameter_estimated_minimum',
            'diameter_estimated_maximum',
            'closest_approach_km',
            'closest_approach_date',
            'closest_approach_velocity_kps',
            'observation_date_first',
            'observation_date_latest',
            'observation_count',
            'orbital_period'
            ]

    df = pd.DataFrame(list_of_lists, columns=columns)

    return df


def clean_data(data, today):
    cleaned = []
    
    for datum in data:
    
        closest_approach_km, closest_approach_date, closest_approach_v_kps = get_closest_approach(datum)
    
        element = [
                datum['id'],
                datum['neo_reference_id'],
                datum['name'],
                datum['name_limited'],
                datum['designation'],
                datum['nasa_jpl_url'],
                datum['absolute_magnitude_h'],
                datum['is_potentially_hazardous_asteroid'],
                datum['estimated_diameter']['meters']['estimated_diameter_min'],
                datum['estimated_diameter']['meters']['estimated_diameter_max'],
                closest_approach_km,
                closest_approach_date,
                closest_approach_v_kps,
                datum['orbital_data']['first_observation_date'] if datum['orbital_data'] is not None else None,
                datum['orbital_data']['last_observation_date'] if datum['orbital_data'] is not None else None,
                datum['orbital_data']['observations_used'] if datum['orbital_data'] is not None else None,
                datum['orbital_data']['orbital_period'] if datum['orbital_data'] is not None else None
                ]

        cleaned.append(element)

    cleaned = list_of_lists_to_df(cleaned)
#    cleaned.to_csv(f'./neo/{today}/data/clean.csv')
    cleaned.to_parquet(f'./neo/{today}/data/clean.parquet.gzip', compression='gzip')

    print('done cleaning data')
