# File: data/tests/test___init__.py

import builtins
import os
from unittest.mock import patch, mock_open, MagicMock

import tekmetric_data.__init__ as tdi


def test_init_logging_reads_yaml_and_sets_levels(monkeypatch):
    fake_yaml = """
loggers:
  '':
    level: INFO
  tekmetric:
    level: WARNING
"""
    dict_config_mock = MagicMock()
    monkeypatch.setattr(tdi, "dictConfig", dict_config_mock)
    monkeypatch.setattr(tdi, "yaml", MagicMock(safe_load=lambda x: {
        "loggers": {"": {"level": "INFO"}, "tekmetric": {"level": "WARNING"}}
    }))
    with patch.object(builtins, "open", mock_open(read_data=fake_yaml)):
        with patch.dict(os.environ, {"LOG_LEVEL": "DEBUG", "TEK_LOG_LEVEL": "ERROR"}):
            tdi.init_logging()
    config = dict_config_mock.call_args[0][0]
    assert config["loggers"][""]["level"] == "DEBUG"
    assert config["loggers"]["tekmetric"]["level"] == "ERROR"


def test_parse_args_defaults(monkeypatch):
    test_args = ["prog"]
    monkeypatch.setattr("sys.argv", test_args)
    args = tdi.parse_args()
    assert args.api_key == "DEMO_KEY"
    assert args.page_size == 20
    assert args.num_pages == 2
    assert args.metric == "close_approach"
    assert args.output_type == "disk"
    assert args.output_dir == "output"


def test_parse_args_custom(monkeypatch):
    test_args = [
        "prog", "--api-key", "KEY", "--page-size", "5", "--num-pages", "1",
        "--metric", "m", "--output-type", "s3", "--output-dir", "d"
    ]
    monkeypatch.setattr("sys.argv", test_args)
    args = tdi.parse_args()
    assert args.api_key == "KEY"
    assert args.page_size == 5
    assert args.num_pages == 1
    assert args.metric == "m"
    assert args.output_type == "s3"
    assert args.output_dir == "d"
