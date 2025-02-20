
import os

from dotenv import load_dotenv


load_dotenv() # load environment variables from .env file

class Config:
    api_key = None
    browse_api_url = None
    storage_path = None

    def __init__(self):
        self.api_key = os.getenv("NASA_API_KEY")
        self.browse_api_url = os.getenv("NASA_BROWSE_API_URL")
        self.storage_path = os.getenv("STORAGE_PATH")
        self.validate()

    def validate(self):
        if not self.api_key:
            raise ValueError("NASA_API_KEY is not set")
        if not self.browse_api_url:
            raise ValueError("NASA_BROWSE_API_URL is not set")
        if not self.storage_path:
            raise ValueError("STORAGE_PATH is not set")
