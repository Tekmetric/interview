#!/bin/sh
# This script checks if thera are issues in the code. It can be used in the CI pipeline.


set -eo pipefail

echo "Checking formatting and linting..."
uv run ruff format --check

echo "Checking type hints..."
uv run mypy .  
