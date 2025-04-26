import abc
import logging
from pathlib import Path
from typing import Optional

import pyarrow as pa
import pyarrow.parquet as pq

logger = logging.getLogger("tekmetric")


class Writer(abc.ABC):
    """
    Abstract base class for writing data to different storage systems.
    """

    @abc.abstractmethod
    def __init__(self):
        pass

    @abc.abstractmethod
    def write(self, data):
        """
        Write data to the storage system.
        :param data: The data to write.
        """

    @abc.abstractmethod
    def close(self):
        """
        Close the writer and release any resources.
        """


class DiskWriter(Writer):
    """
    DiskWriter is a concrete implementation of the Writer class that writes data to disk in Parquet format.
    """

    def __init__(self, schema: pa.schema, output_dir: str, filename: str):
        super().__init__()

        self._output_dir = Path(output_dir)
        self._full_path = self._output_dir / filename

        if not self._output_dir.exists():
            self._output_dir.mkdir(parents=True, exist_ok=True)

        if self._full_path.exists():  # TODO Force write flag
            logger.warning(
                "Parquet file %s already exists. Overwriting it. We should probably introduce a flag to force this.",
                filename
            )

        self._writer = pq.ParquetWriter(
            where=str(self._full_path),
            schema=schema,
        )

    def write(self, data: Optional[pa.RecordBatch]):
        """
        Write a RecordBatch to the Parquet file.
        :param data: The RecordBatch to write.
        """
        if data is None:
            return
        self._writer.write(data)

    def close(self):
        """
        Close the Parquet writer.
        """
        self._writer.close()

    def __del__(self):
        """
        Ensure the Parquet writer is closed when the object is deleted.
        """
        self.close()


class S3Writer(Writer):
    """
    S3Writer is a concrete implementation of the Writer class that writes data to Amazon S3.
    """

    def __init__(self, schema: pa.schema, bucket_name: str, s3_client):
        super().__init__()

    def write(self, data):
        raise NotImplementedError()

    def close(self):
        """
        Close the writer and release any resources.
        """
        raise NotImplementedError()


class WriterFactory:
    """
    Factory class for creating Writer instances.
    """

    @staticmethod
    def get_writer(writer_type: str, **kwargs):
        """
        Factory method to create a Writer instance based on the writer type.
        :param writer_type: The type of writer to create (e.g., "disk", "s3").
        :param kwargs: Additional arguments to pass to the writer constructor.
        :return: The created Writer instance.
        """
        if writer_type == "disk":
            return DiskWriter(**kwargs)
        elif writer_type == "s3":
            return S3Writer(**kwargs)
        else:
            raise ValueError(f"Unknown writer type: {writer_type}")
