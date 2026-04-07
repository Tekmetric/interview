.PHONY: install install-dev run run-dev clean

install:
	uv sync

install-dev:
	uv sync --extra dev 2>/dev/null || uv sync

run: install
	uv run python recall_data.py

run-dev: install
	uv run python recall_data_dev.py

clean:
	rm -rf .venv neo/
