# Python Coding Exercise

Your task is to build a python script to gather data from NASA's Near Earth Object Web Service API, and save that data. We'll also perform some aggregations to make reporting on Near Earth Objects simpler for our theoretical website.

The page for the API is here: https://api.nasa.gov

To save our data, we'll write it out to the local filesystem as if we're saving it to an S3 Data Lake. This will save having to mess with AWS credentials. Your files should be saved in the same data directory in which this README resides, in whatever folder structure you would use to save the data in S3.

## 🚀 Quick Start

**For detailed setup and usage instructions, see [USAGE.md](USAGE.md)**

```bash
# Quick setup
./setup.sh

# Run the scraper
python nasa_neo_scraper.py

# Run tests
python run_tests.py --coverage
```

## 📋 Requirements
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

## 📚 Documentation

- **[USAGE.md](USAGE.md)**: Complete setup, usage, and testing instructions
- **[OVERVIEW.md](OVERVIEW.md)**: Technical architecture and implementation details
- **[FAQ.md](FAQ.md)**: Comprehensive Q&A for technical interviews

## 🧪 Testing

The project includes comprehensive testing with 100% coverage for core modules:
- 56 total tests covering all code paths
- Unit tests, integration tests, and edge case coverage
- Performance testing with large datasets
- Error recovery and retry logic testing

## 🏗️ Architecture

- **Modular Design**: Clean separation of concerns (API client, data processor, file manager)
- **Scalable**: Handles development (200 NEOs) to production (tens of GBs) workloads
- **Memory Efficient**: Generator pattern for streaming data processing
- **S3-Compatible**: Partitioned Parquet storage ready for cloud migration
- **Production Ready**: Comprehensive error handling, logging, and monitoring

### Submitting your coding exercise
Once you have finished your script, please create a PR into Tekmetric/interview. Don't forget to update the gitignore if that is required!
