# NASA NEO Data Pipeline - Design Decisions

This document covers the architectural decisions, assumptions, and trade-offs in the implementation. It also lists questions that would need answers from stakeholders before going to production.

## Current Implementation

The pipeline uses a three-layer data lake structure:

```
data/s3/
├── raw/neo/year=YYYY/month=MM/day=DD/data.parquet          # Ingestion date partitioning
├── curated/neo/year=YYYY/month=MM/day=DD/data.parquet      # Approach date partitioning
├── error/neo/year=YYYY/month=MM/day=DD/data.parquet        # Ingestion date partitioning
└── aggregates/neo/
    ├── close_approaches/summary.parquet                     # Single file, no partitioning
    └── approaches_by_year/summary.parquet                   # Single file, no partitioning
```

The implementation uses streaming to keep memory usage constant regardless of data size, explicit schemas to avoid PyArrow type inference problems, and separate error storage for failed records. The code is split into 11 modules with clear responsibilities.

## Partitioning Strategy

The raw and error layers partition by ingestion date (when we pulled the data). The curated layer partitions by approach date (when the NEO passes Earth). This assumes:

1. This is a one-time snapshot, not an ongoing pipeline
2. NEO data doesn't change after we ingest it
3. Most queries will filter by approach date ("show me 2029 approaches")
4. Each NEO appears once (no deduplication needed)

This works well for time-based queries since the database can skip irrelevant partitions. But looking up a specific NEO by ID requires scanning all partitions. And if NASA ever updates the data, we could end up with duplicates that inflate the aggregates.

## The Deduplication Problem

If NASA updates NEO trajectory data, we could end up with the same asteroid in multiple partitions:

```
Day 1:  Apophis approach_date = 2029-04-13
        → Stored in: data/s3/curated/neo/year=2029/month=04/day=13/

Day 30: NASA updates trajectory, approach_date = 2029-04-14
        → Stored in: data/s3/curated/neo/year=2029/month=04/day=14/

Result: Two records for the same NEO
```

This breaks the aggregates since they'd count Apophis twice. There's no way to tell which record is current.

Some ways to handle this:

**Partition by ingestion date instead of approach date**
```
data/s3/curated/neo/ingestion_year=2026/ingestion_month=02/ingestion_day=19/data.parquet
```
Add an `is_current` flag and filter on it when aggregating. This gives you a time-series of snapshots and full history, but approach date queries are slower since you can't use partition pruning.

**Partition by NEO ID**
```
data/s3/curated/neo/neo_id=2001916/data.parquet
data/s3/curated/neo/neo_id=2001980/data.parquet
```
One file per NEO means natural deduplication, but you can't efficiently query by approach date and you end up with 30,000+ small files.

**Slowly Changing Dimension (Type 2)**
Add `valid_from`, `valid_to`, and `is_current` columns. You get full history plus current state, but queries get more complex and storage grows.

**Hybrid approach**
```
data/s3/curated/neo/current/data.parquet           # Latest version only
data/s3/curated/neo/history/ingestion_date=.../    # All versions
```
Fast queries on current data with a full audit trail, but you have to write to two places.

## Aggregate Storage

The aggregates are stored as single unpartitioned files since they're already summarized data:

```
data/s3/aggregates/neo/close_approaches/summary.parquet      # Single record: count, threshold, date
data/s3/aggregates/neo/approaches_by_year/summary.parquet    # Multiple records: year, count, date
```

For tens of GBs of raw data, the aggregates would still be tiny (few KB). Partitioning them would add unnecessary complexity and overhead. If we needed time-series tracking of how aggregates change over time, we'd partition by aggregation date, but the requirements only ask for storing the computed values.

## Error Handling

Records with missing approach dates go to:

```
data/s3/error/neo/year=2026/month=02/day=19/data.parquet
```

Each error record includes `_error_reason` and `_error_details` fields. This lets you set up CloudWatch alerts if the error rate crosses a threshold, and you can manually review or reprocess the bad data later. The downside is added complexity - you need a separate process to handle these records.

