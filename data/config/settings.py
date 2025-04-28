import os
from pathlib import Path
from typing import List

from pydantic import BaseModel


class ComponentConfig(BaseModel):
    """
    Configuration for a component in the system.
    """

    path: str
    class_name: str
    args: dict

    def process_env_vars(self) -> None:
        """
        Process environment variables in the args dictionary.

        Raises:
            ValueError: If an environment variable is not found in the system.
        """
        for key, value in self.args.items():
            if isinstance(value, str) and value.startswith("env:"):
                env_var = value.split(":", 1)[1]
                if env_var not in os.environ:
                    raise ValueError(f"Environment variable {env_var} not found")
                self.args[key] = os.environ[env_var]


class Settings(BaseModel):
    """
    Configuration settings for the entire system.
    """

    scraper: ComponentConfig
    persistence: ComponentConfig
    aggregators: List[ComponentConfig]

    @classmethod
    def from_json(cls, config_path: Path) -> "Settings":
        """
        Load settings from a JSON configuration file.

        Args:
            config_path (Path): Path to the configuration file.

        Returns:
            Settings: An instance of the Settings class populated with data from the configuration file.
        """
        with open(config_path) as f:
            settings = cls.parse_raw(f.read())
            [
                component.process_env_vars()
                for component in [settings.scraper, settings.persistence] + settings.aggregators
            ]
            return settings
