## Purna Shah Tekmetric Interview
Here is my completed assignment. My only deviation from the instructions is that the column names in the raw data appear to have changed from when the instructions were written, so I just extract all of them into the Parquet files.

Installation instructions:

Create a new environment in your environment manager, navigate to the project directory, and run pip install -r requirements.txt.
Create two directories, one to hold the raw data and one to receive the Parquet files, and put the raw data CSV in the raw data folder.
Update the paths in the script to your local paths to the two directories/CSV file mentioned above. The paths are located on lines 10, 13, 21, and 85 in the main script (recall_data.py) and on lines 7, 19, 33, 55, and 75 in test.py.
Run using python recall_data.py. This project is tested in Python 3.10.
Run tests with pytest test.py.
