package com.interview;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;

/**
 * Standalone class to start Prometheus and Grafana using Testcontainers for local development and monitoring.
 *
 * Usage:
 * 1. Start the Spring Boot application on port 8080
 * 2. Run this class as a Java application
 * 3. Access Grafana at http://localhost:3000 (admin/admin)
 * 4. Prometheus is available at http://localhost:9090
 */
public class ObservabilityStack {

    public static void main(String[] args) {
        System.out.println("Starting Observability Stack (Prometheus + Grafana)...");
        System.out.println();

        // Check if Docker is available
        try {
            System.out.println("Checking Docker environment...");
            org.testcontainers.DockerClientFactory.instance().client();
            System.out.println("✓ Docker is running");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("ERROR: Docker is not running or not accessible!");
            System.err.println();
            System.err.println("Please ensure:");
            System.err.println("  1. Docker Desktop is installed and running");
            System.err.println("  2. Docker daemon is accessible (try 'docker ps' in terminal)");
            System.err.println("  3. You have permissions to access Docker");
            System.err.println();
            System.err.println("On macOS/Windows: Start Docker Desktop");
            System.err.println("On Linux: Run 'sudo systemctl start docker' or add user to docker group");
            System.err.println();
            System.exit(1);
        }

        System.out.println("Make sure your Spring Boot application is running on port 8080");
        System.out.println();

        // Create a network for Prometheus and Grafana to communicate
        Network network = Network.newNetwork();

        // Start Prometheus
        GenericContainer<?> prometheus = new GenericContainer<>(DockerImageName.parse("prom/prometheus:latest"))
                .withNetwork(network)
                .withNetworkAliases("prometheus")
                .withExposedPorts(9090)
                .withCopyFileToContainer(
                        MountableFile.forClasspathResource("prometheus.yml"),
                        "/etc/prometheus/prometheus.yml")
                .withCommand("--config.file=/etc/prometheus/prometheus.yml",
                           "--storage.tsdb.path=/prometheus",
                           "--web.console.libraries=/usr/share/prometheus/console_libraries",
                           "--web.console.templates=/usr/share/prometheus/consoles")
                .withExtraHost("host.testcontainers.internal", "host-gateway")
                .waitingFor(Wait.forHttp("/-/ready").forStatusCode(200))
                .withStartupTimeout(Duration.ofMinutes(2));

        prometheus.start();

        // Start Grafana
        GenericContainer<?> grafana = new GenericContainer<>(DockerImageName.parse("grafana/grafana:latest"))
                .withNetwork(network)
                .withNetworkAliases("grafana")
                .withExposedPorts(3000)
                .withEnv("GF_SECURITY_ADMIN_PASSWORD", "admin")
                .withEnv("GF_SECURITY_ADMIN_USER", "admin")
                .withEnv("GF_AUTH_ANONYMOUS_ENABLED", "true")
                .withEnv("GF_AUTH_ANONYMOUS_ORG_ROLE", "Admin")
                .withCopyFileToContainer(
                        MountableFile.forClasspathResource("grafana-datasource.yml"),
                        "/etc/grafana/provisioning/datasources/datasource.yml")
                .waitingFor(Wait.forHttp("/api/health").forStatusCode(200))
                .withStartupTimeout(Duration.ofMinutes(2));

        grafana.start();

        // Print access information
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Observability Stack Started Successfully!");
        System.out.println("=".repeat(80));
        System.out.println();
        System.out.println("Prometheus:");
        System.out.println("  URL: http://localhost:" + prometheus.getMappedPort(9090));
        System.out.println("  Targets: http://localhost:" + prometheus.getMappedPort(9090) + "/targets");
        System.out.println();
        System.out.println("Grafana:");
        System.out.println("  URL: http://localhost:" + grafana.getMappedPort(3000));
        System.out.println("  Username: admin");
        System.out.println("  Password: admin");
        System.out.println();
        System.out.println("Application Metrics:");
        System.out.println("  Prometheus Endpoint: http://localhost:8080/actuator/prometheus");
        System.out.println("  Metrics Endpoint: http://localhost:8080/actuator/metrics");
        System.out.println();
        System.out.println("Sample Metrics to Explore:");
        System.out.println("  - widget_create_seconds (Timer for widget creation)");
        System.out.println("  - widget_update_seconds (Timer for widget updates)");
        System.out.println("  - widget_delete_seconds (Timer for widget deletion)");
        System.out.println("  - widget_getAll_seconds (Timer for getting all widgets)");
        System.out.println("  - widget_getById_seconds (Timer for getting widget by ID)");
        System.out.println();
        System.out.println("Press Ctrl+C to stop the containers...");
        System.out.println("=".repeat(80));

        // Keep the containers running
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down containers...");
            grafana.stop();
            prometheus.stop();
            network.close();
            System.out.println("Containers stopped.");
        }));

        // Block forever
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
