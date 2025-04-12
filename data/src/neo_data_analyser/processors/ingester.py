from datetime import date

import pandas as pd
import structlog

from neo_data_analyser.models import NearEarthObject
from neo_data_analyser.storage import Storage

from .processor import Processor

logger = structlog.get_logger()


class Ingester(Processor):
    def __init__(self, storage: Storage, file_name_prefix: str = "neo_data") -> None:
        self._ingested_files_count = 0
        self._storage = storage
        self._file_name_prefix = file_name_prefix

    def _compute_parquet_file_name(self) -> str:
        return f"{self._file_name_prefix}_{self._ingested_files_count}.parquet"

    def _transform_near_earth_object_to_dataframe_row(
        self, near_earth_object: NearEarthObject
    ) -> dict[str, str | float | date | None]:
        return {
            "id": near_earth_object.id,
            "neo_reference_id": near_earth_object.neo_reference_id,
            "name": near_earth_object.name,
            "name_limited": near_earth_object.name_limited,
            "designation": near_earth_object.designation,
            "nasa_jpl_url": near_earth_object.nasa_jpl_url,
            "absolute_magnitude_h": near_earth_object.absolute_magnitude_h,
            "is_potentially_hazardous_asteroid": near_earth_object.is_potentially_hazardous_asteroid,
            "minimum_estimated_diameter_in_m": near_earth_object.estimated_diameter.meters.estimated_diameter_min,
            "maximum_estimated_diameter_in_m": near_earth_object.estimated_diameter.meters.estimated_diameter_max,
            "closest_approach_date": (
                near_earth_object.closest_approach.close_approach_date
                if near_earth_object.closest_approach is not None
                else None
            ),
            "closest_approach_miss_distance_in_km": (
                near_earth_object.closest_approach.miss_distance.kilometers
                if near_earth_object.closest_approach
                else None
            ),
            "closest_approach_relative_velocity_in_kph": (
                near_earth_object.closest_approach.relative_velocity.kilometers_per_hour
                if near_earth_object.closest_approach
                else None
            ),
            "first_observation_date": near_earth_object.orbital_data.first_observation_date,
            "last_observation_date": near_earth_object.orbital_data.last_observation_date,
            "observations_used": near_earth_object.orbital_data.observations_used,
            "orbital_period": near_earth_object.orbital_data.orbital_period,
        }

    def process(self, data: list[NearEarthObject]) -> None:
        """Process the data and save it to a file."""
        logger.info("Ingesting data", objects_count=len(data))
        dataframe = pd.DataFrame([self._transform_near_earth_object_to_dataframe_row(obj) for obj in data])
        file_name = self._compute_parquet_file_name()
        self._storage.store_dataframe_to_parquet_file(
            dataframe=dataframe,
            file_name=file_name,
        )

        logger.info(
            "Saved batch to parquet file",
            file_name=file_name,
            objects_count=len(data),
        )
        self._ingested_files_count += 1

    def finalize(self) -> None: ...
