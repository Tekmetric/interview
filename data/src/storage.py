from abc import ABC, abstractmethod

class StorageABC(ABC):

    @abstractmethod
    def save(self, file_name: str, data: bytes):
        pass


class S3Storage(StorageABC):

    def save(self, file_name, data):
        print(f'Saving file {file_name} to S3. nah, just kidding, this does nothing')
