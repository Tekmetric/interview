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

## API Endpoints

All endpoints are versioned under `/api/v1`.

| Method | Path                          | Description                        | Status |
|--------|-------------------------------|------------------------------------|--------|
| POST   | `/api/v1/repair-orders`       | Create a repair order              | 201    |
| GET    | `/api/v1/repair-orders`       | List repair orders (paginated)     | 200    |
| GET    | `/api/v1/repair-orders/{id}`  | Get repair order with line items   | 200    |
| PUT    | `/api/v1/repair-orders/{id}`  | Update a repair order              | 200    |
| DELETE | `/api/v1/repair-orders/{id}`  | Delete a repair order (idempotent) | 204    |

### Optimistic Concurrency

PUT requires an `If-Match` header with the entity version. On conflict, returns `412 Precondition Failed`.

### Error Responses

All errors use RFC 9457 Problem Detail format (`application/problem+json`).

## Demo

API requests are in [`http/api.http`](../http/api.http) — runnable from IntelliJ or VS Code REST Client.

OpenAPI spec available at `/v3/api-docs`, Swagger UI at `/swagger-ui.html`.

## Build & Run

```bash
mvn package && java -jar target/interview-1.0-SNAPSHOT.jar
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

## Code Quality

- **Checkstyle** (Google style) — `mvn checkstyle:check`
- **SpotBugs** — static analysis
- **JaCoCo** — coverage reports in `target/site/jacoco-*/`

## Architecture Decisions

See [`docs/adr/`](adr/) for ADRs covering architecture, DTOs, error handling, testing, and OpenAPI.
