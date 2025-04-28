import pandas as pd
from pydantic import BaseModel

from .base_serializer import BaseSerializer


class ParquetLocalSerializer(BaseSerializer):
    """
    Serializer for storing validated objects in Parquet format.
    """

    def __init__(self, output_dir: str, object_count: int):
        """
        Initialize the ParquetLocalSerializer with the output directory and object count.

        Args:
            output_dir (str): The directory where the Parquet files will be saved.
            object_count (int): The number of objects to be stored in each Parquet file.
        """
        super().__init__(output_dir, object_count)
        self.buffer = []

    @property
    def extension(self) -> str:
        return "parquet"

    def add(self, obj: BaseModel) -> None:
        """
        Add a single object to the serializer.

        Args:
            obj (BaseModel): The object to be serialized. It should be a Pydantic model.
        """
        self.buffer.append(obj.model_dump())
        self.current_count += 1

        if self.current_count >= self.object_count:
            self.flush()

    def flush(self) -> None:
        """
        Flush all pending objects to disk by writing them to a Parquet file.
        """
        if not self.buffer:
            return

        output_file = self._get_unique_output_file()

        df = pd.DataFrame(self.buffer)
        df.to_parquet(output_file, index=False)

        self.buffer = []
        self.current_count = 0
