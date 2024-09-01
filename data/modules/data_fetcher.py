from pyspark.sql import SparkSession
from pyspark.sql.functions import col, explode
import requests
import json
import os
from dotenv import load_dotenv
load_dotenv()

class NEODataFetcher:
    def __init__(self):
        self.api_key = os.getenv('NASA_API_KEY')
        self.base_url = os.getenv('NASA_NEO_URL')
        self.spark = SparkSession.builder \
            .appName("NEODataProcessor") \
            .getOrCreate()
        
    def fetch_data(self, num_pages, raw_json_file_path):
        # make api call for n pages 
        all_data = []
        for page_number in range(num_pages) :
            url = f"{self.base_url}?api_key={self.api_key}&page={page_number}&size=20"
            response = requests.get(url)
            data = response.json()
            near_earth_objects = data.get('near_earth_objects', [])
            all_data.extend(near_earth_objects)
        # Save data to JSON file
        print(f"There are {len(all_data)} objects fetched")
        with open(raw_json_file_path, 'w') as f:
            json.dump(all_data, f)
        print(f"Data from {num_pages} pages saved to {raw_json_file_path}")

    def flatten_json(self,sql_query_path, raw_json_file_path, target_file_path):
        # read data from json 
        df = self.spark.read.json(raw_json_file_path)
        print(f"Initial DataFrame Row Count: {df.count()}")
        # flatten data 
        exploded_df = df.withColumn("approach_data", explode(col("close_approach_data"))) 
        exploded_df.createOrReplaceTempView("view")
        # data cleaning 
        with open(sql_query_path, 'r') as file:
            sql_query = file.read()
        processed_df = self.spark.sql(sql_query)
        processed_df.printSchema()
        print(f"Row Count After Flattening: {processed_df.count()}")
        # write data to parquet
        processed_df.write.mode("overwrite").parquet(target_file_path)

    def stop(self):
        self.spark.stop()