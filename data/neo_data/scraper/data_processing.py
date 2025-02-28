from abc import ABC, abstractmethod
from omegaconf import DictConfig
from queue import Queue

import logging
import pandas as pd
import numpy as np
from enum import Enum

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


class DIAMETER(str, Enum):
    ESTIMATED_DIAMETER: str = "estimated_diameter"
    METERS: str = "meters"


class ORBIT(str, Enum):
    ORBITAL_DATA: str = "orbital_data"


class STRINGS(str, Enum):
    NO_VAL: str = "N/A"

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


class DataProcessor(ABC):
    """ The DataProcessor class serves as an interface for various implementations that can process NASA NEO data according to the rqeuirements of the application.
    Attributes:
        cfg (DictConfig): Configuration object containing data processing settings.
        queue (Queue): A queue object to store processed data.
    """

    def __init__(self, cfg: DictConfig, queue: Queue):
        self.cfg = cfg
        self.queue = queue

    @abstractmethod
    def process_data(self):
        """ Implementations of the DataProcessor class must implement this method to process the NEO data in the queue.
        """
        pass


class PrintProcessor(DataProcessor):
    """ The PrintProcessor class is a simple implementation of the DataProcessor interface that prints the data to the console.
    """

    def process_data(self):
        """ Prints the NEO data from the queue to the console.
        """
        logging.info("Processing data...")
        while True:
            item = self.queue.get()
            if item is None:
                break
            print("-----------------------------------")
            print(item["page"])
            print("-----------------------------------")
            self.queue.task_done()
    

class PandasDFProcessor(DataProcessor):
    """ The PandasDFProcessor class is an implementation of the DataProcessor interface that processes the data into a pandas DataFrame.
    """

    def __init__(self, cfg: DictConfig, queue: Queue):
        super().__init__(cfg, queue)
        self.neo_data = pd.DataFrame(columns=[col.value for col in NEO_COLS])
        self.aggregated_data = None

    def _add_neo_row(self, neo_item: dict):
        neo_row = [
            neo_item.get(COL_NAMES.ID.value, STRINGS.NO_VAL),
            neo_item.get(COL_NAMES.NEO_REFERENCE_ID.value, STRINGS.NO_VAL),
            neo_item.get(COL_NAMES.NAME.value, STRINGS.NO_VAL),
            neo_item.get(COL_NAMES.NAME_LIMITED.value, STRINGS.NO_VAL),
            neo_item.get(COL_NAMES.DESIGNATION.value, STRINGS.NO_VAL),
            neo_item.get(COL_NAMES.NASA_JPL_URL.value, STRINGS.NO_VAL),
            neo_item.get(COL_NAMES.ABSOLUTE_MAGNITUDE_H.value, STRINGS.NO_VAL),
            neo_item.get(COL_NAMES.IS_POTENTIALLY_HAZARDOUS_ASTEROID.value, STRINGS.NO_VAL),
            neo_item.get(DIAMETER.ESTIMATED_DIAMETER.value, {}).get(DIAMETER.METERS.value, {}).get(COL_NAMES.ESTIMATED_DIAMETER_MIN.value, STRINGS.NO_VAL),
            neo_item.get(DIAMETER.ESTIMATED_DIAMETER.value, {}).get(DIAMETER.METERS.value, {}).get(COL_NAMES.ESTIMATED_DIAMETER_MAX.value, STRINGS.NO_VAL),
            neo_item.get(ORBIT.ORBITAL_DATA.value, {}).get(COL_NAMES.FIRST_OBSERVATION_DATE.value, STRINGS.NO_VAL),
            neo_item.get(ORBIT.ORBITAL_DATA.value, {}).get(COL_NAMES.LAST_OBSERVATION_DATE.value, STRINGS.NO_VAL),
            neo_item.get(ORBIT.ORBITAL_DATA.value, {}).get(COL_NAMES.OBSERVATIONS_USED.value, STRINGS.NO_VAL),
            neo_item.get(ORBIT.ORBITAL_DATA.value, {}).get(COL_NAMES.ORBITAL_PERIOD.value, STRINGS.NO_VAL)
        ]
        neo_row_df = pd.DataFrame([neo_row], index=[0], columns=self.neo_data.columns)
        self.neo_data = pd.concat([self.neo_data, neo_row_df], ignore_index=True)

    def _process_neo_data(self, item: dict):
        """ Processes the NEO data from the queue into a pandas DataFrame.
        """
        neo_list = item["near_earth_objects"]
        for neo_item in neo_list:
            self._add_neo_row(neo_item)

    def process_data(self):
        """ Processes the NEO data from the queue into a pandas DataFrame.
        """
        logging.info("Processing data...")
        while True:
            item = self.queue.get()
            if item is None:
                break
            self._process_neo_data(item)
            self.queue.task_done()
        
        logging.info(f"Processed data length: {len(self.neo_data)}")
        logging.info("Data processing complete.")
        print(self.neo_data.head())