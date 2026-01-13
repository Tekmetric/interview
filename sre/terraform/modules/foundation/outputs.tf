# =============================================================================
# Outputs for foundation module
# =============================================================================

output "ecr_repository_url" {
  description = "URL of the ECR repository"
  value       = aws_ecr_repository.backend.repository_url
}

output "ecr_repository_arn" {
  description = "ARN of the ECR repository"
  value       = aws_ecr_repository.backend.arn
}

output "ecr_repository_name" {
  description = "Name of the ECR repository"
  value       = aws_ecr_repository.backend.name
}

output "github_actions_role_arn" {
  description = "ARN of the IAM role for GitHub Actions"
  value       = aws_iam_role.github_actions.arn
}

output "github_oidc_provider_arn" {
  description = "ARN of the GitHub OIDC provider"
  value       = var.create_github_oidc ? aws_iam_openid_connect_provider.github_actions[0].arn : var.github_oidc_provider_arn
}

output "aws_account_id" {
  description = "AWS Account ID"
  value       = data.aws_caller_identity.current.account_id
}

output "aws_region" {
  description = "AWS Region"
  value       = data.aws_region.current.name
}

output "cloudflare_secret_arn" {
  description = "ARN of the Cloudflare API token secret in Secrets Manager"
  value       = length(aws_secretsmanager_secret.cloudflare_api_token) > 0 ? aws_secretsmanager_secret.cloudflare_api_token[0].arn : ""
}
