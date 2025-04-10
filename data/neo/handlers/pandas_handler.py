from datetime import datetime

import pandas as pd
from typing import List, Dict
from neo.handlers.base import DataHandlerBase
from neo.handlers.schema import Columns
from neo.logger import logger


class PandasDataHandler(DataHandlerBase):
    """
    A data handler that uses pandas to process NEO data.
    """

    def __init__(self):
        """
        Initialize the Pandas data handler with the required columns.
        """
        self.required_columns = [column.value for column in Columns]

    def to_dataframe(self, records: List[Dict]) -> pd.DataFrame:
        """
        Convert raw records into a Pandas DataFrame, accounting for nested data.
        """
        df = pd.json_normalize(records)

        if "close_approach_data" in df.columns:
            df = df.explode("close_approach_data")

            close_approach_df = pd.json_normalize(df["close_approach_data"].dropna())
            close_approach_df.columns = [
                f"close_approach_data.{col}" for col in close_approach_df.columns
            ]

            df = pd.concat(
                [df.reset_index(drop=True), close_approach_df.reset_index(drop=True)],
                axis=1,
            )

        try:
            return self.select_columns(df)
        except KeyError as e:
            logger.error(f"Missing required columns: {e}")
            return pd.DataFrame(columns=self.required_columns)

    def select_columns(self, data: pd.DataFrame) -> pd.DataFrame:
        """
        Select and rename the required columns from the DataFrame.
        """
        selected_columns = {column.value: column.name.lower() for column in Columns}

        # Select and rename the columns
        data = data[selected_columns.keys()]
        data = data.rename(columns=selected_columns)
        return data

    def prepare(self, data: pd.DataFrame) -> pd.DataFrame:
        data[Columns.CLOSEST_APPROACH_DATE.name.lower()] = pd.to_datetime(
            data[Columns.CLOSEST_APPROACH_DATE.name.lower()]
        )

        # Calculate the absolute difference between `close_approach_date` and the current date
        # This is being done in order to keep the closest date
        current_date = datetime.now()
        data["date_diff"] = (
            data[Columns.CLOSEST_APPROACH_DATE.name.lower()] - current_date
        ).abs()

        data = data.sort_values("date_diff").groupby("id").first().reset_index()

        data[Columns.CLOSEST_APPROACH_DATE.name.lower()] = data[
            Columns.CLOSEST_APPROACH_DATE.name.lower()
        ].dt.strftime("%Y-%m-%d")
        data = data.drop(columns=["date_diff"])

        return data

    def _calculate_total_close_approaches(self, data: pd.DataFrame) -> pd.DataFrame:
        """
        Calculate the total number of close approaches within 0.2 AU.
        """
        total_close = (
            data[
                Columns.CLOSEST_APPROACH_MISS_DISTANCE_ASTRONOMICAL.name.lower()
            ].astype(float)
            < 0.2
        ).sum()
        return pd.DataFrame({"total_close_approaches_within_0.2_au": [total_close]})

    def _calculate_yearly_approaches(self, data: pd.DataFrame) -> pd.DataFrame:
        """
        Calculate the number of close approaches recorded in each year.
        """
        yearly_counts = (
            pd.to_datetime(data[Columns.CLOSEST_APPROACH_DATE.name.lower()])
            .dt.year.value_counts()
            .sort_index()
        )
        yearly_counts_df = yearly_counts.reset_index()
        yearly_counts_df.columns = ["year", "count"]
        return yearly_counts_df

    def run_aggregations(self, data) -> Dict:
        """
        Run all required aggregations on the data.
        """
        total_close_df = self._calculate_total_close_approaches(data)
        yearly_counts_df = self._calculate_yearly_approaches(data)

        return {
            "total_close_approaches_within_0.2_au": total_close_df,
            "approaches_per_year": yearly_counts_df,
        }
