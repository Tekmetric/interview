import json

from pydantic import BaseModel

from .base_serializer import BaseSerializer


class JsonLocalSerializer(BaseSerializer):
    """
    Serializer for storing validated objects in JSON format (for testing).
    """

    def __init__(self, output_dir: str, object_count: int):
        super().__init__(output_dir, object_count)
        self.buffer = []

    @property
    def extension(self) -> str:
        """
        Returns the file extension for the output file.
        """
        return "json"

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
        Flush all pending objects to disk by writing them to a JSON file.
        """
        if not self.buffer:
            return

        output_file = self._get_unique_output_file()

        with open(output_file, "w", encoding="utf-8") as f:
            json.dump(self.buffer, f, ensure_ascii=False, indent=2, default=str)

        self.buffer = []
        self.current_count = 0
