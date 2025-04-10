import os
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# Get the API key from the environment variable
API_KEY = os.getenv("API_KEY")

# Other configuration settings
BASE_URL = "https://api.nasa.gov/neo/rest/v1/neo/browse"
BASE_PATH = "files"
