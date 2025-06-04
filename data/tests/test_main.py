# File: data/tests/test_main.py

from unittest.mock import MagicMock, patch

import tekmetric_data.__main__ as main_mod


def test_fetch_and_process_success():
    mock_client = MagicMock()
    mock_metric = MagicMock()
    mock_client.browse.return_value = {"foo": "bar"}
    result = main_mod.fetch_and_process(mock_client, mock_metric, 1)
    assert result == {"foo": "bar"}
    mock_metric.add.assert_called_once_with({"foo": "bar"})


def test_fetch_and_process_error_logs_and_returns_none():
    mock_client = MagicMock()
    mock_metric = MagicMock()
    mock_client.browse.return_value = {"error": {"message": "fail"}}
    with patch.object(main_mod.logger, "error") as mock_log:
        result = main_mod.fetch_and_process(mock_client, mock_metric, 2)
    assert result is None
    mock_log.assert_called_once()
    mock_metric.add.assert_not_called()


def test_data_to_record_batch_none_returns_none():
    assert main_mod.data_to_record_batch(None) is None


def test_data_to_record_batch_builds_record_batch(monkeypatch):
    data = {
        "near_earth_objects": [
            {"id": 1, "name": "A"},
            {"id": 2, "name": "B"}
        ]
    }
    # Patch data_to_record_dict to return a dict with keys
    monkeypatch.setattr(main_mod, "data_to_record_dict", lambda x: {"id": x["id"], "name": x["name"]})
    # Patch data_schema to None (not used by from_pydict in this test)
    monkeypatch.setattr(main_mod, "data_schema", None)
    with patch("pyarrow.RecordBatch.from_pydict", return_value="batch") as mock_rb:
        result = main_mod.data_to_record_batch(data)
    assert result == "batch"
    mock_rb.assert_called_once()
    args, kwargs = mock_rb.call_args
    assert "mapping" in kwargs
    assert kwargs["mapping"]["id"] == [1, 2]
    assert kwargs["mapping"]["name"] == ["A", "B"]


def test_write_metrics_writes_and_closes(monkeypatch):
    mock_writer = MagicMock()
    mock_metric = MagicMock()
    mock_metric.per_year_miss = {"2020": 2, "2021": 3}
    mock_metric.number_of_near_misses = 5
    monkeypatch.setattr(main_mod, "metric_schema", None)
    with patch("pyarrow.Table.from_pydict", return_value="table") as mock_table:
        main_mod.write_metrics(mock_writer, mock_metric)
    mock_table.assert_called_once()
    mock_writer.write.assert_called_once_with("table")
    mock_writer.close.assert_called_once()
