from config import CONFIG
from src.preprocess import normalize_text

# Ensure config is predictable during testing
CONFIG["text"]["normalize_case"] = True
CONFIG["text"]["remove_punctuation"] = True


def test_normalize_basic():
    input_text = "Engine Mount Bracket"
    expected = "engine mount bracket"
    assert normalize_text(input_text) == expected


def test_normalize_empty_string():
    input_text = "   "
    expected = ""
    assert normalize_text(input_text) == expected


def test_normalize_special_characters():
    input_text = "Brakes & Rotors! (Front)"
    expected = (
        "brakes  rotors front"  # '&', '!', and '()' removed, double space left from '&'
    )
    assert normalize_text(input_text) == expected
