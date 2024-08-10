import os
from enum import Enum
from typing import Generator

import pandas as pd

from recall.file_storage import FileStorage


class SummaryFiles(str, Enum):
    MANUFACTURER_YEAR_COUNT = "manufacturer_year_count.parquet"
    COMPONENT_YEAR_COUNT = "component_year_count.parquet"
    TYPE_MANUFACTURER_COUNT = "type_manufacturer_count.parquet"


class RecallDataExtractor:
    COLUMN_NAME_MAP = {
        "Report Received Date": "report_received_date",
        "NHTSA ID": "nhtsa_id",
        "Recall Link": "recall_link",
        "Manufacturer": "manufacturer",
        "Subject": "subject",
        "Component": "component",
        "Mfr Campaign Number": "mfr_campaign_number",
        "Recall Type": "recall_type",
        "Potentially Affected": "potentially_affected",
        "Recall Description": "defect_summary",
        "Consequence Summary": "consequence_summary",
        "Corrective Action": "corrective_action",
        "Park Outside Advisory ": "fire_risk_when_parked",
        "Do Not Drive Advisory": "do_not_drive",
        "Completion Rate % (Blank - Not Reported)": "completion_rate",
    }

    def __init__(self, csv_path: str, file_storage: FileStorage):
        os.makedirs("/tmp/recall-data", exist_ok=True)
        self.csv_path = csv_path
        self.file_storage = file_storage

    def extract_data(self):
        df = self._read_csv()
        self._upload_yearly_data(df)
        self._upload_summary(df)

    def _read_csv(self) -> pd.DataFrame:
        df = pd.read_csv(self.csv_path)
        df = df[self.COLUMN_NAME_MAP.keys()]
        df = df.rename(columns=self.COLUMN_NAME_MAP)
        return df

    def _upload_yearly_data(self, df) -> None:
        local_dir = "/tmp/recall-data/yearly"
        os.makedirs(local_dir, exist_ok=True)

        for year, df_for_year in self._extract_yearly_data(df):
            file_name = f"{year}.parquet"
            local_file_path = f"{local_dir}/{file_name}"
            df_for_year.to_parquet(local_file_path, index=False)
            self.file_storage.write(f"yearly/{file_name}", local_file_path)

    @staticmethod
    def _extract_yearly_data(df) -> Generator[pd.DataFrame, None, None]:
        df["year"] = pd.to_datetime(df["report_received_date"]).dt.year
        for year, group in df.groupby("year"):
            df_for_year = group.drop(columns="year")
            yield year, df_for_year

    def _upload_summary(self, df: pd.DataFrame) -> None:
        local_dir = "/tmp/recall-data/summary"
        os.makedirs(local_dir, exist_ok=True)
        df["year"] = pd.to_datetime(df["report_received_date"]).dt.year
        self._upload_manufacturer_year_count(df, local_dir)
        self._upload_component_year_count(df, local_dir)
        self._upload_type_manufacturer_count(df, local_dir)

    def _upload_manufacturer_year_count(self, df: pd.DataFrame, local_dir: str) -> None:
        file_name = SummaryFiles.MANUFACTURER_YEAR_COUNT
        file_path = f"{local_dir}/{file_name}"
        manufacturer_year_count = df.groupby(["manufacturer", "year"]).size().reset_index(name="count")
        manufacturer_year_count.to_parquet(file_path, index=False)
        self.file_storage.write(f"summary/{file_name}", file_path)

    def _upload_component_year_count(self, df: pd.DataFrame, local_dir: str) -> None:
        file_name = SummaryFiles.COMPONENT_YEAR_COUNT
        file_path = f"{local_dir}/{file_name}"
        component_year_count = df.groupby(["component", "year"]).size().reset_index(name="count")
        component_year_count.to_parquet(file_path, index=False)
        self.file_storage.write(f"summary/{file_name}", file_path)

    def _upload_type_manufacturer_count(self, df: pd.DataFrame, local_dir: str) -> None:
        file_name = SummaryFiles.TYPE_MANUFACTURER_COUNT
        file_path = f"{local_dir}/{file_name}"
        type_manufacturer = df.groupby(["recall_type", "manufacturer"]).size().reset_index(name="count")
        type_manufacturer.to_parquet(file_path, index=False)
        self.file_storage.write(f"summary/{file_name}", file_path)
