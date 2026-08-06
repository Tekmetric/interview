# Tekmetric Interview — Jamiell Jack

## Overview

This submission implements:
1. A production-ready **Spring Boot CRUD REST API** for a `Vehicle` resource backed by an in-memory H2 database.
2. A **multi-stage Dockerfile** for containerizing the application.
3. A **GitHub Actions CI/CD pipeline** that builds, tests, and publishes the Docker image to GitHub Container Registry (GHCR).
4. A **production-grade Helm chart** for deploying to Kubernetes/K3s.
5. **Full observability stack** — Spring Boot Actuator + Micrometer + Prometheus metrics, structured JSON logging, a pre-wired Prometheus + Grafana docker-compose, a provisioned Grafana dashboard, and Helm `PrometheusRule` alerts.

---

## Architecture

```
┌──────────────────────────────────────────┐
│              HTTP Client                 │
└──────────────────┬───────────────────────┘
                   │
         ┌─────────▼──────────┐
         │   VehicleResource  │  REST Controller (@RestController)
         └─────────┬──────────┘
                   │
         ┌─────────▼──────────┐
         │   VehicleService   │  Business logic interface + impl
         └─────────┬──────────┘
                   │
         ┌─────────▼──────────┐
         │ VehicleRepository  │  Spring Data JPA (JpaRepository)
         └─────────┬──────────┘
                   │
         ┌─────────▼──────────┐
         │   H2 In-Memory DB  │  Seeded via database/data.sql
         └────────────────────┘
```

---

## Spring Boot API

### Vehicle Object

| Field     | Type    | Constraints                              |
|-----------|---------|------------------------------------------|
| `id`      | Long    | Auto-generated                           |
| `make`    | String  | Required, max 100 chars                  |
| `model`   | String  | Required, max 100 chars                  |
| `year`    | Integer | Required, 1886–2100                      |
| `vin`     | String  | Optional, max 17 chars, unique           |
| `mileage` | Integer | Required, ≥ 0                            |

### API Endpoints

| Method | Path                  | Description           | Success Code |
|--------|-----------------------|-----------------------|--------------|
| GET    | `/api/vehicles`       | List all vehicles     | 200          |
| GET    | `/api/vehicles/{id}`  | Get vehicle by ID     | 200          |
| POST   | `/api/vehicles`       | Create vehicle        | 201          |
| PUT    | `/api/vehicles/{id}`  | Update vehicle        | 200          |
| DELETE | `/api/vehicles/{id}`  | Delete vehicle        | 204          |

### Error Responses

| Scenario                    | HTTP Status | Example body                                              |
|-----------------------------|-------------|-----------------------------------------------------------|
| Vehicle not found           | 404         | `{"error": "Vehicle not found with id: 99"}`             |
| Invalid request body        | 400         | `{"error": "Validation failed", "details": {...}}`        |
| Duplicate VIN               | 409         | `{"error": "A vehicle with the provided VIN already exists"}` |

### Example Requests

```bash
# List all vehicles
curl http://localhost:8080/api/vehicles

# Get by ID
curl http://localhost:8080/api/vehicles/1

# Create
curl -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{"make":"BMW","model":"M3","year":2023,"vin":"WBS8M9C52J5J12345","mileage":5000}'

# Update
curl -X PUT http://localhost:8080/api/vehicles/1 \
  -H "Content-Type: application/json" \
  -d '{"make":"Toyota","model":"Camry SE","year":2020,"vin":"1HGBH41JXMN109186","mileage":35000}'

# Delete
curl -X DELETE http://localhost:8080/api/vehicles/1
```

---

## Prerequisites

| Tool           | Version   | Notes                                      |
|----------------|-----------|--------------------------------------------|
| Java JDK       | 8+        | `eclipse-temurin` recommended              |
| Maven          | 3.8+      | Bundled via Spring Boot parent             |
| Docker         | 24+       | For container builds                       |
| kubectl        | 1.28+     | For K8s deployments                        |
| Helm           | 3.14+     | For chart installs                         |
| k3s / minikube | any       | Local cluster for testing                  |

---

## Running Locally

```bash
# 1. Build
cd backend
mvn package

# 2. Run
java -jar target/interview-1.0-SNAPSHOT.jar

# 3. Verify
curl http://localhost:8080/api/welcome
curl http://localhost:8080/api/vehicles
curl http://localhost:8080/actuator/health
```

