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

Foreign keys use `ON DELETE CASCADE` at the database level.

## API Endpoints

All endpoints are versioned under `/api/v1`.

| Method | Path                               | Description                              | Status |
|--------|----------------------------------  |------------------------------------------|--------|
| POST   | `/api/v1/repair-orders`            | Create a repair order                    | 201    |
| GET    | `/api/v1/repair-orders`            | List repair orders (paginated, sortable) | 200    |
| GET    | `/api/v1/repair-orders/{id}`       | Get repair order with line items         | 200    |
| PUT    | `/api/v1/repair-orders/{id}`       | Update a repair order                    | 200    |
| DELETE | `/api/v1/repair-orders/{id}`       | Delete a repair order (idempotent)       | 204    |
| POST   | `/api/v1/repair-orders/{id}/start` | Start order (PENDING -> IN_PROGRESS)     | 200    |
| POST   | `/api/v1/repair-orders/{id}/close` | Close order (IN_PROGRESS -> COMPLETED)   | 200    |

### Optimistic Concurrency

PUT, start, and close require an `If-Match` header with the entity version. On conflict, returns `412 Precondition Failed`.

### Status Transitions

Orders follow a simplified linear flow: `PENDING -> IN_PROGRESS -> COMPLETED`. Invalid transitions return `409 Conflict`. A production system would use a proper state machine to support reopen/cancel and emit domain events.

### Sort Validation

The list endpoint validates the `sort` parameter against JPA static metamodel constants at compile time. Invalid fields return `400` with the list of allowed values.

### Error Responses

All errors use RFC 9457 Problem Detail format (`application/problem+json`).

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

A standalone Gatling project is available in `gatling/` for load testing the POST endpoint.

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
