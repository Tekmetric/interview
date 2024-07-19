## Purna Shah Tekmetric Interview
Here is my completed assignment. My only deviation from the instructions is that the column names in the raw data appear to have changed from when the instructions were written, so I just extract all of them into the Parquet files.

Installation instructions:

- Create a new environment in your environment manager, navigate to the project directory, and run `pip install -r requirements.txt`.
- Create two directories named raw_data and s3_bucket in the project directory, and put Recalls_Data.csv (must be named this, available [here](https://catalog.data.gov/dataset/recalls-data)) in the raw_data folder.
- Run using `python recall_data.py`. This project is tested in Python 3.10.
- Run tests with `pytest test.py`.
