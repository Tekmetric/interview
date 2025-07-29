# NASA Near Earth Object Data Processor

A scalable Python application that fetches Near Earth Object (NEO) data from NASA's API and processes it using PySpark. The application extracts, transforms, and saves the data in Parquet format with aggregations for analysis.

## Features

- 🚀 **Scalable**: Built with PySpark for processing large datasets (scales from local development to distributed clusters)
- 📊 **Data Pipeline**: Complete ETL pipeline for NASA NEO data
- 💾 **Parquet Storage**: Efficient columnar storage format for analytics
- 📈 **Aggregations**: Pre-calculated metrics for reporting
- 🏗️ **S3-like Structure**: Organized data lake directory structure
- 🔍 **Comprehensive**: Extracts all required fields including closest approach data

## Requirements

- Python 3.8 or higher
- Java 8 or higher (required for PySpark)
- NASA API key (free from [api.nasa.gov](https://api.nasa.gov))

## Quick Start

### 1. Automated Setup (Recommended)

```bash
# Clone or download the project
git clone <repository-url>
cd nasa-neo-processor

# Run the automated setup script
python setup_environment.py
```

The setup script will:
- Check Python and Java requirements
- Create a virtual environment
- Install all dependencies
- Create a `.env` file for your API key

### 2. Manual Setup

```bash
# Create virtual environment
python -m venv venv

# Activate virtual environment
# On Windows:
venv\Scripts\activate
# On Linux/Mac:
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Create environment file
cp .env.example .env
```

### 3. Configure API Key

Edit the `.env` file and add your NASA API key:

```env
NASA_API_KEY=your_actual_api_key_here
```

Get your free API key at: https://api.nasa.gov

### 4. Run the Processor

```bash
# Make sure virtual environment is activated
python neo_data_processor.py
```

## Output Structure

The application creates an S3-like data lake structure:

```
data/
├── raw/neo/year=2024/
│   ├── neo_raw_data.json          # Raw JSON backup
│   └── neo_raw_data.parquet       # Raw data in Parquet
├── processed/neo/year=2024/
│   └── neo_processed_data.parquet # Cleaned and processed data
└── aggregations/neo/year=2024/
    ├── neo_aggregations.json      # Summary statistics
    └── approaches_by_year.parquet  # Yearly approach counts
```

## Data Schema

The processed data includes all required fields:

| Field | Type | Description |
|-------|------|-------------|
| id | string | Object designation |
| neo_reference_id | string | SPK ID reference |
| name | string | Full object name |
| name_limited | string | Short name |
| designation | string | Object designation |
| nasa_jpl_url | string | JPL database URL |
| absolute_magnitude_h | double | Absolute magnitude |
| is_potentially_hazardous_asteroid | boolean | PHA classification |
| minimum_estimated_diameter_meters | double | Min diameter estimate |
| maximum_estimated_diameter_meters | double | Max diameter estimate |
| closest_approach_miss_distance_kilometers | double | Miss distance in km |
| closest_approach_date | string | Date of closest approach |
| closest_approach_relative_velocity_kms | double | Relative velocity km/s |
| first_observation_date | string | First observation |
| last_observation_date | string | Last observation |
| observations_used | integer | Number of observations |
| orbital_period | double | Orbital period in days |

## Aggregations

The application calculates:

1. **Close Approaches < 0.2 AU**: Total count of approaches closer than 0.2 astronomical units
2. **Approaches by Year**: Number of close approaches recorded in each year

Results are saved in both JSON and Parquet formats for easy consumption.

## Scalability

### Local Development
- Runs on single machine with local Spark session
- Optimized for datasets up to several GB
- Uses adaptive query execution and Arrow optimization

### Production Scale
The code is designed to scale to larger datasets by:

- **Spark Configuration**: Easily configurable for cluster deployment
- **Partitioning**: Data partitioned by year for efficient querying  
- **Compression**: Snappy compression for optimal storage/performance balance
- **Schema Evolution**: Structured schema for consistent data types
- **Rate Limiting**: Built-in API rate limiting to handle large object lists

### Scaling to Tens of GBs

To handle larger datasets:

1. **Cluster Deployment**: Deploy to Spark cluster (EMR, Databricks, etc.)
2. **Batch Processing**: Process data in batches by time periods
3. **Caching**: Use Spark caching for iterative operations
4. **Partitioning**: Increase partition counts for larger datasets

Example cluster configuration:
```python
spark = SparkSession.builder \
    .appName("NASA_NEO_Processor") \
    .config("spark.executor.memory", "4g") \
    .config("spark.executor.cores", "4") \
    .config("spark.sql.adaptive.coalescePartitions.maxPartitionBytes", "128MB") \
    .getOrCreate()
```

## API Usage

The application uses two NASA APIs:

1. **Close Approach Data API**: For getting NEO close approach records
2. **Small Body Database API**: For detailed object information

Key features:
- Rate limiting to respect NASA's fair use policy
- Error handling and retry logic
- Efficient data extraction and transformation

## Development

### Project Structure

```
├── neo_data_processor.py      # Main application
├── requirements.txt           # Python dependencies  
├── setup_environment.py       # Automated setup script
├── .env.example              # Environment template
├── .gitignore                # Git ignore rules
└── README.md                 # This file
```

### Running Tests

```bash
# Install test dependencies
pip install pytest pytest-spark

# Run tests
pytest tests/
```

### Code Quality

The code includes:
- Type hints for better maintainability
- Comprehensive error handling
- Structured logging
- Documentation and comments
- Modular design for easy testing

## Troubleshooting

### Common Issues

1. **Java Not Found**
   ```
   Error: Java not found
   ```
   Solution: Install Java 8+ from [AdoptOpenJDK](https://adoptopenjdk.net/)

2. **PySpark Import Error**
   ```
   ImportError: No module named 'pyspark'
   ```
   Solution: Ensure virtual environment is activated and dependencies installed

3. **API Rate Limiting**
   ```
   429 Too Many Requests
   ```
   Solution: The application includes rate limiting. For large datasets, consider using a registered NASA API key.

4. **Memory Issues**
   ```
   OutOfMemoryError
   ```
   Solution: Increase Spark memory settings or process data in smaller batches

### Getting Help

- Check the logs for detailed error messages
- Ensure all requirements are installed
- Verify your NASA API key is valid
- For large datasets, consider cluster deployment

## License

This project is for educational and research purposes. NASA data is public domain.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## Acknowledgments

- NASA for providing the Near Earth Object data APIs
- Apache Spark community for the excellent data processing framework
- JPL Small-Body Database for comprehensive object information
