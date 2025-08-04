import logging
from pathlib import Path
from tempfile import TemporaryDirectory
from typing import Optional

import typer

from neo import workflow

logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s", datefmt="%H:%M:%S")
logger = logging.getLogger("neo")

cli = typer.Typer(name="neo", pretty_exceptions_show_locals=False)


@cli.command()
def run(
    limit: int = typer.Option(default=200, help="Number of NEOs to fetch"),
    threshold_au: float = typer.Option(default=0.2, help="Distance threshold in astronomical units (AU)"),
    output_dir: Optional[Path] = typer.Option(
        default=Path(TemporaryDirectory(prefix="neo_output").name), help="Folder to store output data"
    ),
):
    """Run the data processing workflow."""
    workflow.run(limit, threshold_au, output_dir)


@cli.command()
def version():
    """Show package version information."""
    typer.echo("neo:0.1.0")


def main():
    cli()
