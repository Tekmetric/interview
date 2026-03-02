# NASA Near Earth Object Data Pipeline

A Python ELT pipeline that fetches Near Earth Object data from [NASA's NeoWs API](https://api.nasa.gov/), stores it in Parquet using a Medallion Architecture layout, and provides a [Streamlit](https://streamlit.io/) dashboard to explore the results.

## Setup

### Prerequisites

- [uv](https://docs.astral.sh/uv/getting-started/installation/) (Python package manager)
- A NASA API key (get one free at https://api.nasa.gov/)

### Installation

```bash
git clone <repo-url>
cd nasa-neows
uv sync
```

`uv sync` creates a Python 3.11+ virtual environment, installs all dependencies from `uv.lock`, and installs the project as an editable package (needed for the `nasa-neows` CLI entry point defined in `pyproject.toml`).

### Configuration

Copy the example env file and add your API key:

```bash
cp .env.example .env
```

Edit `.env` and replace the placeholder with your key:

```
NASA_API_KEY=your_api_key_here
```

## Usage

### Run the pipeline

```bash
uv run nasa-neows
```

Fetches 200 NEOs (this is per the instructions for the interview) from the NASA NeoWs Browse API and writes Parquet files to the `data/` directory.

### View the dashboard

```bash
uv run streamlit run src/nasa_neows/dashboard.py
```

Opens a browser with the total count of approaches under 0.2 AU (astronomical units), a bar chart of close approaches by year, and an interactive table of all close approach data of the 200 NEOs.


## Project Structure

### Repository

```
nasa-neows/
├── src/
│   └── nasa_neows/
│       ├── config.py          # Settings and data directory paths
│       ├── models.py          # Pydantic models for API response validation
│       ├── fetch.py           # API requests with pagination and retry
│       ├── load.py            # Loads raw API data into Parquet via DuckDB
│       ├── transform.py       # Flattens and aggregates raw data via SQL
│       ├── main.py            # Pipeline orchestration and entry point
│       ├── dashboard.py       # Streamlit dashboard for exploring results
│       └── sql/               # DuckDB SQL templates
│           ├── flatten_neos.sql
│           ├── close_approaches_under_0_2_au.sql
│           └── approaches_by_year.sql
├── data/                      # Output directory (gitignored)
├── pyproject.toml             # Project metadata and dependencies
├── uv.lock                    # Pinned dependency versions
├── .env.example               # Environment variable template
├── .gitignore
└── README.md
```

### Data output

Data is saved in a layout that mimics an S3 data lake following Medallion Architecture:

```
data/
├── raw/                            # Bronze — full API response
│   └── neo_browse/
│       └── neos.parquet
├── processed/                      # Silver — flattened 17-column dataset
│   └── neos/
│       └── neos.parquet
└── aggregations/                   # Gold — aggregated summaries
    ├── close_approaches_under_0_2_au.parquet
    └── approaches_by_year.parquet
```

## Data Reference

### API data structure

The NeoWs Browse API returns nested JSON. Fields used by this pipeline:

```
near_earth_object
├── id
├── neo_reference_id
├── name
├── name_limited
├── designation
├── nasa_jpl_url
├── absolute_magnitude_h
├── is_potentially_hazardous_asteroid
├── estimated_diameter
│   └── meters
│       ├── estimated_diameter_min
│       └── estimated_diameter_max
├── close_approach_data[]
│   ├── close_approach_date
│   ├── relative_velocity
│   │   └── kilometers_per_second
│   └── miss_distance
│       ├── astronomical
│       └── kilometers
└── orbital_data
    ├── first_observation_date
    ├── last_observation_date
    ├── observations_used
    └── orbital_period
```
