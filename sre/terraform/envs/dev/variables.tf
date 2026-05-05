variable "admin_iam_arns" {
  description = "List of IAM user/role ARNs to grant cluster-admin access via EKS Access Entries."
  type        = list(string)
  default     = []
}
