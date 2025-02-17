from omegaconf import DictConfig
from .data_processing import DataProcessor
from .request_handler import RequestHandler



class Scraper:
    """ The Scraper is responsible for scraping data from a range of pages and persisting it. To do so, it uses a given DataPersister and RequestHandler.

    Attributes
    ----------
    cfg : DictConfig
        Configuration object containing scraper settings.
    persister : DataPersister
        Object responsible for persisting the scraped data.
    request_handler : RequestHandler
        Object responsible for handling page requests.
    Methods
    -------
    scrape():
        Scrapes data from a range of pages and persists the data.
    """

    def __init__(self, cfg: DictConfig, persister: DataProcessor, request_handler: RequestHandler):
        self.cfg = cfg
        self.persister = persister
        self.request_handler = request_handler

    def scrape(self):
        """
        Scrapes data from a range of pages defined in the configuration.
        This method iterates over a range of pages specified by the `start_page` 
        and `end_page` attributes in the scraper configuration. For each page, 
        it fetches the data using the request handler and persists the data 
        using the persister if data is successfully retrieved.
        Returns:
            None
        """

        start_page = self.cfg.scraper.request.start_page
        end_page = self.cfg.scraper.request.end_page

        for page in range(start_page, end_page):
            data = self.request_handler.fetch_page(page)
            if data:
                self.persister.process_json_data(data)
