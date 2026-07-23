from __future__ import annotations

import argparse

from .pipeline import run


def main() -> None:
    parser = argparse.ArgumentParser(description="NASA NEO data pipeline")
    parser.add_argument(
        "command",
        choices=["run"],
        help="Command to execute",
    )
    args = parser.parse_args()

    if args.command == "run":
        run()


if __name__ == "__main__":
    main()
