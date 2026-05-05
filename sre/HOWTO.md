# How to Build and Deploy the Interview Backend

## Prerequisites

| Tool | Purpose | Install |
|------|---------|---------|
| Docker | Build and run the container image | [docs.docker.com](https://docs.docker.com/get-docker/) |
| k3d | Create a local K3s cluster inside Docker | Auto-installed by `bootstrap-k3s.sh` |
| kubectl | Interact with the cluster | Auto-installed by `bootstrap-k3s.sh` |
| Helm ≥ 3.2 | Install Helm charts | Auto-installed by `bootstrap-k3s.sh` |
| SSH key | ArgoCD clones the repo via SSH | `~/.ssh/id_ed25519` or `~/.ssh/id_rsa` |

Docker must be installed manually before running the bootstrap script. Everything else is installed automatically.

---

## 1. Building the Docker Image

The `backend/Dockerfile` uses a two-stage build:

- **Stage 1** (`maven:3.9-eclipse-temurin-8`) — compiles the Spring Boot app with Maven and downloads the OpenTelemetry Java agent (v2.27.0).
- **Stage 2** (`eclipse-temurin:8-jre-jammy`) — copies only the compiled JAR and the OTel agent into a minimal JRE image.

From the **project root**, run:

```bash
docker build -t interview-backend:latest ./backend
```

This produces a local image tagged `interview-backend:latest`. The OTel Java agent is baked in and activated at startup via `JAVA_TOOL_OPTIONS`.

### Verify the image built correctly

```bash
docker images interview-backend
```

---

## 2. Running the Container Locally

Run the image and map port 8080:

```bash
docker run --rm -p 8080:8080 interview-backend:latest
```

Verify the application is responding:

```bash
curl http://localhost:8080/api/welcome
```

You should see a `Welcome` response. The H2 console is also available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`, no password).

### Override OTel endpoint (optional)

By default the container tries to send telemetry to `http://localhost:4317`. To point it at a local collector:

```bash
docker run --rm -p 8080:8080 \
  -e OTEL_EXPORTER_OTLP_ENDPOINT=http://host.docker.internal:4317 \
  interview-backend:latest
```

---

## 3. Testing the Helm Chart Locally

### 3a. Lint and template validation (no cluster required)

```bash
helm lint sre/helm/interview-backend -f sre/helm/interview-backend/values-k3s.yaml
```

Render all templates to stdout to inspect the output:

```bash
helm template interview-backend sre/helm/interview-backend \
  -f sre/helm/interview-backend/values-k3s.yaml \
  --debug
```

### 3b. Deploy to an existing k3d cluster

The `values-k3s.yaml` overlay is designed for local k3d use — it sets `pullPolicy: Never`, clears the ECR registry prefix, and applies lightweight resource limits.

> **Note:** The Helm chart depends on Istio CRDs (VirtualService, Gateway, DestinationRule), the OpenTelemetry Operator CRD (OpenTelemetryCollector), and Argo Rollouts CRD (Rollout). The fastest way to get all of these is to run `./sre/bootstrap-k3s.sh --up` (see Section 4). The steps below assume those operators are already installed.

**Step 1 — Load the image into k3d** (so `pullPolicy: Never` can find it):

```bash
k3d image import interview-backend:latest --cluster interview
```

**Step 2 — Install the chart with the k3s values overlay:**

```bash
helm upgrade --install interview-backend sre/helm/interview-backend \
  -f sre/helm/interview-backend/values-k3s.yaml \
  --wait --timeout 5m
```

**Step 3 — Verify pods are running:**

```bash
kubectl get pods -n interview-backend
```

**Step 4 — Access the application:**

```bash
kubectl port-forward svc/interview-backend -n interview-backend 8080:8080
curl http://localhost:8080/api/welcome
```

**Upgrade after a code change:**

```bash
docker build -t interview-backend:latest ./backend
k3d image import interview-backend:latest --cluster interview
helm upgrade interview-backend sre/helm/interview-backend \
  -f sre/helm/interview-backend/values-k3s.yaml
```

**Uninstall:**

```bash
helm uninstall interview-backend -n interview-backend
```

---

## 4. Full Local Stack — `bootstrap-k3s.sh`

`sre/bootstrap-k3s.sh` automates the complete local environment. It creates a k3d cluster and installs all platform components in the correct order.

### What it installs

| Component | Namespace | Purpose |
|-----------|-----------|---------|
| Istio + Istiod | `istio-system` | Service mesh, mTLS, traffic management |
| Istio Ingress Gateway | `istio-ingress` | External ingress via VirtualService |
| cert-manager | `cert-manager` | TLS certificate issuance |
| OpenTelemetry Operator | `opentelemetry-operator-system` | Manages OTel Collector sidecars |
| External Secrets Operator | `eso` | Syncs secrets from AWS Secrets Manager |
| Argo Rollouts | `argo-rollouts` | Progressive (canary) delivery |
| Observability stack | `observability-stack` | Prometheus, Loki, Tempo, Grafana, k8s-monitoring |
| ArgoCD | `argocd` | GitOps continuous delivery |
| interview-backend | `interview-backend` | The application (deployed by ArgoCD) |

After all operators are running, the script builds the Docker image, loads it into k3d, configures ArgoCD with your SSH key to sync from this repo, and runs a smoke test against `GET /api/welcome`.

### Running the bootstrap

```bash
# Start everything (takes ~15–20 minutes on first run)
./sre/bootstrap-k3s.sh --up
```

The script prints progress at each step. If any step fails it exits immediately with an `[ERROR]` message.

### Environment variables

You can override defaults before running:

```bash
CLUSTER_NAME=my-cluster K3D_MEMORY=12g ./sre/bootstrap-k3s.sh --up
```

| Variable | Default | Description |
|----------|---------|-------------|
| `CLUSTER_NAME` | `interview` | k3d cluster name |
| `K3D_MEMORY` | `20g` | Memory allocated to the k3d server node |
| `K3D_VERSION` | _(latest)_ | Pin a specific k3d version (e.g. `v5.7.5`) |
| `HELM_VERSION` | `v4.1.4` | Helm version to install if not present |

### Accessing services (WSL2 on Windows)

After `--up` completes, open a **separate** WSL2 terminal and keep it running:

```bash
kubectl port-forward svc/istio-ingress -n istio-ingress 8080:80
```

Then add these entries to your Windows hosts file (`C:\Windows\System32\drivers\etc\hosts`):

```
127.0.0.1  grafana.local interview-backend.local
```

Open in your browser:

| URL | Credentials |
|-----|-------------|
| `http://interview-backend.local:8080/api/welcome` | — |
| `http://grafana.local:8080` | `admin` / `admin` |

### Teardown

```bash
./sre/bootstrap-k3s.sh --teardown
```

This deletes the k3d cluster and all data inside it.

---

## 5. Key Configuration Reference

### Helm chart value files

| File | Purpose |
|------|---------|
| `values.yaml` | Defaults — ECR registry, Istio enabled, canary rollout |
| `values-k3s.yaml` | Local k3d overlay — no registry, `pullPolicy: Never`, resource limits |
| `values-eks.yaml` | AWS EKS overlay — External Secrets enabled, ECR pull |

### Configurable parameters

| Parameter | Default | Description |
|-----------|---------|-------------|
| `replicaCount` | `1` | Pod replicas (ignored when rollout enabled) |
| `image.registry` | ECR URL | Container registry prefix |
| `image.tag` | `""` | Image tag; empty uses chart `appVersion` |
| `image.pullPolicy` | `IfNotPresent` | `Never` for local k3d |
| `rollout.enabled` | `true` | Use Argo Rollouts canary instead of Deployment |
| `autoscaling.enabled` | `false` | Enable HPA (CPU/memory) |
| `autoscaling.minReplicas` | `1` | HPA minimum |
| `autoscaling.maxReplicas` | `100` | HPA maximum |
| `networkPolicy.enabled` | `false` | Enable NetworkPolicy |
| `podDisruptionBudget.enabled` | `false` | Enable PDB |
| `externalSecrets.enabled` | `false` | Sync secrets from AWS Secrets Manager |
| `istio.gateway.host` | `interview-backend.local` | Istio Gateway SNI hostname |
