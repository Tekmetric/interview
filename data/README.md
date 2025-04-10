# Python Coding Exercise

Your task is to build a python script to gather data from NASA's Near Earth Object Web Service API, and save that data. We'll also perform some aggregations to make reporting on Near Earth Objects simpler for our theoretical website.

The page for the API is here: https://api.nasa.gov

To save our data, we'll write it out to the local filesystem as if we're saving it to an S3 Data Lake. This will save having to mess with AWS credentials. Your files should be saved in the same data directory in which this README resides, in whatever folder structure you would use to save the data in S3.

### Requirements
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

### Submitting your coding exercise
Once you have finished your script, please create a PR into Tekmetric/interview. Don't forget to update the gitignore if that is required!

---

### Features

- Fetch NEO data from NASA's API
- Parse and store it as partitioned Parquet files
- Easily swappable components (for data retrieval, processing and storage)
    - Ingesters: currently using an mechanism of retrieving data synchronously, in a single batch, but can be easily swapped with a more performant way by leveraging the `DataIngesterBase` class
    - Handlers: currently using Pandas as an engine, but can be easily swapped with other solutions (Spark, Polars, etc.) for better performance by leveraging the `DataHandlerBase` class
    - Storage: currently storing locally, without any data partitioning, but can be easily extended to use a more idempotent way for storing multiple results, or another solution (such as S3) by leveraging the `BaseStorage` class

### Requirements

- Python 3.8+
- Poetry (for dependency management)
If you don't have Poetry installed, you can install it using the following command:
```bash
  pip install poetry
```
To set up the project environment, run:
```bash
  poetry install
```

### Usage

To run the pipeline using synchronous data fetching (default):

```bash
poetry run python recall_data.py
```

### Folder Structure

```text
├── data/                         
    ├── neo/                      # Core project code
    │   ├── ingesters/            # Data processing logic
    │   ├── handlers/             # Data handling utilities
    │   ├── storage/              # Storage-related functionality
    │   ├── tests/                # Unit tests for the project
    ├── files/                    # Local storage (in place of S3)
    ├── pyproject.toml            # Poetry configuration file
    ├── config.py                 # Lightweight config file
    ├── recall_data.py            # Main script to start data gathering and aggregating
```

### Testing

To run the tests, simply execute:

```bash
poetry run pytest
```
    