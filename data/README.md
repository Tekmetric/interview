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

## Development Workflow

### Setup
```bash
pip install -r requirements.txt
export NASA_API_KEY=your_api_key_here
```

### Testing Before Submitting
```bash
# Run test suite
python3 -m pytest tests/ -v

# Run pipeline end-to-end
python3 -m src.main

# Verify aggregates
python3 -c "import pyarrow.parquet as pq; print(pq.read_table('s3/aggregates/neo/close_approaches/summary.parquet').to_pandas())"
```

## Running the Pipeline

### Setup
1. Install dependencies:
```bash
pip install -r requirements.txt
```

2. Set your NASA API key:
```bash
export NASA_API_KEY=your_api_key_here
```

### Execute Pipeline
Run the full pipeline (fetch, transform, aggregate):
```bash
python3 -m src.main
```

Or with custom data path:
```bash
export BASE_DATA_PATH=/path/to/data
python3 -m src.main
```

## Testing and Verification

### Run Test Suite
```bash
# Run all tests
python3 -m pytest tests/ -v

# Run specific test modules
python3 -m pytest tests/test_config.py -v
python3 -m pytest tests/test_api_client.py -v
python3 -m pytest tests/test_data_extractor.py -v
```

### Verify Output Data

After running the pipeline, verify the data was written correctly:

**Check data structure:**
```bash
find s3 -name "*.parquet" -type f
```

Expected output:
```
s3/raw/neo/year=YYYY/month=MM/day=DD/data.parquet
s3/curated/neo/year=YYYY/month=MM/day=DD/data.parquet (multiple partitions)
s3/aggregates/neo/close_approaches/summary.parquet
s3/aggregates/neo/approaches_by_year/summary.parquet
```

**Inspect close approaches aggregate:**
```bash
python3 -c "import pyarrow.parquet as pq; table = pq.read_table('s3/aggregates/neo/close_approaches/summary.parquet'); print(table.to_pandas())"
```

Expected output:
```
   count  threshold_au  threshold_km aggregation_date
0    171           0.2   29919574.14       YYYY-MM-DD
```

**Inspect approaches by year aggregate:**
```bash
python3 -c "import pyarrow.parquet as pq; table = pq.read_table('s3/aggregates/neo/approaches_by_year/summary.parquet'); print(table.to_pandas().head(20))"
```

Expected output:
```
    year  count aggregation_date
0   1902      1       YYYY-MM-DD
1   1904      1       YYYY-MM-DD
...
```

**Count total records in curated layer:**
```bash
python3 -c "import pyarrow.parquet as pq; dataset = pq.ParquetDataset('s3/curated/neo', use_legacy_dataset=False); print(f'Total records: {dataset.read().num_rows}')"
```

**Check for error records:**
```bash
find s3/error -name "*.parquet" -type f 2>/dev/null || echo "No error records found"
```
