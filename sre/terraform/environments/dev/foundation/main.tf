# =============================================================================
# Dev environment - Foundation configuration
# =============================================================================
# This creates ECR repository and IAM roles for GitHub Actions.
# =============================================================================

terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  backend "s3" {
    bucket         = "tekmetric-demo-tfstate"
    key            = "dev/foundation/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "terraform-locks"
    encrypt        = true
  }
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

module "foundation" {
  source = "../../../modules/foundation"

  project_name         = var.project_name
  environment          = var.environment
  ecr_repository_name  = var.ecr_repository_name
  github_repository    = var.github_repository
  state_bucket_name    = var.state_bucket_name
  dynamodb_table_name  = var.dynamodb_table_name
  create_github_oidc   = var.create_github_oidc
  cloudflare_api_token = var.cloudflare_api_token
}
