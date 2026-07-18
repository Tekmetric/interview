# Employee Goal Tracker API

The **Employee Goal Tracker API** is a Spring Boot service that allows organizations to manage employees and their associated goals. It exposes a RESTful API defined by an OpenAPI 3.0.3 specification and supports full CRUD operations on both employees and goals. The project emphasizes clean validation, auto-generated API docs, repository-based persistence, and production-style testing and quality gates.

---

## Features

- **Employees**
  - Create, read, update, and delete employees  
  - Includes fields for name, role, department, and email  
  - Automatic timestamps (`createdAt`, `updatedAt`)

- **Goals**
  - Create, read, update, and delete goals scoped to an employee  
  - Track name, description, status, and due dates  
  - Automatic timestamps

- **Pagination**
  - Paginated listing of employees with their goals

- **Error Handling**
  - Consistent structured JSON error responses  
  - Validation errors include field-level messages  

- **OpenAPI Specification**
  - Complete OpenAPI definition  
  - Auto-generated API interfaces 
  - Swagger UI for live testing

---

## Technologies Used

### Spring Boot
- REST controllers (`EmployeeController`, `GoalController`)
- Service + Repository pattern  
- Validation via `javax.validation`  
- Global exception handler for error normalization

### OpenAPI
- OpenAPI YAML for auto-generated interfaces  
- Controllers stay in sync with spec  
- Strongly typed request/response models

### Data Persistence
- Spring Data JPA  
- H2 for local development  
- UUID ID generation  
- Automatic timestamps

### Lombok
- Reduces boilerplate (`@Getter`, `@Setter`, `@Builder`, etc.)

### Validation
- Length, email, enum, and required-field validation
- Structured `ErrorResponse` and `ValidationError`

### Jacoco Test Coverage
- Coverage reports generated during Maven build  
- Verification rules enforced in `verify` phase  
- Exclusions configured for models/config

### Swagger Documentation
- Interactive documentation for all endpoints  
- Mirrors OpenAPI spec

---

## How to Run

```
mvn verify && java -jar target/interview-1.0-SNAPSHOT.jar
```

---

## Accessing API Documentation

Swagger UI is available at:

```
http://localhost:8080/swagger
```

OpenAPI JSON documentation is available at:

```
http://localhost:8080/api-docs
```

Postman collection for easy import is available in repo at:

```
.../backend/tekmetric_employee_goal_tracker_api.postman_collection.json
```

---

## Summary of Changes Made

### API Design

* Implemented controllers that follow the OpenAPI spec
* Updated request/response models
* Added light validation (length, email, enums)

### Persistence

* Added timestamp fields
* Ensured schema and entities align
* Ensured referential integrity between Employee and Goal objects

### Error Handling

* Unified error response format
* Detailed validation error messaging

### Jacoco Integration

* Added reporting + verification
* Ensured exclusions apply during `verify` phase

### Documentation Enhancements

* Full OpenAPI spec
* Swagger UI enabled

---

## Future Improvements

### Platform

* Update Java version

### Authentication & Authorization

* JWT-based auth
* Role-based access control

### Reactive / Async Support

* Replace MVC with WebFlux
* Better scalability under load

### Metrics & Observability

* Structured app events for business visibility
* Add monitoring through observability platform like Datadog

### Testing Improvements

* Integration tests
* Full end-to-end API suite

### Database + Migrations

* Use persistent DB like MySQL or Postgres instead of H2

### Deployment

* Containerization
* CI/CD pipeline
* Cloud deployment options

---

## OpenAPI Specification

The repo includes the full OpenAPI specification that defines:

* Endpoints
* Request bodies
* Response schemas
* Error responses
* Pagination formats
* Validation rules

Controllers implement the generated interfaces to ensure strict alignment with the spec.

## Original README below

---

# Java Spring Boot API Coding Exercise

## Steps to get started:

#### Prerequisites
- Maven
- Java 1.8 (or higher, update version in pom.xml if needed)

#### Fork the repository and clone it locally
- https://github.com/Tekmetric/interview.git

#### Import project into IDE
- Project root is located in `backend` folder

#### Build and run your app
- `mvn package && java -jar target/interview-1.0-SNAPSHOT.jar`

#### Test that your app is running
- `curl -X GET   http://localhost:8080/api/welcome`

#### After finishing the goals listed below create a PR

### Goals
1. Design a CRUD API with data store using Spring Boot and in memory H2 database (pre-configured, see below)
2. API should include one object with create, read, update, and delete operations. Read should include fetching a single item and list of items.
3. Provide SQL create scripts for your object(s) in resources/data.sql
4. Demo API functionality using API client tool

### Considerations
This is an open ended exercise for you to showcase what you know! We encourage you to think about best practices for structuring your code and handling different scenarios. Feel free to include additional improvements that you believe are important.

#### H2 Configuration
- Console: http://localhost:8080/h2-console 
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

### Submitting your coding exercise
Once you have finished the coding exercise please create a PR into Tekmetric/interview
