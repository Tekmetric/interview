# Solution

## Overview

This is a REST API for managing automotive repair orders, built with Spring Boot 4, Java 25, and an H2 in-memory database. The focus was on building a clean, well-tested CRUD API that demonstrates practical engineering decisions at every layer — from validation and error handling to concurrency control and testing strategy.

## Data Model

```
Customer (1) ──── (N) RepairOrder (1) ──── (N) LineItem
```

- **Customer**: name, email, phone
- **RepairOrder**: description, status (PENDING/IN_PROGRESS/COMPLETED), vehicle info (make, model, year, license plate), timestamps
- **LineItem**: description, unit price — belongs to a repair order

## API Design

All endpoints live under `/api/v1/repair-orders`. The main CRUD operations are straightforward:

| Method | Path | Description |
|--------|------|-------------|
| POST | `/repair-orders` | Create with optional line items |
| GET | `/repair-orders` | List (paginated, sortable) |
| GET | `/repair-orders/{id}` | Get with line items |
| PUT | `/repair-orders/{id}` | Update (requires `If-Match`) |
| DELETE | `/repair-orders/{id}` | Delete (idempotent) |

### Status Transitions

Rather than allowing status changes through PUT (which would bypass business rules), status is managed through dedicated controller endpoints:

| Method | Path | Transition |
|--------|------|------------|
| POST | `/repair-orders/{id}/start` | PENDING -> IN_PROGRESS |
| POST | `/repair-orders/{id}/close` | IN_PROGRESS -> COMPLETED |

The current flow is intentionally simplified to a linear progression. A production system would likely need reopen/cancel support and domain events.

## Demo

API requests are in [`repair-orders.http`](../repair-orders.http) — runnable from IntelliJ or VS Code REST Client. It covers happy paths, error scenarios, and status transitions.

OpenAPI spec available at `/v3/api-docs`, Swagger UI at `/swagger-ui.html`.

## Build & Run

```bash
mvn spring-boot:run
```

### Docker (Cloud Native Buildpacks)

No Dockerfile needed — Spring Boot's Maven plugin builds an OCI image directly, including the OpenTelemetry Java Agent, using the corresponding buildpack.

```bash
mvn spring-boot:build-image
```

## Testing

The testing strategy follows the TestingTrophy model — without E2E tests in this context, so static analysis forms the base, with unit, component, and integration tests layered on top:

- **Static analysis**: Checkstyle (code style), SpotBugs (bug detection), and compile-time checks like JPA metamodel validation catch issues before any test runs.
- **Unit tests** (`src/test/`): Service logic and MapStruct mappers tested in isolation with Mockito and Instancio for test data generation. These run fast and catch logic bugs early.
- **Component tests** (`src/it/`, `*ComponentTest.java`): `@WebMvcTest` slices that verify controller routing, validation, error responses, and HTTP semantics without booting the full app.
- **Integration tests** (`src/it/`, `*IT.java`): Full `@SpringBootTest` with RestTestClient hitting the real stack end-to-end — these catch wiring issues and verify everything works together.

```bash
mvn verify        # runs all three layers
mvn test          # unit tests only
```

### Load Testing

A standalone Gatling project lives in `gatling/` for basic load testing. It currently covers the POST endpoint with a ramp-up scenario. It's intentionally separate from the main Maven build to keep concerns clean.

```bash
cd gatling && mvn gatling:test
```

## CI/CD

GitHub Actions workflow in `.github/workflows/ci.yml` runs four stages sequentially: compile, unit tests, integration tests, and Docker image build.

## Code Quality

Three tools run as part of `mvn verify`:

- **Checkstyle** (Google style) — enforces consistent formatting
- **SpotBugs** — static bug analysis (with an exclude filter for generated JPA metamodel classes)
- **JaCoCo** — test coverage reports in `target/site/jacoco-*/`

## Observability

The application exposes Spring Boot Actuator health endpoints (`/actuator/health`) with liveness/readiness probes enabled. Structured logging follows a consistent pattern across layers — `log.info` at controller entry, `log.debug`/`log.trace` in services.

For distributed tracing, metrics, and log collection, the approach is to attach the OpenTelemetry Java agent at runtime via the Paketo `opentelemetry` buildpack — no code dependencies or SDK wiring needed. The agent auto-instruments HTTP requests, JDBC queries, and JPA operations, exporting to any OTLP-compatible backend through environment variables. This keeps observability as a deployment concern rather than an application concern. See [ADR-006](adr/006-observability-with-opentelemetry-agent.md) for the full rationale.

A Docker Compose setup in [`infra/`](../infra/) spins up the full observability stack locally: the app with OTel agent enabled, an OpenTelemetry Collector, Grafana Tempo (traces), Loki (logs), Prometheus (metrics), and Grafana for visualization. Build the image first, then bring it all up:

```bash
mvn spring-boot:build-image
cd infra && docker compose up
```

Grafana is available at `http://localhost:3000` (no login required) with all datasources pre-provisioned.

## Architecture Decisions

Detailed reasoning behind key choices is documented in [`docs/adr/`](adr/). Topics covered include layered architecture, DTO strategy, error handling, testing approach, OpenAPI integration, observability, and sort field validation.
