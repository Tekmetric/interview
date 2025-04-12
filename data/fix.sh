#!/bin/sh

set -eo pipefail

# This script fixes the formatting and linting issues.
echo "Formatting code..."
uv run ruff format

echo "Fixing linting issues..."
uv run ruff check --fix 
