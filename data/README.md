# Python Coding Exercise

Your task is to build a python script to gather data from NASA's Near Earth Object Web Service API, and save that data. We'll also perform some aggregations to make reporting on Near Earth Objects simpler for our theoretical website.

The page for the API is here: https://api.nasa.gov

To save our data, we'll write it out to the local filesystem as if we're saving it to an S3 Data Lake. This will save having to mess with AWS credentials. Your files should be saved in the same data directory in which this README resides, in whatever folder structure you would use to save the data in S3.

### Requirements
- Create an account at [api.nasa.gov](https://api.nasa.gov) to get an API key
- Find the docs for the Near Earth Object Web Service (below the signup on the same page)
- Data should be saved in Parquet format
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

### Submitting your coding exercise
Once you have finished your script, please create a PR into Tekmetric/interview. Don't forget to update the gitignore if that is required!

## in the data folder I created 4 folders :
file : to save all the json and parquet files 
jobs : all the python scripts to run in the pipeline or airflow 
modules : class and modules to use for the python scripts 
sql : all the sql queries for spark to run 

## I have an .env in jobs folder , if you need to run this code you can add .env file in jobs folder and the file should looks like this 
NASA_API_KEY=replace_to_your_api_key
NASA_NEO_URL=https://api.nasa.gov/neo/rest/v1/neo/browse
SQL_ALL_DATA=../sql/all_data.sql
SQL_CLOSEST_DATA=../sql/closest_data.sql
CLOSER_APPROACH_SQL=../sql/closer_approach.sql
APPROACH_COUNT_YEAR_SQL=../sql/approach_count_year.sql
JSON_FILE_NAME=../file/neo_data.json
PARQUET_FILE_PATH_FLAT=../file/neo_data_flat.parquet
CLOSEST_DATA_PARQUET_PATH=../file/neo_data_closest.parquet
CLOSER_APPROACH_PARQUET_PATH=../file/closer_approach.parquet
APPROACH_COUNT_YEAR_PARQUET_PATH=../file/approach_count_year.parquet