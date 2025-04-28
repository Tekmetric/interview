# Python Coding Exercise

## Problem Statement

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

## Solution Design
Since the key point of this exercise is to design a scalable system, I'm chosing a highly customisable design, where I  split the data pipeline functionality in the following components:
1. The orchestrator
2. The scraper(s)
3. The serializer(s) / persistence
4. The aggregators
5. Other minor components, such as configuration or data models.

### 1. The orchestractor
This main component is responsible for wiring everything up together: based on the settings, it instantiates the parser, the serializer and the aggregators.

Next, it traverses the objects list from the scraper and passes the *processed* data to the serializer, and the *raw* data to each of the loaded aggregators for processing.

### 2. The scraper(s)
The main purpose of the scraper is to establish the contract with an API, web page etc. In this case it is the NASA NeoWs API.

Based on the configuration, the scraper yields objects one by one via a property called objects. Each object has two "flavours": *raw* (a dict from the raw JSON from the API response) and *processed* (a pydantic object of type AsteroidValidatedObject, which inherits from BaseModel and validates the fields obtained by the scraper). Once it is done with traversing a page from the base_url, it loads the next one, by accessing the links.next field from the JSON response.

### 3. The serializer(s)
This component is responsible for persisting the data somewhere (such as disk, AWS S3, MongoDB etc).

In this particular case, it serializes the objects on disk once it reaches the object count or upon flushing it.

### 4. The aggregator(s)
These components are also highly stateful objects, whose main purpose is to aggregate data based on raw views of the data.

They persist their data in JSON format upon flushing. For even further extensibility, one could have another component, whose main purpose would be to just serialize aggregations. This was not implemented as part of this solution.

## Solution Execution
The entry-point for the solution is the `recall_data.py`, as it was designed initially.

### Installing dependencies
For running this project, `poetry` is required, since it offers package management for it.

Depending on the OS you are running, installation instructions can be found on [the official web page](https://python-poetry.org/docs/#installing-with-the-official-installer).

To install all deps, run the following:
1. `poetry install`
2. `poetry run pre-commit install` (Optional, to set up pre-commit hooks)

Furthermore, for an integrated dev experience, I'm using the following VSCode extensions:

- ruff
- pyright
- autoDocstring.

Ideally, I'd set up a **devcontainer** for all this, but in the interest of time, I choose to skip this step. Same goes for **unit tests**.

### Running the script
Running the script requires the following command:
`poetry run python recall_data.py [config_file]`.

Of course, in a prod environment, one can package this and run it normally, i.e. directly via `python`.

## Potential future developments

As already mentioned, there are at least 3 engineering activities which are still required besides the actual implementation, to make this solution "production-ready":

1. Unit and integration tests (and potentially integrate their runtime in a CI/CD GitHub Actions pipeline)
2. Devcontainer setup, for x-platform compatibility
3. Package and deploy script (potentially as a subsequent step of the pipeline)

On top of this, another area which is worth considering is the recurrent run of the pipeline, which would be used to update potentially changed data from a source.

However, plain parquet data is not suited for in-place updates. Hence, for these workloads, depending on the data volume and data scenarios, a data lake platform such as Delta Lake (on top of parquet), or a document DB, or even a simple table data store would be suitable, such as AWS's DynamoDB.

On the execution side of things, recurrent pipeline runs can be achieved with various platforms, such as Apache Airflow, or serverlessly with Amazon EventBridge and AWS Batch.
