# Solution Notes

## Overview

CRUD API for a Vehicle resource based on README requirements

## Stack

Upgraded baseline **Spring Boot 2.2.1 → 3.2.3**, **Java 8 → 17** (records, `jakarta.*`).

Notable dependencies beyond the starter web/JPA stack: **springdoc-openapi** (Swagger UI), **spring-boot-starter-validation**, **spring-boot-starter-actuator**.

## Design decisions

- **Package layout** — layered by concern; **`resource`** instead of `controller` to stress REST resources.
- **API keys** — **`api_key`** table plus **`ApiKeyAuthFilter`**: validates **`X-API-Key`** on `/api/**` except `/api/welcome`; supports multiple keys and `active` for revocation; (Keys are plaintext in H2 for the exercise)
- **IDs** — **UUID** chosed over auto-increment numerical
- **Customer** — **`customerName`** as a string for now; can grow into a **`Customer`** entity and ownership/history later.

## API Notes

- **List** — `GET /api/vehicles` is paged with optional query **`make`**, **`year`**, **`customerName`** (case-insensitive match on name).
- **VIN** — optional; **unique when set**; **cannot be cleared** once set (400). Duplicate VIN → **409**.
- **Payload** — optional **`fuelType`**, **`licensePlate`**, flat **`metadata`** - freeform key/value strings with size limit

## Running locally

From the **`backend`** directory:

```bash
mvn test    # all tests (Surefire: unit + Spring Boot tests such as VehicleIntegrationTest)
mvn package && java -jar target/interview-1.0-SNAPSHOT.jar
```

|  | URL |
|------|-----|
| API base | http://localhost:8080/api/vehicles |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| H2 console | http://localhost:8080/h2-console |
| Postman collection | [`postman/Interview-API.postman_collection.json`](postman/Interview-API.postman_collection.json) |

**Test API key** (from seed data): `test-secret-123`