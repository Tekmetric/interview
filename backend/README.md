# 🚗 Vehicle & User Management API

A **modular Spring Boot application** for managing **Cars** and **Users** with support for **CRUD, filtering, pagination, ownership assignment, bulk operations, validation, logging/observability, tests, and clean architecture**.

This project demonstrates **production-style system design**, good layering discipline, DTO mapping, domain validation, and extensibility patterns.

---

## ✨ Highlights

### 🚘 Vehicle Module
- Full **CRUD** operations
- **Dynamic filtering** via JPA Specifications  
  (`make`, `model`, `year`, `color`, `ownerFilter`)
- Owner assignment with validation
- Manufacture year rules (future not allowed)

### 👤 User Module
- Create / Fetch / List / Update endpoints
- Partial updates (`UserUpdateRequest`)
- Email + Birthdate validation logic
- Used by cars for owner linking

### 🧩 Platform & Architecture
- Multi-module project structure:
    - `commons`, `business-module`, `server-module`
- DTO ↔ Domain ↔ Entity ↔ Response mapping layers
- Global exception handler with structured error JSON
- Pagination response format via `PagedResponse<T>`
- Swagger UI auto-generated documentation

---

## 💡 Design Principles

| Principle | Approach |
|---|---|
| Separation of Concerns | API, service, persistence, and HTTP layers isolated |
| Validation at boundaries | Bean Validation + domain utilities |
| Extensibility | New resources/modules easily attachable |
| Maintainability | Domain structured modules + mappers |
| Clear fault surface | Global exception handler + custom errors |
| Production alignment | Logging, pagination, filtering patterns |

---

## 📈 Observability & Logging

- **Structured JSON logs (Logstash Encoder)**
- **Traceable requests via MDC correlation fields**
    - `requestId`, `method`, `path`, `status`, `durationMs`
- Ready for ingestion into **ELK / Loki / Datadog / Splunk**

---

## 📚 API Documentation

### Swagger UI
**📌 http://localhost:8080/swagger-ui.html**

Browse & execute requests directly:
- Schemas + DTOs
- Query params
- Live Try-It-Out mode

---

## 🧪 Testing

```bash
mvn test
```

### Coverage Areas

| Module | Includes                        |
|---|---------------------------------|
| commons | ValidationUtil, PageUtil        |
| cars-impl | PortalImpl, Mapper, Query Specs |
| users-impl | PortalImpl, UserValidationUtil  |
| server | Controllers (User/Car/Admin)    |

### Highlights

✔ Filtering Specifications coverage  
✔ Year validation enforcement  
✔ Bulk create → 201 or 207 Multi-Status  
✔ DTO ↔ Mapper correctness tests

---

## 🏁 Quick Start

### Requirements

- Java **21+**
- Maven CLI installed

### Run

```bash
./run-server
```

Server URL:

👉 `http://localhost:8080`

### H2 Console

```
http://localhost:8080/h2-console
JDBC: jdbc:h2:mem:testdb
user: sa
pass: sa
```

---

## 🔗 REST Endpoints

### Users

| Method | Path | Description |
|---|---|---|
| POST | `/api/v1/user` | Create user |
| GET | `/api/v1/user/{id}` | Get user by ID |
| GET | `/api/v1/user` | Paginated list |
| PUT | `/api/v1/user/{id}` | Update email or birthdate |

### Cars

| Method | Path | Description |
|---|---|---|
| GET | `/api/v1/cars` | Filter & paginate cars |
| GET | `/api/v1/cars/{id}` | Get car by ID |
| POST | `/api/v1/cars` | Create a car |
| PUT | `/api/v1/cars/{id}` | Update owner/make/model/year/color |
| DELETE | `/api/v1/cars/{id}` | Delete if no owner |

Filtering example:

```bash
GET /api/v1/cars?make=Toyota&year=2020&ownerFilter=NO_OWNER&page=0&size=10
```

### Bulk Admin

| Method | Path | Description |
|---|---|---|
| POST | `/api/v1/admin/addCars` | Bulk car creation |

Response example:

```json
{
  "successCount": 2,
  "failureCount": 1,
  "failures": [
    { "index": 2, "message": "Invalid make" }
  ]
}
```

---

## 🏗 Architecture Overview

```bash
backend
├── commons                # Shared utilities
│   ├── ValidationUtil
│   └── PageUtil
│
├── business-module
│   ├── cars
│   │   ├── cars-api       # DTOs & public API interfaces
│   │   └── cars-impl      # Services + Repository + Specs
│   │       ├── CarPortalImpl
│   │       ├── CarMapper
│   │       └── CarQuery
│   └── users
│       ├── users-api
│       └── users-impl
│           ├── UserPortalImpl
│           ├── UserMapper
│           └── UserValidationUtil
│
└── server-module          # HTTP layer
    ├── resource           # REST controllers
    ├── mapper             # REST ↔ Domain conversions
    ├── handler            # Error handling
    └── config             # Swagger, logging, beans
```

---

## 🔮 Future Enhancements

### Security
- JWT auth
- RBAC permissions

### Database
- PostgreSQL/MySQL support
- Flyway/Liquibase migrations

### Performance
- Redis/Caffeine caching
- Rate limiting middleware

### Observability
- Prometheus metrics
- OpenTelemetry tracing

### CI/CD
- GitHub Actions pipelines
- Testcontainers integration testing

### Deployment
- Docker multi-stage build
- docker-compose / Kubernetes manifests
- Health/liveness/readiness probes

---
