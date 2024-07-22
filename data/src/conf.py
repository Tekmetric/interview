from pydantic_settings import BaseSettings


class Settings(BaseSettings):

    data_dot_gov_api_key: str = ''


settings = Settings()
