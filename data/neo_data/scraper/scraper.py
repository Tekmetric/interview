from omegaconf import DictConfig
from .data_processing import DataProcessor
from .data_gathering import DataHandler

import threading
import logging
class Scraper:

    def __init__(self, cfg: DictConfig, data_processor: DataProcessor, data_handler: DataHandler):
        self.cfg = cfg
        self.data_processor = data_processor
        self.data_handler = data_handler

    def scrape(self):
        """
        Scrapes data by starting two threads: one for downloading data and one for processing data.
        This method initializes and starts two threads:
        - The producer thread, which is responsible for downloading data.
        - The consumer thread, which is responsible for processing the downloaded data.
        The method logs the start of each thread and waits for both threads to complete before logging that the data scraping is complete.
        """
        producer = threading.Thread(target=self.data_handler.download_data, name="producer")
        consumer = threading.Thread(target=self.data_processor.run_processing, name="consumer")

        logging.info("Starting data download...")
        producer.start()
        logging.info("Starting data processing...")
        consumer.start()
        producer.join()
        consumer.join()
        logging.info("Data scraping complete.")
