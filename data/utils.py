import logging
import boto3
import io
import os

S3_BUCKET = f"s3://test-bucket"

def get_logger():
    logger = logging.getLogger()
    logging.basicConfig(
        level=logging.DEBUG, format="%(asctime)s - %(levelname)s - %(message)s"
    )
    logger.setLevel(logging.INFO)
    return logger


def write_data(data, destination_path, storage_type, file_name):
    """
    Takes dataframe as input and writes data to required storage location
    """
    try:
        if "s3" in storage_type:
            s3_client = boto3.client("s3", region="us-east-1")

            with io.StringIO() as csv_buffer:
                data.to_csv(csv_buffer, index=False)
                s3_client.put_object(
                    Bucket=S3_BUCKET,
                    Key=f"{destination_path}/{file_name}.parquet",
                    Body=csv_buffer.getvalue(),
                )

        else:
            if not os.path.exists(destination_path):
                os.makedirs(destination_path)
            data.to_csv(
                f"{destination_path}/{file_name}.parquet",
                sep=",",
                index=False,
                encoding="utf-8",
            )

        get_logger().info(
            f"File Name: {file_name}, Storage Type: {file_name}, Num Records: {len(data)}"
        )

    except Exception as e:
        get_logger().error(f"There was an error with writing data to {storage_type}: ", e)
