import requests
import pandas as pd
from datetime import datetime
import io
import utils


API_KEY = (
    "OU8fYYglLHny7h8JAqKRrqN2nU4SCPO9L6K2MqO4"  # must be stored in secrets manager
)
URL = f"https://data.transportation.gov/api/views/6axg-epim/rows.csv?API_KEY={API_KEY}"



RENAME_COLS = {
    "Recall Description": "defect_summary",
    "Park Outside Advisory": "fire_risk_when_parked",
    "Do Not Drive Advisory": "do_not_drive",
    "Completion Rate % (Blank - Not Reported)": "completion_rate",
}


def process_and_write_data(df_recalls_data):
    """
    Takes dataframe as input and renames columns, adds new columns and cleans up required columns.
    """
    destination_path = "recalls_data/dim_recalls/"

    # Rename columns
    df_recalls_data.columns = [
        (
            RENAME_COLS[col.strip()]
            if col.strip() in RENAME_COLS
            else col.strip().lower().replace(" ", "_")
        )
        for col in df_recalls_data.columns
    ]

    # Cleanup rows
    df_recalls_data["recall_link"] = df_recalls_data["recall_link"].str.extract(
        r"\((.*?)\)"
    )
    df_recalls_data["report_received_date"] = pd.to_datetime(
        df_recalls_data["report_received_date"]
    )
    df_recalls_data["report_received_year"] = df_recalls_data[
        "report_received_date"
    ].dt.year

    df_recalls_data["id"] = range(1, len(df_recalls_data) + 1)
    df_recalls_data.set_index("id", inplace=True)

    current_timestamp = datetime.now()
    df_recalls_data["created_at"] = current_timestamp
    df_recalls_data["updated_at"] = current_timestamp

    df_recalls_partitioned = df_recalls_data.groupby("report_received_year")

    if df_recalls_partitioned:
        for year, data in df_recalls_partitioned:
            file_name = f"{year}"
            utils.write_data(data, destination_path, "local", file_name)

    return df_recalls_data


def aggregate_and_write_data(processed_recalls_data):

    recalls_per_manufacturer_per_year = (
        processed_recalls_data.groupby(["manufacturer", "report_received_year"])
        .size()
        .reset_index(name="recalls_per_manufacturer_per_year")
    )
    utils.write_data(
        recalls_per_manufacturer_per_year,
        "recalls_data/recalls_per_manufacturer_per_year/",
        "local",
        "recalls_per_manufacturer_per_year",
    )

    recalls_per_component_per_year = (
        processed_recalls_data.groupby(["component", "report_received_year"])
        .size()
        .reset_index(name="recalls_per_component_per_year")
    )
    utils.write_data(
        recalls_per_component_per_year,
        "recalls_data/recalls_per_component_per_year/",
        "local",
        "recalls_per_component_per_year",
    )

    recalls_per_type_per_manufacturer = (
        processed_recalls_data.groupby(["recall_type", "manufacturer"])
        .size()
        .reset_index(name="recalls_per_type_per_manufacturer")
    )
    utils.write_data(
        recalls_per_type_per_manufacturer,
        "recalls_data/recalls_per_type_per_manufacturer/",
        "local",
        "recalls_per_type_per_manufacturer",
    )

    return


def get_data():
    """
    Gets data from API and processes is as per requirement.
    """
    response = requests.get(URL)
    str_recalls_data = io.StringIO(response.text)
    df_recalls_data = pd.read_csv(str_recalls_data)
    processed_recalls_data = process_and_write_data(df_recalls_data)
    aggregate_and_write_data(processed_recalls_data)


if __name__ == "__main__":
    get_data()