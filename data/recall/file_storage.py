from abc import ABC
from abc import abstractmethod


class FileStorage(ABC):
    @abstractmethod
    def write(self, to_path: str, from_path: str) -> None:
        raise NotImplementedError


class FakeS3FileStorage(FileStorage):
    def __init__(self, bucket_name: str):
        self.bucket_name = bucket_name

    def write(self, to_path: str, from_path: str) -> None:
        print(f"Writing to S3 bucket: {self.bucket_name} path: {to_path} from local path: {from_path}")
