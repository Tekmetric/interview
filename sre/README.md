---
# Backend Service – Kubernetes Productionization Demo
This project shows how Java Spring Backend Service can be built, containerized, and deployed to Kubernetes using Docker and Helm.

The focus is on creating a setup that is easy to run while reflecting common production considerations such as health checks, resource management, scaling, and observability.


## Quick Start

The application can be built either locally using Docker **or** via the included GitHub Actions pipeline.

The GitHub workflow runs on pushes and pull requests and performs the same container build automatically, which mirrors a typical CI setup.  
For local testing or demo purposes, the steps below can be used.

```bash
# -----------------------------
# Build Docker Image
# -----------------------------
docker build -t docker.io/<youruser>/tekmetric-backend:local .

# Optional local run
docker run --rm -p 8080:8080 docker.io/<youruser>/tekmetric-backend:local

# Push image if your cluster cannot pull local images
docker login
docker push docker.io/<youruser>/tekmetric-backend:local

# -----------------------------
# Install Monitoring Stack
# -----------------------------
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

helm upgrade \
  --install kube-prometheus prometheus-community/kube-prometheus-stack \
  -n monitoring --create-namespace \
  -f ./helm/kube-prom-stack/values.yaml

# -----------------------------
# Install Spring Boot Dashboards
# (Dashboards can also be imported manually via Grafana UI)
# -----------------------------
helm upgrade \
  --install springboot-dashboards ./helm/springboot-grafana-dashboards \
  -n monitoring --create-namespace

# -----------------------------
# Deploy Application
# -----------------------------
helm upgrade \
  --install backend ./backend \
  -n backend --create-namespace \
  --set image.repository=docker.io/<youruser>/tekmetric-backend \
  --set image.tag=local

# -----------------------------
# Access Grafana
# -----------------------------
kubectl -n monitoring port-forward svc/kube-prometheus-grafana 3000:80

# Open browser:
# http://localhost:3000
# Default credentials: admin / admin
# Dashboard location:
# Home → Dashboards → Home assignment - Spring Boot Backend
```


## Notes & Design

### Observability

Spring Boot Actuator is enabled to expose metrics required by Prometheus.

The necessary Actuator and Micrometer dependencies are included, and the application is configured to expose the relevant endpoints via `application.properties`.

Monitoring is included directly in the deployment.

Prometheus discovers application metrics using a ServiceMonitor, and Grafana dashboards are provided via ConfigMaps.  
This allows the service to be observable immediately after deployment.

---

### Dockerfile

The Dockerfile uses a multi-stage build.

The application is built with Maven and then executed using small JRE image.  
This keeps the runtime container lightweight.

Dependencies are resolved separately for faster rebuilds.
The container runs as a non-root user.

Basic JVM flags are included to ensure stable behavior inside k8s.

---

### CI/CD

A minimal GitHub Actions workflow is included.

The pipeline runs manually as well as on push/pull requests and builds the Docker image using the same Dockerfile.  
Builds are consistent between local and CI environments.

---

### Deployment

The Helm chart includes basic production considerations:

- Resource requests and limits
- Readiness/liveliness probes
- Horizontal Pod Autoscaler (HPA)
- Pod Disruption Budget (PDB) -  not enabled by default
- ServiceMonitor integration

The goal is a stable deployment while keeping the setup simple.
