# Backend Helm Chart (kind + Helm local workflow)

These steps run the backend container locally on macOS inside a kind Kubernetes cluster using Helm.

## Prerequisites
- Docker Desktop (or Docker CLI) running
- kind, kubectl, helm, make (installed automatically via `devbox shell` at repo root)
- macOS (Intel/Apple Silicon) with enough resources for a single-node kind cluster

## TL;DR (Make)

```bash
# One-time: open toolchain
devbox shell

# End-to-end: build → kind → load → helm (ClusterIP) → verify → test
make all

# Redeploy fast after code/image changes
make redeploy

# Cleanup (uninstall release and delete kind cluster)
make cleanup
```

## Quick start with Makefile

From repo root:

```bash
# Enter devbox shell for tools (kind, kubectl, helm, make)
devbox shell

# Full end-to-end: build → kind → load → helm (ClusterIP) → verify → test
make all

# Redeploy fast after code/image changes
make redeploy

# Cleanup (uninstall release and delete kind cluster)
make cleanup
```

## 0) Dev environment

Use Devbox from the repo root to install tools:

```bash
devbox shell
```

Available scripts:

- `cluster:create` – create kind cluster `dev-cluster`
- `cluster:delete` – delete kind cluster `dev-cluster`

## 1) Build the Docker image

From repo root (or `backend/`):

```bash
cd backend
# Build image that the chart expects by default
docker build -t backend-service:dev .
```

## 2) Create a kind cluster

```bash
devbox run cluster:create
```

## 3) Load the image into kind

```bash
kind load docker-image backend-service:dev --name dev-cluster
```

## 4) Install the Helm chart

Install into namespace `backend` (created if missing). This exposes the service as NodePort for easy local access.

```bash
helm upgrade --install backend platform/charts/backend \
  --namespace backend --create-namespace \
  --set image.repository=backend-service \
  --set image.tag=dev \
  --set service.type=NodePort \
  --set service.nodePort=30080
```

- To enable Prometheus scrape annotations (if your app exposes `/metrics`):

```bash
helm upgrade --install backend platform/charts/backend \
  --namespace backend --create-namespace \
  --set image.repository=backend-service \
  --set image.tag=dev \
  --set metrics.enabled=true
```

## 4.1) Optional: JVM metrics via JMX Exporter sidecar (no app changes)

Enable a sidecar that exposes JVM metrics on port 9404 using Prometheus JMX Exporter. This works even if the app does not expose `/metrics`.

```bash
helm upgrade --install backend platform/charts/backend \
  --namespace backend --reuse-values \
  --set jmxExporter.enabled=true
```

Verify the sidecar serves metrics:

```bash
# Find pod
POD=$(kubectl -n backend get pod -l app.kubernetes.io/name=backend -o jsonpath='{.items[0].metadata.name}')
# Port-forward sidecar metrics port
kubectl -n backend port-forward "$POD" 9404:9404 &
PF=$!; sleep 2
curl -s http://localhost:9404/metrics | head -n 20
kill $PF
```

Notes:
- When enabled, the chart injects `JAVA_TOOL_OPTIONS` to open JMX on 9010, and adds a `prom/jmx-exporter` sidecar scraping `127.0.0.1:9010`.
- Default JMX rules cover common JVM metrics; override via `--set-file jmxExporter.config=path/to/config.yaml` if needed.

## 5) Verify the deployment

```bash
kubectl -n backend rollout status deploy/backend
kubectl -n backend get pods -o wide
kubectl -n backend get svc backend -o wide
```

- If installed as NodePort (30080):

```bash
curl -s http://localhost:30080/api/welcome
```

- If installed as ClusterIP (default), port-forward:

```bash
kubectl -n backend port-forward svc/backend 8080:8080 &
curl -s http://localhost:8080/api/welcome
```

## 6) Cleanup

```bash
helm -n backend uninstall backend || true
devbox run cluster:delete
```

## Values

Key values you can override:

- `replicaCount` – number of replicas
- `image.repository`, `image.tag`, `image.pullPolicy`
- `service.type` (ClusterIP|NodePort), `service.port`, `service.nodePort`
- `probes.liveness.*`, `probes.readiness.*`
- `resources.requests.*`, `resources.limits.*`
- `metrics.enabled`, `metrics.path`, `metrics.port`
- `jmxExporter.enabled`, `jmxExporter.port`, `jmxExporter.image`, `jmxExporter.config`
