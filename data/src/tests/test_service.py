import pytest

from storage import S3Storage
from clients import DataDotGovClient
from service import RecallsService


@pytest.fixture
def storage():
    return S3Storage()


@pytest.fixture
def data_gov(http_get_recalls_mock) -> DataDotGovClient:
    return DataDotGovClient('some api key')


@pytest.fixture
def service(storage, data_gov):
    return RecallsService(storage, data_gov)


class TestRecallsService:

    def test_pick_interesting_fields(self, service: RecallsService, data_gov):
        data_df = data_gov.get_resource(data_gov.Resource.RECALLS)

        summary_df = service.pick_only_interesting_fields(data_df)
        assert set(summary_df.columns) == set(service.INTERESTING_FIELDS)

    def test_group_by_year_returns_data_by_year(self, service: RecallsService, data_gov):
        process_results = service.process_recalls()

        assert set((2023, 2024)) == set(process_results.data_by_year['year'].values)
        assert (
            set(('SUSPENSION', 'STEERING'))
            == set(process_results.recalls_per_component_per_year.keys())
        )
        assert (
            set(('Porsche Cars North America, Inc.', 'Auto Pro USA, Inc.'))
            == set(process_results.recalls_per_manufacturer_per_year.keys())
        )
        assert (
            set(('Equipment', 'Vehicle'))
            == set([type_ for _, type_ in process_results.type_of_recalls_per_manufacturer.index])
        )

    def test_process_and_save_recalls(self, storage: S3Storage, data_gov, mocker, monkeypatch):
        mocker.patch('service.')
        save_mock = mocker.MagicMock()
        monkeypatch.setattr(storage, 'save', save_mock)

        service = RecallsService(storage, data_gov)
        service.process_and_save_recalls()
