import argparse
import hydra
from omegaconf import DictConfig
from neo_data.scraper.scraper import Scraper
from neo_data.scraper.persistence import PrintPersister
from neo_data.scraper.request_handler import RequestHandler

@hydra.main(config_path="config", config_name="scraper")
def recall_data(cfg: DictConfig) -> None:
    print(cfg)
    persister = PrintPersister()
    request_handler = RequestHandler(cfg)
    scraper = Scraper(cfg, persister, request_handler)

    scraper.scrape()


if __name__ == "__main__":
    recall_data()