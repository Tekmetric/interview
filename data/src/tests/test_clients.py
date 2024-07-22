import json

import pandas as pd

from clients import DataDotGovClient


class TestDataDotGov:

    def test_get_resource_url_make_correct_url(self):
        client = DataDotGovClient('some api key', base_url='https://data.gov/api/')

        url = client.get_resource_url(client.Resource.RECALLS)
        assert url == f'https://data.gov/api/{client.Resource.RECALLS.value}/rows.json'

    def test_get_resource_url_base_url_without_ending_slash_works_ok(self):
        client = DataDotGovClient('some api key', base_url='https://data.gov/api')

        url = client.get_resource_url(client.Resource.RECALLS)
        assert url == f'https://data.gov/api/{client.Resource.RECALLS.value}/rows.json'

    def test_get_resource_makes_call_to_url(self, mocker):
        client = DataDotGovClient('some api key', base_url='https://data.gov/api')

        get_mock = mocker.patch('clients.requests.get')
        client.get_resource(client.Resource.RECALLS)

        get_mock.assert_called_once_with(f'https://data.gov/api/{client.Resource.RECALLS.value}/rows.json')

    def test_get_resource_caches_response(self, mocker, tmp_path, recalls_data):
        client = DataDotGovClient(
            'some api key',
            base_url='https://data.gov/api',
            cache_dir=str(tmp_path)
        )

        get_response = mocker.MagicMock()
        get_mock = mocker.patch('clients.requests.get', return_value=get_response)

        get_response.json.return_value = recalls_data
        get_response.content = json.dumps(recalls_data).encode()

        client.get_resource(client.Resource.RECALLS, use_cache=True)
        get_mock.assert_called_once()

        client.get_resource(client.Resource.RECALLS, use_cache=True)
        get_mock.assert_called_once()

    def test_get_resource_returns_data_frame(self, http_get_recalls_mock):
        client = DataDotGovClient(
            'some api key',
            base_url='https://data.gov/api'
        )

        response = client.get_resource(client.Resource.RECALLS)
        assert isinstance(response, pd.DataFrame)
