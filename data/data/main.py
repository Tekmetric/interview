import pyarrow as pa
import pyarrow.parquet as pq

from data.config import logger
from data.nasa_client import NASAClient
from data.process import map_neo_api_entry


def main():
    nasa_client = NASAClient.from_env()
    data = nasa_client.neo.browse()

    near_earth_objects = data["near_earth_objects"]
    records = []
    for entry in near_earth_objects:
        record = map_neo_api_entry(entry)
        records.append(record)

    table = pa.Table.from_pylist(records)
    output_path = "output.parquet"
    logger.info(f"writing_output path={output_path}")
    pq.write_table(table, "output.parquet")


if __name__ == "__main__":
    main()
