# Observability Stack Setup

This project includes Prometheus and Grafana integration using Testcontainers for monitoring application metrics.

## Prerequisites

**⚠️ IMPORTANT: Docker must be running before starting the observability stack!**

## Quick Start

### 1. Start the Spring Boot Application

```bash
mvn spring-boot:run
```

The application will start on port 8080 with Prometheus metrics enabled at:
- **Prometheus Endpoint**: `http://localhost:8080/actuator/prometheus`
- **Metrics Endpoint**: `http://localhost:8080/actuator/metrics`

### 2. Start Prometheus and Grafana

**Using the provided script**

```bash
./start-observability.sh
```
Generate some load to see metrics in action:

```bash
./run-gatling.sh
```
This script will run for approximately 4 minutes, generating traffic on the application at 8080.

### 3. Access the Monitoring Tools

URLs to the tools (with dynamic ports) should be output at the end of the script execution.

## Available Metrics

In addition to the full set of auto-instrumentation metrics, the application exposes the following custom metrics:

| Metric Name | Type | Description |
|------------|------|-------------|
| `widget_create_seconds` | Timer | Time taken to create a widget |
| `widget_update_seconds` | Timer | Time taken to update a widget |
| `widget_delete_seconds` | Timer | Time taken to delete a widget |
| `widget_getAll_seconds` | Timer | Time taken to retrieve all widgets |
| `widget_getById_seconds` | Timer | Time taken to retrieve a widget by ID |

### Sample Prometheus Queries

```promql
# Average widget creation time (last 5 minutes)
rate(widget_create_seconds_sum[5m]) / rate(widget_create_seconds_count[5m])

# Total number of widget operations
sum(rate(widget_create_seconds_count[5m]))

# 95th percentile response time for getById
histogram_quantile(0.95, rate(widget_getById_seconds_bucket[5m]))

# Request rate for all widget operations
sum(rate({__name__=~"widget_.*_seconds_count"}[5m])) by (__name__)
```
**Widget Operation Latency (p95)**:
```promql
histogram_quantile(0.95,
  sum(rate(widget_create_seconds_bucket[5m])) by (le)
)
```

**Cache Hit Ratio**:
```promql
rate(caffeine_cache_hit_total[5m]) /
  (rate(caffeine_cache_hit_total[5m]) + rate(caffeine_cache_miss_total[5m]))
```

## Stopping the Stack

Press `Ctrl+C` in the terminal where ObservabilityStack is running. This will gracefully shut down both Prometheus and Grafana containers.