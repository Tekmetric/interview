import pandas as pd

from utils.data_utils import filter_dataset_for_training


def test_no_labels_filtered():
    df = pd.DataFrame({"label": ["A", "A", "B", "B", "C", "C"], "value": range(6)})
    filtered_df, removed = filter_dataset_for_training(df, "label", min_count=2)
    assert len(filtered_df) == 6
    assert removed == []


def test_some_labels_filtered():
    df = pd.DataFrame({"label": ["A", "A", "B", "C", "C", "D"], "value": range(6)})
    filtered_df, removed = filter_dataset_for_training(df, "label", min_count=2)
    assert set(removed) == {"B", "D"}
    assert set(filtered_df["label"]) == {"A", "C"}


def test_all_labels_filtered():
    df = pd.DataFrame({"label": ["X", "Y", "Z"], "value": [1, 2, 3]})
    filtered_df, removed = filter_dataset_for_training(df, "label", min_count=2)
    assert len(filtered_df) == 0
    assert set(removed) == {"X", "Y", "Z"}


def test_custom_min_count():
    df = pd.DataFrame(
        {
            "label": ["a", "a", "a", "b", "b", "c", "c", "c", "c"],
            "value": list(range(9)),
        }
    )
    filtered_df, removed = filter_dataset_for_training(df, "label", min_count=3)
    assert set(removed) == {"b"}
    assert set(filtered_df["label"]) == {"a", "c"}


def test_numeric_labels():
    df = pd.DataFrame({"label": [1, 1, 2, 3, 3, 3], "value": list(range(6))})
    filtered_df, removed = filter_dataset_for_training(df, "label", min_count=2)
    assert set(removed) == {2}
    assert set(filtered_df["label"]) == {1, 3}
