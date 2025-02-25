from __future__ import annotations
from dataclasses import dataclass
import os
from typing import Dict, List
from dotenv import load_dotenv


# load environment variables from .env file
load_dotenv()


@dataclass
class ExtractorConfig:
    url: str
    api_key: str

    @classmethod
    def from_env(cls):
        api_key = os.getenv("NASA_API_KEY", None)
        if not api_key:
            raise ValueError("NASA_API_KEY not found in env")
        url = os.getenv("NASA_BROWSE_API_URL", None)
        if not url:
            raise ValueError("NASA_BROWSE_API_URL found in env")
        return cls(url=url, api_key=api_key)


@dataclass
class TransformerConfig:
    columns_to_return: List[str]
    aggregations_rules: Dict[str, dict]

    @classmethod
    def from_env(cls):
        return cls(
            columns_to_return=[
                "id",
                "neo_reference_id",
                "name",
                "name_limited",
                "designation",
                "nasa_jpl_url",
                "absolute_magnitude_h",
                "is_potentially_hazardous_asteroid",
                "minimum_estimated_diameter_in_meters",
                "maximum_estimated_diameter_in_meters",
                "closest_approach_miss_distance_in_kilometers",
                "closest_approach_date",
                "closest_approach_relative_velocity_in_kilometers_per_second",
                "first_observation_date",
                "last_observation_date",
                "observations_used",
                "orbital_period",
            ],
            aggregations_rules={
                "total_approaches_under_threshold": {
                    "column": "close_approach_data",
                    "path": "miss_distance.astronomical",
                    "threshold": 0.2,
                    },
                "approach_yearly_counts": {
                    "column": "close_approach_data",
                    "path": "close_approach_year",
                }
            },
        )


@dataclass
class LoaderConfig:
    storage_path_raw: str
    storage_path_processed: str
    storage_path_aggregations: str

    @classmethod
    def from_env(cls):
        path_raw = os.getenv("STORAGE_PATH_RAW", None)
        if not path_raw:
            raise ValueError("STORAGE_PATH_RAW not found in env")
        path_processed = os.getenv("STORAGE_PATH_PROCESSED", None)
        if not path_processed:
            raise ValueError("STORAGE_PATH_PROCESSED not found in env")
        path_aggregations = os.getenv("STORAGE_PATH_AGGREGATIONS", None)
        if not path_aggregations:
            raise ValueError("STORAGE_PATH_AGGREGATIONS not found in env")
        return cls(
            storage_path_raw=path_raw,
            storage_path_processed=path_processed,
            storage_path_aggregations=path_aggregations,
        )


@dataclass
class Config:
    extractor: ExtractorConfig
    transformer: TransformerConfig
    loader: LoaderConfig

    @classmethod
    def load(cls) -> Config:
        return cls(
            extractor=ExtractorConfig.from_env(),
            transformer=TransformerConfig.from_env(),
            loader=LoaderConfig.from_env(),
        )
