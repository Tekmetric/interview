# Tekmetric Backend Interview - Vehicle CRUD API

Spring Boot 3 / Java 17 CRUD service for `Vehicle`, backed by in-memory H2.
Demo rows are seeded from `database/data.sql` on startup; `api.http` provides a ready-to-run flow for IntelliJ.

## Out of scope

- **Authentication / Authorization**
- **Concurrency control for updates**
- **Rate limiting**
- **Caching**
- **CI/CD apart from a basic GH Action**
- **Deployment manifests**
- **Performance: profiling and proper indexes**
- **Postgres profile + Testcontainers**

---

## Quick start

### Prerequisites

- Java 17+
- Maven 3.9+
- (Optional) Docker 24+ for the containerized path

### Run locally

```bash
cd backend
mvn spring-boot:run
```

Smoke test:

```bash
# Seed data is loaded from resources/database/data.sql on startup.
curl -s http://localhost:8080/api/v1/vehicles?size=5 | jq
```

Open endpoints:

- API: <http://localhost:8080/api/v1/vehicles>
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- H2 console: <http://localhost:8080/h2-console> (JDBC URL `jdbc:h2:mem:testdb`, user `sa`, pass `password`)
- Health/metrics (admin port): <http://localhost:8081/actuator/health>, `/actuator/prometheus`

### Run in Docker

```bash
cd backend
docker build -t tekmetric-interview:dev .
docker run --rm -p 8080:8080 -p 8081:8081 tekmetric-interview:dev
```

---

## API

Base URL: `/api/v1/vehicles`

| Verb   | Path    | Success          | Notes                                                                                                                    |
|--------|---------|------------------|--------------------------------------------------------------------------------------------------------------------------|
| POST   | `/`     | 201 + `Location` | Creates a vehicle. Returns 409 if VIN already exists.                                                                    |
| GET    | `/`     | 200              | Paged list. Params: `page`, `size` (capped at 100), `sort`, optional filters `make`, `model`, `year` (case-insensitive). |
| GET    | `/{id}` | 200 / 404        |                                                                                                                          |
| PATCH  | `/{id}` | 200 / 404        | Partial update: fields omitted or `null` are left unchanged (not strict RFC 7396 Merge Patch)                            |
| DELETE | `/{id}` | 204              | Response-idempotent: 204 whether or not the row existed. Safe to retry.                                                  |

---

## Build & test

```bash
mvn verify
```

Test layers:

- `VehicleServiceTest` - pure Mockito unit tests.
- `VehicleRepositoryTest` - `@DataJpaTest` over an auto-provisioned embedded H2 with the Flyway-managed schema (
  generated `make_lower` / `model_lower` columns + indexes), verifies the unique-VIN constraint and Specification
  filters.
- `VehicleResourceTest` - `@WebMvcTest`, exercises HTTP codes, validation, and the Problem Detail shape end-to-end
  through the MVC stack.
- `RequestIdFilterTest` - filter behavior in isolation.

---

## CI

`.github/workflows/build-verify.yml` runs on every `push` and `pull_request`:

1. `mvn verify` - compiles, runs all tests, runs SpotBugs.
2. `docker build` - confirms the Dockerfile still produces a valid image.
3. Uploads Surefire and SpotBugs reports as artifacts.

No publishing step - this is a validation-only pipeline.

---

## Project layout

Packages are organized **by layer** (resource / service / repository / entity / dto / ...) to match the seed project's
existing `com.interview.resource.WelcomeResource` convention.

```
backend/
├── Dockerfile
├── .dockerignore
├── pom.xml
├── spotbugs-exclude.xml
└── src/
    ├── main/
    │   ├── java/com/interview/
    │   │   ├── Application.java
    │   │   ├── config/          JpaConfig, OpenApiConfig, WebConfig
    │   │   ├── dto/             VehicleRequest, VehiclePatchRequest, VehicleResponse, VehicleFilter
    │   │   ├── entity/          Vehicle
    │   │   ├── exception/       VehicleNotFoundException, GlobalExceptionHandler
    │   │   ├── filter/          RequestIdFilter
    │   │   ├── repository/      VehicleRepository
    │   │   ├── resource/        WelcomeResource, VehicleResource
    │   │   └── service/         VehicleService, VehicleMapper (package-private)
    │   └── resources/
    │       ├── application.yml, application-dev.yml, application-prod.yml
    │       ├── logback-spring.xml
    │       ├── db/migration/V1__create_vehicle.sql
    │       └── database/data.sql
    └── test/java/com/interview/
        ├── filter/RequestIdFilterTest.java
        ├── repository/VehicleRepositoryTest.java
        ├── resource/VehicleResourceTest.java
        └── service/VehicleServiceTest.java
```