H2 console is available at `http://localhost:8080/h2-console`
(JDBC URL: `jdbc:h2:mem:testdb;MODE=LEGACY;NON_KEYWORDS=YEAR`, user: `sa`, password: `password`).

---

## Docker

### Build image

```bash
cd backend
docker build -t interview-app:latest .
```

### Run container

```bash
docker run -p 8080:8080 \
  -e JAVA_OPTS="-Xms256m -Xmx512m -XX:TieredStopAtLevel=1 -XX:+UseSerialGC" \
  interview-app:latest
```

### Design decisions

- **Multi-stage build** — the Maven builder stage (`maven:3.9-eclipse-temurin-8`) compiles the JAR; the final runtime image (`eclipse-temurin:8-jre-jammy`) is ~120 MB. Both images are multi-arch (amd64 + arm64).
- **Dependency cache layer** — `pom.xml` is copied first and dependencies are downloaded before source is added, so re-builds only re-download when `pom.xml` changes.
- **Non-root user** — the container runs as `appuser` (UID 1000), satisfying `runAsNonRoot: true`.
- **`JAVA_OPTS` env var** — heap tuning without rebuilding the image. Default: `-Xms256m -Xmx512m -XX:TieredStopAtLevel=1 -XX:+UseSerialGC`.

---

## CI/CD Pipeline (GitHub Actions)

File: `.github/workflows/ci-cd.yml`

### Triggers

| Event              | Condition                                |
|--------------------|------------------------------------------|
| `push`             | Branch `master`, paths under `backend/` |
| `pull_request`     | Target `master`, paths under `backend/` |

### Jobs

```
push to master
│
├─ test            ← mvn verify (runs on every push + PR)
│
├─ docker          ← builds & pushes to GHCR (master push only)
│   └── tags: <sha>, master, latest
│
└─ helm-lint       ← helm lint --strict (runs on every push + PR)
```

### Image registry

Images are pushed to **GitHub Container Registry (GHCR)** using the built-in `GITHUB_TOKEN` — no extra secrets needed.

Image format: `ghcr.io/<org>/interview-app:<sha|branch|latest>`

To use Docker Hub instead, replace the login step:
```yaml
- uses: docker/login-action@v3
  with:
    username: ${{ secrets.DOCKERHUB_USERNAME }}
    password: ${{ secrets.DOCKERHUB_TOKEN }}
```
and set `REGISTRY: docker.io`.

---

## Helm Chart

Location: `backend/helm/interview-app/`

### Quick install

```bash
# Prerequisites: a running K8s/K3s cluster, kubectl configured, Helm 3.14+

# 1. Create namespace
kubectl create namespace interview

# 2. (Optional) Create a Secret for the DB password
kubectl create secret generic interview-app-secret \
  --namespace interview \
  --from-literal=db-password=password

# 3. Install the chart
helm install interview-app ./backend/helm/interview-app \
  --namespace interview \
  --set image.repository=ghcr.io/<your-org>/interview-app \
  --set image.tag=latest

# 4. Access via port-forward
kubectl port-forward -n interview svc/interview-app 8080:8080
curl http://localhost:8080/api/vehicles
```

### Upgrade

```bash
helm upgrade interview-app ./backend/helm/interview-app \
  --namespace interview \
  --set image.tag=<new-sha>
```

### Uninstall

```bash
helm uninstall interview-app --namespace interview
```

### Key values

| Value                                       | Default                 | Description                                  |
|---------------------------------------------|-------------------------|----------------------------------------------|
| `image.repository`                          | `ghcr.io/your-org/...`  | Container image                              |
| `image.tag`                                 | `latest`                | Image tag                                    |
| `replicaCount`                              | `2`                     | Initial pod count (overridden by HPA)        |
| `autoscaling.enabled`                       | `true`                  | Enable HPA                                   |
| `autoscaling.minReplicas`                   | `2`                     | Minimum pods                                 |
| `autoscaling.maxReplicas`                   | `5`                     | Maximum pods                                 |
| `autoscaling.targetCPUUtilizationPercentage`| `70`                    | HPA CPU target                               |
| `podDisruptionBudget.enabled`               | `true`                  | Enable PDB                                   |
| `podDisruptionBudget.minAvailable`          | `1`                     | Pods guaranteed during disruptions           |
| `ingress.enabled`                           | `false`                 | Enable Ingress                               |
| `serviceMonitor.enabled`                    | `false`                 | Enable Prometheus ServiceMonitor (requires Operator)|
| `resources.requests.cpu`                    | `250m`                  | CPU request                                  |
| `resources.limits.memory`                   | `1Gi`                   | Memory limit                                 |

