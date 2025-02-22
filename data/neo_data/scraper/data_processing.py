from abc import ABC, abstractmethod
from omegaconf import DictConfig
from queue import Queue

import logging
import pandas as pd

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

    def process_data(self):
        """ Processes the NEO data from the queue into a pandas DataFrame.
        """
        pass