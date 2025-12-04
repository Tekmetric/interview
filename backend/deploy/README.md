# Tekmetric Backend

A Spring Boot backend service built with Maven, containerized with Docker, and deployed via Helm on Kubernetes. The project supports both local development and CI/CD pipelines using GitHub Actions and GHCR.

---

## 📁 Project Structure

```
interview/
├── backend/
│   ├── src/                     # Java source code
│   ├── pom.xml                  # Maven configuration
│   ├── Dockerfile               # Multi-stage Docker build
│   ├── deploy/
│   │   └── chart/               # Helm chart for deployment
│   │       ├── Chart.yaml
│   │       ├── values.yaml
│   │       └── templates/
│   └── application.properties   # Spring Boot configuration
└── .github/workflows/cicd.yml   # CI/CD pipeline
```

---

## 🧱 Local Development

### Prerequisites

* Java 21 (Temurin)
* Maven 3.9+
* Docker
* Helm 3.11+
* Minikube (optional, for local K8s deploy)

### Run Locally

```bash
cd backend
mvn spring-boot:run
```

Access the app at: [http://localhost:8080](http://localhost:8080)

#### H2 Console

To access the in-memory database:

```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (leave blank or use configured value)
```

> Note: For development only. `webAllowOthers=true` is enabled via Docker for remote access within cluster.

---

## 🐳 Docker

### Build locally

```bash
cd backend
docker build -t tekmetric-backend:latest .
```

### Run locally

```bash
docker run -p 8080:8080 tekmetric-backend:latest
```

---

## ⚙️ CI/CD (GitHub Actions)

The CI/CD workflow builds, tests, lints Helm charts, and publishes a Docker image to GHCR.

```yaml
context: ./backend
file: ./backend/Dockerfile
push: true
platforms: linux/amd64,linux/arm64
tags:
  ghcr.io/<OWNER>/tekmetric-backend:<SHA>
  ghcr.io/<OWNER>/tekmetric-backend:latest
```

Secrets used:

* `GHCR_PAT`: Personal Access Token with `write:packages`

---

## ☸️ Helm Deployment (Minikube)

### Start Minikube

```bash
minikube start --driver=docker
```

### Load image from GHCR

```bash
docker login ghcr.io -u <your-username> -p <your-ghcr-pat>
```

### Deploy with Helm

```bash
helm upgrade --install tekmetric-backend backend/deploy/chart \
  --set image.repository=ghcr.io/<your-username>/tekmetric-backend \
  --set image.tag=latest
```

### Check deployment

```bash
kubectl get pods
kubectl get svc
```

Access via:

```bash
minikube service tekmetric-backend
```

---

## CRUD API endpoints (examples)
The service exposes standard CRUD endpoints for managing Customers at the base path `/api/customers`.
| Operation | Method | Path | Example |
| :--- | :---: | :---: | :--- |
| List All | GET | /api/customers | `curl http://localhost:8080/api/customers` |
| Get By ID | GET | /api/customers/{id} | `curl http://localhost:8080/api/customers/1` |
| Create New | POST | /api/customers | `curl -X POST -H "Content-Type: application/json" -d '{"name":"Eve","email":"eve@example.com"}' http://localhost:8080/api/customers` |
| Update Existing | PUT | /api/customers/{id} | `curl -X PUT -H "Content-Type: application/json" -d '{"name":"Eve Updated","email":"eve2@example.com"}' http://localhost:8080/api/customers/3` |
| Delete | DELETE | /api/customers/{id} | `curl -X DELETE http://localhost:8080/api/customers/3` |

---

## 🧩 Actuator & Metrics

| Purpose | Path | Example |
| :--- | :---: | :--- |
| Health (overall) | /actuator/health | `curl -sS http://localhost:8080/actuator/health`
| Liveness | /health/liveness | `curl -sS http://localhost:8080/actuator/health/liveness`
| Readiness | /health/readiness | `curl -sS http://localhost:8080/actuator/health/readiness`
| Prometheus metrics | /actuator/prometheus | `curl -sS http://localhost:8080/actuator/prometheus`
| Generic metrics list | /actuator/metrics | `curl -sS http://localhost:8080/actuator/metrics`
| Specific metric | /actuator/metrics/{metric.name} | `curl -sS http://localhost:8080/actuator/metrics/{metric.name}` (e.g. jvm.memory.used)
| Info | /actuator/info | `curl -sS http://localhost:8080/actuator/info`

Prometheus metrics and readiness/liveness groups are explicitly configured.

---

## ServiceMonitor Verification

This section verifies that Prometheus is scraping metrics successfully from the Tekmetric backend.

> Assumes:
>
> * Prometheus Operator is installed in the `monitoring` namespace.

### 1. Confirm the ServiceMonitor Exists

```bash
kubectl get servicemonitors -A
```

If missing:

```bash
kubectl apply -f deploy/monitoring/servicemonitor.yaml
```

### 2. Inspect the ServiceMonitor

```bash
kubectl describe servicemonitor tekmetric-backend-monitor
```

Ensure:

* Endpoint path `/actuator/prometheus`
* Port `http`

### 3. Verify the Target Service

```bash
kubectl get svc tekmetric-backend-tekmetric-backend -o yaml
```

Confirm the port is named `http`:

```yaml
ports:
  - name: http
    port: 8080
```

### 4. Check Prometheus Targets

Forward Prometheus UI:

```bash
kubectl port-forward svc/prometheus-operated -n monitoring 9090:9090
```

Open: [http://localhost:9090](http://localhost:9090)

Go to **Status → Targets**, and look for:

```
tekmetric-backend-monitor /actuator/prometheus
State: UP
```

### 5. Verify Metrics in Prometheus

Query in UI or via curl:

```bash
curl 'http://localhost:9090/api/v1/query?query=http_server_requests_seconds_count'
```

You should see metric samples returned in JSON format.

### 6. Troubleshooting

* **ServiceMonitor missing** → Reapply YAML.
* **Label mismatch** → Ensure `app: tekmetric-backend` matches selector.
* **Port mismatch** → Service port name must match `endpoints.port`.
* **Namespace issues** → Set `namespaceSelector.any: true`.
* **Prometheus label mismatch** → Ensure `release: prometheus` label matches Prometheus instance.
* **RBAC or Network issues** → Check Prometheus operator permissions.
* **Metrics not exposed** → Ensure backend exposes `/actuator/prometheus`.

---

## 🔍 Troubleshooting

**Issue:** `COPY pom.xml not found`

> Ensure Docker build context is `./backend` and the Dockerfile is referenced as `./backend/Dockerfile`.

**Issue:** `Helm lint path error`

> Always use relative path `backend/deploy/chart` from repo root in workflows.

**Issue:** Architecture mismatch on M1 Mac

> Build a multi-platform image:

```bash
docker buildx build --platform linux/amd64,linux/arm64 -t ghcr.io/<user>/tekmetric-backend:latest .
```
