import os
from collections.abc import Iterator
from unittest import mock

import pytest
from _pytest.monkeypatch import MonkeyPatch
from pydantic import ValidationError

from neo_data_analyser.settings import get_settings


@pytest.fixture(autouse=True)
def clean_env() -> Iterator[None]:
    with mock.patch.dict(os.environ, clear=True):
        yield


VALID_ENV = {
    "NEO_API_KEY": "test_api_key",
    "NEO_API_URL": "https://api.example.com",
}

ENV_KEYS = ("NEO_API_KEY", "NEO_API_URL")


@pytest.mark.parametrize("env_key", ENV_KEYS)
def test_get_settings_raises_pydantic_validation_error_if_key_is_missing(
    monkeypatch: MonkeyPatch, env_key: str
) -> None:
    # Arrange
    env = VALID_ENV.copy()
    env.pop(env_key)
    monkeypatch.setattr(os, "environ", value=env)
    # Act
    with pytest.raises(ValidationError):
        get_settings()


@pytest.mark.parametrize("env_key", ENV_KEYS)
def test_get_settings_raises_pydantic_validation_error_if_key_is_empty(
    monkeypatch: MonkeyPatch, env_key: str
) -> None:
    # Arrange
    env = VALID_ENV.copy()
    env[env_key] = ""
    monkeypatch.setattr(os, "environ", value=env)

    # Act
    with pytest.raises(ValidationError):
        get_settings()


def test_get_settings_doesnt_raise_error_if_all_keys_are_present(
    monkeypatch: MonkeyPatch,
) -> None:
    # Arrange
    monkeypatch.setattr(os, "environ", value=VALID_ENV)

    # Act
    settings = get_settings()

    # Assert
    assert settings.neo_api_key == VALID_ENV["NEO_API_KEY"]
    assert settings.neo_api_url == VALID_ENV["NEO_API_URL"]
