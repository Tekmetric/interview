# NeoWS Fetcher & Metric Calculator

This is a sample project to fetch data from NASA's Near Earth Object Web Service (NeoWS) API and calculate some metrics
based on the fetched data. The project is structured to allow easy scaling and modification for larger datasets.

## Installation

Please see [SETUP.md](SETUP.md) for installation and running instructions.

## Project Structure

- All the required code exists in the [tekmetric_data](tekmetric_data) directory organized as a package with
  the following structure:
    - `__init__.py`: Initializes the package.
    - `__main__.py`: Main entry point for the package. It fetches data from the NeoWS API and saves it in Parquet
      format.
    - `data.py`: Contains the parquest schema and conversion function to convert the fetched data into a schema
      compatible dictionary.
    - `logging_config.yaml`: Contains the logging configuration. logging dictConfig is used to configure the logging
      module. The logging level can be set to DEBUG, INFO, WARNING, ERROR, or CRITICAL.
    - `metric.py`: Contains the metric calculation classes. Only a single metric is implemented in this version, but
      the structure allows for easy addition of more metrics.
    - `nasa_client.py`: Contains the NASA API client classes. It handles the API requests and responses. Other API
      endpoints can be added here as needed. The factory method allows for easy instantiation of the client with.
    - `output.py`: Contains the output classes. It handles the saving of the fetched data in Parquet format. Other
      storage engines can be added here as needed. Only writing to the local filesystem is implemented in this, but the
      factory method allows for easy instantiation of the output class with different storage engines.
- All the tests are in the [tests](tests) directory. Additional files for the tests are `conftest.yaml`,
  `test_requirements.txt`, `tox.ini`. The tests can be run with:
    ```shell
    pip install tox
    tox
    ```
  Tox will generate the coverage report in `htmlcov` directory and as a xml with the name `coverage.xml`.
- [requirements.txt](requirements.txt) contains the required packages for the project.

## Design Considerations

- The project is designed to be easily extensible. New metrics, output formats, and API endpoints can be added with
  minimal changes to the existing code.
- Fetching and metric calculation are separated. This allows for independent scaling of the two components.
- Fetching each page is independent can be easily parallelized. The current implementation fetches each page
  sequentially, but this can be easily changed to fetch multiple pages in parallel.

## Possible Future Improvements

- There is precision loss for some fields (distance and speed measurements marked in
  the [data.py](tekmetric_data/data.py)) assuming float64 precision is enough. float64 is chosen because processing
  float64 values is faster and float64 values have smaller storage footprint. If this precision loss is important, the
  field can be converted to decimal. This will prevent precision loss, but will increase the size of the data and
  processing requirements.
- Metric calculation is not fully independent for each fetch operation. A single metric class combines each new metric
  with the previous metric. Instead, separate metric instances could be created and accumulated in a single metric.
- Separate data schemas can be introduced for passing the data around and for saving it to disk. This would allow for
  more flexibility in the data processing pipeline. Pyarrow record batches can be used for passing the data around with
  minimal serialization/deserialization overhead.
- API rate limiting improvements. Multiple API keys could be used for reducing the rate limit. This would allow for
  faster data fetching.
- Running everything in parallel with a tool like Spark, Flink etc. S3 with S3 Notifications?
- Asyncio for the API calls. This would allow for faster data fetching.
- Factories could be removed all together and the classes could be instantiated directly. This would reduce the
  complexity of the code. 
