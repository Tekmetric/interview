import requests
from settings import URL

def fetch_data():
    response = requests.get(URL)
    if response.status_code == 200:
        return response.json()
    else:
        print(f'Failed to retrieve data: {response.status_code}')
        return None
