import hydra
import logging
from omegaconf import DictConfig
from neo_data.scraper.scraper import Scraper
from neo_data.scraper.data_processing import PrintProcessor
from neo_data.scraper.data_gathering import RequestsDataHandler
from queue import Queue

@hydra.main(config_path="config", config_name="scraper")
def recall_data(cfg: DictConfig) -> None:
    logging.basicConfig(level=logging.INFO)
    logging.info("Starting data scraping process...")

    queue = Queue()
    data_handler = RequestsDataHandler(cfg, queue)
    data_processor = PrintProcessor(cfg, queue)
    
    scraper = Scraper(cfg, data_processor, data_handler)
    scraper.scrape()


if __name__ == "__main__":
    recall_data()