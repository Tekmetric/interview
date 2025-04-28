import logging
from collections import defaultdict
from typing import Optional

import pyarrow as pa

from tekmetric_data import init_logging, parse_args
from tekmetric_data.data import data_schema, data_to_record_dict, metric_schema
from tekmetric_data.metric import MetricRegistry, Metric
from tekmetric_data.nasa_client import NasaClientRegistry, NasaClient
from tekmetric_data.output import WriterRegistry
from tekmetric_data.release import __version__

logger = logging.getLogger("tekmetric")


def fetch_and_process(client, metric: Metric, page: int) -> Optional[dict]:
    """
    Consume a page of data from the client.
    :param client: The client to use for fetching data.
    :param metric: The metric to use for processing data.
    :param page: The page number to fetch.
    :return: A tuple containing the data and metadata.
    """
    # Fetch data from the client
    data = client.browse(page=page)
    if 'error' in data:
        logger.error("error getting the page %d: %s", page, data['error']['message'])
        return None
    logger.debug("page %d: %s", page, data)
    # Process the data with the metric
    metric.add(data)
    return data


def data_to_record_batch(data: dict) -> Optional[pa.RecordBatch]:
    """
    Convert the data to a record batch.
    :param data: The data to convert.
    :return: The record batch.
    """
    if data is None:
        logger.debug("no data to convert")
        return None

    column_data = defaultdict(list)
    for item in data["near_earth_objects"]:
        processed_record = data_to_record_dict(item)
        for key, value in processed_record.items():
            column_data[key].append(value)

    logger.debug("columns: %s", column_data)
    return pa.RecordBatch.from_pydict(mapping=dict(column_data), schema=data_schema)


def write_metrics(writer, metrics: Metric) -> None:
    """
    Write the metrics with the writer.
    :param writer: The writer to use for writing data.
    :param metrics: The metrics to write.
    """
    # TODO this is not metric independent
    per_year_miss_cleaned = {int(k): int(v) for k, v in metrics.per_year_miss.items()}

    data_for_arrow = {
        'number_of_near_misses': [metrics.number_of_near_misses],
        'per_year_miss': [per_year_miss_cleaned]
    }
    logger.info("metrics: %s", data_for_arrow)

    metrics_table = pa.Table.from_pydict(data_for_arrow, schema=metric_schema)

    writer.write(metrics_table)
    writer.close()
    logger.debug("metrics written")


def main():
    """
    Main function to run the Tekmetric Data application.
    """
    init_logging()
    logger.info("Starting Tekmetric Data version %s", __version__)

    args = parse_args()
    logger.debug("Args: %s", args)

    limit = args.page_size
    url = args.url

    # Create the NasaClient and NeoClient
    client_factory = NasaClientRegistry(client=NasaClient(url=url))
    neo_client = client_factory.get("neo", page_size=limit)
    logger.debug("NeoClient created with page size: %d", limit)

    # Create the metric
    metrics = MetricRegistry.get(args.metric)
    logger.info("Metric %s created.", metrics)

    # Create the output writer
    data_writer = WriterRegistry.get(
        writer_type=args.output_type, schema=data_schema, output_dir=args.output_dir,
        filename="neo_data.parquet"
    )  # could be used as a context manager too.
    logger.debug("Writer created with type %s, path %s is created for data", args.output_type, args.output_dir)

    end_page = args.num_pages
    num_objects = args.page_size * end_page
    logger.info("Will fetch pages 0 to %d: %d number of instances", end_page, num_objects)

    for page in range(0, end_page):
        # fetch data from the client
        data = fetch_and_process(neo_client, metrics, page)

        # convert data to record batch
        record_batch = data_to_record_batch(data)

        # write data with the writer
        data_writer.write(record_batch)
        logger.debug("page %d written", page)

    data_writer.close()
    logger.debug("data writer closed")

    # write metrics to file
    with WriterRegistry.get(
            writer_type=args.output_type, schema=metric_schema, output_dir=args.output_dir,
            filename="metrics.parquet"
    ) as metric_writer:  # context manager usage example
        logger.debug("Writer created with type %s, path %s is created for metrics", args.output_type, args.output_dir)
        write_metrics(metric_writer, metrics)
    logger.info("all done")


if __name__ == '__main__':
    main()