## Questions for Stakeholders

Before going to production, these questions need answers:

1. How will this data be consumed? Real-time dashboards, batch analytics (Spark/Athena), ad-hoc SQL queries, or ML models?

2. What are the primary query patterns? By approach date ("show me all approaches in 2029"), by NEO ID ("show me history for Apophis"), time-series ("how have predictions changed"), or aggregations ("count approaches by year")?

3. What are the SLAs for query latency, data freshness, and availability?

4. How often does NASA update NEO data? Daily full refresh, weekly updates, or only when trajectories change?

5. Do we need to track how predictions evolve over time, or just maintain the latest state?

6. What triggers a data refresh? Scheduled cron job, event-driven when NASA publishes updates, or manual trigger?

7. What's the expected data growth? Currently 200 NEOs for the interview, but production would be all 30,000+ known NEOs, and potentially real-time tracking of new discoveries.

8. What's the expected query volume in terms of concurrent users and queries per second?

9. How should we handle duplicate NEOs? Keep all versions (SCD Type 2), keep only latest (overwrite), or flag duplicates for review?

10. What defines a duplicate? Same neo_id, same neo_id + approach_date, or same neo_id + ingestion_date?

11. For aggregations, which records should count? Latest version only, all historical versions, or point-in-time snapshot?

## Alternative Architectures Considered

**Single Parquet File**

Just dump everything into `data/s3/nasa_neo_data.parquet`. Simplest implementation, easy to query, but doesn't scale beyond a few GBs and has no data lineage. Doesn't meet the "scale to tens of GBs" requirement.

**Partition by NEO ID Only**

One file per NEO like `data/s3/neo_id=2001916/data.parquet`. Natural deduplication and fast NEO lookups, but you get 30,000+ small files, slow time-based queries, and high S3 API costs. Wrong access pattern for time-based queries.

**Delta Lake / Iceberg**

Use a table format like Delta or Iceberg to manage the data. You get ACID transactions, time travel queries, automatic compaction, and schema evolution. Great for production, but adds dependencies and complexity that's overkill for an interview.

## Performance Characteristics

* Memory usage stays constant at around 10-50 MB regardless of data size since we stream in batches of 1000 records. This scales to 100x the data (20,000 NEOs) without memory issues.

* Query performance depends on the query type. Approach date ranges are fast because of partition pruning - querying "2029 approaches" only scans 2029 partitions. NEO ID lookups are slow since they require a full table scan. Aggregates are fast since they're pre-computed.

* Parquet compression gives about 10x savings vs JSON, and partitioning reduces query scan size. Storing errors separately prevents schema conflicts.

## Testing Strategy

* Current test coverage includes unit tests for configuration (25 tests) and API client (30 tests), plus property tests for API pagination (3 tests), API error handling (5 tests), and data extraction (4 tests).

* Still need to implement integration tests for the full pipeline, property tests for data transformation, storage manager, and aggregation engine, and end-to-end tests with the real API.

* For production, you'd also want load testing at 10x and 100x data volumes, failure injection (API errors, disk full, network issues), data quality validation, and performance benchmarking.

## Schema Definitions

Raw layer:
```python
id: string
neo_reference_id: string
name: string
name_limited: string
designation: string
nasa_jpl_url: string
absolute_magnitude_h: float64
is_potentially_hazardous: bool
diameter_min_meters: float64
diameter_max_meters: float64
closest_approach_date: string
closest_miss_distance_km: float64
closest_relative_velocity_kms: float64
first_observation_date: string
last_observation_date: string
observations_used: int64
orbital_period_days: float64
```

Curated layer adds:
```python
ingestion_year: int64
ingestion_month: int64
ingestion_day: int64
approach_year: int64
approach_month: int64
approach_day: int64
```

Error layer adds:
```python
_error_reason: string
_error_details: string
```

Aggregates:

close_approaches:
```python
count: int64
threshold_au: float64
threshold_km: float64
aggregation_date: string
```

approaches_by_year:
```python
year: int64
count: int64
aggregation_date: string
```
