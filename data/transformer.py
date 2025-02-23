import pandas as pd
from abc import ABC, abstractmethod
from typing import Dict, List, Optional
from data import NEOData, Pages
from datetime import datetime


def get_nested_value(nested: dict, path: str, default=None) -> any:
    """
    Get value from nested dict based on a path.
    """
    keys = path.split('.')
    value = nested
    for key in keys:
        if not isinstance(value, dict):
            return None
        value = value.get(key)
        if not value:
            return None
    return value


def date_to_year(date: str) -> int:
    try:
        return datetime.strptime(date, "%Y-%m-%d").year
    except ValueError:
        return None


def count_approaches_under_threshold(approaches, threshold, path) -> int:
    count = 0
    for approach in approaches:
        value = get_nested_value(approach, path, float("inf"))
        if float(value) < threshold:
            count += 1
    return count


class Transformer(ABC):
    @abstractmethod
    def process(self, raw_data: Pages, columns_to_keep: List) -> pd.DataFrame:
        pass


class Standard(Transformer):
    aggregation_rules: Dict[str, dict]

    def __init__(self, aggregation_rules: Dict[str, dict]) -> None:
        self.aggregation_rules = aggregation_rules

    @staticmethod
    def process_estimated_diameter(estimated_diameter: dict) -> tuple[Optional[float], Optional[float]]:
        estimated_diameter_meters = estimated_diameter.get("meters", {})
        estimated_diameter_min = estimated_diameter_meters.get("estimated_diameter_min", None)
        estimated_diameter_max = estimated_diameter_meters.get("estimated_diameter_max", None)

        return estimated_diameter_min, estimated_diameter_max

    @staticmethod
    def process_close_approach_data(
            data: list[dict],
    ) -> tuple[Optional[list[dict]], Optional[float], Optional[float], Optional[str], Optional[float], Optional[int]]:
        if len(data) == 0:
            return None, None, None, None, None, None

        # add closest_approach_year to list items
        data = [{**item, "close_approach_year": date_to_year(item.get("close_approach_date", ""))} for item in data]
        # sort close approach data based on closest miss distance
        close_approach_data_ascending = sorted(
            data,
            key=lambda x: x.get("miss_distance", {}).get("astronomical", float("inf"))
        )

        # extract information from closest approach
        first_entry = close_approach_data_ascending[0]
        closest_approach_miss_distance_in_kilometers = first_entry.get("miss_distance", {}).get("kilometers", None)
        closest_approach_miss_distance_astronomical = float(first_entry.get("miss_distance", {}).get("astronomical", 0))
        closest_approach_date = first_entry.get("close_approach_date", None)
        relative_velocity = first_entry.get("relative_velocity", {}).get("kilometers_per_second", None)
        closest_approach_year = first_entry.get("close_approach_year", None)

        return (
            close_approach_data_ascending,
            closest_approach_miss_distance_in_kilometers,
            relative_velocity,
            closest_approach_date,
            closest_approach_miss_distance_astronomical,
            closest_approach_year
        )

    @staticmethod
    def process_orbital_data(orbital_data: dict) -> tuple[Optional[str], Optional[str], Optional[int]]:
        return (
            orbital_data.get("first_observation_date", None),
            orbital_data.get("last_observation_date", None),
            orbital_data.get("observations_used", None)
        )

    def process_neo_entry(self, entry: dict) -> NEOData:
        estimated_diameter_min, estimated_diameter_max = self.process_estimated_diameter(entry.get("estimated_diameter", {}))
        close_approach_data = entry.get("close_approach_data", [])
        (
            close_approach_data,
            closest_approach_miss_distance_in_kilometers,
            relative_velocity,
            closest_approach_date,
            closest_approach_miss_distance_astronomical,
            closest_approach_year
        ) = self.process_close_approach_data(close_approach_data)
        first_obs_date, last_obs_date, observations_used = self.process_orbital_data(entry.get("orbital_data", {}))
        orbital_period = entry.get("orbital_data", {}).get("orbital_period", None)

        return NEOData(
            id=str(entry["id"]),
            neo_reference_id=str(entry["neo_reference_id"]),
            name=str(entry["name"]),
            name_limited=entry.get("name_limited", None),
            designation=str(entry["designation"]),
            nasa_jpl_url=str(entry["nasa_jpl_url"]),
            absolute_magnitude_h=float(entry["absolute_magnitude_h"]),
            is_potentially_hazardous_asteroid=bool(entry["is_potentially_hazardous_asteroid"]),
            minimum_estimated_diameter_in_meters=estimated_diameter_min,
            maximum_estimated_diameter_in_meters=estimated_diameter_max,
            closest_approach_miss_distance_in_kilometers=closest_approach_miss_distance_in_kilometers,
            closest_approach_date=closest_approach_date,
            closest_approach_relative_velocity_in_kilometers_per_second=relative_velocity,
            first_observation_date=first_obs_date,
            last_observation_date=last_obs_date,
            observations_used=observations_used,
            orbital_period=orbital_period,
            closest_approach_miss_distance_astronomical=closest_approach_miss_distance_astronomical,
            closest_approach_year=closest_approach_year,
            close_approach_data=close_approach_data,
        )

    @staticmethod
    def aggregation_close_approaches_under_threshold(df: pd.DataFrame, rule: dict) -> pd.DataFrame:
        # total approaches under threshold per item
        df["total_approaches_under_threshold"] = df[rule["column"]].apply(
            lambda x: count_approaches_under_threshold(x, rule["threshold"], rule["path"]) if x is not None else 0,
        )
        # total approaches under threshold for all items
        return pd.DataFrame([int(df["total_approaches_under_threshold"].sum())], columns=["count"])

    @staticmethod
    def aggregation_approach_yearly_counts(df: pd.DataFrame, rule: dict) -> pd.DataFrame:
        # approach years per item
        df["approach_yearly_counts"] = df[rule["column"]].apply(
            lambda approaches: [
                approach.get('close_approach_year')
                for approach in (approaches or [])
                if approach.get('close_approach_year')
            ]
        )
        # merge all years in a single list with int items
        all_years = []
        for year_list in df["approach_yearly_counts"]:
            for year in year_list:
                all_years.append(int(year))

        # count how many times each year appears in list order by year ASC
        df = pd.DataFrame(pd.Series(all_years).value_counts().sort_index())
        df.index = df.index.astype(int)
        df.index.name = 'year'
        df.columns = ['count']

        return df

    def aggregations_pre_compute(self, df: pd.DataFrame) -> tuple[pd.DataFrame, pd.DataFrame]:
        return (
            self.aggregation_close_approaches_under_threshold(df, self.aggregation_rules["total_approaches_under_threshold"]),
            self.aggregation_approach_yearly_counts(df, self.aggregation_rules["approach_yearly_counts"]),
        )

    @staticmethod
    def clean(df: pd.DataFrame, columns_to_keep: list) -> pd.DataFrame:
        return df
        return df[columns_to_keep]

    def process(self, raw_data: Pages, columns_to_keep: List) -> tuple[pd.DataFrame, pd.DataFrame, pd.DataFrame]:
        processed_data = []
        for page_index, page in enumerate(raw_data):
            for entry in page:
                try:
                    processed_data.append(self.process_neo_entry(entry))
                except Exception as e:
                    log_entry = {
                        "page": page_index,
                        "id": entry.get("id"),
                        "error": str(e)
                    }
                    print(f"Skipping entry: {log_entry}")
                    continue
        
        if not processed_data:
            return pd.DataFrame()
        df = pd.DataFrame(processed_data)
        df['closest_approach_year'] = df['closest_approach_year'].astype(pd.Int64Dtype())

        total_approaches_under_threshold, approach_yearly_counts = self.aggregations_pre_compute(df)

        return self.clean(df, columns_to_keep), total_approaches_under_threshold, approach_yearly_counts
