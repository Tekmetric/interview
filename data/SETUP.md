## Installation

- Clone the repository.
- Options:

1. Run it locally:
    - Create a virtual environment and activate it:

     ```shell
     python -m venv .venv
     source .venv/bin/activate
     ```

    - Install the required packages using pip:

     ```shell
     pip install -r requirements.txt
     ```

    - Run the package:

     ```shell
     python -m tekmetric_data
     ```

2. Run it in a Docker container:
    - Build the Docker image:

     ```shell
        docker build -t tekmetric_data .
      ```

    - Run the Docker container:

    ```shell
        docker run -v $(pwd)/data:/opt/app/output -e TEK_NASA_API_KEY=SuperSecretKey tekmetric_data --page-size 10 --num-pages 5
     ```

### Usage

:warning: API key should be always passed as an environment variable. `DEMO_KEY` is used if the environment variable is not set.

```shell
python3 -m tekmetric_data --help
# or 
docker run --rm -it tekmetric_data:latest --help

2025-04-27 00:52:44,553 - __main__.main:83 - INFO - Starting Tekmetric Data version 0.1.0
usage: tekmetric_data [-h] [--url URL] [--page-size PAGE_SIZE] [--num-pages NUM_PAGES] [--metric METRIC] [--output-type OUTPUT_TYPE] [--output-dir OUTPUT_DIR]

Tekmetric Data

options:
  -h, --help            show this help message and exit
  --url URL             NASA API URL (default: https://api.nasa.gov)
  --page-size PAGE_SIZE
                        Limit number of neo items on each page (default: 20)
  --num-pages NUM_PAGES
                        Number of pages to fetch (default: 10)
  --metric METRIC       Metric type (default: close_approach)
  --output-type OUTPUT_TYPE
                        Output directory (default: disk)
  --output-dir OUTPUT_DIR
                        Output directory (default: output)
```