# Platform Engineering Interview Solution - Tekmetric Backend Service

## Table of Contents
- [Executive Summary](#executive-summary)
- [Architecture Overview](#architecture-overview)
- [Technical Implementation](#technical-implementation)
  - [Application Modernization](#application-modernization)
  - [Containerization Strategy](#containerization-strategy)
  - [Helm Chart Design](#helm-chart-design)
  - [CI/CD Pipeline Architecture](#cicd-pipeline-architecture)
- [Deployment Guide](#deployment-guide)
  - [Prerequisites](#prerequisites)
  - [Local Development Setup](#local-development-setup)
  - [Staging Deployment](#staging-deployment)
  - [Production Deployment](#production-deployment)
- [Monitoring and Observability](#monitoring-and-observability)
- [Security Implementation](#security-implementation)
- [Testing and Validation](#testing-and-validation)
- [Python Deployment Script Deep Dive](#python-deployment-script-deep-dive)

## Executive Summary

This solution implements a production-grade platform engineering infrastructure for the Tekmetric backend service, demonstrating modern DevOps practices and cloud-native architecture patterns. The implementation focuses on reliability, scalability, security, and observability while maintaining operational simplicity.

### Key Achievements
- **Zero-downtime deployments** with rolling updates and health-check validation
- **Multi-environment support** with environment-specific configurations
- **Automated CI/CD** with semantic versioning and progressive deployment
- **Enterprise-grade monitoring** with Prometheus, OpenTelemetry, and custom metrics
- **Security-first design** with non-root containers and SOPS-encrypted secrets
- **High availability** with 99.9% uptime targets through redundancy and auto-scaling

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         GitHub Repository                       │  
├─────────────────────────────────────────────────────────────────│  
│                                                                 │    
│  ┌──────────────┐   ┌──────────────┐   ┌──────────────┐         │  
│  │   Backend    │   │     Helm     │   │   GitHub     │         │  
│  │  Application │   │    Charts    │   │   Actions    │         │  
│  │  (Java 25)   │   │              │   │   Workflows  │         │  
│  └──────┬───────┘   └──────┬───────┘   └──────┬───────┘         │  
│         │                  │                  │                 │    
└─────────┼──────────────────┼──────────────────┼─────────────────┘
          │                  │                  │
          ▼                  ▼                  ▼
    ┌─────────────┐    ┌─────────────┐   ┌─────────────┐
    │   Docker    │    │ ChartMuseum │   │    CI/CD    │
    │   Image     │    │   (Helm     │   │  Pipeline   │
    │  (Multi-    │    │   Repo)     │   │             │
    │  Platform)  │    └─────────────┘   └─────────────┘
    └─────┬───────┘                              │
          │                                      │
          └──────────────┬───────────────────────┘
                         │
                         ▼
           ┌──────────────────────────────┐
           │     Kubernetes Cluster       │
           ├──────────────────────────────┤
           │  ┌────────┐    ┌────────┐    │
           │  │Staging │    │  Prod  │    │
           │  │  Env   │    │  Env   │    │
           │  └────────┘    └────────┘    │
           │                              │
           │  ┌────────────────────────┐  │
           │  │  Monitoring Stack      │  │
           │  │  - Prometheus          │  │
           │  │  - OpenTelemetry       │  │
           │  │  - Service Mesh        │  │
           │  └────────────────────────┘  │
           └──────────────────────────────┘
```

### Component Interactions

1. **Development Flow**: Code changes trigger automated builds via GitHub Actions
2. **Build Pipeline**: Maven builds → Docker multi-platform images → Container registry push
3. **Deployment Pipeline**: Helm charts + environment values → Kubernetes deployment
4. **Monitoring Flow**: Application metrics → Prometheus → Alerting/Dashboards

## Technical Implementation

### Application Modernization

#### Java and Framework Upgrades

**From Legacy Stack:**
```xml
<!-- Old Stack -->
<java.version>1.8</java.version>
<spring-boot.version>2.2.1.RELEASE</spring-boot.version>
```

**To Modern Stack:**
```xml
<!-- New Stack -->
<java.version>25</java.version>
<spring-boot.version>3.5.6</spring-boot.version>
<maven.compiler.release>25</maven.compiler.release>
```

#### Key Enhancements

1. **Health Endpoints Implementation**
   - `/actuator/health/liveness` - Kubernetes liveness probe
   - `/actuator/health/readiness` - Kubernetes readiness probe
   - `/actuator/prometheus` - Metrics exposition
   - Custom health indicators for database connectivity

2. **Metrics and Monitoring**
   ```yaml
   management:
     endpoints:
       web:
         exposure:
           include: health,prometheus,info,metrics
     metrics:
       export:
         prometheus:
           enabled: true
   ```

3. **Dynamic Versioning System**
   ```xml
   <project.base.version>1.0</project.base.version>
   <revision>${project.base.version}-SNAPSHOT</revision>
   ```
   Version format: `{base}.{build_number}+{date}.{sha}`
   Example: `1.0.45+20250923.abc1234`

### Containerization Strategy

#### Dockerfile Implementation
```dockerfile
FROM amazoncorretto:25.0.0-alpine3.22@sha256:807ea...

# Create non-root user
RUN addgroup -S tekmetric && adduser -S tekmetric -G tekmetric

WORKDIR /app

# Copy pre-built JAR from GitHub Actions artifact
COPY --chown=tekmetric:tekmetric target/app.jar app.jar

USER tekmetric

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Security Features
- Non-root user execution (tekmetric:tekmetric)
- Minimal Alpine-based image
- No build tools in runtime image (JAR pre-built in CI)
- Specific image digest pinning for reproducibility

#### JVM Optimization
```yaml
# Automatic memory calculation in Helm chart
autoJvmMemory: true
jvmMemoryPercentage: 75  # Use 75% of container memory for heap
```

### Helm Chart Design

#### Chart Structure
```
backend-service-chart/
├── Chart.yaml              # Chart metadata and dependencies
├── values.yaml             # Default configuration values
└── templates/
    ├── _helpers.tpl        # Template helpers and functions
    ├── deployment.yaml     # Main application deployment
    ├── service.yaml        # Kubernetes service
    ├── ingress.yaml        # Ingress configuration
    ├── configmap.yaml      # Application configuration
    ├── secret.yaml         # Sensitive configuration
    ├── hpa.yaml            # Horizontal pod autoscaler
    ├── vpa.yaml            # Vertical pod autoscaler
    ├── pdb.yaml            # Pod disruption budget
    ├── network-policy.yaml # Network segmentation
    ├── service-monitor.yaml # Prometheus monitoring
    ├── pod-monitor.yaml    # Direct pod monitoring
    ├── cronjob.yaml        # Scheduled tasks
    ├── migration.yaml      # Database migration jobs
    └── scaled-object.yaml  # KEDA autoscaling
```

#### Key Features

1. **Resource Profiles**
   ```yaml
   sizeProfile: medium
   sizing:
     profiles:
       small:
         limits: {cpu: 250m, memory: 256Mi}
         requests: {cpu: 100m, memory: 128Mi}
       medium:
         limits: {cpu: 500m, memory: 512Mi}
         requests: {cpu: 250m, memory: 256Mi}
       large:
         limits: {cpu: 1000m, memory: 1Gi}
         requests: {cpu: 500m, memory: 512Mi}
   ```

2. **Advanced Scheduling**
   ```yaml
   topologySpreadConstraints:
     enabled: true
     maxSkew: 1
     whenUnsatisfiable: "DoNotSchedule"
   ```

3. **OpenTelemetry Integration**
   ```yaml
   apm:
     enabled: true
     env:
       OTEL_SERVICE_NAME: '{{ include "helm.fullname" . }}'
       OTEL_EXPORTER_OTLP_ENDPOINT: "http://opentelemetry-collector:4318"
   ```

### CI/CD Pipeline Architecture

#### Composite Actions Structure

The pipeline uses reusable composite actions for modularity:

1. **java-build** - Compilation and workspace artifact creation with Maven caching
2. **java-test** - Test execution, packaging, and semantic versioning
3. **docker-build** - Multi-platform image construction and registry push
4. **scan-image** - Trivy security vulnerability scanning
5. **publish-chart** - Helm chart packaging with SHA256 checksums

#### Caching Strategy

```yaml
# Maven dependency caching in build action
- uses: actions/setup-java@v4
  with:
    java-version: 25
    distribution: corretto
    cache: maven  # Caches .m2 repository
    cache-dependency-path: backend/pom.xml

# Workspace artifact caching between jobs
- uses: actions/upload-artifact@v4
  with:
    name: workspace
    path: .
    retention-days: 1  # Short retention for CI artifacts
```

#### Workflow Orchestration

```yaml
jobs:
  build:
    # Compiles code and caches dependencies
    uses: ./.github/actions/java-build
    
  test:
    needs: build
    # Downloads workspace, runs tests, creates versioned JAR
    uses: ./.github/actions/java-test
    outputs:
      version: ${{ steps.test_package.outputs.version }}
      
  docker-build:
    needs: test
    # Downloads JAR artifact, builds multi-platform image
    uses: ./.github/actions/docker-build
    with:
      platforms: linux/amd64,linux/arm64
      
  scan-image:
    needs: docker-build
    # Scans for CVEs and uploads SARIF results
    uses: ./.github/actions/scan-image
    
  publish-chart:
    needs: scan-image
    # Publishes to ChartMuseum with checksums
    uses: ./.github/actions/publish-chart
```

## Deployment Guide

### Prerequisites

#### Required Tools
```bash
# Core tools - REQUIRED
helm version     # >= 3.13.0
kubectl version  # >= 1.28.0
python --version # >= 3.12
docker --version # >= 24.0.0

# Helm plugins - REQUIRED for secret management
helm plugin install https://github.com/jkroepke/helm-secrets

# Python dependencies - REQUIRED for deployment script
pip install kubernetes pyyaml rich
```

#### Required Kubernetes Components

| Component | Purpose | Required | Installation Command |
|-----------|---------|----------|---------------------|
| **Prometheus Operator** | ServiceMonitor CRD, metrics collection | ✅ Yes | See section 3 below |
| **OpenTelemetry Collector** | APM traces, metrics, logs | ✅ Yes | See section 3 below |
| **Metrics Server** | Pod metrics for HPA | ✅ Yes (if using HPA) | See section 3 below |
| **ChartMuseum** | Helm chart repository | ✅ Yes (for CI/CD) | See section 3 below |
| **KEDA** | Event-driven autoscaling | ❌ Optional | See section 3 below |
| **Grafana** | Metrics visualization | ❌ Optional | See section 3 below |
| **VPA** | Vertical Pod Autoscaler | ❌ Optional | Requires VPA operator |

#### Cluster Requirements

- **Minimum Nodes**: 3 (1 control plane + 2 workers for HA)
- **Node Labels**: `topology.kubernetes.io/zone` for topology spreading
- **Resource Capacity**: 
  - Staging: ~4 CPU cores, 8GB RAM total
  - Production: ~8 CPU cores, 16GB RAM total
- **Storage**: PersistentVolume support for stateful components

#### Kubernetes Cluster Setup

1. **Create k3d Cluster**
   ```bash
   k3d cluster create tekmetric \
     --servers 1 \
     --agents 3 \
     --port "80:80@loadbalancer" \
     --port "443:443@loadbalancer"
   ```

2. **Label Nodes for Topology Spreading**
   ```bash
   kubectl label node k3d-tekmetric-server-0 topology.kubernetes.io/zone=local-zone-a
   kubectl label node k3d-tekmetric-agent-0 topology.kubernetes.io/zone=local-zone-b
   kubectl label node k3d-tekmetric-agent-1 topology.kubernetes.io/zone=local-zone-c
   ```

3. **Install Required Components**
   
   ```bash
   # Install Prometheus Operator (required for ServiceMonitor CRD)
   helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
   helm install prometheus prometheus-community/kube-prometheus-stack \
     --namespace monitoring \
     --create-namespace
   
   # Install OpenTelemetry Collector
   helm repo add open-telemetry https://open-telemetry.github.io/opentelemetry-helm-charts
   
   # Create OpenTelemetry Collector values file
   cat <<EOF > otel-values.yaml
   config:
     exporters:
       logging:
         loglevel: info
       prometheus:
         endpoint: 0.0.0.0:8889
     processors:
       batch:
         send_batch_size: 10000
         timeout: 10s
       memory_limiter:
         check_interval: 1s
         limit_mib: 512
         spike_limit_mib: 128
     receivers:
       otlp:
         protocols:
           grpc:
             endpoint: 0.0.0.0:4317
           http:
             endpoint: 0.0.0.0:4318
     service:
       pipelines:
         logs:
           exporters:
           - logging
           processors:
           - memory_limiter
           - batch
           receivers:
           - otlp
         metrics:
           exporters:
           - prometheus
           - logging
           processors:
           - memory_limiter
           - batch
           receivers:
           - otlp
         traces:
           exporters:
           - logging
           processors:
           - memory_limiter
           - batch
           receivers:
           - otlp
   image:
     repository: otel/opentelemetry-collector-contrib
     tag: 0.90.1
   mode: deployment
   ports:
     metrics:
       containerPort: 8889
       enabled: true
       protocol: TCP
       servicePort: 8889
     otlp:
       containerPort: 4317
       enabled: true
       protocol: TCP
       servicePort: 4317
     otlp-http:
       containerPort: 4318
       enabled: true
       protocol: TCP
       servicePort: 4318
   service:
     type: ClusterIP
   serviceAccount:
     create: true
     name: opentelemetry-collector
   EOF
   
   # Install OpenTelemetry Collector
   helm install opentelemetry-collector open-telemetry/opentelemetry-collector \
     --namespace observability \
     --create-namespace \
     --values otel-values.yaml
   
   # Install Metrics Server (required for HPA)
   helm repo add metrics-server https://kubernetes-sigs.github.io/metrics-server/
   helm install metrics-server metrics-server/metrics-server \
     --namespace kube-system \
     --set args={--kubelet-insecure-tls}  # For local k3d
   
   # Install KEDA (optional, for advanced autoscaling)
   helm repo add kedacore https://kedacore.github.io/charts
   helm install keda kedacore/keda \
     --namespace keda \
     --create-namespace
   
   # Install Grafana (optional, for visualization)
   helm repo add grafana https://grafana.github.io/helm-charts
   helm install grafana grafana/grafana \
     --namespace monitoring \
     --set adminPassword='admin' \
     --set service.type=LoadBalancer
   
   # Install ChartMuseum (local chart repository)
   docker run -d --rm -p 9090:8080 \
     --name chartmuseum \
     -e DEBUG=true \
     -e STORAGE=local \
     -e STORAGE_LOCAL_ROOTDIR=/charts \
     chartmuseum/chartmuseum:latest
   ```

### Local Development Setup

#### Building the Application
```bash
cd backend
mvn clean package -Drevision=1.0.0-local
```

#### Building Docker Image Locally
```bash
docker build -t tekmetric-backend:local backend/
docker tag tekmetric-backend:local ghcr.io/${GITHUB_USER}/tekmetric-backend:local
```

#### Local Helm Deployment
```bash
helm install backend-local \
  ./backend/src/main/helm/backend-service-chart \
  --namespace tekmetric-local \
  --create-namespace \
  --set image.tag=local \
  --set image.pullPolicy=Never
```

### Staging Deployment

#### Using the Python Deployment Script
```bash
python .github/scripts/release-and-monitor/release.py \
  --namespace tekmetric-staging \
  --release-name staging-tekmetric-backend \
  --chart-path ./backend/src/main/helm/backend-service-chart \
  --values-file ./backend/src/main/helm/values/staging/staging.yaml \
  --secrets-file ./backend/src/main/helm/values/staging/staging.secrets.yaml \
  --timeout 600
```

### Production Deployment

#### Pre-deployment Checklist
- [ ] All tests passing in staging
- [ ] Security scan completed with no critical vulnerabilities
- [ ] Database migrations tested and rolled back successfully
- [ ] Monitoring alerts configured

#### Production Deployment Command
```bash
python .github/scripts/release-and-monitor/release.py \
  --namespace tekmetric-production \
  --release-name production-tekmetric-backend \
  --chart-path ./backend/src/main/helm/backend-service-chart \
  --values-file ./backend/src/main/helm/values/production/production.yaml \
  --secrets-file ./backend/src/main/helm/values/production/production.secrets.yaml \
  --timeout 1200 \
  --dry-run  # Remove for actual deployment
```

## Monitoring and Observability

### Metrics Collection

#### Application Metrics
```yaml
serviceMonitor:
  enabled: true
  endpoints:
    - port: http-management
      path: /actuator/prometheus
      interval: 15s
    - port: otel-metrics
      path: /metrics
      interval: 15s
```

#### Key Metrics to Monitor
1. **Application Performance**
   - Request latency (p50, p95, p99)
   - Request throughput
   - Error rates by endpoint
   - Database connection pool utilization

2. **JVM Metrics**
   - Heap memory usage
   - GC pause times and frequency
   - Thread pool utilization
   - Class loading statistics

3. **Kubernetes Metrics**
   - Pod CPU/Memory utilization
   - Pod restart count
   - Deployment replica status
   - Network ingress/egress

### OpenTelemetry Configuration

The solution uses OpenTelemetry Collector as a central observability pipeline:

```yaml
# Application sends telemetry to collector
apm:
  enabled: true
  env:
    OTEL_TRACES_EXPORTER: "otlp"
    OTEL_METRICS_EXPORTER: "otlp,prometheus"
    OTEL_LOGS_EXPORTER: "otlp"
    OTEL_EXPORTER_OTLP_ENDPOINT: "http://opentelemetry-collector.observability:4318"
    OTEL_SERVICE_NAME: "tekmetric-backend"
    OTEL_RESOURCE_ATTRIBUTES: "service.namespace={{ .Release.Namespace }}"
```

**Data Flow:**
1. Application → OpenTelemetry Collector (OTLP protocol)
2. Collector processes and batches telemetry data
3. Metrics exported to Prometheus (port 8889)
4. Traces and logs to stdout (can be configured for Jaeger/Elasticsearch)

**Collector Endpoints:**
- `4317`: OTLP gRPC endpoint
- `4318`: OTLP HTTP endpoint  
- `8889`: Prometheus metrics endpoint

### Alerting Rules

Example Prometheus alerting rules:
```yaml
groups:
  - name: tekmetric-backend
    rules:
      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) > 0.05
        for: 5m
        annotations:
          summary: "High error rate detected"
          
      - alert: PodMemoryUsage
        expr: container_memory_usage_bytes / container_spec_memory_limit_bytes > 0.9
        for: 5m
        annotations:
          summary: "Pod memory usage above 90%"
```

## Security Implementation

### Container Security

1. **Base Image Security**
   - Using official AWS Corretto images with digest pinning
   - Regular base image updates
   - Minimal attack surface with Alpine Linux

2. **Runtime Security**
   ```yaml
   securityContext:
     allowPrivilegeEscalation: false
     readOnlyRootFilesystem: true
     runAsNonRoot: true
     runAsUser: 10001
     capabilities:
       drop: ["ALL"]
   ```

3. **Network Policies**
   ```yaml
   networkPolicy:
     enabled: true
     ingress:
       - from:
           - namespaceSelector:
               matchLabels:
                 name: ingress-nginx
   ```

### Secret Management

1. **SOPS Encryption**
   - Secrets encrypted using SOPS with GPG/Age keys
   - Helm-secrets plugin for transparent decryption during deployment
   - Git-friendly encrypted secrets storage

2. **Secret Usage**
   ```bash
   # Encrypt secrets file
   sops -e staging.secrets.yaml > staging.secrets.yaml.enc
   
   # Deployment automatically decrypts via helm-secrets
   helm secrets upgrade --install release-name chart/ \
     --values secrets.yaml.enc
   ```

### Supply Chain Security

1. **Image Scanning**
   ```yaml
   - uses: aquasecurity/trivy-action@0.24.0
     with:
       severity: "CRITICAL,HIGH"
       exit-code: "1"
   ```

2. **Dependency Management**
   - Maven dependency check in CI
   - GitHub Dependabot for automated updates

3. **Artifact Integrity**
   - SHA256 checksums for Helm charts
   - Digest pinning for base images

## Testing and Validation

### Pre-deployment Validation

The Python deployment script performs comprehensive validation:

1. **Resource Validation**
   - Checks CPU/Memory availability
   - Validates against ResourceQuotas
   - Verifies LimitRanges compliance
   - Ensures node capacity

2. **Configuration Validation**
   - Schema validation for Helm values
   - Secret decryption verification
   - Network connectivity checks

### Health Check Configuration

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: http-management
  initialDelaySeconds: 30
  periodSeconds: 10
  failureThreshold: 3

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: http-management
  initialDelaySeconds: 15
  periodSeconds: 10
  failureThreshold: 3

startupProbe:
  httpGet:
    path: /actuator/health/liveness
    port: http-management
  failureThreshold: 30
  periodSeconds: 5
```

## Python Deployment Script Deep Dive

### Key Features

The `release.py` script provides enterprise-grade deployment automation with:

1. **Intelligent Timeout Calculation**
   ```python
   def calculate_deployment_timeout(values: Dict, safety_factor: float):
       # Analyzes probe configurations
       # Considers replica count and rolling update strategy
       # Applies safety factor for reliability
       # Returns optimized timeout value
   ```

2. **Resource Validation**
   ```python
   def validate_cluster_resources(namespace: str, values: Dict):
       # Checks namespace quotas and limits
       # Validates node capacity
       # Provides detailed resource availability report
       # Warns about potential issues
   ```

3. **Secret Masking**
   ```python
   def redact_values_by_path(data: Dict, secret_paths: Set[Tuple]):
       # Identifies secret values from SOPS-decrypted files
       # Replaces with SHA256 hash previews
       # Maintains security in logs and output
   ```

4. **Real-time Monitoring**
   ```python
   def monitor_deployment_rollout():
       # Watches deployment generation changes
       # Tracks pod rollout progress
       # Provides live status updates
       # Captures debug info on failure
   ```

5. **Graceful Shutdown**
   - Handles SIGINT/SIGTERM signals
   - Cleanly terminates Helm subprocess
   - Preserves deployment state

### Deployment Workflow

1. **Pre-flight Checks**
   - Load and decrypt configuration
   - Display configuration diff
   - Validate cluster resources
   - Calculate deployment timeout

2. **Deployment Execution**
   - Run Helm upgrade with helm-secrets
   - Monitor deployment rollout in parallel
   - Stream logs with secret redaction
   - Track pod readiness

3. **Post-deployment Verification**
   - Verify all replicas are ready
   - Capture debug info if needed
   - Report final status

### Usage Examples

**Dry Run Mode:**
```bash
python release.py --dry-run \
  --namespace staging \
  --release-name backend \
  --chart-path ./chart.tgz \
  --values-file values.yaml \
  --secrets-file secrets.yaml
```

**Production Deployment with Custom Timeout:**
```bash
python release.py \
  --namespace production \
  --release-name backend \
  --chart-path ./chart.tgz \
  --values-file values.yaml \
  --secrets-file secrets.yaml \
  --timeout 1800 \
  --safety-factor 3.0
```