# NASA NEO Data Pipeline

This is my solution for the Tekmetric Senior Data Engineer exercise. The design follows a **medallion architecture**:

- **Bronze**: raw flattened datasets
- **Silver**: curated one-row-per-object dataset
- **Gold**: reporting aggregations

The pipeline reads from NASA's Near Earth Object Browse API, keeps a raw Bronze layer in parquet, builds a curated Silver dataset with one row per object, and writes the required Gold aggregations.

## What it does

- reads the first 200 Near Earth Objects from the Browse API
- stores raw flattened `neo_objects` and `close_approaches` data in Bronze
- builds a curated Silver `neo_objects` file with the required output columns
- builds one Gold parquet file with:
  - `total_close_approaches_lt_0_2_au`
  - `approaches_by_year`

## Why I structured it this way

One important clarification was that all `close_approach_data` records should be preserved from the initial pull because they are part of the raw dataset and may be useful later.

Because of that, I split Bronze into two raw datasets:

- `bronze/neo_objects`
- `bronze/close_approaches`

Silver is then built by joining the raw object data to the single closest close-approach row for each object.

## Data layout

```text
data/
├── bronze/
│   ├── neo_objects/
│   │   └── YYYY-MM-DD/
│   │       ├── page_000.parquet
│   │       └── ...
│   └── close_approaches/
│       └── YYYY-MM-DD/
│           ├── page_000.parquet
│           └── ...
├── silver/
│   └── neo_objects/
│       └── YYYY-MM-DD/
│           └── neo_objects.parquet
└── gold/
    └── aggregations/
        └── YYYY-MM-DD/
            └── aggregations.parquet
```

## Notes on implementation

- The pipeline uses the NASA Browse API.
- It fetches full pages until 200 objects are collected.
- It trims the final page so only the first 200 objects are processed.
- Bronze writes page-level parquet files to avoid building one large in-memory raw dataset.
- Silver writes one compact parquet file.
- Gold writes one compact parquet file with both required aggregations.
- Snapshot folders use UTC date in `YYYY-MM-DD` format.
- Existing output for the same date is overwritten to make reruns simple for this exercise.
- The pipeline fails fast if a required validation check fails.

## Required Silver output

The Silver dataset contains these fields:

- `id`
- `neo_reference_id`
- `name`
- `name_limited`
- `designation`
- `nasa_jpl_url`
- `absolute_magnitude_h`
- `is_potentially_hazardous_asteroid`
- `estimated_diameter_min_meters`
- `estimated_diameter_max_meters`
- `closest_approach_miss_distance_kilometers`
- `closest_approach_date`
- `closest_approach_relative_velocity_kilometers_per_second`
- `first_observation_date`
- `last_observation_date`
- `observations_used`
- `orbital_period`

## Validation built in

The pipeline includes simple fail-fast checks for all three layers.

- **Bronze**
  - row counts match the API payload
  - required columns exist
  - required key fields are not null
  - duplicate object IDs are rejected
- **Silver**
  - exact required columns exist
  - row count matches `OBJECT_LIMIT`
  - duplicate object IDs are rejected
  - key identity fields are not null
- **Gold**
  - total is an integer
  - yearly aggregation is a dictionary with string keys and integer values

## How to run

Create and activate a virtual environment, then install dependencies:

```bash
python -m venv .venv
source .venv/bin/activate
pip install -e ".[dev]"
```

Set your NASA API key:

```bash
export NASA_API_KEY=your_api_key_here
```

Run tests:

```bash
pytest -v
```


Run the pipeline:

```bash
python -m nasa_neo_pipeline.cli run
```


## Tests included

The tests cover:

- Bronze flattening
- Silver schema and closest-approach selection
- Gold aggregation logic
- object limit handling
- validation helpers

## Tradeoffs

For this exercise I kept the implementation simple and local-first:

- local filesystem instead of S3
- pandas instead of Spark
- overwrite-on-rerun for the same snapshot date

If I needed to scale it further, I would keep the same layered shape but move storage to object storage and use distributed processing for the larger transforms.
