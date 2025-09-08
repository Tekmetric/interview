# NASA NEO Data Scraper - Usage Guide

## Setup

### Quick Setup (Automated)
```bash
# On macOS/Linux:
./setup.sh

# On Windows:
setup.bat
```

### Manual Setup

1. **Create and activate virtual environment:**
   ```bash
   # Create virtual environment with Python 3.12.6
   python3.12 -m venv .venv
   
   # Activate virtual environment
   # On macOS/Linux:
   source .venv/bin/activate
   
   # On Windows:
   # .venv\Scripts\activate
   ```

2. **Install dependencies:**
   ```bash
   pip install --upgrade pip
   pip install -r requirements.txt
   ```

3. **Set up your NASA API key:**
   - Create a `.env` file in the data directory
   - Add your NASA API key: `NASA_API_KEY=your_actual_api_key_here`
   - Get your API key from [api.nasa.gov](https://api.nasa.gov)

## Usage

### Basic Usage
```bash
# Run with default settings (200 NEOs)
python nasa_neo_scraper.py

# Or use the convenience script
python run_scraper.py
```

### Advanced Usage
```bash
# Test with 5 NEOs
python run_scraper.py --test

# Fetch custom number of NEOs
python run_scraper.py --count 100

# Run test script
python test_scraper.py
```

## Output

The scraper creates an S3-like data lake structure:

```
s3_data_lake/
├── raw/
│   └── year=2024/
│       └── month=01/
│           └── neo_data.parquet
└── aggregated/
    ├── yearly_approaches.parquet
    └── summary_stats.parquet
```

## Data Schema

The main data file contains these columns:
- `id`, `neo_reference_id`, `name`, `name_limited`, `designation`
- `nasa_jpl_url`, `absolute_magnitude_h`, `is_potentially_hazardous_asteroid`
- `min_diameter_meters`, `max_diameter_meters`
- `closest_approach_distance_km`, `closest_approach_date`, `closest_approach_velocity_km_s`
- `first_observation_date`, `last_observation_date`, `observations_used`, `orbital_period`

## Aggregations

- **Close approaches under 0.2 AU**: Count of all approaches closer than 0.2 astronomical units
- **Yearly approach breakdown**: Number of close approaches per year

## Performance

- **Development scale**: Processes 200 NEOs in ~30-60 seconds
- **Production scale**: Designed to handle tens of GBs with batch processing
- **Memory efficient**: Streams data processing to avoid memory issues
- **Rate limited**: Respects NASA API rate limits with built-in delays

## Testing

### Run Tests
```bash
# Run all tests
python run_tests.py

# Run only unit tests
python run_tests.py --unit

# Run only integration tests
python run_tests.py --integration

# Run with coverage report
python run_tests.py --coverage

# Run specific test file
python run_tests.py --file test_api_client.py

# Run tests with pytest directly
pytest tests/ -v
```

### Test Coverage
The test suite provides comprehensive coverage including:
- **Unit Tests**: Individual module testing with mocked dependencies
- **Integration Tests**: End-to-end pipeline testing
- **Edge Cases**: Missing data, invalid types, API failures, retry exhaustion
- **Performance Tests**: Memory efficiency and large dataset handling
- **Error Recovery**: Partial failure and retry logic testing
- **100% Coverage**: Core modules (API client, data processor, file manager)
- **95% Overall Coverage**: 56 tests covering all critical code paths

## Documentation

- **[Technical Overview](OVERVIEW.md)**: Detailed technical architecture and implementation details
- **[Interview FAQ](FAQ.md)**: Comprehensive Technical Overview Q&A
