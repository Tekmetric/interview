import settings
from recall.file_storage import FakeS3FileStorage
from recall.recall_data_extractor import RecallDataExtractor
from recall.recall_data_fetcher import RecallDataFetcher


def main():
    data_fetcher = RecallDataFetcher(base_url=settings.BASE_URL, use_cache=settings.USE_CACHE)
    file_path = data_fetcher.download_vehicle_recall_csv(settings.STARTING_PATH)

    file_storage = FakeS3FileStorage(bucket_name=settings.S3_BUCKET_NAME)
    data_extractor = RecallDataExtractor(csv_path=file_path, file_storage=file_storage)
    data_extractor.extract_data()


if __name__ == "__main__":
    main()
