# Python Coding Exercise

Your task is to build a python script to gather data from NASA's Near Earth Object Web Service API, and save that data. We'll also perform some aggregations to make reporting on Near Earth Objects simpler for our theoretical website.

The page for the API is here: https://api.nasa.gov

To save our data, we'll write it out to the local filesystem as if we're saving it to an S3 Data Lake. This will save having to mess with AWS credentials. Your files should be saved in the same data directory in which this README resides, in whatever folder structure you would use to save the data in S3.

### Requirements
- Create an account at [api.nasa.gov](https://api.nasa.gov) to get an API key
- Find the docs for the Near Earth Object Web Service (below the signup on the same page)
- Data should be saved in Parquet format
- Design the code such that the scraping and processing part could easily be scaled up to tens of GBs of data but it can also be easily run 
locally at development scale. 
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


## **Solution Overview**

This project implements NASA NEO data processing requirements using PySpark which should accomodate any amount of data in the future and should be able to easily scale up. Cloud providers such as AWS and Azure both provide Spark support. Additionally, the current script, even though it uses Spark, it can run locally to for testing/developing purposes or on a single machine in production. The solution is by no means perfect and there are several potential improvements that can be done. 

As an overview, the current implementation fetches data from the API in a distributed manner (i.e. the script starts a Spark app which instantiates several executors). Each of those executors is instructed what pages to request from the API and how to process those pages. When fetching small amounts of data, the overhead of managing an entire SPark context, multiple partitions and executors communication is actually not worth it. However, it pays off when the app is deployed in production and does ETL on big amounts of data. In that scenario, additional optimizations that Spark performs under the hood will be valuable. (query optimization, data shuffling optimization, etc). 

I stored all the data on the local filesystem under a path that partitions the data by year, month and day (extracted from the date of the execution). This is not necessarily the best partitioning, but in the absence of other informations on what will that data be used for, this works. We can discuss more around this during our itnerview. 

Monitoring should be implemented, other than Spark's default job monitoring (CPU, memory, networking). We should monitor data, publish counters, error counts and so on. 


## **Quick Start**

### Setup Environment
```bash
# Create virtual environment
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Configure NASA API key (get free key at https://api.nasa.gov)
echo "NASA_API_KEY=your_nasa_api_key_here" > .env
# For testing: echo "NASA_API_KEY=DEMO_KEY" > .env
```

### Run the Processor

```bash
source venv/bin/activate
./venv/bin/python neo_data_processor.py
```

## **Module Architecture**


```
src/
├── config.py          # Configuration management
├── models.py          # Data models + custom exceptions
├── api_client.py      # Distributed NASA API client using Spark
├── data_processor.py  # Distributed data processing using Spark
├── storage.py         # Data lake operations
├── neo_processor.py   # Distributed pipeline orchestrator
└── __init__.py
neo_data_processor.py   # Main entry point
```



## **Output Structure**

S3-like data lake structure with year/month/day-based partitioning:

```
data/
├── raw/neo/year=2025/month=7/day=30
│   ├── neo_raw_data.json          # JSON backup
│   └── neo_raw_data.parquet       # Raw Parquet data
└── aggregations/neo/year=2025/month=7/day=30
    ├── neo_aggregations.json      # Summary statistics
    └── approaches_by_year.parquet  # Yearly approach counts
```

## **Sample Results**

The pipeline processes NEO data and calculates:

1. **Close Approaches < 0.2 AU**: Total count of approaches closer than 0.2 astronomical units
2. **Approaches by Year**: Number of close approaches recorded in each year

Example output:
```json
{
  "close_approaches_under_02_au": 35,
  "approaches_by_year": {"2025": 35},
  "total_objects_processed": 35,
  "calculation_timestamp": "2025-07-29T23:34:21.525473"
}
```

## **Scalability Features**

### **High-Performance Distributed Processing**
**Spark-powered architecture that eliminates single-threaded bottlenecks:** (and creates other issues, similar to any respectable distributed system :D )
- **Distributed API Calls**: NASA API requests parallelized across Spark workers 
- **Distributed Data Processing**: All operations use Spark DataFrames
- **Distributed Joins**: High-performance distributed joins and aggregations
- **Performance Monitoring**: Real-time speed metrics
- **Overhead penalty on local runs**: Given that on local runs this might be executed with very little amount of input data, it would clearly be faster to just use sequential fetching. I am aware of this limitation, I can explain it better during our interview. 

**Performance Benefits:**
- Linear scalability with Spark cluster size
- Adaptive query execution enabled and partition coallesece, which should improve performance (benchmark required to be more precise)

### **Local Development:**
- Optimized Spark configuration for single machine
- Handles datasets up to several GB efficiently
- Adaptive query execution for performance
- Distributed processing even on single machine

### **Production Scale:**
- Easy deployment to Spark clusters (EMR, Databricks, etc.)
- Date-based partitioning for efficient querying
- Snappy compression for optimal storage/performance
- Horizontal scaling across multiple nodes

Example production configuration:
```python
spark = SparkSession.builder \
    .appName("NASA_NEO_Processor") \
    .config("spark.executor.memory", "4g") \
    .config("spark.executor.cores", "4") \
    .config("spark.executor.instances", "10") \
    .getOrCreate()
```

## **API Integration**

**NASA APIs Used:**
1. **Close Approach Data API** - NEO close approach records
2. **Small Body Database API** - Detailed object information

**Features:**
- Rate limiting for fair use compliance
- Comprehensive error handling and retry logic
- Efficient data extraction and transformation

## **Monitoring**

- **Spark UI**: Real-time job monitoring at `http://localhost:4040`
- **Data quality counters**: Counters describing the data published at every stage. Currently they are only printed to console. Given a monitoring solution, these logs and other metrics should be published/exported into those systems. 

## **Technical Stack**

- **Python 3.8+** 
- **PySpark 3.5.0** for distributed processing
- **pandas** for data manipulation
- **PyArrow** for efficient Parquet operations
- **Jupyter** with visualization libraries


## **Development Workflow**

```bash
# Quick setup and test
python -m venv venv && source venv/bin/activate
pip install -r requirements.txt
echo "NASA_API_KEY=DEMO_KEY" > .env
./venv/bin/python neo_data_processor.py
```
