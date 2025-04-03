from abc import ABC, abstractmethod
from omegaconf import DictConfig
from queue import Queue

import logging
import pandas as pd
import numpy as np
from enum import Enum
import os

class COL_NAMES(str, Enum):
    ID: str = "id"
    NEO_REFERENCE_ID: str = "neo_reference_id"
    NAME: str = "name"
    NAME_LIMITED: str = "name_limited"
    DESIGNATION: str = "designation"
    NASA_JPL_URL: str = "nasa_jpl_url"
    ABSOLUTE_MAGNITUDE_H: str = "absolute_magnitude_h"
    IS_POTENTIALLY_HAZARDOUS_ASTEROID: str = "is_potentially_hazardous_asteroid"
    ESTIMATED_DIAMETER_MIN: str = "estimated_diameter_min"
    ESTIMATED_DIAMETER_MAX: str = "estimated_diameter_max"
    FIRST_OBSERVATION_DATE: str = "first_observation_date"
    LAST_OBSERVATION_DATE: str = "last_observation_date"
    OBSERVATIONS_USED: str = "observations_used"
    ORBITAL_PERIOD: str = "orbital_period"
    CLOSEST_MISS_DISTANCE: str = "closest_miss_distance"
    CLOSEST_APPROACH_DATE: str = "closest_approach_date"
    CLOSEST_RELATIVE_VELOCITY: str = "closest_relative_velocity"
    YEAR: str = "year"
    CLOSE_APPROACH_COUNT: str = "close_approach_count"
    NEO_COUNT: str = "neo_count"


class DIAMETER(str, Enum):
    ESTIMATED_DIAMETER: str = "estimated_diameter"
    METERS: str = "meters"


class ORBIT(str, Enum):
    ORBITAL_DATA: str = "orbital_data"

class APPROACH(str, Enum):
    CLOSE_APPROACH_DATA: str = "close_approach_data"
    CLOSE_APPROACH_DATE: str = "close_approach_date"
    RELATIVE_VELOCITY: str = "relative_velocity"
    KILMETERS_PER_SECOND: str = "kilometers_per_second"
    MISS_DISTANCE: str = "miss_distance"
    ASTRONOMICAL: str = "astronomical"
    KILOMETERS: str = "kilometers"


class PLACEHOLDERS(str, Enum):
    STR_NO_VAL: str = "N/A"
    NUM_NO_VAL: float = np.nan
    BOOL_NO_VAL: bool = False

NEO_COLS = [
        COL_NAMES.ID,
        COL_NAMES.NEO_REFERENCE_ID,
        COL_NAMES.NAME,
        COL_NAMES.NAME_LIMITED,
        COL_NAMES.DESIGNATION,
        COL_NAMES.NASA_JPL_URL,
        COL_NAMES.ABSOLUTE_MAGNITUDE_H,
        COL_NAMES.IS_POTENTIALLY_HAZARDOUS_ASTEROID,
        COL_NAMES.ESTIMATED_DIAMETER_MIN,
        COL_NAMES.ESTIMATED_DIAMETER_MAX,
        COL_NAMES.FIRST_OBSERVATION_DATE,
        COL_NAMES.LAST_OBSERVATION_DATE,
        COL_NAMES.OBSERVATIONS_USED,
        COL_NAMES.ORBITAL_PERIOD,
]

APPROACH_COLS = [
        COL_NAMES.ID,
        APPROACH.CLOSE_APPROACH_DATE,
        APPROACH.RELATIVE_VELOCITY,
        APPROACH.KILOMETERS,
        APPROACH.ASTRONOMICAL,
]

class DataProcessor(ABC):
    """ The DataProcessor class serves as an interface for various implementations that can process NASA NEO data according to the rqeuirements
    of the application.
    As it is designed now, the DataProcessor requires a queue object from which it reads the data to process. A better design would be to instead
    use some sort of data transport wrapper object which abstracts the method of data transport (e.g. queue, file, etc.) from the DataProcessor.
    Attributes:
        cfg (DictConfig): Configuration object containing data processing settings.
        queue (Queue): A queue object to store processed data.
    """

    def __init__(self, cfg: DictConfig, queue: Queue):
        self.cfg = cfg
        self.queue = queue

    @abstractmethod
    def save_data(self):
        """Saves the processed data to a file."""
        pass
    
    @abstractmethod
    def do_aggregations(self):
        """Performs aggregations on the processed data."""
        pass
    
    @abstractmethod
    def process_neo_item(self, item: dict):
        """Processes a single NEO item. Its implementation should take care of accumulating the neo data and approach data."""
        pass
    
    def process_data(self):
        """Processes the data from the queue."""
        logging.info("Processing data...")

        while True:
            item = self.queue.get()
            if item is None:
                break
            self.process_neo_item(item)
            self.queue.task_done()

        logging.info("Data processing complete.")
    
    def run_processing(self):
        """Runs the data processing."""
        self.process_data()
        self.do_aggregations()
        self.save_data()


class PrintProcessor(DataProcessor):
    """The PrintProcessor class is a simple implementation of the DataProcessor interface that prints the data to the console."""

    def process_neo_item(self, item):
        print("-----------------------------------")
        print(item["page"])
        print("-----------------------------------")    

