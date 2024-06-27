import re
import uuid
from datetime import datetime
from typing import Optional

from pydantic import BaseModel, field_validator, model_validator

RECALL_URL_PATTERN = re.compile(r"Go to Recall \((?P<url>.+)\)")
TRUE_VALUES = {"yes", "true", "t", "1"}
FALSE_VALUES = {"no", "false", "f", "0"}


class RecallNotice(BaseModel):
    id: str
    created_at: datetime
    updated_at: datetime
    report_received_date: datetime
    nhtsa_id: str
    recall_link: str
    manufacturer: str
    subject: str
    component: str
    mfr_campaign_number: str
    recall_type: str
    potentially_affected: int
    defect_summary: str
    consequence_summary: str
    corrective_action: str
    fire_risk_when_parked: bool
    do_not_drive: bool
    completion_rate: Optional[float] = None

    @field_validator("report_received_date", mode="before")
    @classmethod
    def parser_report_received_date(cls, v) -> datetime:
        if isinstance(v, str):
            return datetime.strptime(v, "%m/%d/%Y")
        return v

    @field_validator("potentially_affected", mode="before")
    @classmethod
    def parser_potentially_affected(cls, v) -> int:
        if isinstance(v, str) and v == "":
            return 0
        return v

    @field_validator("recall_link", mode="before")
    @classmethod
    def parser_recall_link(cls, v) -> str:
        """_summary_
        pattern - Go to Recall (https://www.nhtsa.gov/recalls?nhtsaId=24V456000)

        :param _type_ v: raw value
        :return datetime: url link
        """
        if match := RECALL_URL_PATTERN.match(v):
            return match.group("url")
        return v

    @field_validator("fire_risk_when_parked", mode="before")
    @classmethod
    def parser_fire_risk_when_parked(cls, v) -> bool:
        if isinstance(v, str):
            v = v.lower().strip()
            if v in TRUE_VALUES:
                return True
            elif v in FALSE_VALUES:
                return False
            else:
                raise ValueError(f"Invalid value for 'fire_risk_when_parked': {v}")
        return v

    @field_validator("do_not_drive", mode="before")
    @classmethod
    def parser_do_not_drive(cls, v) -> bool:
        if isinstance(v, str):
            v = v.lower().strip()
            if v in TRUE_VALUES:
                return True
            elif v in FALSE_VALUES:
                return False
            else:
                raise ValueError(f"Invalid value for 'do_not_drive': {v}")
        return v

    @field_validator("completion_rate", mode="before")
    @classmethod
    def parser_completion_rate(cls, v) -> float:
        if isinstance(v, str) and v:
            return float(v.strip("%"))
        return None

    @model_validator(mode="before")
    def validate_date(cls, values):
        if "id" not in values:
            values["id"] = uuid.uuid5(uuid.NAMESPACE_DNS, values["nhtsa_id"]).hex
        if "created_at" not in values:
            values["created_at"] = datetime.now()
        if "updated_at" not in values:
            values["updated_at"] = datetime.now()
        return values
