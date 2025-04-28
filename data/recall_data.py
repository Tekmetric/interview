import argparse
import sys
from pathlib import Path

from dotenv import load_dotenv

from config.settings import Settings
from orchestration.orchestrator import Orchestrator


def parse_args():
    parser = argparse.ArgumentParser(description="Process recall data using a configuration file.")
    parser.add_argument("config_file", type=Path, help="Path to the configuration file")
    return parser.parse_args()


def main():
    args = parse_args()

    try:
        if not args.config_file.is_file():
            print(f"Error: Config file not found: {args.config_file}")
            sys.exit(1)

        # Load environment variables from .env file if it exists
        load_dotenv()

        # Load settings from config file
        settings = Settings.from_json(args.config_file)

        # Create and run orchestrator
        orchestrator = Orchestrator(settings)
        orchestrator.run()

    except Exception as e:
        print(f"Error: {str(e)}")
        sys.exit(1)


if __name__ == "__main__":
    main()
