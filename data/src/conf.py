from pydantic_settings import BaseSettings


class Settings(BaseSettings):

    data_dot_gov_api_key: str = 'UIC8M8NzyuAC2CJiZE0KbIGNvKNRLhIWbzlKO2Eu   '


settings = Settings()