class PandasDFProcessor(DataProcessor):
    """The PandasDFProcessor class is an implementation of the DataProcessor interface that processes the data into a pandas DataFrame."""

    def __init__(self, cfg: DictConfig, queue: Queue):
        super().__init__(cfg, queue)
        self.neo_data = pd.DataFrame(columns=[col.value for col in NEO_COLS])
        self.neo_approach_data = pd.DataFrame(columns=[col.value for col in APPROACH_COLS])
        self.close_approach_count = 0
        self.yearly_approach_counts = None

    def _add_neo_row(self, neo_item: dict):
        """Adds a new row to the neo_data DataFrame with information from the given neo_item dictionary which comes from the NASA NEO Service API call.
        Parameters:
        neo_item (dict): A dictionary containing Near-Earth Object (NEO) data. The dictionary is expected to have keys
                         corresponding to the column names defined in COL_NAMES, DIAMETER, and ORBIT enums.
        Returns:
        None
        """
        neo_row = [
            neo_item.get(COL_NAMES.ID.value, PLACEHOLDERS.STR_NO_VAL.value),
            neo_item.get(COL_NAMES.NEO_REFERENCE_ID.value, PLACEHOLDERS.STR_NO_VAL.value),
            neo_item.get(COL_NAMES.NAME.value, PLACEHOLDERS.STR_NO_VAL.value),
            neo_item.get(COL_NAMES.NAME_LIMITED.value, PLACEHOLDERS.STR_NO_VAL.value),
            neo_item.get(COL_NAMES.DESIGNATION.value, PLACEHOLDERS.STR_NO_VAL.value),
            neo_item.get(COL_NAMES.NASA_JPL_URL.value, PLACEHOLDERS.STR_NO_VAL.value),
            neo_item.get(COL_NAMES.ABSOLUTE_MAGNITUDE_H.value, PLACEHOLDERS.NUM_NO_VAL),
            neo_item.get(COL_NAMES.IS_POTENTIALLY_HAZARDOUS_ASTEROID.value, PLACEHOLDERS.BOOL_NO_VAL.value),
            neo_item.get(DIAMETER.ESTIMATED_DIAMETER.value, {}).get(DIAMETER.METERS.value, {}).get(COL_NAMES.ESTIMATED_DIAMETER_MIN.value, PLACEHOLDERS.NUM_NO_VAL.value),
            neo_item.get(DIAMETER.ESTIMATED_DIAMETER.value, {}).get(DIAMETER.METERS.value, {}).get(COL_NAMES.ESTIMATED_DIAMETER_MAX.value, PLACEHOLDERS.NUM_NO_VAL.value),
            neo_item.get(ORBIT.ORBITAL_DATA.value, {}).get(COL_NAMES.FIRST_OBSERVATION_DATE.value, PLACEHOLDERS.STR_NO_VAL.value),
            neo_item.get(ORBIT.ORBITAL_DATA.value, {}).get(COL_NAMES.LAST_OBSERVATION_DATE.value, PLACEHOLDERS.STR_NO_VAL.value),
            neo_item.get(ORBIT.ORBITAL_DATA.value, {}).get(COL_NAMES.OBSERVATIONS_USED.value, PLACEHOLDERS.NUM_NO_VAL.value),
            neo_item.get(ORBIT.ORBITAL_DATA.value, {}).get(COL_NAMES.ORBITAL_PERIOD.value, PLACEHOLDERS.STR_NO_VAL.value),
        ]
        neo_row_df = pd.DataFrame([neo_row], index=[0], columns=self.neo_data.columns)
        self.neo_data = pd.concat([self.neo_data, neo_row_df], ignore_index=True)

    def _add_approach_row(self, neo_id: str, approach_item: dict):
        """Adds a new row to the neo_approach_data DataFrame with information from the given approach_item dictionary which comes from the 
        NASA NEO Service API call.
        Parameters:
        neo_id (str): The Near-Earth Object (NEO) ID.
        approach_item (dict): A dictionary containing NEO approach data. The dictionary is expected to have keys
                              corresponding to the column names defined in APPROACH enum.
        Returns:
        None
        """
        approach_row = [
            neo_id,
            approach_item.get(APPROACH.CLOSE_APPROACH_DATE.value, PLACEHOLDERS.STR_NO_VAL.value),
            approach_item.get(APPROACH.RELATIVE_VELOCITY.value, {}).get(APPROACH.KILMETERS_PER_SECOND.value, PLACEHOLDERS.STR_NO_VAL.value),
            approach_item.get(APPROACH.MISS_DISTANCE.value, {}).get(APPROACH.KILOMETERS.value, PLACEHOLDERS.STR_NO_VAL.value),
            approach_item.get(APPROACH.MISS_DISTANCE.value, {}).get(APPROACH.ASTRONOMICAL.value, PLACEHOLDERS.STR_NO_VAL.value),
        ]

        approach_row_df = pd.DataFrame([approach_row], index=[0], columns=self.neo_approach_data.columns)
        self.neo_approach_data = pd.concat([self.neo_approach_data, approach_row_df], ignore_index=True)

    def _process_approach_data(self, neo_item: dict):
        """Processes the NEO approach data from the queue into a pandas DataFrame."""
        approach_data = neo_item.get(APPROACH.CLOSE_APPROACH_DATA.value, [])
        neo_id = neo_item.get(COL_NAMES.ID.value, PLACEHOLDERS.STR_NO_VAL.value)
        for approach_item in approach_data:
            self._add_approach_row(neo_id, approach_item)

    def _process_closest_approach_data(self):
        """Processes the closest approach data from the neo_approach_data DataFrame and merges it to the neo_data DataFrame."""
        self.neo_approach_data[APPROACH.KILOMETERS.value] = pd.to_numeric(self.neo_approach_data[APPROACH.KILOMETERS.value], errors='coerce')
        self.neo_approach_data[APPROACH.ASTRONOMICAL.value] = pd.to_numeric(self.neo_approach_data[APPROACH.ASTRONOMICAL.value], errors='coerce')

        closest_approaches = self.neo_approach_data.loc[self.neo_approach_data.groupby(COL_NAMES.ID)[APPROACH.KILOMETERS].idxmin()]
        closest_approaches = closest_approaches.reset_index(drop=True)
        closest_approaches = closest_approaches.rename(columns={APPROACH.KILOMETERS: COL_NAMES.CLOSEST_MISS_DISTANCE.value})
        closest_approaches = closest_approaches.rename(columns={APPROACH.CLOSE_APPROACH_DATE: COL_NAMES.CLOSEST_APPROACH_DATE.value})
        closest_approaches = closest_approaches.rename(columns={APPROACH.RELATIVE_VELOCITY: COL_NAMES.CLOSEST_RELATIVE_VELOCITY.value})
        closest_approaches = closest_approaches.drop(columns=[APPROACH.ASTRONOMICAL])
        
        # Some NEOs do not have any close approach data, so merging using left join to preserve all NEOs
        self.neo_data = pd.merge(self.neo_data, closest_approaches, on=COL_NAMES.ID, how='left')

    def _filtered_approach_count(self, threshold: float = 0.2):
        """Calculates the number of close approaches that are within the given threshold distance and stores the value in self.close_approach_count.
        Parameters:
        threshold (float): The distance threshold in kilometers.
        """
        self.close_approach_count = (self.neo_approach_data[APPROACH.ASTRONOMICAL] <= threshold).sum()
        logging.info(f"Total number of close approaches within {threshold} astronomical units is: {self.close_approach_count}.")
    
    def _yearly_approach_count(self):
        """Calculates the number of close approaches per year and stores the values in self.yearly_approach_counts."""
        self.neo_approach_data[COL_NAMES.YEAR.value] = pd.to_datetime(self.neo_approach_data[APPROACH.CLOSE_APPROACH_DATE.value]).dt.year
        self.yearly_approach_counts = self.neo_approach_data.groupby(COL_NAMES.YEAR.value).size().reset_index(name=COL_NAMES.CLOSE_APPROACH_COUNT.value)
        logging.info("Yearly close approach counts:")
        logging.info(self.yearly_approach_counts.head())

    def process_neo_item(self, item: dict):
        """Processes the NEO data from the queue into a pandas DataFrame."""
        neo_list = item["near_earth_objects"]
        for neo_item in neo_list:
            self._add_neo_row(neo_item)
            self._process_approach_data(neo_item)

    def do_aggregations(self):
        """Performs aggregations on the processed data."""
        logging.debug(self.neo_data.head())
        logging.debug("===================================")
        logging.debug(self.neo_approach_data.head())
        logging.debug("===================================")

        self._process_closest_approach_data()
        self._filtered_approach_count()
        self._yearly_approach_count()

        logging.debug("neo data length: ", len(self.neo_data))
        logging.debug(self.neo_data.head())


    def save_data(self):
        """Saves the processed data to parquet/text files. This includes the neo data, the yearly approach counts and the total close approach count."""
        data_dir = os.path.join(self.cfg.scraper.persist.output_dir, self.cfg.scraper.persist.root_dir)
        logging.info(f"Data directory: {data_dir}")
        os.makedirs(data_dir, exist_ok=True)

        neo_file_path = os.path.join(data_dir, self.cfg.scraper.persist.neo_data_file)
        self.neo_data.to_parquet(neo_file_path, index=False)

        yearly_approach_file_path = os.path.join(data_dir, self.cfg.scraper.persist.yearly_approach_file)
        self.yearly_approach_counts.to_parquet(yearly_approach_file_path, index=False)

        approach_count_file_path = os.path.join(data_dir, self.cfg.scraper.persist.approach_count_file)
        approach_count_df = pd.DataFrame([[self.close_approach_count, len(self.neo_data)]],
                                        index=[0],
                                        columns=[COL_NAMES.CLOSE_APPROACH_COUNT.value, COL_NAMES.NEO_COUNT.value])
        approach_count_df.to_parquet(approach_count_file_path, index=False)
        print(approach_count_df.head())
