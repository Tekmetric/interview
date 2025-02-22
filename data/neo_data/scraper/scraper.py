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
        producer = threading.Thread(target=self.data_handler.download_data, name="producer")
        consumer = threading.Thread(target=self.data_processor.process_data, name="consumer")

        logging.info("Starting data download...")
        producer.start()
        logging.info("Starting data processing...")
        consumer.start()
        producer.join()
        consumer.join()
        logging.info("Data scraping complete.")
