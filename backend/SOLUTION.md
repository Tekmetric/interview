# Solution Notes

## Overview
- Domain: Chose Vehicle resource to align with tekmetric domain
- Philosophy: Focused on laying out initial structure & patterns that should work if project grew while trying to maintain focus on simplicity 

## Upgrades + library additions

- **Versions:** Spring Boot 2.2.1 → **3.2.3**; Java 8 → **17** (records, `jakarta.*`).

- **Dependencies** (see [`pom.xml`](pom.xml)):

  | Artifact | Role |
  |----------|------|
  | `spring-boot-starter-web` | REST API |
  | `spring-boot-starter-data-jpa` | Persistence |
  | `spring-boot-starter-validation` | Bean Validation on DTOs |
  | `h2` (runtime) | In-memory DB |
  | `springdoc-openapi-starter-webmvc-ui` (2.3.0) | OpenAPI + Swagger UI |
  | `spring-boot-starter-actuator` | Health/metrics endpoints |
  | `spring-boot-starter-test` (test) | JUnit, MockMvc, etc. |

## Design Decisions

- **API keys** — basic scheme to secure API access managed in **`api_key`** table handled via **`ApiKeyAuthFilter`**
- **IDs** — **UUID** chosen over auto-increment numerical (or other schemes)
- **Customer** — **`customerName`** as a string for now; can grow into a  **`Customer`** entity and ownership/history later.

## API Notes
- Use `X-API-Key` Header to authenticate requests
- **List** — `GET /api/vehicles` is paged with optional query **`make`**, **`year`**, **`customerName`** (case-insensitive exact match on name).
- **VIN** — optional but unique when set; cannot be cleared once set.
- **Vehicle Attributes** — optional **`fuelType`**, **`licensePlate`**, flat **`metadata`** - freeform key/value strings with size limit
- **Customer Association** - freeform text - see design decisions
- **filtering + pagination** - leverage out of the box schema from Spring Data

## Running locally

From the **`backend`** directory:

```bash
mvn test    # all tests (Surefire: unit + Spring Boot tests such as VehicleIntegrationTest)
mvn package && java -jar target/interview-1.0-SNAPSHOT.jar
```
Use tool of choice using Test API key (defined in seed data): `test-secret-123`

|  | URL |
|------|-----|
| API base | http://localhost:8080/api/vehicles |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| Postman collection | [`postman/Interview-API.postman_collection.json`](postman/Interview-API.postman_collection.json) |
| H2 console | http://localhost:8080/h2-console |

**List all vehicles (first page):**

```bash
curl -s -H "X-API-Key: test-secret-123" \
  "http://localhost:8080/api/vehicles?page=0&size=20"
```
