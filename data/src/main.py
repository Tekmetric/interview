from service import RecallsService
from storage import S3Storage
from clients import DataDotGovClient
from conf import settings


def run():
    data_gov_client = DataDotGovClient(settings.data_dot_gov_api_key)
    storage = S3Storage()

    service = RecallsService(storage, data_gov_client, use_cache=settings.use_cache)
    service.process_and_save_recalls(verbose=True)


if __name__ == '__main__':
    run()
