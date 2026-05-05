locals {
  # Resolve the local chart paths relative to this module directory so Terraform
  # works regardless of the working directory it is invoked from.
  charts = "${path.module}/../../helm"
}

# ── 1. Istio ─────────────────────────────────────────────────────────────────
resource "helm_release" "istio" {
  name             = "istio"
  chart            = "${local.charts}/istio-chart"
  namespace        = kubernetes_namespace.istio_system.metadata[0].name
  create_namespace = false
  wait             = true
  timeout          = 600
}

# ── 2. Istio Ingress Gateway ──────────────────────────────────────────────────
# NLB terminates TLS using the ACM wildcard cert on port 443.
# Port 443 targetPort is overridden to 8080 (Istio's HTTP port) so the
# NLB forwards plain HTTP after decryption — Istio's 8443 port expects TLS
# which would conflict with NLB termination.
resource "helm_release" "gateway" {
  name             = "gateway"
  chart            = "${local.charts}/gateway-chart"
  namespace        = kubernetes_namespace.istio_ingress.metadata[0].name
  create_namespace = false
  wait             = true
  timeout          = 300

  values = [yamlencode({
    gateway = {
      service = {
        annotations = {
          "service.beta.kubernetes.io/aws-load-balancer-type"             = "nlb"
          "service.beta.kubernetes.io/aws-load-balancer-ssl-cert"         = aws_acm_certificate_validation.wildcard.certificate_arn
          "service.beta.kubernetes.io/aws-load-balancer-ssl-ports"        = "443"
          "service.beta.kubernetes.io/aws-load-balancer-backend-protocol" = "tcp"
          "external-dns.alpha.kubernetes.io/hostname"                     = "interview-backend.${var.domain_name}"
        }
        ports = [
          { name = "status-port", port = 15021, protocol = "TCP", targetPort = 15021 },
          { name = "http2",       port = 80,    protocol = "TCP", targetPort = 8080  },
          { name = "https",       port = 443,   protocol = "TCP", targetPort = 8080  },
        ]
      }
    }
  })]

  depends_on = [helm_release.istio]
}

# ── 3. cert-manager ───────────────────────────────────────────────────────────
resource "helm_release" "cert_manager" {
  name             = "cert-manager"
  chart            = "${local.charts}/cert-manager-chart"
  namespace        = kubernetes_namespace.cert_manager.metadata[0].name
  create_namespace = false
  wait             = true
  timeout          = 600

  depends_on = [helm_release.istio]
}

# ── 4. OpenTelemetry Operator ─────────────────────────────────────────────────
# Requires cert-manager for webhook certificate issuance.
# skip_schema_validation mirrors the --skip-schema-validation flag in bootstrap-k3s.sh.
resource "helm_release" "opentelemetry_operator" {
  name                   = "opentelemetry-operator"
  chart                  = "${local.charts}/opentelemetry-operator-chart"
  namespace              = kubernetes_namespace.opentelemetry_operator_system.metadata[0].name
  create_namespace       = false
  wait                   = true
  timeout                = 600
  depends_on = [helm_release.cert_manager]
}

# ── 5. External Secrets Operator ──────────────────────────────────────────────
# The service account is annotated with the IRSA role so the controller pods
# get AWS credentials via IRSA without any static keys.
resource "helm_release" "external_secrets" {
  name             = "external-secrets"
  chart            = "${local.charts}/external-secrets-chart"
  namespace        = kubernetes_namespace.eso.metadata[0].name
  create_namespace = false
  wait             = true
  timeout          = 600

  set {
    name  = "external-secrets.serviceAccount.annotations.eks\\.amazonaws\\.com/role-arn"
    value = module.eso_irsa.iam_role_arn
  }

  depends_on = [module.eso_irsa, helm_release.istio]
}

# ── 6. Argo Rollouts ──────────────────────────────────────────────────────────
resource "helm_release" "argo_rollouts" {
  name             = "argo-rollouts"
  chart            = "${local.charts}/argo-rollouts-chart"
  namespace        = kubernetes_namespace.argo_rollouts.metadata[0].name
  create_namespace = false
  wait             = true
  timeout          = 600
}

# ── 7. Observability Stack ────────────────────────────────────────────────────
# Prometheus, Loki, and Tempo each claim a PersistentVolume, so the EBS CSI
# addon must be ready. Istio must be up for namespace-level sidecar injection.
resource "helm_release" "observability_stack" {
  name             = "observability-stack"
  chart            = "${local.charts}/observability-stack"
  namespace        = kubernetes_namespace.observability_stack.metadata[0].name
  create_namespace = false
  wait             = true
  timeout          = 1200

  set {
    name  = "k8s-monitoring.cluster.provider"
    value = "eks"
  }

  set {
    name  = "k8s-monitoring.cluster.name"
    value = var.cluster_name
  }

  # Grafana's chart only exposes one service port. Setting port=443 with
  # targetPort=3000 (Grafana's HTTP port) lets the NLB terminate TLS and
  # forward plain HTTP to Grafana without any in-pod TLS configuration.
  values = [yamlencode({
    grafana = {
      service = {
        type       = "LoadBalancer"
        port       = 443
        targetPort = 3000
        annotations = {
          "service.beta.kubernetes.io/aws-load-balancer-type"             = "nlb"
          "service.beta.kubernetes.io/aws-load-balancer-ssl-cert"         = aws_acm_certificate_validation.wildcard.certificate_arn
          "service.beta.kubernetes.io/aws-load-balancer-ssl-ports"        = "443"
          "service.beta.kubernetes.io/aws-load-balancer-backend-protocol" = "tcp"
          "external-dns.alpha.kubernetes.io/hostname"                     = "grafana.${var.domain_name}"
        }
      }
    }
  })]

  depends_on = [
    helm_release.istio,
    aws_eks_addon.ebs_csi,
  ]
}

