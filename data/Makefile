.PHONY: install
install:
	poetry install

.PHONY: lint
lint:
	poetry run pre-commit run --all-files

.PHONY: run
run:
	poetry run recall-data

.PHONY: tests
tests:
	poetry run pytest
