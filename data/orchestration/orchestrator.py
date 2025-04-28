import importlib
import logging
import sys
from pathlib import Path
from typing import Any

from config.settings import ComponentConfig, Settings

LOGGER = logging.getLogger(__name__)


class Orchestrator:
    """
    Orchestrator class to manage the workflow of scraping, processing, and storing data.
    It initializes components based on the provided settings and runs the main process.
    """

    def __init__(self, settings: Settings):
        """
        Initialize the Orchestrator with the provided settings.

        Args:
            settings (Settings): The settings object containing configuration for all components.
        """
        self.settings = settings
        self.scraper = self._instantiate_component(settings.scraper)
        self.serializer = self._instantiate_component(settings.persistence)
        self.aggregators = [self._instantiate_component(agg_config) for agg_config in settings.aggregators]
        LOGGER.info("Orchestrator successfully initialized pipeline components")

    def run(self) -> None:
        """
        Run the main process of the orchestrator.
        This includes scraping data, processing it, and storing it using the serializer.
        """
        try:
            # Process objects from the scraper
            for raw_obj, processed_obj in self.scraper.objects:
                # Send processed object to serializer
                self.serializer.add(processed_obj)

                # Send raw object to aggregators
                for aggregator in self.aggregators:
                    aggregator.process(raw_obj)

        finally:
            # Ensure everything is flushed even if there's an error, as errors will be logged
            self.serializer.flush()
            for aggregator in self.aggregators:
                aggregator.flush()
            LOGGER.info("Orchestrator finished processing all objects")

    def _instantiate_component(self, config: ComponentConfig) -> Any:
        # Add the project root to sys.path to allow imports
        project_root = Path(__file__).parent.parent
        if str(project_root) not in sys.path:
            sys.path.append(str(project_root))

        # Import the module and get the class
        module_path = config.path.replace("/", ".").replace(".py", "")
        module = importlib.import_module(module_path)
        class_name = getattr(module, config.class_name)

        # Instantiate the class with the provided arguments
        return class_name(**config.args)
