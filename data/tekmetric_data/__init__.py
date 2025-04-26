import argparse
import os
from logging.config import dictConfig

import yaml


def init_logging() -> None:
    """
    Initialize logging configuration from a YAML file.
    """
    with open("tekmetric_data/logging_config.yaml", 'r') as f:
        config = yaml.safe_load(f.read())
    tekmetric_log_level_override = os.environ.get("TEK_LOG_LEVEL")
    log_level_override = os.environ.get("LOG_LEVEL")
    if log_level_override is not None:
        config['loggers']['']['level'] = log_level_override
    if tekmetric_log_level_override is not None:
        config['loggers']['tekmetric']['level'] = tekmetric_log_level_override
    dictConfig(config)


def parse_args() -> argparse.Namespace:
    """
    Parse command line arguments.
    :return: parsed arguments as a Namespace object
    """
    parser = argparse.ArgumentParser(
        prog="tekmetric_data",
        description="Tekmetric Data",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter
    )
    parser.add_argument("--api-key", type=str, default="DEMO_KEY", help="NASA API key")
    parser.add_argument("--page-size", type=int, default=20, help="Limit number of neo items on each page")
    parser.add_argument("--num-pages", type=int, default=10, help="Number of pages to fetch")
    parser.add_argument("--metric", type=str, default="close_approach", help="Metric type")  # TODO choices
    parser.add_argument("--output-type", type=str, default="disk", help="Output directory")  # TODO choices
    parser.add_argument("--output-dir", type=str, default="output", help="Output directory")
    args = parser.parse_args()
    return args