Full configuration: `backend/helm/interview-app/values.yaml`

### Production-ready features

| Feature                   | Implementation                                                   |
|---------------------------|------------------------------------------------------------------|
| Zero-downtime deploys      | `RollingUpdate` with `maxUnavailable: 0`                        |
| Auto-scaling               | `HorizontalPodAutoscaler` on CPU + memory (autoscaling/v2)      |
| High availability          | 2 replicas minimum + pod anti-affinity to spread across nodes   |
| Graceful drain protection  | `PodDisruptionBudget` with `minAvailable: 1`                    |
| Health-gated deploys       | Startup / Liveness / Readiness probes on `/actuator/health`     |
| ConfigMap reload detection | SHA checksum annotation triggers pod restart on config change   |
| Least-privilege runtime    | Non-root user, `allowPrivilegeEscalation: false`, drop `ALL` caps|
| Secret separation          | DB password via K8s Secret, not baked into ConfigMap            |
| Ingress + TLS              | nginx Ingress with optional cert-manager annotations            |
| Prometheus scraping        | Pod annotations (always on) + optional `ServiceMonitor` CRD     |
| Zone awareness             | `topologySpreadConstraints` template for multi-AZ clusters      |

---

## Observability

The observability stack covers three pillars: **metrics**, **logs**, and **alerting**.

```
┌─────────────┐    scrape /actuator/prometheus    ┌──────────────┐
│ interview-  │ ─────────────────────────────────▶│  Prometheus  │
│    app      │                                   └──────┬───────┘
│             │ stdout JSON logs                         │ query
│  /actuator/ │ ──────────────────▶ Loki / ELK    ┌─────▼────────┐
│    health   │   (or any log aggregator)          │   Grafana    │
└─────────────┘                                   └──────────────┘
```

### Local observability stack (docker-compose)

Spin up the app + Prometheus + Grafana in one command:

```bash
cd backend
docker compose -f docker-compose.observability.yml up --build
```

| Service    | URL                              | Credentials   |
|------------|----------------------------------|---------------|
| App        | http://localhost:8080            | —             |
| Prometheus | http://localhost:9090            | —             |
| Grafana    | http://localhost:3000            | admin / admin |

The Grafana dashboard **"Interview App — Spring Boot"** is provisioned automatically. It includes:
- App up/down status
- HTTP request rate and P99 latency by endpoint
- 5xx error rate
- JVM heap usage
- DB connection pool (active / idle / pending)
- GC pause duration
- CPU usage

### Health endpoint

```bash
curl http://localhost:8080/actuator/health
# { "status": "UP", "components": { "db": { "status": "UP" }, ... } }
```

Used as liveness, readiness, and startup probes in the Helm chart.

### Prometheus metrics

```bash
curl http://localhost:8080/actuator/prometheus
```

All metrics carry the `application="interview-app"` label for easy filtering.

| Metric                              | Description                        |
|-------------------------------------|------------------------------------|
| `http_server_requests_seconds_*`    | Latency + count by method/status   |
| `jvm_memory_used_bytes`             | Heap & non-heap memory usage       |
| `jvm_gc_pause_seconds_*`            | GC pause duration                  |
| `hikaricp_connections_*`            | DB connection pool saturation      |
| `process_cpu_usage`                 | JVM CPU utilization                |
| `logback_events_total`              | Log volume by level (warn/error)   |

### Structured logging

Two logging profiles are configured via `logback-spring.xml`:

| Profile       | Format                        | Use case                                |
|---------------|-------------------------------|-----------------------------------------|
| (default)     | Human-readable console        | Local development                       |
| `prod`        | JSON per line                 | Log aggregators (Loki, Datadog, ELK)    |

Enable JSON logging:
```bash
java -Dspring.profiles.active=prod -jar target/interview-1.0-SNAPSHOT.jar
# or in K8s via env var: SPRING_PROFILES_ACTIVE=prod
```

### Alerting (Helm + Prometheus Operator)

When `serviceMonitor.enabled=true`, a `PrometheusRule` is deployed alongside the `ServiceMonitor` with five alerts:

