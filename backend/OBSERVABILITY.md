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

### 3. Access the Monitoring Tools

URLs to the tools (with dynamic ports) should be output at the end of the script execution.

## Available Metrics

The application exposes the following custom metrics:

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

## Creating Grafana Dashboards

1. Login to Grafana (`http://localhost:3000`)
2. Click **+** → **Import Dashboard**
3. Use dashboard ID `4701` (JVM Micrometer) or `12900` (Spring Boot Statistics)
4. Or create custom dashboards using the metrics listed above

### Sample Dashboard Panels

**Widget Operations Rate**:
```promql
sum(rate(widget_create_seconds_count[1m])) by (job)
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

## Testing the Metrics

Generate some load to see metrics in action:

```bash
# Create widgets
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/widgets \
    -H "Content-Type: application/json" \
    -d "{\"name\":\"Widget $i\",\"description\":\"Test widget $i\"}"
done

# Get all widgets (to see cache performance)
for i in {1..100}; do
  curl http://localhost:8080/api/widgets
done

# Get widget by ID (to see cache hits)
for i in {1..100}; do
  curl http://localhost:8080/api/widgets/1
done
```

Then check:
- Prometheus targets: `http://localhost:9090/targets`
- Prometheus graph: `http://localhost:9090/graph`
- Grafana explore: `http://localhost:3000/explore`

## Stopping the Stack

Press `Ctrl+C` in the terminal where ObservabilityStack is running. This will gracefully shut down both Prometheus and Grafana containers.

## Troubleshooting

### "Could not find a valid Docker environment" Error

**This is the most common issue!** It means Docker is not running.

**Solution:**

1. **Start Docker Desktop**
   - macOS: Open Docker Desktop from Applications
   - Windows: Open Docker Desktop from Start Menu
   - Wait until the Docker icon in system tray shows "Docker Desktop is running"

2. **Verify Docker is accessible:**
   ```bash
   docker ps
   ```
   Should show a list of containers (may be empty) without errors

3. **If still not working:**
   - **macOS/Windows**: Restart Docker Desktop
   - **Linux**:
     ```bash
     sudo systemctl start docker
     # or add your user to the docker group
     sudo usermod -aG docker $USER
     # then log out and back in
     ```

4. **Check Docker resources:**
   - Docker Desktop → Settings → Resources
   - Ensure at least 4GB RAM is allocated
   - Ensure disk space is available

5. **After fixing Docker, retry:**
   ```bash
   ./start-observability.sh
   ```

### Prometheus shows target as DOWN

1. Verify the Spring Boot app is running on port 8080
2. Check the Prometheus endpoint is accessible: `curl http://localhost:8080/actuator/prometheus`
3. Check Docker can reach host: The configuration uses `host.testcontainers.internal` which should resolve to your host machine

### Grafana can't connect to Prometheus

1. Verify both containers are on the same network
2. Check the datasource configuration in Grafana → Configuration → Data Sources
3. The URL should be `http://prometheus:9090` (using the network alias)

### No metrics appearing

1. Generate some load by calling the API endpoints
2. Wait 15-30 seconds for Prometheus to scrape
3. Check the `/actuator/prometheus` endpoint directly to see if metrics are being exposed

## Configuration Files

- **Prometheus Config**: `src/test/resources/prometheus.yml`
- **Grafana Datasource**: `src/test/resources/grafana-datasource.yml`
- **Application Config**: `src/main/resources/application.properties`

## Architecture

```
┌─────────────────┐
│  Spring Boot    │
│  Application    │
│  :8080          │
│                 │
│  /actuator/     │
│  prometheus     │
└────────┬────────┘
         │ scrapes every 15s
         │
┌────────▼────────┐         ┌─────────────────┐
│   Prometheus    │◄────────│    Grafana      │
│   :9090         │  reads  │    :3000        │
│                 │  data   │                 │
│  Metrics DB     │         │  Visualization  │
└─────────────────┘         └─────────────────┘
```

## Additional Resources

- [Micrometer Documentation](https://micrometer.io/docs)
- [Prometheus Query Language](https://prometheus.io/docs/prometheus/latest/querying/basics/)
- [Grafana Dashboards](https://grafana.com/grafana/dashboards/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
