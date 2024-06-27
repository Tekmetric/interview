import csv
import logging
import uuid
from pathlib import Path
from typing import Dict, List
from urllib.parse import urljoin

import pandas as pd
import requests
import requests_cache

from src import errors
from src.entities import RecallNotice

logger = logging.getLogger(__name__)


class DataCollector:
    ENTITY_FILE_COLUMNS_MAP = {
        "report_received_date": "Report Received Date",
        "nhtsa_id": "NHTSA ID",
        "recall_link": "Recall Link",
        "manufacturer": "Manufacturer",
        "subject": "Subject",
        "component": "Component",
        "mfr_campaign_number": "Mfr Campaign Number",
        "recall_type": "Recall Type",
        "potentially_affected": "Potentially Affected",
        "defect_summary": "Recall Description",
        "consequence_summary": "Consequence Summary",
        "corrective_action": "Corrective Action",
        "fire_risk_when_parked": "Park Outside Advisory",  # Assuming 'Park Outside Advisory' maps to 'fire_risk_when_parked'
        "do_not_drive": "Do Not Drive Advisory",
        "completion_rate": "Completion Rate % (Blank - Not Reported)",  # Assuming this maps to 'completion_rate'
    }

    def __init__(
        self,
        api_base_url: str,
        api_key: str,
        output_bucket: str,
        use_requests_cache: bool = False,
    ):
        self.api_base_url = api_base_url
        self.api_key = api_key
        self.output_bucket = output_bucket

        if use_requests_cache:  # for dev purposes
            requests_cache.install_cache("req_cache")

    def run(self):
        # download file
        file_path = self.fetch_raw_data_file()
        # parse file
        data_frame = self.parse_csv_file(file_path)
        # save data in S3 bucket per year
        self.save_data_per_year(data_frame)
        # save data in S3 bucket for requested aggregations
        self.save_data_aggregations(data_frame)

    def fetch_raw_data_file(self) -> str:
        """Fetch the raw data file from the API

        :raises errors.DataFetchRequestError:
        :return str: File path to the downloaded CSV file
        """
        url = urljoin(
            str(self.api_base_url), "/api/views/6axg-epim/rows.csv?accessType=DOWNLOAD"
        )
        headers = {}
        if self.api_key:  # no auth needed for this API call
            headers["Authorization"] = f"Bearer {self.api_key}"
        response = requests.get(url, headers=headers)
        if not response.ok:
            raise errors.DataFetchRequestError(f"Failed to fetch data: {response.text}")
        tmp_file_name = f"/tmp/{uuid.uuid4()}.csv"
        with open(tmp_file_name, "wb") as tmp_file:
            tmp_file.write(response.content)
        return tmp_file_name

    def parse_csv_file(self, file_path: str) -> pd.DataFrame:
        """Read the CSV file and return a DataFrame with the data

        :param str file_path: Local path to the CSV file
        :return pd.DataFrame: DataFrame with the file data
        """
        parsed_items = []
        rows_parsed = 0
        with open(file_path, "r") as csv_file:
            csv_reader = csv.reader(csv_file, delimiter=",", quotechar='"')
            header_map = self._entity_header_map(next(csv_reader))
            for row in csv_reader:
                rows_parsed += 1
                try:
                    recall_notice = RecallNotice(
                        **{
                            entity_field: row[column_index]
                            for entity_field, column_index in header_map.items()
                        }
                    )
                    parsed_items.append(recall_notice.model_dump())
                except ValueError as ex:
                    logger.error(
                        "Failed to parse file row (%s): %s. Reason: %s",
                        rows_parsed,
                        row,
                        ex,
                    )
        return pd.DataFrame(parsed_items)

    def save_data_per_year(self, data_frame: pd.DataFrame):
        base_path = Path(self.output_bucket, "yearly_data")
        base_path.mkdir(parents=True, exist_ok=True)  # ensure the directory exists
        for year in data_frame["report_received_date"].dt.year.unique():
            year_data = data_frame[data_frame["report_received_date"].dt.year == year]
            year_data.to_parquet(f"{base_path}/{year}.parquet", engine="pyarrow")

    def save_data_aggregations(self, data_frame: pd.DataFrame):
        base_path = Path(self.output_bucket, "aggregations")
        base_path.mkdir(parents=True, exist_ok=True)  # ensure the directory exists
        # Number of recalls per manufacturer per year
        recalls_manufacturer_year = data_frame.groupby(
            [data_frame["manufacturer"], data_frame["report_received_date"].dt.year]
        ).agg({"potentially_affected": "sum"})
        recalls_manufacturer_year.rename(
            columns={"report_received_date": "year", "potentially_affected": "recalls"},
            inplace=True,
        )
        recalls_manufacturer_year.to_parquet(
            f"{base_path}/recalls_manufacturer_year.parquet", engine="pyarrow"
        )

        # Number of recalls per component per year
        recalls_component_year = data_frame.groupby(
            [data_frame["component"], data_frame["report_received_date"].dt.year]
        ).agg({"potentially_affected": "sum"})
        recalls_component_year.rename(
            columns={"report_received_date": "year", "potentially_affected": "recalls"},
            inplace=True,
        )
        recalls_component_year.to_parquet(
            f"{base_path}/recalls_component_year.parquet", engine="pyarrow"
        )

        # Number of recalls per type per manufacturer
        recalls_type_manufacturer = data_frame.groupby(
            [data_frame["recall_type"], data_frame["manufacturer"]]
        ).agg({"potentially_affected": "sum"})
        recalls_component_year.rename(
            columns={"potentially_affected": "recalls"}, inplace=True
        )
        recalls_type_manufacturer.to_parquet(
            f"{base_path}/recalls_type_manufacturer.parquet", engine="pyarrow"
        )

    def _entity_header_map(self, header_row: List[str]) -> Dict[str, int]:
        """Map file headers to entity fields (auto adapt to column order changes)

        :param List[str] header_row: List of columns in the file
        :raises errors.DataHeaderError:
        :return Dict[str, int]: Mapping between entity field and column index in the file
        """
        file_headers = list(map(lambda x: x.lower().strip(), header_row))
        expected_headers = set(
            map(lambda x: x.lower().strip(), self.ENTITY_FILE_COLUMNS_MAP.values())
        )
        if set(expected_headers).difference(file_headers):
            raise errors.DataHeaderError("Missing expected data columns")

        return {
            entity_field: file_headers.index(column_header.lower().strip())
            for entity_field, column_header in self.ENTITY_FILE_COLUMNS_MAP.items()
        }
