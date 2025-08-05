import os
from pathlib import Path
from unittest.mock import patch

import pyarrow.parquet as pq
import pytest

from neo import workflow


def test_run_workflow_mocked(neos: list[dict], tmp_path: Path):
    """Test the NEO workflow by mocking the fetch data function."""
    with patch("neo.workflow.fetch_neos", return_value=neos):
        workflow.run(limit=1, threshold_au=0.2, output_dir=tmp_path)

    parquet_files = list(tmp_path.rglob("*.parquet"))
    assert parquet_files, "No parquet files found in output directory"

    output_filepath = parquet_files[0]
    assert output_filepath.exists()

    table = pq.read_table(output_filepath)
    assert table.num_rows == 1


@pytest.mark.integrationtest
def test_run_workflow(tmp_path: Path):
    """Test the NEO workflow with data fetch."""
    # Make sure NASA_API_KEY is set in your environment
    if not os.getenv("NASA_API_KEY"):
        pytest.skip("NASA_API_KEY not set in environment")

    workflow.run(limit=1, threshold_au=0.2, output_dir=tmp_path)

    parquet_files = list(tmp_path.rglob("*.parquet"))
    assert parquet_files, "No parquet files found in output directory"

    output_filepath = parquet_files[0]
    assert output_filepath.exists()

    table = pq.read_table(output_filepath)
    assert table.num_rows == 1
