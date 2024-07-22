from datetime import datetime
from collections import defaultdict
from typing import Any

from clients import DataDotGovClient
from storage import StorageABC

import pandas as pd
from pydantic import BaseModel


class RecallsResult(BaseModel):
    data_by_year: Any
    recalls_per_manufacturer_per_year: Any
    recalls_per_component_per_year: Any
    type_of_recalls_per_manufacturer: Any


class RecallsService:

    INTERESTING_FIELDS = [
        'id',
        'created_at',
        'updated_at',
        'report_received_date',
        'nhtsa_id',
        'recall_link',
        'manufacturer',
        'subject',
        'component',
        'mfr_campaign_number',
        'recall_type',
        'potentially_affected',
        'defect_summary',
        'consequence_summary',
        'corrective_action',
        'fire_risk_when_parked',
        'do_not_drive',
        'completion_rate',
    ]

    def __init__(self, storage: StorageABC, data_dot_gov_client: DataDotGovClient):
        self.storage = storage
        self.data_dot_gov_client = data_dot_gov_client

    def pick_only_interesting_fields(self, recalls_df: pd.DataFrame) -> pd.DataFrame:
        summary = recalls_df[self.INTERESTING_FIELDS]
        return summary

    def safe_timestamp_to_year(self, timestamp: int):
        value = 0
        try:
            value = datetime.fromtimestamp(timestamp).year
        except Exception:
            pass

        return value

    def get_grouped_by_year(self, recalls_df: pd.DataFrame):
        grouped_df = recalls_df.set_index('created_at').groupby(by=self.safe_timestamp_to_year)

        return grouped_df

    def agg_on_axis(self, recalls_df: pd.DataFrame, axis: str):
        group_sizes = recalls_df.groupby(axis).size()
        return group_sizes

    def process_recalls(self) -> RecallsResult:
        full_data_df = self.data_dot_gov_client.get_resource(self.data_dot_gov_client.Resource.RECALLS)
        data_df = self.pick_only_interesting_fields(full_data_df)

        by_year = self.get_grouped_by_year(data_df)
        data_by_year = {}
        recalls_per_manufacturer_per_year = pd.Series()
        recalls_per_component_per_year = pd.Series()

        for year, _ in by_year:
            this_year_data = by_year.get_group(year)
            data_by_year[year] = this_year_data

            manufacturer_recalls_this_year = self.agg_on_axis(this_year_data, 'manufacturer')
            recalls_per_manufacturer_per_year = pd.concat((
                recalls_per_manufacturer_per_year,
                manufacturer_recalls_this_year
            ))

            component_recalls_this_year = self.agg_on_axis(this_year_data, 'component')
            recalls_per_component_per_year = pd.concat((
                recalls_per_component_per_year,
                component_recalls_this_year
            ))

        type_of_recalls_per_manufacturer = data_df.groupby(['manufacturer', 'recall_type']).size()

        return RecallsResult(
            data_by_year=pd.DataFrame({'year': data_by_year.keys(), 'recalls': data_by_year.values()}),
            recalls_per_manufacturer_per_year=recalls_per_manufacturer_per_year,
            recalls_per_component_per_year=recalls_per_component_per_year,
            type_of_recalls_per_manufacturer=type_of_recalls_per_manufacturer
        )

    def process_and_save_recalls(self):
        recalls_results = self.process_recalls()

        for _, row in recalls_results.data_by_year.iterrows():
            self.storage.save(
                f'{row["year"]}.parquet',
                row['recalls'].to_parquet()
            )

        self.storage.save(
            'manufacturer_recalls.parquet',
            recalls_results.recalls_per_manufacturer_per_year.to_frame('manufacturer_recalls').to_parquet()
        )

        self.storage.save(
            'component_recalls.parquet',
            recalls_results.recalls_per_component_per_year.to_frame('component_recalls').to_parquet()
        )

        self.storage.save(
            'recall_types_per_manufacturer.parquet',
            recalls_results.type_of_recalls_per_manufacturer.to_frame('recall_types_per_manufacturer').to_parquet()
        )
