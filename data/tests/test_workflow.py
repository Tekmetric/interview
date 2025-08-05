import os
from pathlib import Path
from unittest.mock import AsyncMock, patch

import pyarrow.parquet as pq
import pytest

from _neo.client.models import NearEarthObject
from neo import workflow


@pytest.mark.asyncio
async def test_run_workflow_mocked(sample_neos_models: list[NearEarthObject], tmp_path: Path):
    """Test the NEO workflow by mocking the fetch data function."""
    with patch(
        "_neo.client.nasa_client.NeoClient.list_entries",
        new_callable=AsyncMock,
        return_value=sample_neos_models,
    ):
        await workflow.run(limit=2, threshold_au=0.2, output_dir=tmp_path)

    parquet_files = list(tmp_path.rglob("*.parquet"))
    assert parquet_files, "No parquet files found in output directory"

    output_filepath = parquet_files[0]
    assert output_filepath.exists()

    table = pq.read_table(output_filepath)
    assert table.num_rows == 2


@pytest.mark.integrationtest
@pytest.mark.asyncio
async def test_run_workflow(tmp_path: Path):
    """Test the NEO workflow with data fetch."""
    # Make sure NASA_API_KEY is set in your environment
    if not os.getenv("NASA_API_KEY"):
        pytest.skip("NASA_API_KEY not set in environment")

    await workflow.run(limit=1, threshold_au=0.2, output_dir=tmp_path)

    parquet_files = list(tmp_path.rglob("*.parquet"))
    assert parquet_files, "No parquet files found in output directory"

    output_filepath = parquet_files[0]
    assert output_filepath.exists()

    table = pq.read_table(output_filepath)
    assert table.num_rows == 1
