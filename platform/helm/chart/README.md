# Interview Helm Chart

A Helm chart for deploying the Interview application on Kubernetes.

## Installation

```bash
helm install interview . --namespace interview --create-namespace
```

With custom values:

```bash
helm install interview . \
  --namespace interview \
  --create-namespace \
  --set image.tag=1.0.1 \
  --set replicaCount=3
```

## Configuration

### Image

| Parameter | Default |
|-----------|---------|
| `image.repository` | `github.com/bryanwp/interview` |
| `image.tag` | `1.0.0` |
| `image.pullPolicy` | `IfNotPresent` |
| `imagePullSecrets` | `[]` |

### Deployment

| Parameter | Default |
|-----------|---------|
| `replicaCount` | `1` |
| `autoscaling.enabled` | `true` |
| `autoscaling.minReplicas` | `1` |
| `autoscaling.maxReplicas` | `5` |
| `autoscaling.targetCPUUtilizationPercentage` | `80` |
| `pdb.enabled` | `true` |
| `pdb.minAvailable` | `1` |

### Service & Ingress

| Parameter | Default |
|-----------|---------|
| `service.type` | `ClusterIP` |
| `service.port` | `443` |
| `ingress.enabled` | `true` |
| `ingress.className` | `nginx` |
| `ingress.hosts[].host` | `interview.example.com` |

### Resources

| Parameter | Default |
|-----------|---------|
| `resources.limits.cpu` | `500m` |
| `resources.limits.memory` | `512Mi` |
| `resources.requests.cpu` | `250m` |
| `resources.requests.memory` | `256Mi` |

### Secrets

| Parameter | Default |
|-----------|---------|
| `secrets.vault-secrets.enabled` | `false` |
| `secrets.kubernetes-secrets.enabled` | `false` |

### ServiceAccount

| Parameter | Default |
|-----------|---------|
| `serviceAccount.create` | `true` |
| `serviceAccount.annotations` | `{}` |

### Scheduling

| Parameter | Default |
|-----------|---------|
| `nodeSelector` | `{}` |
| `tolerations` | `[]` |
| `affinity` | `{}` |
