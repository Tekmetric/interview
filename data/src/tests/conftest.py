from pathlib import Path
import json

import pytest


@pytest.fixture
def recalls_data():
    file = Path(__file__).parent / 'data/recalls.json'

    return json.loads(file.read_text())


@pytest.fixture
def http_get_recalls_mock(mocker, recalls_data):
    get_response = mocker.MagicMock()
    mocker.patch('clients.requests.get', return_value=get_response)

    get_response.json.return_value = recalls_data
    get_response.content = json.dumps(recalls_data).encode()

    return get_response
