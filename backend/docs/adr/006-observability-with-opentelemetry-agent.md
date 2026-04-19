# ADR-006: Observability with OpenTelemetry Java Agent

## Context
We need an observability strategy for the application. We considered several approaches: Spring Boot Actuator with Micrometer exporting to Prometheus/Grafana, the OpenTelemetry SDK integrated as a code dependency, or the OpenTelemetry Java agent attached at runtime via the container image.

## Alternatives Considered
- **Actuator + Micrometer + Prometheus:** the traditional Spring Boot observability stack. Provides metrics and health checks out of the box, but is metrics-centric — distributed tracing requires additional dependencies (Micrometer Tracing, Zipkin/Jaeger bridges). Ties the application to a specific monitoring backend (Prometheus scrape model).
- **OpenTelemetry SDK as a dependency:** add `opentelemetry-api` and `opentelemetry-sdk` to the application code. Gives fine-grained control over spans and metrics but couples the application to OTel at the code level — instrumentation annotations, SDK configuration, and exporter setup leak into the codebase.
- **Micrometer Tracing bridge to OTel:** Spring Boot's native approach via `micrometer-tracing-bridge-otel`. Works well but still requires code dependencies and configuration within the application.

## Decision
We use the OpenTelemetry Java agent, injected at build time via the Paketo `opentelemetry` buildpack. The agent auto-instruments the application at runtime — no code dependencies, no annotations, no SDK configuration in the codebase. Telemetry signals (traces, metrics, logs) are enabled and configured entirely through environment variables at deployment time.

## Consequences
- **Zero application code changes:** observability is a deployment concern, not an application concern. The codebase stays clean.
- **Vendor-neutral:** OTel is the industry standard. The same agent exports to any OTLP-compatible backend (Jaeger, Grafana Tempo, Datadog, etc.) — no lock-in to Prometheus or any specific stack.
- **Full signal coverage:** the agent auto-instruments HTTP requests, JDBC queries, JPA operations, and Spring components out of the box — broader coverage than manual Micrometer instrumentation.
- **Buildpack integration:** the agent is added as a CNB layer, not baked into the JAR. Upgrading the agent version is a rebuild, not a code change.
- **Less control:** custom spans or business-specific metrics would require adding the OTel SDK as a dependency. For this scope, auto-instrumentation is sufficient.
