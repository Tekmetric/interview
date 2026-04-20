# Solution

## Overview

REST API for managing repair orders built with Spring Boot 4, Java 25, and H2.

## Data Model

```
Customer (1) ──── (N) RepairOrder (1) ──── (N) LineItem
```

- **Customer**: name, email, phone
- **RepairOrder**: description, status (PENDING/IN_PROGRESS/COMPLETED), vehicle info, timestamps
- **LineItem**: description, unit price

## Demo

API requests are in [`repair-orders.http`](../repair-orders.http) — runnable from IntelliJ or VS Code REST Client.

OpenAPI spec available at `/v3/api-docs`, Swagger UI at `/swagger-ui.html`.

## Build & Run

```bash
mvn package && java -jar target/interview-1.0-SNAPSHOT.jar
```

### Docker (Cloud Native Buildpacks)

```bash
mvn spring-boot:build-image
```

## Testing

Three test layers:

- **Unit tests** (`src/test/`): Service and mapper logic with Mockito + Instancio
- **Component tests** (`src/it/`, `*ComponentTest.java`): `@WebMvcTest` and `@DataJpaTest` slices
- **Integration tests** (`src/it/`, `*IT.java`): `@SpringBootTest` with RestTestClient

Run all:

```bash
mvn verify
```

### Load Testing

A simple, standalone Gatling project is available in `gatling/` for load testing the POST endpoint. Actually contains scenario only for the POST endpoint.

```bash
cd gatling && mvn gatling:test
```

## CI/CD

GitHub Actions workflow in `.github/workflows/ci.yml` with four stages: compile, unit tests, integration tests, and Docker build. Currently set to manual dispatch (`workflow_dispatch`) — uncomment the push/PR triggers to enable automatic CI.

## Code Quality

- **Checkstyle** (Google style) — `mvn checkstyle:check`
- **SpotBugs** — static analysis
- **JaCoCo** — coverage reports in `target/site/jacoco-*/`

## Architecture Decisions

See [`docs/adr/`](adr/) for ADRs covering architecture, DTOs, error handling, testing, OpenAPI, observability, and sort validation.
