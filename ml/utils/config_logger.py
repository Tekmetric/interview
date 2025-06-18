import json

from config import CONFIG


def print_config():
    print("🔧 Loaded Config:\n")
    print(json.dumps(CONFIG, indent=4))
