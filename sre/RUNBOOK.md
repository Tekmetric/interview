# Kind + Argo CD Runbook

For local demo, everything is deployed to a kind cluster via Argo CD. The only manual
steps are installing Argo CD and applying the Argo CD Applications; the system
components (ingress-nginx, metrics-server) and backend app are then managed by Argo CD.

## Prerequisites
- Docker
- kubectl
- kind
- Helm
- Kustomize (either `kubectl kustomize` or standalone `kustomize`)
- Docker Hub account (or any reachable container registry)
- Optional: Prometheus Operator (for ServiceMonitor)

### 1) Create a kind cluster
```bash
kind create cluster --config sre/kind/kind-config.yaml
```

### 2) Build and push the backend image
The image is built via the GitHub Actions workflow.

### 3) Point Argo CD Applications to your fork and image
Update these files:
- `sre/argocd/applications/system.yaml` (`repoURL`, `targetRevision`)
- `sre/argocd/applications/interview-backend.yaml` (`repoURL`, `targetRevision`)
- `sre/charts/interview-backend/values.yaml` (`image.repository`, `image.tag`, `image.pullPolicy`)

### 4) Install Argo CD
```bash
kubectl apply --server-side -k sre/argocd/install
kubectl -n argocd rollout status deploy/argocd-server
```

### 5) Access the Argo CD UI and log in
```bash
kubectl -n argocd port-forward svc/argocd-server 8080:443
```

Open: `https://localhost:8080`

Get the initial admin password:
```bash
kubectl -n argocd get secret argocd-initial-admin-secret \
  -o jsonpath="{.data.password}" | base64 --decode; echo
```

Login:
- Username: `admin`
- Password: (output above)

### 6) Apply Argo CD Applications (system + backend)
```bash
kubectl apply -k sre/argocd/applications
```

Check app status:
```bash
argocd app list
argocd app get system
argocd app get interview-backend
```

### 7) Validate (after Argo CD syncs)
```bash
kubectl -n interview port-forward svc/interview-backend 8080:80
curl http://localhost:8080/api/welcome
curl http://localhost:8080/actuator/health
```

If using ingress-nginx, you can also hit:
```
http://backend.localtest.me:8080/api/welcome
```

## Observability
- Health endpoint: `/actuator/health`
- Prometheus metrics: `/actuator/prometheus`
- To enable a ServiceMonitor (Prometheus Operator), set:
  - `metrics.serviceMonitor.enabled=true`
  - optional `metrics.serviceMonitor.additionalLabels`

## CI/CD
The GitHub Actions workflow builds, scans (Trivy), and pushes to Docker Hub on `push` to `master`.
Configure repository secrets:
- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`

