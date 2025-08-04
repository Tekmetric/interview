from pathlib import Path

import pytest
from typer.testing import CliRunner

from neo.cli import cli

runner = CliRunner()


@pytest.mark.smoketest
def test_cli(tmp_path: Path):
    """Test CLI command."""
    result = runner.invoke(
        cli, ["run", "--limit", "10", "--threshold-au", "0.2", "--output-dir", tmp_path / "output.parquet"]
    )
    assert result.exit_code == 0

    assert (tmp_path / "output.parquet").exists()
