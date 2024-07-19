import os
import logging
from datetime import datetime
from typing import Dict, List, Any

import boto3
import pandas as pd
import pyarrow as pa
import pyarrow.parquet as pq
import requests
from botocore.exceptions import ClientError
from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException
from pydantic import BaseSettings

# Load environment variables
load_dotenv()

# Configuration
class Settings(BaseSettings):
  API_KEY: str
  S3_BUCKET: str
  AWS_ACCESS_KEY_ID: str
  AWS_SECRET_ACCESS_KEY: str
  AWS_REGION: str = "us-east-1"
  BASE_URL: str = "https://api.nhtsa.gov/recalls/recallsByVehicle"
  LOG_LEVEL: str = "INFO"

  class Config:
      env_file = ".env"

settings = Settings()

# Set up logging
logging.basicConfig(level=settings.LOG_LEVEL)
logger = logging.getLogger(__name__)

# Initialize S3 client
s3_client = boto3.client(
  's3',
  aws_access_key_id=settings.AWS_ACCESS_KEY_ID,
  aws_secret_access_key=settings.AWS_SECRET_ACCESS_KEY,
  region_name=settings.AWS_REGION
)

# Initialize FastAPI app
app = FastAPI()

def fetch_recalls_data(year: int) -> List[Dict[str, Any]]:
  """Fetch recall data for a specific year from the NHTSA API."""
  params = {
      'api_key': settings.API_KEY,
      'year': year,
      'format': 'json'
  }
  try:
      response = requests.get(settings.BASE_URL, params=params)
      response.raise_for_status()
      return response.json()['results']
  except requests.RequestException as e:
      logger.error(f"Error fetching data for year {year}: {e}")
      return []

def process_recalls_data(data: List[Dict[str, Any]]) -> pd.DataFrame:
  """Process raw recall data into a structured DataFrame."""
  df = pd.DataFrame(data)
  required_columns = [
      'Id', 'created_at', 'updated_at', 'report_received_date', 'nhtsa_id',
      'recall_link', 'manufacturer', 'subject', 'component',
      'mfr_campaign_number', 'recall_type', 'potentially_affected',
      'defect_summary', 'consequence_summary', 'corrective_action',
      'fire_risk_when_parked', 'do_not_drive', 'completion_rate'
  ]
  df = df[required_columns]
  
  # Convert dates to datetime
  date_columns = ['created_at', 'updated_at', 'report_received_date']
  for col in date_columns:
      df[col] = pd.to_datetime(df[col], errors='coerce')
  
  # Extract year from report_received_date
  df['year'] = df['report_received_date'].dt.year
  
  return df

def save_to_parquet_and_upload(df: pd.DataFrame, filename: str) -> None:
  """Save DataFrame to Parquet and upload to S3."""
  local_path = f"/tmp/{filename}"
  s3_path = f"recalls/{filename}"
  
  table = pa.Table.from_pandas(df)
  pq.write_table(table, local_path)
  
  try:
      s3_client.upload_file(local_path, settings.S3_BUCKET, s3_path)
      logger.info(f"Successfully uploaded {filename} to S3")
  except ClientError as e:
      logger.error(f"Error uploading {filename} to S3: {e}")
  finally:
      os.remove(local_path)

def calculate_aggregations(df: pd.DataFrame) -> None:
  """Calculate and save required aggregations."""
  aggregations = [
      ('agg_manufacturer_year', ['year', 'manufacturer']),
      ('agg_component_year', ['year', 'component']),
      ('agg_type_manufacturer', ['manufacturer', 'recall_type'])
  ]
  
  for agg_name, group_cols in aggregations:
      agg_df = df.groupby(group_cols).size().reset_index(name='recall_count')
      save_to_parquet_and_upload(agg_df, f"{agg_name}.parquet")

def main() -> None:
  """Main function to orchestrate the ETL process."""
  all_data = []
  current_year = datetime.now().year

  for year in range(1966, current_year + 1):
      logger.info(f"Fetching data for year {year}")
      data = fetch_recalls_data(year)
      all_data.extend(data)

  df = process_recalls_data(all_data)

  # Save data for each year
  for year, group in df.groupby('year'):
      save_to_parquet_and_upload(group, f"{year}.parquet")

  # Calculate and save aggregations
  calculate_aggregations(df)

# API routes
@app.get("/recalls/{year}")
async def get_recalls(year: int):
  """API endpoint to retrieve recalls for a specific year."""
  try:
      s3_object = s3_client.get_object(Bucket=settings.S3_BUCKET, Key=f"recalls/{year}.parquet")
      df = pd.read_parquet(s3_object['Body'])
      return df.to_dict(orient='records')
  except ClientError as e:
      logger.error(f"Error retrieving data for year {year}: {e}")
      raise HTTPException(status_code=404, detail=f"Data for year {year} not found")

@app.get("/aggregations/{agg_type}")
async def get_aggregation(agg_type: str):
  """API endpoint to retrieve aggregations."""
  valid_agg_types = ['manufacturer_year', 'component_year', 'type_manufacturer']
  if agg_type not in valid_agg_types:
      raise HTTPException(status_code=400, detail="Invalid aggregation type")
  
  try:
      s3_object = s3_client.get_object(Bucket=settings.S3_BUCKET, Key=f"recalls/agg_{agg_type}.parquet")
      df = pd.read_parquet(s3_object['Body'])
      return df.to_dict(orient='records')
  except ClientError as e:
      logger.error(f"Error retrieving aggregation {agg_type}: {e}")
      raise HTTPException(status_code=404, detail=f"Aggregation {agg_type} not found")

if __name__ == "__main__":
  main()
  # To run the API: uvicorn script_name:app --reload