# ── ClusterSecretStore ────────────────────────────────────────────────────────
# Uses null_resource + local-exec instead of kubernetes_manifest to avoid the
# Terraform plan-time CRD validation failure that occurs when ESO is not yet
# installed. The ESO controller's IRSA role provides ambient AWS credentials,
# so no explicit auth block is needed in the store spec.
resource "null_resource" "cluster_secret_store" {
  triggers = {
    region       = var.region
    cluster_name = var.cluster_name
  }

  provisioner "local-exec" {
    interpreter = ["bash", "-c"]
    command     = <<-SHELL
      aws eks update-kubeconfig \
        --region '${var.region}' \
        --name '${var.cluster_name}' \
        --kubeconfig '/tmp/tf-kubeconfig-${var.cluster_name}' && \
      kubectl --kubeconfig '/tmp/tf-kubeconfig-${var.cluster_name}' apply -f - <<'YAML'
apiVersion: external-secrets.io/v1beta1
kind: ClusterSecretStore
metadata:
  name: aws-secretsmanager
spec:
  provider:
    aws:
      service: SecretsManager
      region: ${var.region}
YAML
    SHELL
  }

  provisioner "local-exec" {
    when        = destroy
    interpreter = ["bash", "-c"]
    command     = <<-SHELL
      aws eks update-kubeconfig \
        --region '${self.triggers.region}' \
        --name '${self.triggers.cluster_name}' \
        --kubeconfig '/tmp/tf-kubeconfig-${self.triggers.cluster_name}' 2>/dev/null || true
      kubectl --kubeconfig '/tmp/tf-kubeconfig-${self.triggers.cluster_name}' \
        delete clustersecretstore aws-secretsmanager --ignore-not-found 2>/dev/null || true
    SHELL
  }

  depends_on = [helm_release.external_secrets]
}

# ── 8. ArgoCD ─────────────────────────────────────────────────────────────────
# Overrides vs. the local k3s defaults:
#   - useExternalSecret: true  → repo SSH key comes from Secrets Manager via ESO
#   - valueFiles              → uses values-eks.yaml instead of values-k3s.yaml
#   - targetRevision          → tracks var.git_branch
#   - server.insecure         → disables the HTTPS redirect so the UI loads over HTTP
#   - server.service.type     → LoadBalancer gives ArgoCD its own ELB on EKS
#
# Prerequisite: store the repo SSH private key in AWS Secrets Manager at
#   secret name : argocd/repo/interview
#   JSON key    : sshPrivateKey
# before running `terraform apply`.
resource "helm_release" "argocd" {
  name             = "argocd"
  chart            = "${local.charts}/argocd-chart"
  namespace        = kubernetes_namespace.argocd.metadata[0].name
  create_namespace = false
  wait             = true
  timeout          = 600

  # server.insecure disables ArgoCD's own TLS — the NLB handles it instead.
  # With NLB termination, ArgoCD receives plain HTTP on port 8080 whether
  # the client connects to port 80 or 443 on the NLB.
  values = [yamlencode({
    "argo-cd" = {
      configs = {
        params = { "server.insecure" = "true" }
      }
      server = {
        service = {
          type = "LoadBalancer"
          annotations = {
            "service.beta.kubernetes.io/aws-load-balancer-type"             = "nlb"
            "service.beta.kubernetes.io/aws-load-balancer-ssl-cert"         = aws_acm_certificate_validation.wildcard.certificate_arn
            "service.beta.kubernetes.io/aws-load-balancer-ssl-ports"        = "443"
            "service.beta.kubernetes.io/aws-load-balancer-backend-protocol" = "tcp"
            "external-dns.alpha.kubernetes.io/hostname"                     = "argocd.${var.domain_name}"
          }
        }
      }
    }
  })]

  set {
    name  = "repo.secret.useExternalSecret"
    value = "true"
  }

  set {
    name  = "apps.interviewBackend.targetRevision"
    value = var.git_branch
  }

  set {
    name  = "apps.interviewBackend.valueFiles[0]"
    value = "values.yaml"
  }

  set {
    name  = "apps.interviewBackend.valueFiles[1]"
    value = "values-eks.yaml"
  }

  # Inject the real hostname into the Istio Gateway resource so it only accepts
  # traffic addressed to interview-backend.<domain>. ArgoCD passes this as a
  # Helm parameter override on top of the value files.
  set {
    name  = "apps.interviewBackend.parameters[0].name"
    value = "istio.gateway.host"
  }

  set {
    name  = "apps.interviewBackend.parameters[0].value"
    value = "interview-backend.${var.domain_name}"
  }

  depends_on = [
    null_resource.cluster_secret_store,
    helm_release.argo_rollouts,
    helm_release.opentelemetry_operator,
    helm_release.observability_stack,
    helm_release.external_dns,
  ]
}
