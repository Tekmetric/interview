### Library imports

import pandas as pd, os

### Accessing the flat file data source from a predetermined directory location. This is meant
### to mirror pipelines where the raw data source are flat files that are placed in a predetermined
### local directory or S3 bucket for ingestion. We're doing a check first to make sure this directory
### for raw data exists, as well as the local directory we are using to emulate an S3 bucket.

if not os.path.exists("/Users/purnashah/Desktop/raw_data"):
    raise Exception("Raw data folder doesn't exist")

if not os.path.exists("/Users/purnashah/Desktop/s3_bucket"):
    raise Exception("Dummy destination S3 bucket doesn't exist")

### If we weren't dividing up the parquet files by year but just by a certain number of rows per file,
### I'd use chunk processing for better memory usage and performance. Instead we're reading the whole
### CSV into a dataframe, doing some pre-processing on it, and then using the itertuples method
### (rather than the slower iterrows) to iterate through the rows and build yearly Parquet files.

df = pd.read_csv("/Users/purnashah/Desktop/raw_data/Recalls_Data.csv")

### Converting the recall date to a date type so that we may sort by date and also easily extract
### the year portion for comparisons. The variable current_year is being initialized before we
### enter the itertuples loop so that it may be compared to the year of each row.

df["Report Received Date"] = pd.to_datetime(df["Report Received Date"]).dt.date
df.sort_values("Report Received Date", ascending=False)
current_year = df["Report Received Date"].iloc[0].year

### Renaming columns to remove characters not allowed in parquet column names.

df = df.rename(
    columns={"Completion Rate % (Blank - Not Reported)": "Completion_Rate_Percent"}
)
df.columns = df.columns.str.replace(" ", "_")

### Performing the first requested aggregation, which is total recalls per type and manufacturer across
### the entire dataset, and storing the value for each row.

df["Total_recalls_for_this_recall_type_and_manufacturer"] = df.apply(
    lambda row: (
        (df["Manufacturer"] == row.Manufacturer)
        & (df["Recall_Type"] == row.Recall_Type)
    ).sum(),
    axis=1,
)

### Appending each named tuple directly to the year-specific dataframe before it's written to Parquet has quadratic time complexity,
### so instead I will build a list of named tuples and then append the whole list to the year-specific dataframe.

temp_df_list = list()

### Here's our main loop. The instructions mention specific columns to select but I think the dataset has changed since those instructions
### were written so I just selected all the columns. Instead of writing code to S3, I'm writing to a local directory to mirror an S3 bucket
### per the instructions.

for i, row in enumerate(df.itertuples(index=False)):

    ### In each iteration of the loop, we are grabbing the recall year from the row and comparing
    ### it to the year of the prior row (stored in the current_year variable.)

    iter_year = pd.to_datetime(row[0]).year

    ### If the year of the current row doesn't match the prior row, or if we hit the last value of
    ### the dataset, we convert the current list of named tuples to a dataframe and write its contents
    ### to a Parquet file in order to put all of a given year's rows in a single file.

    if iter_year != current_year or i == len(df) - 1:
        temp_df = pd.DataFrame(temp_df_list)

        ### Here we do the other two requested aggregations. I calculate the number of recalls for a given manufacturer for the year
        ### and for a given component for the year and provide the value for each row.

        temp_df["Recalls_for_this_manufacturer_this_year"] = temp_df.apply(
            lambda row: (temp_df["Manufacturer"] == row.Manufacturer).sum(), axis=1
        )
        temp_df["Recalls_for_this_component_this_year"] = temp_df.apply(
            lambda row: (temp_df["Component"] == row.Component).sum(), axis=1
        )

        ### Here we write the file, log the successful output, reinitialize the empty list for the next file,
        ### and change the year value for the prior row to the current value to compare in the next iteration.

        temp_df.to_parquet(f"/Users/purnashah/Desktop/s3_bucket/{current_year}.parquet")
        print(f"{current_year}.parquet uploaded to S3 bucket!")
        temp_df_list = list()
        current_year = iter_year
    temp_df_list.append(row)
