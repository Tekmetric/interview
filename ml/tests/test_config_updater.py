import os
import tempfile

from utils.config_updater import update_thresholds_in_config


def test_update_thresholds_basic():
    original_content = """
CONFIG = {
    "model": {
        "confidence_threshold": 0.7,
        "max_iterations": 300
    },
    "fallback_matcher": {
        "similarity_threshold": 0.9
    }
}
"""

    with tempfile.NamedTemporaryFile("w+", delete=False) as tmp:
        tmp.write(original_content)
        tmp_path = tmp.name

    try:
        update_thresholds_in_config(0.6, 0.88, config_path=tmp_path)

        with open(tmp_path, "r") as f:
            updated = f.read()

        assert '"confidence_threshold": 0.6' in updated
        assert '"similarity_threshold": 0.88' in updated
        assert '"max_iterations": 300' in updated  # Unchanged line remains
    finally:
        os.remove(tmp_path)


def test_update_thresholds_float_precision():
    original_content = """
CONFIG = {
    "model": {
        "confidence_threshold": 0.75
    },
    "fallback_matcher": {
        "similarity_threshold": 0.95
    }
}
"""
    with tempfile.NamedTemporaryFile("w+", delete=False) as tmp:
        tmp.write(original_content)
        tmp_path = tmp.name

    try:
        update_thresholds_in_config(0.67, 0.89123, config_path=tmp_path)

        with open(tmp_path, "r") as f:
            updated = f.read()

        assert '"confidence_threshold": 0.67' in updated
        assert '"similarity_threshold": 0.89123' in updated
    finally:
        os.remove(tmp_path)
