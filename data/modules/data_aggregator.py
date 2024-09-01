from pyspark.sql import SparkSession
import os

class NEODataAggregator:
    def __init__(self, source_parquet_path):
        self.spark = SparkSession.builder \
            .appName("NEODataAggregator") \
            .getOrCreate()
        self.source_parquet_path = source_parquet_path 
        self.df = self.spark.read.parquet(self.source_parquet_path)
        # create a spark view for aggregation 
        self.df.createOrReplaceTempView("view")

    def process_and_save_aggregations(self, sql_query_file_path, target_file_path):
        with open(sql_query_file_path, 'r') as file:
            sql_query = file.read()
        df = self.spark.sql(sql_query)
        df.printSchema()
        print(df.count())
        df.write.mode("overwrite").parquet(target_file_path)

    def stop(self):
        self.spark.stop()