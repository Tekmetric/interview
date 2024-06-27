from pathlib import Path

from pydantic import AnyUrl
from pydantic_settings import BaseSettings, SettingsConfigDict

PROJECT_ROOT = Path(__file__).parent.parent


class Settings(BaseSettings):
    api_base_url: AnyUrl = "https://data.transportation.gov/"
    api_key: str = ""
    requests_cache: bool = False
    output_bucket: str = str(Path(PROJECT_ROOT, "s3_bucket"))

    model_config = SettingsConfigDict(env_file=Path(PROJECT_ROOT, ".env"))


settings = Settings()
