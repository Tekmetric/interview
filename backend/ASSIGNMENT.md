
---

### Feature

The implementation presents a `Vehicle` management API.
The `Vehicle` contains a minimal number of fields like: brand, model, color ...

The API contains endpoints for the CRUD operations (create, read, update, delete),
and also a `search` endpoint.

---

### Demo

You can use the OpenAPI specification to import the API as a collection
directly in Postman or any other API client, using the URL:
http://localhost:8080/docs/api

You can also use Swagger to discover and play around with the API:
http://localhost:8080/swagger-ui/index.html

---

### Implementation

The current implementation leverages and contains the following concepts:

- Basic API for CRUD operations
- Pagination and sorting
- Filtering
- Security
- Java 21
- Spring Boot 3.5.5
- Unit and integration tests
- Logging filter
- Request validation
- OpenAPI

---

### Future upgrades and improvements

- Adding OpenTelemetry
- Dockerization
- Using Testcontainers
- Redis
- Implementing audit
- Using OAuth2 server for obtaining tokens
- Detailed API docs

---
