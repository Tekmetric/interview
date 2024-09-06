import requests
import datetime
from pyspark.sql import SparkSession
from pyspark.sql.functions import *

# Create Spark Session
spark = (SparkSession
         .builder
         .master("local")
         .appName("NASA")
         .getOrCreate()
         )

data_base_path = 'tekmetric/nasa/ingestion'
ingestion_data_path = f'{data_base_path}/near_earth_objects/date={datetime.datetime.today().strftime("%Y-%m-%d")}/'

'''
    Data Ingestion
'''

def ingestion():
    near_earth_objects = []

    def get_json_field(data_dict, keys):
        """ Retrieve a nested value from a dictionary using a list of keys. """
        for key in keys:
            try:
                data_dict = data_dict[key]
            except KeyError:
                return None  # Return None if the key is not found
        return data_dict

    def get_data_from_api(url):
        try:
            response = requests.get(url)
            response.raise_for_status()  # Raise an exception for unsuccessful HTTP status codes
            data = response.json()
            return data
        except requests.exceptions.RequestException as e:
            raise Exception('An error occurred while making the API request:', e)
        except ValueError:
            raise Exception("Error parsing the JSON response")

    link = None

    while len(near_earth_objects) < 200:
        if link is None:
            link = 'https://api.nasa.gov/neo/rest/v1/neo/browse?api_key=API_KEY'
        else:
            link = get_data_from_api(link).get('links').get('next')

        json_result = get_data_from_api(link)

        if 'near_earth_objects' in json_result:
            for neo in json_result['near_earth_objects']:
                near_earth_object = {
                    'id': get_json_field(neo, ['id']),
                    'neo_reference_id': get_json_field(neo, ['neo_reference_id']),
                    'name': get_json_field(neo, ['name']),
                    'name_limited': get_json_field(neo, ['name_limited']),
                    'designation': get_json_field(neo, ['designation']),
                    'nasa_jpl_url': get_json_field(neo, ['nasa_jpl_url']),
                    'absolute_magnitude_h': get_json_field(neo, ['absolute_magnitude_h']),
                    'is_potentially_hazardous_asteroid': get_json_field(neo, ['is_potentially_hazardous_asteroid']),
                    'minimum_estimated_diameter_in_meters': get_json_field(neo, ['estimated_diameter', 'meters',
                                                                                 'estimated_diameter_min']),
                    'maximum_estimated_diameter_in_meters': get_json_field(neo, ['estimated_diameter', 'meters',
                                                                                 'estimated_diameter_max']),
                    'closest_approach_miss_distance_in_astronomical': [get_json_field(x, ['miss_distance',
                                                                                          'astronomical'])
                                                                     for x in get_json_field(neo,
                                                                                             ['close_approach_data'])],
                    'closest_approach_miss_distance_in_kilometers': [
                        get_json_field(x, ['miss_distance', 'kilometers'])
                        for x in get_json_field(neo, ['close_approach_data'])],
                    'closest_approach_date': [get_json_field(x, ['close_approach_date'])
                        for x in get_json_field(neo, ['close_approach_data'])],
                    'closest_approach_relative_velocity_in_kilometers_per_second': [
                        get_json_field(x, ['relative_velocity', 'kilometers_per_second'])
                        for x in get_json_field(neo, ['close_approach_data'])],
                    'first_observation_date': get_json_field(neo, ['orbital_data', 'first_observation_date']),
                    'last_observation_date': get_json_field(neo, ['orbital_data', 'last_observation_date']),
                    'observations_used': get_json_field(neo, ['orbital_data', 'observations_used']),
                    'orbital_period': get_json_field(neo, ['orbital_data', 'orbital_period']),
                }

                near_earth_objects.append(near_earth_object)

    df = spark.createDataFrame(near_earth_objects[:200])

    df.coalesce(1).write.mode('overwrite').parquet(ingestion_data_path)


'''
    Transformation
'''

def transformation():
    # Aggregation one

    agg_data_path = f'{data_base_path}/aggregated/date={datetime.datetime.today().strftime("%Y-%m-%d")}'

    df = spark.read.parquet(ingestion_data_path)

    filtered_closest_approach_df = df.withColumn('closest_approach_miss_distance_in_astronomical_filtered',
                                                 filter(col('closest_approach_miss_distance_in_astronomical'),
                                                        lambda x: x < 0.2))

    # The total number of times our 200 near earth objects approached closer than 0.2 astronomical units (per object)
    filtered_closest_approach_df  = filtered_closest_approach_df.select('id',
                                        size('closest_approach_miss_distance_in_astronomical_filtered')
                                                                        .alias('agg_one_per_id'))

    # The total number of times our 200 near earth objects approached closer than 0.2 astronomical units (total)
    agg_one_total = filtered_closest_approach_df.select(sum('agg_one_per_id').alias('agg_one_total'))

    # Write to local in parquet
    agg_one_total.coalesce(1).write.mode('overwrite').parquet(f'{agg_data_path}/one')


    # Aggregation two

    exploded_df = (df
        .select(arrays_zip('closest_approach_date', 'closest_approach_miss_distance_in_astronomical')
                .alias('zipped'))
        .withColumn('exploded', explode('zipped'))
        .select('exploded.closest_approach_date', 'exploded.closest_approach_miss_distance_in_astronomical')
    )

    agg_two = exploded_df.groupby(substring('closest_approach_date', 0, 4).alias('year')).agg(count("*")
        .alias('close_approaches_per_year'))

    agg_two.coalesce(1).write.mode('overwrite').parquet(f'{agg_data_path}/two')

if __name__ == '__main__':
    ingestion()
    transformation()
