output "configure_kubectl"      { value = module.eks.configure_kubectl }
output "cluster_endpoint"        { value = module.eks.cluster_endpoint }
output "eso_irsa_role_arn"       { value = module.eks.eso_irsa_role_arn }
output "grafana_service"         { value = module.eks.grafana_service }
output "argocd_initial_password" { value = module.eks.argocd_initial_password }
output "secrets_manager_prereq"  { value = module.eks.secrets_manager_prereq }
