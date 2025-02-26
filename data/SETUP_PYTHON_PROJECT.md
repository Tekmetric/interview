# Local development

## Requirements
- [pyenv](https://github.com/pyenv/pyenv): (optional) for managing multiple Python versions
- [Poetry](https://python-poetry.org/): for dependency management and packaging


##  Virtualenv setup
```
poetry install
# check python path (handy for setting the venv in the IDE)
poetry run which python
```


## Install pre-commit hooks

Ensure code quality by installing pre-commit hooks:
```
poetry run pre-commit install
```

## Run project

Create .env file and set `NEO_API_KEY`. You can check `config.py` for other env variables.

```
poetry run neo_collect
```

## Run tests
```
poetry run pytest
```
