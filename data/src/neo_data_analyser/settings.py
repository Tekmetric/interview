import sys
from functools import lru_cache

import structlog
from pydantic import Field, ValidationError
from pydantic_settings import BaseSettings, SettingsConfigDict

logger = structlog.get_logger()


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_ignore_empty=True)
    neo_api_key: str = Field(alias="NEO_API_KEY")
    neo_api_url: str = Field(alias="NEO_API_URL")


@lru_cache(1)
def get_settings() -> Settings:
    return Settings()


def check_settings() -> None:
    try:
        get_settings()
    except ValidationError as exc:
        logger.error(  # noqa: TRY400
            "Environment is not configured properly.",
            reason=[(error["type"], error["loc"]) for error in exc.errors()],
        )
        sys.exit(1)
