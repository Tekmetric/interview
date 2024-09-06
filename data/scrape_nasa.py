import requests
import json
from datetime import date, timedelta

# globals:
columns = [
    'id',
    'neo_reference_id',
    'name',
    'name_limited',
    'designation',
    'nasa_jpl_url',
    'absolute_magnitude_h',
    'is_potentially_hazardous_asteroid',
    'estimated_diameter',
    'close_approach_data',
    'orbital_data'
]

def call_api(url):

    response = requests.get(url).json()

    prev = response['links']['previous']
    element_count = response['element_count']
    objects = response['near_earth_objects']


    #print(json.dumps(objects, indent=4))
    #print(response)

    return prev, element_count, objects


def append_objects(raw_json, data):
    for day in raw_json.keys():
        for element in raw_json[day]:
            if len(data) >= 200:
                break
#            print(element)
            obj = {}
            for column in columns:
                if column in element.keys():
                    obj[column] = element[column]  # Will need tests and bug fixes for if a column is not present
                else:
                    obj[column] = None
            data.append(obj)
        if len(data) >= 200:
            break


def fetch_data(start_date, end_date):
    api_key = 'IctxdqrqDpUTnLg6FifAkSncnbjHJbIwMTxTDEG9'
    url = f'https://api.nasa.gov/neo/rest/v1/feed?start_date={start_date}&end_date={end_date}&api_key={api_key}'

    data = []

    while len(data) < 200:
        prev, element_count, objects = call_api(url)
        url = prev
        print(f'elements on this page: {element_count}')
        append_objects(objects, data)

    print('done fetching data')
    return data


if __name__ == '__main__':
    start_date = date.today() - timedelta(days=7)  # API fetch limit is 7 days, we'll get 7 days at a time
    end_date = date.today()
    
    print(start_date) 
    print(end_date)
    
    fetch_data(start_date, end_date)
    