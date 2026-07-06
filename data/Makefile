COUNT ?= 200

.PHONY: install install-dev run run-dev clean

install:
	uv sync

install-dev: #At some point i really need to add the --extra dev group to uv. until then this just installs the same depenedencies
	uv sync --extra dev 2>/dev/null || uv sync

run: install
	uv run python recall_data.py --count $(COUNT)

run-dev: install
	uv run python recall_data_dev.py --count $(COUNT)

clean:
	rm -rf .venv neo/
