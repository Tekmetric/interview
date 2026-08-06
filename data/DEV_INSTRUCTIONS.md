# DEV_INSTRUCTIONS

## Setup

### Requirements
- Python >=3.11
- [uv](https://docs.astral.sh/uv/) for dependency management
- Java 17 — required for the Spark variant only (see below)

### Install uv
```bash
curl -LsSf https://astral.sh/uv/install.sh | sh
```
Then restart your shell or run `source $HOME/.local/bin/env`. Verify with `uv --version`.

### Install dependencies
```bash
make install
```

### Configure API key
Get a free API key at [https://api.nasa.gov](https://api.nasa.gov) (takes 30 seconds — provide your email and a key is issued immediately).

Create a `.env` file in this directory and paste your key:
```
NASA_API_KEY=your_key_here
```
Leave the value blank or omit the file entirely to fall back to `DEMO_KEY` (see Known Quirks).

### Java 17 (Spark variant only)
Install OpenJDK 17 and set it as the system default:
```bash
sudo apt-get install -y openjdk-17-jdk
sudo update-alternatives --set java /usr/lib/jvm/java-17-openjdk-amd64/bin/java
```
Not required for `recall_data_dev.py`.

---

## Running

| Command | What it does |
|---------|-------------|
| `make run` | Spark variant, 200 records |
| `make run-dev` | Polars dev variant, 200 records |
| `make run COUNT=N` | Spark variant, N records |
| `make run-dev COUNT=N` | Polars dev variant, N records |
| `make clean` | Remove `.venv/` and `neo/` output |

---

## Choosing a variant

| | `recall_data_dev.py` (Polars) | `recall_data.py` (Spark) |
|-|-------------------------------|--------------------------|
| Use case | Local development | Production / cluster |
| JVM required | No | Yes (Java 17) |
| Startup time | <1s | 5–30s |
| Output format | Single Parquet files | Partitioned Parquet directories |
| Partitioning | None | By `year` and `month` of ingest date |

Both variants share the same ingestion logic via `neo_ingest.py`.

---

## Environment variables

| Variable | Default | Purpose |
|----------|---------|---------|
| `NASA_API_KEY` | `DEMO_KEY` | NASA NeoWs API key. Blank or unset falls back to DEMO_KEY. |
| `NEO_COUNT` | `200` | Default record count. Overridden by `--count` CLI arg. |
| `SPARK_MASTER` | `local[*]` | Spark master URL. Override for cluster: `yarn`, `spark://host:7077`. |

---

## Key constants (`neo_ingest.py`)

| Constant | Value | Purpose |
|----------|-------|---------|
| `PAGE_SIZE` | `20` | Records(NEOs) per API page. |
| `ASYNC_SEMAPHORE` | `10` | Max concurrent async requests. Also controls batch size: `ASYNC_SEMAPHORE × PAGE_SIZE` records fetched per batch. Tuning one affects the other. |
| `DEMO_KEY_MAX_RECORDS` | `5` | Record cap when using DEMO_KEY. |
| `ROWS_PER_PARTITION` | `250_000` | Spark only. Target rows per Parquet partition. |

---

## Output structure

**Polars variant** — single files per output:
```
neo/
  raw/
    neo_data.parquet
  aggregations/
    close_approaches_under_0_2_au.parquet
    close_approaches_per_year.parquet
```

**Spark variant** — partitioned directories:
```
neo/
  raw/
    neo_data.parquet/
      year=YYYY/month=MM/part-*.parquet
  aggregations/
    close_approaches_under_0_2_au.parquet/
      part-*.parquet
    close_approaches_per_year.parquet/
      part-*.parquet
```

---

## Known quirks

**Rate limit headers are inconsistent**
NASA's CDN returns `X-RateLimit-Remaining` from distributed edge nodes that sync slightly out of step. The remaining count may appear to go up and down between requests — this is expected behavior from their infrastructure, not a bug in the code.

**DEMO_KEY triggers on blank or missing key**
Both `NASA_API_KEY=` (empty string) and an unset variable fall back to `DEMO_KEY`. A warning is printed and the record count is capped at `DEMO_KEY_MAX_RECORDS` (5). NASA's DEMO_KEY is rate limited to 30 requests/hour.

**Async batch size is coupled to semaphore**
`ASYNC_SEMAPHORE` controls both the concurrency limit and the number of pages dispatched per batch. Each batch fetches `ASYNC_SEMAPHORE × PAGE_SIZE` records before checking whether the target count has been reached. Increasing the semaphore increases both parallelism and overshoot.

**Spark and Polars outputs conflict**
Both variants write to the same `neo/` paths but in different formats — Spark writes directories, Polars writes single files. Running both back-to-back without `make clean` will corrupt the output.

**No request retry**
A single failed API call raises immediately. Transient network errors will abort the run.
