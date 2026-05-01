# How to Build the Backend Docker Container

## Prerequisites

- Docker installed on your machine
- Access to the `backend/` directory

## Build the Docker Image

From the project root, run:

```bash
cd backend
docker build -t interview-backend .
```

## Run the Container

```bash
docker run -p 8080:8080 interview-backend
```

## Image Details

| Property | Value |
|----------|-------|
| Base image | OpenJDK 8 (slim) |
| Exposed port | 8080 |
| Framework | Spring Boot 2.2.1 |
| Database | H2 (in-memory) |

## Verify Running Container

```bash
curl http://localhost:8080
```

## Optional: Tag and Push to Registry

```bash
# Tag the image
docker tag interview-backend:latest your-registry/interview-backend:latest

# Push to registry
docker push your-registry/interview-backend:latest
```

## Helm Chart Deployment

The Helm chart for deploying to Kubernetes is located in `sre/helm/interview-backend/`.

### Usage

```bash
# Install the chart
helm install my-release ./sre/helm/interview-backend

# With custom image
helm install my-release ./sre/helm/interview-backend --set image.repository=myregistry/interview-backend,image.tag=v1.0.0

# Upgrade
helm upgrade my-release ./sre/helm/interview-backend

# Uninstall
helm uninstall my-release
```

### Access the Application

```bash
kubectl port-forward svc/<release-name>-interview-backend 8080:8080
```

Then access at `http://localhost:8080`

### Key Features

| Component | Details |
|-----------|---------|
| **Deployment** | 1 replica, port 8080, liveness/readiness probes |
| **Service** | ClusterIP on port 8080 |
| **ConfigMap** | Spring Boot configuration with H2 console enabled |
| **HPA** | Disabled by default, can scale based on CPU/memory |

### Configuration Options

| Parameter | Description | Default |
|-----------|-------------|---------|
| `replicaCount` | Number of replicas | `1` |
| `image.repository` | Docker image repository | `interview-backend` |
| `image.tag` | Docker image tag | `latest` |
| `service.type` | Kubernetes service type | `ClusterIP` |
| `service.port` | Service port | `8080` |
| `autoscaling.enabled` | Enable HPA | `false` |
```