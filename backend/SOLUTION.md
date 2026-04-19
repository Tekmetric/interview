# Solution — Repair Order Management API

## Approach
This solution was developed with an AI-first mindset — using Claude Code as a pair programming partner. In production workflows I also use [OpenSpec](https://github.com/Fission-AI/OpenSpec) for spec-driven development with AI, which was omitted here to keep the scope simple.

## Prerequisites

With [SDKMAN](https://sdkman.io/) installed, run `sdk env` in the project root to set up the correct Java and Maven versions (see `.sdkmanrc`).

## Build & Run

### Maven
```bash
mvn package && java -jar target/interview-1.0-SNAPSHOT.jar
```

### Docker (Cloud Native Buildpacks)
```bash
mvn spring-boot:build-image
docker run -p 8080:8080 interview:1.0-SNAPSHOT
```

The container image is built using Paketo Buildpacks (no Dockerfile) and includes the OpenTelemetry Java agent via the `paketo-buildpacks/opentelemetry` buildpack. To enable collecting Telemetry signals at runtime:

```bash
docker run -p 8080:8080 \
  -e OTEL_SERVICE_NAME=repair-order-api \
  -e OTEL_EXPORTER_OTLP_ENDPOINT=http://collector:4317 \
  interview:1.0-SNAPSHOT
```

## API Overview

Base URL: `http://localhost:8080/api`

### Entities
```
Customer (1) ──── (N) RepairOrder (1) ──── (N) LineItem
```

Each entity supports full CRUD with pagination on list endpoints.

API request examples are available in [`http/api.http`](http/api.http) (IntelliJ HTTP Client / VS Code REST Client).

## Quality & Testing

We follow the **testing trophy** model — static analysis as the foundation, slice tests carrying the most weight, targeted unit tests, and integration tests as the safety net.

### Static Analysis
- **Checkstyle** (Google checks) — runs during `validate` phase
- **SpotBugs** — runs during `verify` phase

### Test Levels

| Source | Plugin | Command | What runs |
|--------|--------|---------|-----------|
| `src/test/` | Surefire | `mvn test` | Unit tests (`*Test.java`) |
| `src/it/` | Failsafe | `mvn verify` | Slice + integration tests (`*IT.java`) |

- **Unit tests** (`src/test/`) — targeted service logic with mocked dependencies
- **Slice tests** (`src/it/`) — `@WebMvcTest` (controllers), `@DataJpaTest` (repositories)
- **Integration tests** (`src/it/`) — `@SpringBootTest` full stack

### Coverage
JaCoCo collects coverage from both surefire and failsafe runs and produces a merged report.

```bash
mvn verify        # static analysis + all tests + merged coverage report
```

## Architecture Decision Records

- [ADR-001: Layered Architecture over Hexagonal](docs/adr/001-layered-architecture-with-entity-as-domain.md)
- [ADR-002: DTOs and Validation at Controller Boundary](docs/adr/002-dtos-and-validation-at-controller-boundary.md)
- [ADR-003: Error Handling Strategy](docs/adr/003-error-handling-strategy.md)
- [ADR-004: Testing Trophy Strategy](docs/adr/004-testing-strategy.md)
- [ADR-005: Code-First OpenAPI](docs/adr/005-code-first-openapi.md)
- [ADR-006: Observability with OpenTelemetry Agent](docs/adr/006-observability-with-opentelemetry-agent.md)

## H2 Console

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`
