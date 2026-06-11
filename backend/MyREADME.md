# Shop management system
Spring Boot backend for an automotive repair shop management system.
Models core shop domain concepts — Customers, Vehicles, Work Orders, and Line Items (parts & labor) — exposed via a REST API.

## Prerequisites
Maven and Java 25. No external database required (in-memory H2).

## Getting started
```bash
mvn clean install
```
Optionally skip all tests (unit + integration): `mvn clean install -DskipTests -DskipITs`

### Run
```bash
mvn spring-boot:run
```

### API browser
Swagger UI at http://localhost:8080/swagger-ui.html

IntelliJ HTTP client files in `http-client/`.

## Project structure

### Shared domain — `domain/`
Java records for domain objects and value objects like `PhoneNumber` and `Vin`.

### API layer — `api/`
- `controller/` — `VehicleRestController`, `WorkOrderRestController` with top-level routes `/vehicles`, `/work-orders`
- `request/` and `response/` — DTOs for REST methods
- `mapper/` — DTO-to-domain mappers (MapStruct)

Query param filtering for related resources (e.g. `/vehicles?customerId={id}`).

### Service layer — `service/`
Business logic for CRUD operations and search. `exception/` for service-generated exceptions.

### Persistence layer — `repository/`
Spring Data repositories with JPA entities (`entity/`), domain-to-entity mappers (`mapper/`), and custom converters for value types (`converter/`).

### Configuration — `config/`
Bean config in `config/`. Property config in `src/main/resources/application.yml`.

## Testing
See [docs/testing.md](docs/testing.md).

## Documentation
- [Architecture](docs/architecture.md) — design decisions (flat REST routes, UUIDv7 keys, layered architecture)
- [Dependencies](docs/dependencies.md) — third-party dependency docs
- [Dev tools](docs/dev-tools.md) — formatting, static analysis, build plugins
- [Testing](docs/testing.md) — test strategy and running tests
