import asyncio
import logging
from pathlib import Path
from tempfile import TemporaryDirectory
from typing import Optional

import typer
from typing_extensions import Annotated

from _neo.logger import configure_logger, logger
from neo import workflow

cli = typer.Typer(name="neo", pretty_exceptions_show_locals=False)


@cli.callback()
def init(verbose: Annotated[Optional[bool], typer.Option("--verbose", "-v", help="Enable verbose output")] = False):
    configure_logger(verbose)


@cli.command()
def run(
    limit: int = typer.Option(default=200, help="Number of NEOs to fetch"),
    threshold_au: float = typer.Option(default=0.2, help="Distance threshold in astronomical units (AU)"),
    output_dir: Optional[Path] = typer.Option(
        default=Path(TemporaryDirectory(prefix="neo_output").name), help="Folder to store output data"
    ),
):
    """Run the data processing workflow."""
    output_dir.mkdir(parents=True, exist_ok=True)

    configure_logger(logger.handlers[0].level == logging.DEBUG, log_file_path=output_dir / "neo.log")
    asyncio.run(workflow.run(limit, threshold_au, output_dir))


def main():
    cli()
