from functools import lru_cache

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_ignore_empty=True)
    neo_api_key: str = Field(alias="NEO_API_KEY")
    neo_api_url: str = Field(alias="NEO_API_URL")


@lru_cache(1)
def get_settings() -> Settings:
    return Settings()
