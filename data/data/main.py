import pyarrow as pa
import pyarrow.parquet as pq

from data.config import logger, OUTPUT_DIR
from data.nasa_client import NASAClient
from data.process import map_neo_api_entry, CloseApproachMetrics
from data.schemas import neo_schema, yearly_count_schema, totals_schema


def main():
    nasa_client = NASAClient.from_env()
    data = nasa_client.neo.browse()

    near_earth_objects = data["near_earth_objects"]

    records, metrics = [], CloseApproachMetrics()
    for entry in near_earth_objects:
        record = map_neo_api_entry(entry)
        records.append(record)
        entry_metrics = CloseApproachMetrics.fill_from_api_data(
            entry["close_approach_data"]
        )
        metrics += entry_metrics

    # write records & metrics
    logger.info(f"writing_output")
    pq.write_table(
        pa.Table.from_pylist(records, schema=neo_schema), OUTPUT_DIR / "neo.parquet"
    )
    pq.write_table(
        pa.Table.from_pylist(
            list(metrics.yearly_approaches.items()), schema=yearly_count_schema
        ),
        OUTPUT_DIR / "yearly_approaches.parquet",
    )
    pq.write_table(
        pa.Table.from_pylist(
            [[metrics.near_miss_approaches_count]], schema=totals_schema
        ),
        OUTPUT_DIR / "near_miss_approach_count.parquet",
    )


if __name__ == "__main__":
    main()
