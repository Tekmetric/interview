# File: data/tests/test_metric.py

import pytest

from tekmetric_data.metric import CloseApproachMetric, MetricRegistry


@pytest.fixture
def sample_records():
    return {
        "near_earth_objects": [
            {
                "close_approach_data": [
                    {
                        "miss_distance": {"astronomical": "0.1"},
                        "close_approach_date": "2024-01-01"
                    },
                    {
                        "miss_distance": {"astronomical": "0.3"},
                        "close_approach_date": "2023-05-01"
                    }
                ]
            },
            {
                "close_approach_data": [
                    {
                        "miss_distance": {"astronomical": "0.05"},
                        "close_approach_date": "2024-02-01"
                    }
                ]
            }
        ]
    }


def test_close_approach_metric_counts(sample_records):
    metric = CloseApproachMetric(miss_limit=0.2)
    metric.add(sample_records)
    assert metric.number_of_near_misses == 2
    assert metric.per_year_miss["2024"] == 2
    assert "2023" not in metric.per_year_miss


def test_close_approach_metric_repr():
    metric = CloseApproachMetric()
    assert repr(metric) == "Close Approach Metric"


def test_metric_factory_valid():
    metric = MetricRegistry.get("close_approach")
    assert isinstance(metric, CloseApproachMetric)


def test_metric_factory_invalid():
    with pytest.raises(ValueError):
        MetricRegistry.get("unknown_metric")
