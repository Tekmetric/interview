# =============================================================================
# Dev environment - Bootstrap configuration
# =============================================================================
# This creates S3 bucket and DynamoDB table for Terraform state.
# Run this once locally before setting up the rest of the infrastructure.
# =============================================================================

terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

# Local state only - this module creates S3 bucket used by other modules
}

provider "aws" {
  region = "us-east-1"

  default_tags {
    tags = {
      Project     = "tekmetric-demo"
      Environment = "dev"
      ManagedBy   = "terraform"
    }
  }
}

module "bootstrap" {
  source = "../../../modules/bootstrap"

  state_bucket_name   = var.state_bucket_name
  dynamodb_table_name = var.dynamodb_table_name
}
