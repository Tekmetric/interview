# =============================================================================
# Variables for dev foundation environment
# =============================================================================

variable "project_name" {
  description = "Name of the project"
  type        = string
  default     = "tekmetric-demo"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "dev"
}

variable "ecr_repository_name" {
  description = "Name of the ECR repository"
  type        = string
  default     = "tekmetric-backend"
}

variable "github_repository" {
  description = "GitHub repository in format owner/repo"
  type        = string
  default     = "YOUR_USERNAME/interview"
}

variable "state_bucket_name" {
  description = "Name of the S3 bucket for Terraform state"
  type        = string
  default     = "tekmetric-demo-tfstate"
}

variable "dynamodb_table_name" {
  description = "Name of the DynamoDB table for state locking"
  type        = string
  default     = "terraform-locks"
}

variable "create_github_oidc" {
  description = "Whether to create the GitHub OIDC provider"
  type        = bool
  default     = true
}

variable "cloudflare_api_token" {
  description = "Cloudflare API token for ExternalDNS"
  type        = string
  default     = ""
  sensitive   = true
}
