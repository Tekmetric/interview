# NASA Near Earth Object Data Pipeline

A data pipeline for collecting, processing, and analyzing Near Earth Object (NEO) data from NASA's API.

## Overview

This project retrieves data about Near Earth Objects from NASA's Near Earth Object Web Service API, processes it, and saves it in a structured format along with useful aggregations. The pipeline focuses on collecting information about the first 200 NEOs from the Browse API endpoint.

## Features

- Fetches NEO data from NASA's API with rate limiting
- Transforms raw API data into clean, structured datasets
- Calculates useful aggregations such as close approaches and yearly statistics
- Saves data in Parquet format for efficient storage and querying
- Includes verification tools to validate pipeline output
- Comprehensive test suite for code reliability

## Setup

### Prerequisites

- Python 3.6+
- pip package manager

### Installation

1. Clone this repository
```
git clone <repository-url>
cd nasa-neo-pipeline
```

2. Create and activate a virtual environment (recommended)
```
python -m venv venv
source venv/bin/activate  # On Windows, use: venv\Scripts\activate
```

3. Install dependencies
```
pip install -r requirements.txt
```

4. Set up your NASA API key
   - Create a free API key at https://api.nasa.gov/
   - Either:
     - Create a `config.json` file in the root directory with the format: `{"api_key": "YOUR_API_KEY"}`
     - Or set an environment variable: `export NASA_API_KEY=YOUR_API_KEY`

## Usage

### Running the Pipeline

Run the main pipeline script:
```
python src/fetch_neo.py
```

This will:
1. Fetch data for up to 200 NEOs from NASA's API
2. Transform the data into a structured format
3. Calculate aggregations on the data
4. Save everything to Parquet files in the `data/` directory

### Verifying Output

To verify that the pipeline ran correctly:
```
python src/verify_neo_output.py
```

This script will check:
- The directory structure
- The existence of required output files
- Data quality and completeness in each file
- Aggregation results

### Running Tests

Run the test suite with pytest:
```
pytest tests/
```

Or run a specific test file:
```
pytest tests/test_fetch_neo.py
```

## Data Structure

The pipeline creates the following directory structure:

```
data/
├── neo/
│   ├── raw/
│   │   ├── neo_data_[timestamp].parquet    (Main NEO object data)
│   │   └── neo_approaches_[timestamp].parquet  (Close approach data)
│   └── aggregations/
│       └── neo_aggregations_[timestamp].parquet  (Aggregated statistics)
```

### Data Fields

#### NEO Data
Contains detailed information about each NEO:
- Basic identifiers (id, neo_reference_id, name, etc.)
- Size data (absolute_magnitude_h, diameter estimates)
- Hazard assessment (is_potentially_hazardous_asteroid)
- Closest approach information
- Observation history

#### Approaches Data
Contains detailed information about each close approach:
- NEO identifier
- Approach date
- Miss distance (in km and AU)
- Relative velocity
- Orbiting body

#### Aggregations
Contains summary statistics:
- Total NEOs collected
- Count of approaches closer than 0.2 AU
- Yearly approach statistics

## Extending the Pipeline

The pipeline is designed to be modular and extensible:

- The data fetching logic can be modified to retrieve more NEOs or different data
- The transformation functions can be adjusted to extract additional fields
- New aggregations can be added to calculate different statistics
- The storage layer can be replaced with a cloud-based solution like AWS S3

## Troubleshooting

- **API Rate Limits**: The script includes a delay between requests to respect NASA's API rate limits. If you encounter rate limit errors, try increasing the `RATE_LIMIT_DELAY` value.
- **Missing Files**: If the verification script reports missing files, check that the pipeline script completed successfully and that the correct directory structure exists.
- **API Key Issues**: Ensure your API key is correctly set in either the config file or environment variable.


## License

This project is licensed under the MIT License - see the LICENSE file for details.