### Library imports

import pandas as pd

### Load csv and create column with just the recall year rather than date for easy comparisons

df = pd.read_csv("/Users/purnashah/Desktop/raw_data/Recalls_Data.csv")
df["Report Received Date"] = pd.to_datetime(df["Report Received Date"]).dt.date
df["Year"] = df.apply(lambda row: row["Report Received Date"].year, axis=1)


def test_row_counts_correct():

    ### Pick a random year from the main dataframe, and make sure the corresponding parquet file
    ### has the correct number of rows in it.

    random_year = pd.to_datetime(df.sample(n=1)["Report Received Date"].iloc[0]).year
    pq = pd.read_parquet(
        "/Users/purnashah/Desktop/s3_bucket/" + str(random_year) + ".parquet"
    )
    rowcount = (df["Year"] == random_year).sum()
    assert rowcount == len(pq)


def test_agg_per_mftr_per_type():

    ### Pick a random year, go to its corresponding Parquet file, pick a random row from that file,
    ### and make sure its aggregation calculation for recalls for that manufacturer and recall type
    ### are correct.

    random_year = pd.to_datetime(df.sample(n=1)["Report Received Date"].iloc[0]).year
    pq = pd.read_parquet(
        "/Users/purnashah/Desktop/s3_bucket/" + str(random_year) + ".parquet"
    )
    random_row = pq.sample(n=1)
    manufacturer = random_row["Manufacturer"].iloc[0]
    recall_type = random_row["Recall_Type"].iloc[0]
    correct_number = (
        (df["Manufacturer"] == manufacturer) & (df["Recall Type"] == recall_type)
    ).sum()
    assert (
        correct_number
        == random_row["Total_recalls_for_this_recall_type_and_manufacturer"].iloc[0]
    )


def test_agg_per_mftr_per_year():

    ### Pick a random year, go to its corresponding Parquet file, pick a random row from that file,
    ### and make sure its aggregation calculation for recalls for that manufacturer for that year
    ### are correct.

    random_year = pd.to_datetime(df.sample(n=1)["Report Received Date"].iloc[0]).year
    pq = pd.read_parquet(
        "/Users/purnashah/Desktop/s3_bucket/" + str(random_year) + ".parquet"
    )
    random_row = pq.sample(n=1)
    manufacturer = random_row["Manufacturer"].iloc[0]
    correct_number = (
        (df["Manufacturer"] == manufacturer) & (df["Year"] == random_year)
    ).sum()
    assert (
        correct_number == random_row["Recalls_for_this_manufacturer_this_year"].iloc[0]
    )


def test_agg_per_cmpt_per_year():

    ### Pick a random year, go to its corresponding Parquet file, pick a random row from that file,
    ### and make sure its aggregation calculation for recalls for that component for that year
    ### are correct.

    random_year = pd.to_datetime(df.sample(n=1)["Report Received Date"].iloc[0]).year
    pq = pd.read_parquet(
        "/Users/purnashah/Desktop/s3_bucket/" + str(random_year) + ".parquet"
    )
    random_row = pq.sample(n=1)
    component = random_row["Component"].iloc[0]
    correct_number = (
        (df["Component"] == component) & (df["Year"] == random_year)
    ).sum()
    assert correct_number == random_row["Recalls_for_this_component_this_year"].iloc[0]
