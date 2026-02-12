# Solution

## Note

Values in this repo (bucket names, domains, etc.) are placeholders. I have a fully working deployment on AWS EKS that I can demonstrate during the interview.

## Prerequisites

- Docker
- kubectl configured for your cluster
- Helm 3.x
- AWS CLI (for ECR)

## Quick start

### Build locally
```bash
docker build -t tekmetric-backend:local -f sre/Dockerfile .
```

### Deploy to Kubernetes
```bash
helm install backend ./sre/helm/backend -f ./sre/helm/backend/values-dev.yaml
```

### Verify
```bash
kubectl get pods -l app.kubernetes.io/name=backend
curl http://<EXTERNAL-IP>/api/welcome
```

## What's included

| Component | Description |
|-----------|-------------|
| Dockerfile | Multi-stage build, non-root user, health checks |
| Helm Chart | HPA, PDB, probes, resource limits, security context |
| CI/CD | GitHub Actions workflows (see below) |
| Terraform | EKS cluster, VPC, IAM roles |

## CI/CD pipelines

| Workflow | Trigger | What it does |
|----------|---------|--------------|
| `app-build.yaml` | Push to main, manual | Build → Push to ECR → Deploy to EKS |
| `infra-foundation.yaml` | Manual | Create ECR repo, IAM roles for GitHub OIDC |
| `infra-eks.yaml` | Manual | Create/destroy EKS cluster and VPC |

The infrastructure workflows use Terraform with remote state in S3. The app workflow uses OIDC for keyless AWS authentication.

## Design decisions

- **Multi-stage Docker build** - Smaller image, no build tools in production
- **Non-root user** - Security best practice
- **HPA with behavior config** - Prevents scaling spikes during JVM startup
- **Resource requests/limits** - Based on actual observed usage (~140Mi memory)
- **Startup probe** - Gives JVM time to initialize before health checks begin

## Potential production improvements

If this were production, I'd add:
- Ingress with TLS (cert-manager) instead of LoadBalancer per service
- WAF / rate limiting on the load balancer
- Distributed tracing (Datadog APM or similar)
- Tag-based promotion (e.g., `v1.0.0` tag triggers prod deploy)
- Multi-environment pipelines (dev → staging → prod)
