import pandas as pd

from config import CONFIG
from utils.label_utils import build_label

# Ensure predictable config for testing
CONFIG["labeling"]["source_fields"] = ["section", "name"]
CONFIG["labeling"]["separator"] = " :: "


def test_build_label_basic():
    row = pd.Series({"section": "Engine", "name": "Camshaft"})
    assert build_label(row) == "Engine :: Camshaft"


def test_build_label_with_numbers():
    row = pd.Series({"section": "Brakes", "name": 123})
    assert build_label(row) == "Brakes :: 123"


def test_build_label_missing_field():
    row = pd.Series({"section": "Suspension"})  # name is missing
    try:
        build_label(row)
        assert False, "Expected KeyError due to missing field"
    except KeyError:
        pass  # expected


def test_build_label_custom_separator():
    CONFIG["labeling"]["separator"] = " --> "
    row = pd.Series({"section": "Lighting", "name": "Fog Lamp"})
    assert build_label(row) == "Lighting --> Fog Lamp"
    CONFIG["labeling"]["separator"] = " :: "  # Reset after test


def test_build_label_multifield_support():
    # Simulate a config where more than two fields are joined
    CONFIG["labeling"]["source_fields"] = ["section", "name", "extra"]
    row = pd.Series({"section": "Engine", "name": "Valve", "extra": "Rear"})
    assert build_label(row) == "Engine :: Valve :: Rear"
    CONFIG["labeling"]["source_fields"] = ["section", "name"]  # Reset after test
