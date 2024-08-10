import json
import os
import shutil
import pandas as pd
import pytest

from recall.file_storage import FakeS3FileStorage
from recall.recall_data_extractor import RecallDataExtractor


@pytest.fixture(scope="session", autouse=True)
def setup_session():
    os.makedirs("/tmp/recall-data/yearly", exist_ok=True)
    os.makedirs("/tmp/recall-data/summary", exist_ok=True)
    yield
    shutil.rmtree("/tmp/recall-data")


@pytest.fixture
def starting_page_content_correct() -> str:
    with open("tests/data/starting_page_correct.html") as file:
        return file.read()


@pytest.fixture
def starting_page_content_wrong() -> str:
    with open("tests/data/starting_page_wrong.html") as file:
        return file.read()


@pytest.fixture
def metadata_correct():
    with open("tests/data/metadata_correct.json") as file:
        return json.load(file)


@pytest.fixture
def metadata_wrong():
    with open("tests/data/metadata_wrong.json") as file:
        return json.load(file)


@pytest.fixture
def parser_data_source_df():
    df = pd.DataFrame({
        "report_received_date": ["04/08/2013", "06/10/2013", "07/11/2013", "05/05/2014", "05/05/2015"],
        "manufacturer": ["Audi", "Audi", "BMW", "Audi", "Audi"],
        "recall_type": ["Vehicle", "Vehicle", "Vehicle", "Tire", "Vehicle"],
        "component": ["Air Bags", "Air Bags", "Seats", "Tires", "Air Bags"],
    })
    df["year"] = pd.to_datetime(df["report_received_date"]).dt.year
    return df


@pytest.fixture
def data_extractor():
    file_storage = FakeS3FileStorage(bucket_name="test-bucket")
    data_extractor = RecallDataExtractor("tests/data/recall_data_correct.csv", file_storage)
    return data_extractor
