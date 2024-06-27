import uuid
from datetime import datetime

import freezegun
import pytest
from src.entities import RecallNotice

# Sample data for creating a RecallNotice instance
sample_data = {
    "nhtsa_id": "NHTSA-2020-0001",
    "report_received_date": "01/26/2020",
    "recall_link": "Go to Recall (https://www.nhtsa.gov/recalls?nhtsaId=24V456000)",
    "manufacturer": "Test Manufacturer",
    "subject": "Test Recall Subject",
    "component": "Test Component",
    "mfr_campaign_number": "TM-2020-01",
    "recall_type": "Safety",
    "potentially_affected": "1000",
    "defect_summary": "Test Defect Summary",
    "consequence_summary": "Test Consequence Summary",
    "corrective_action": "Test Corrective Action",
    "fire_risk_when_parked": "yes",
    "do_not_drive": "NO",
    "completion_rate": "75%",
}


@pytest.mark.parametrize(
    "date_str, expected",
    [
        ("01/25/2020", datetime(2020, 1, 25)),
        (datetime(2020, 12, 19), datetime(2020, 12, 19)),
    ],
)
def test_parser_report_received_date(date_str, expected):
    result = RecallNotice.parser_report_received_date(date_str)
    assert result == expected


@pytest.mark.parametrize(
    "value, expected",
    [
        ("", 0),
        (1000, 1000),
    ],
)
def test_parser_potentially_affected(value, expected):
    result = RecallNotice.parser_potentially_affected(value)
    assert result == expected


def test_parser_recall_link():
    link = "Go to Recall (https://www.nhtsa.gov/recalls?nhtsaId=24V456000)"
    expected = "https://www.nhtsa.gov/recalls?nhtsaId=24V456000"
    result = RecallNotice.parser_recall_link(link)
    assert result == expected


@pytest.mark.parametrize(
    "value, expected",
    [
        ("yes", True),
        ("1", True),
        ("no", False),
        ("f", False),
        ("true", True),
        ("false", False),
    ],
)
def test_parser_fire_risk_when_parked(value, expected):
    result = RecallNotice.parser_fire_risk_when_parked(value)
    assert result == expected


@pytest.mark.parametrize(
    "value, expected",
    [
        ("yes", True),
        ("no", False),
        ("true", True),
        ("false", False),
    ],
)
def test_parser_do_not_drive(value, expected):
    assert RecallNotice.parser_do_not_drive(value) == expected


@pytest.mark.parametrize(
    "value, expected",
    [
        ("75%", 75.0),
        ("", None),
    ],
)
def test_parser_completion_rate(value, expected):
    assert RecallNotice.parser_completion_rate(value) == expected


@freezegun.freeze_time("2020-10-10")
def test_auto_calculated_fields():
    # Omitting 'id', 'created_at', and 'updated_at' to test their automatic generation
    data = sample_data.copy()
    recall_notice = RecallNotice(**data)
    assert isinstance(recall_notice.id, str)
    assert isinstance(recall_notice.created_at, datetime)
    assert isinstance(recall_notice.updated_at, datetime)
    assert recall_notice.created_at == datetime(2020, 10, 10)
    assert recall_notice.updated_at == datetime(2020, 10, 10)


@freezegun.freeze_time("2020-10-10")
def test_recall_notice_creation():
    recall_notice = RecallNotice(**sample_data)
    assert recall_notice.id == uuid.uuid5(uuid.NAMESPACE_DNS, "NHTSA-2020-0001").hex
    assert recall_notice.created_at == datetime(2020, 10, 10)
    assert recall_notice.updated_at == datetime(2020, 10, 10)
    assert recall_notice.report_received_date == datetime(2020, 1, 26)
    assert (
        recall_notice.recall_link == "https://www.nhtsa.gov/recalls?nhtsaId=24V456000"
    )
    assert recall_notice.potentially_affected == 1000
    assert recall_notice.fire_risk_when_parked is True
    assert recall_notice.do_not_drive is False
    assert recall_notice.completion_rate == 75.0


def test_invalid_fire_risk_when_parked():
    with pytest.raises(ValueError) as error:
        RecallNotice(**{**sample_data, "fire_risk_when_parked": "invalid_value"})
    assert "Invalid value for 'fire_risk_when_parked'" in str(error)


def test_invalid_do_not_drive():
    with pytest.raises(ValueError):
        RecallNotice(**{**sample_data, "do_not_drive": "invalid_value"})
