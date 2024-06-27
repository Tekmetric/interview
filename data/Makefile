.ONESHELL:

VENV = .venv
PYTHON = $(VENV)/bin/python3
PIP = $(VENV)/bin/pip

all: help

help: # Display Callable targets
	@sed -n 's/:.*#/:/p' Makefile | grep -v sed

venv:
	@echo "Creating a new virtual environment..."
	python3 -m venv $(VENV)
	$(PIP) install -r requirements.txt requirements-dev.txt
	touch $(VENV)/bin/activate
	$(PYTHON) pre-commit install
	@echo "Virtual environment created."

quality: # Run pre-commit hooks
	$(PYTHON) -m pre_commit run --all-files

test: # Run tests
	$(PYTHON) -m pytest

run: # Run the application
	$(PYTHON) recall_data.py

clean:
	find . -name '*.pyc' -delete
	find . -name '__pycache__' -delete
