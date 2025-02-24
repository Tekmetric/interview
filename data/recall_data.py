import json
import os

import pyspark.sql.functions as F
import uvloop
from pyspark.sql import SparkSession

from aggregations import (
    agg_approaches_by_year,
    agg_closest_approach,
    agg_near_misses,
)
from schemas import NEOWS_SCHEMA
from scraper import scrape_urls

API_KEY = os.getenv("API_KEY")
BASE_URL = "https://api.nasa.gov/neo/rest/v1/neo/browse?page={page}&size={page_size}&api_key={api_key}"
PAGE_SIZE = 20
NUM_OBJECTS = 200
JSON_DIR = "json_data/"


def main():
    # Part 1: Scrape NeoWs API. Build list of URLs to query. Save paginated results as JSON.
    urls = [
        BASE_URL.format(api_key=API_KEY, page_size=PAGE_SIZE, page=i)
        for i in range(NUM_OBJECTS // PAGE_SIZE)
    ]
    result = uvloop.run(scrape_urls(urls))
    os.makedirs(os.path.dirname(JSON_DIR), exist_ok=True)

    for page in result:
        page_num = page["page"]["number"]
        del page["links"]  # Remove 'links' attribute that includes API Key.
        with open(f"{JSON_DIR}/page_{page_num}_size_{PAGE_SIZE}.json", "w") as f:
            json.dump(page["near_earth_objects"], f)

    # Part 2: Calculate closest approach for each object. Save as parquet.
    spark = SparkSession.Builder().getOrCreate()
    df = spark.read.json(JSON_DIR, multiLine=True, schema=NEOWS_SCHEMA)

    closest_approach_df = agg_closest_approach(df)
    closest_approach_df.write.mode("overwrite").parquet("parquet/closest_approach")

    # Part 3: Aggregations. Save as parquet.
    approaches_df = df.select(F.explode("close_approach_data").alias("tmp")).select(
        "tmp.*"
    )

    # Misses within .2 AU.
    miss_df = agg_near_misses(approaches_df)
    miss_df.write.mode("overwrite").parquet("parquet/near_misses")

    # Number of approaches by year.
    year_df = agg_approaches_by_year(approaches_df)
    year_df.write.mode("overwrite").parquet("parquet/approaches_by_year")


if __name__ == "__main__":
    main()
