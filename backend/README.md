# Tekmetric Backend API Exercise

This project is a **Spring Boot CRUD API** for managing `Project` entities. It demonstrates modern backend best practices including auditing, security, rate-limiting, and observability.

---

## Features

### ✅ Covered

- **AOP Logging**: Tracks execution time and logs input/output parameters.
- **Error Handling**: Centralized exception handling for REST endpoints.
- **Rate Limiting**: Limits the number of requests per user/time interval.
- **Auditing**: Tracks created/modified metadata (user and timestamps) using Spring Data JPA.
- **Integration Tests**: End-to-end tests with an in-memory H2 database.
- **Database Migration**: Flyway-based migrations for schema management.
- **Security**: HTTP Basic authentication for secured endpoints.
- **Actuator**: Provides operational endpoints for monitoring.
- **OpenAPI & Swagger**: API documentation.
- **Docker Integration**: Build and run the service in a containerized environment.

### ❌ Not Covered

- Advanced API filtering and sorting.
- More complex data models with multiple relations.

---

## API Documentation

- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **Actuator**: [http://localhost:8080/actuator](http://localhost:8080/actuator)

---

## Getting Started

### Prerequisites

- Java 25
- Docker (optional, for containerized runs)

### Run Development Server

```bash
./mvnw clean spring-boot:run
```
The API will be available at: http://localhost:8080

### Run Integration Tests

```bash
./mvnw clean test
```

### Build Runnable Fat JAR

```bash
./mvnw clean package
```
The JAR will be created in target/ as tekmetric-backend-1.0-SNAPSHOT.jar.

## Docker Integration

### Build Docker image:

```bash
docker build -t tekmetric-backend:latest .
```

### Run Docker container:

```bash
docker run --name tekmetric -p 8080:8080 tekmetric-backend
```

## Demo Usage

A sample script `api-client.sh` is provided to demonstrate usage of the API. It covers the full CRUD workflow.
```bash
./api-client.sh
```
The script demonstrates:
 - Creating a project
 - Retrieving all projects and a single project
 - Updating a project
 - Deleting a project

Make sure the server is running before executing the script.

## Notes
 - The H2 in-memory database is used for testing and development purposes.
 - All endpoints require HTTP Basic authentication, except explicitly permitted ones (like /api/welcome).
 - CRUD operations for Project include create, read (single + list), update, and delete.
 - The project includes logging, auditing, rate-limiting, security, OpenAPI/Swagger docs, Actuator, and Docker support.