| Alert                      | Condition                                     | Severity |
|----------------------------|-----------------------------------------------|----------|
| `AppDown`                  | No pods scraped for 1 min                     | critical |
| `HighErrorRate`            | 5xx rate > 5% for 2 min                       | warning  |
| `HighP99Latency`           | P99 latency > 2 s for 5 min                   | warning  |
| `HighHeapUsage`            | Heap > 85% for 5 min                          | warning  |
| `DBConnectionPoolExhausted`| Pending connections > 0 for 2 min             | warning  |

### Connecting to kube-prometheus-stack (K8s)

```bash
# Install Prometheus Operator (one-time)
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm install kube-prom prometheus-community/kube-prometheus-stack \
  --namespace monitoring --create-namespace

# Enable ServiceMonitor + PrometheusRule
helm upgrade interview-app ./backend/helm/interview-app \
  --namespace interview \
  --set serviceMonitor.enabled=true \
  --set serviceMonitor.labels.release=kube-prom \
  --set "extraEnv[0].name=SPRING_PROFILES_ACTIVE" \
  --set "extraEnv[0].value=prod"

# Open Grafana
kubectl port-forward -n monitoring svc/kube-prom-grafana 3000:80
# Credentials: admin / prom-operator
# Import dashboard ID 4701 for full JVM coverage
```

### Pod-annotation scraping (no Operator required)

When `serviceMonitor.enabled=false`, Prometheus still scrapes the app automatically if configured with a standard pod-annotation job — the required annotations (`prometheus.io/scrape`, `prometheus.io/path`, `prometheus.io/port`) are applied to every pod via `values.yaml → podAnnotations`.

---

## Design Decisions & Trade-offs

### H2 in-memory database
Chosen per the interview spec. In production, replace with PostgreSQL or MySQL by updating the Spring datasource config and providing a proper database Secret. The Helm chart already externalizes all datasource config to a ConfigMap + Secret.

### H2 2.x + Spring Boot 2.2.x compatibility
H2 2.1.210 introduced strict SQL keyword handling. Two parameters added to the JDBC URL fix it:
- `MODE=LEGACY` — allows Hibernate 5.4 to insert identity columns as `NULL` (H2 2.x previously required `DEFAULT`)
- `NON_KEYWORDS=YEAR` — `YEAR` became a reserved word in H2 2.x; this re-enables it as a column name

### Layered architecture
Controller → Service interface → Service impl → Repository keeps the layers testable in isolation and keeps business logic out of the web layer.

### Input validation
`@Valid` + JSR-303 annotations (`@NotBlank`, `@NotNull`, `@Min`, `@Max`) on the `Vehicle` entity guard the API boundary. The `GlobalExceptionHandler` maps `MethodArgumentNotValidException` → 400 and `DataIntegrityViolationException` (duplicate VIN) → 409.

### Multi-arch Docker images (amd64 + arm64)
The original Dockerfile used `eclipse-temurin:8-jre-alpine` and `maven:3.8.8-eclipse-temurin-8-alpine`. Alpine-based Eclipse Temurin images only publish `linux/amd64` builds, causing `no match for platform` failures on Apple Silicon (arm64). Switched to `eclipse-temurin:8-jre-jammy` (Ubuntu 22.04) and `maven:3.9-eclipse-temurin-8` which publish multi-arch manifests for both amd64 and arm64.

### Java 8 API compatibility
`Map.of()` (used in `GlobalExceptionHandler`) is a Java 9+ factory method. The project targets Java 8 (`<java.version>1.8</java.version>`), so all single-entry maps were replaced with `Collections.singletonMap()` which compiles cleanly under Java 8.

### JVM tuning for container-constrained startup
Spring Boot 2.2.x + Hibernate + H2 performs significant class loading and JIT compilation on first boot. Inside a container limited to `250m` CPU and `512Mi` memory this caused startup times exceeding 3 minutes, repeatedly triggering the startup probe. Two flags were added to `JAVA_OPTS`:
- `-XX:TieredStopAtLevel=1` — disables the heavy C2 optimising JIT, cutting startup time by ~40% (acceptable trade-off; runtime throughput is not a concern for this demo)
- `-XX:+UseSerialGC` — reduces GC thread overhead in a single-core constrained environment

### GHCR over Docker Hub for CI
GHCR uses the repository's `GITHUB_TOKEN` — no manual secret setup, and images inherit repository visibility and access controls automatically.
