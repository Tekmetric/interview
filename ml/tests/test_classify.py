from classify import classify_title
from config import CONFIG

sample_titles = [
    "Replace brake pads",
    "Install new alternator",
    "Wheel alignment",
    "Transmission fluid flush",
    "Unrecognized system service",
]


def test_classify_outputs_valid_label_and_source():
    for title in sample_titles:
        label, source = classify_title(title)

        assert isinstance(label, str), f"Label should be string, got {type(label)}"
        assert isinstance(source, str), f"Source should be string, got {type(source)}"
        assert source in {
            "classifier",
            "fallback",
            "unknown",
            "bad input unknown",
        }, f"Unexpected source: {source}"

        # Optional: check label is not empty
        assert label, f"Empty label returned for: {title}"


def test_classify_handles_bad_inputs():
    bad_inputs = [None, "", 123, [], {}]
    for bad in bad_inputs:
        label, source = classify_title(bad)
        assert label == CONFIG["output"]["unknown_label"]
        assert source == "bad input unknown"
