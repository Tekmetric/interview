import os

import pandas as pd
import pytest

from recall.recall_data_extractor import RecallDataExtractor
from recall.recall_data_extractor import SummaryFiles


def test_read_extract_data_missing_column():
    data_extractor = RecallDataExtractor("tests/data/recall_data_wrong.csv", None)
    with pytest.raises(KeyError):
        data_extractor.extract_data()


def test_extract_yearly_data(parser_data_source_df, data_extractor):
    yearly_data = list(data_extractor._extract_yearly_data(parser_data_source_df))

    assert len(yearly_data) == 3
    assert yearly_data[0][0] == 2013
    assert yearly_data[1][0] == 2014
    assert yearly_data[2][0] == 2015
    assert yearly_data[0][1].shape == (3, 4)
    assert yearly_data[1][1].shape == (1, 4)
    assert yearly_data[2][1].shape == (1, 4)


def test_upload_manufacturer_year_count(parser_data_source_df, data_extractor):
    local_dir = "/tmp/recall-data/summary"
    data_extractor._upload_manufacturer_year_count(parser_data_source_df, local_dir)
    df = pd.read_parquet(f'{local_dir}/{SummaryFiles.MANUFACTURER_YEAR_COUNT}')
    os.remove(f"{local_dir}/{SummaryFiles.MANUFACTURER_YEAR_COUNT}")

    assert df.columns.tolist() == ["manufacturer", "year", "count"]
    assert df["count"].tolist() == [2, 1, 1, 1]
    assert df["manufacturer"].tolist() == ["Audi", "Audi", "Audi", "BMW"]
    assert df["year"].tolist() == [2013, 2014, 2015, 2013]


def test_upload_component_year_count(parser_data_source_df, data_extractor):
    local_dir = "/tmp/recall-data/summary"
    data_extractor._upload_component_year_count(parser_data_source_df, local_dir)
    file_path = f"{local_dir}/{SummaryFiles.COMPONENT_YEAR_COUNT}"
    df = pd.read_parquet(file_path)
    os.remove(file_path)

    assert df.columns.tolist() == ["component", "year", "count"]
    assert df["count"].tolist() == [2, 1, 1, 1]
    assert df["component"].tolist() == ["Air Bags", "Air Bags", "Seats", "Tires"]
    assert df["year"].tolist() == [2013, 2015, 2013, 2014]


def test_upload_type_manufacturer_count(parser_data_source_df, data_extractor):
    local_dir = "/tmp/recall-data/summary"
    data_extractor._upload_type_manufacturer_count(parser_data_source_df, local_dir)
    file_path = f"{local_dir}/{SummaryFiles.TYPE_MANUFACTURER_COUNT}"
    df = pd.read_parquet(file_path)
    os.remove(file_path)

    assert df.columns.tolist() == ["recall_type", "manufacturer", "count"]
    assert df["count"].tolist() == [1, 3, 1]
    assert df["recall_type"].tolist() == ["Tire", "Vehicle", "Vehicle"]
    assert df["manufacturer"].tolist() == ["Audi", "Audi", "BMW"]


