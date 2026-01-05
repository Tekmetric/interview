# 🛠️ Repair Job Service — Spring Boot REST API

A Spring Boot REST API for managing **automotive repair jobs**, featuring:

✔ CRUD operations  
✔ Filtering + Pagination  
✔ Global exception handling  
✔ H2 in-memory DB  
✔ Request validation  
✔ OpenAPI / Swagger UI  
✔ JPA auditing (`created`, `lastModified`)  
✔ Integration & service tests

# Prerequisites
- Maven
- Java 21
---

## 🚀 Tech Stack

- Java 21+
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- H2 Database
- Springdoc OpenAPI
- Jakarta Bean Validation
- JPA Auditing
- JUnit + MockMvc

---

## 📁 Project Structure

```
src/main/java/com/interview
 ├── model
 ├── resource
 ├── service
 ├── repository
 ├── specification
 ├── exception
 ├── response
 ├── config
```

---
## 📦 Domain Model

### `RepairJob`

| Field | Type | Required | Notes |
|------|------|---------|------|
| id | Long | — | Auto-generated, read-only |
| name | String | ✔ | Job title |
| userId | String | ✔ | Owner / customer id |
| repairDescription | String | ✔ | Work description |
| licensePlate | String | ✔ | Plate (7 chars) |
| make | String | ✔ | Vehicle make |
| model | String | ✔ | Vehicle model |
| status | Enum | — | CREATED / IN_PROGRESS / COMPLETED / CANCELLED |
| created | LocalDateTime | auto | Set on insert |
| lastModified | LocalDateTime | auto | Updated on save |

📌 `id`, `created`, and `lastModified` are **read-only** in JSON input.

---

## ⚙️ Running the Application

### Run
```bash
mvn package && java -jar target/interview-1.0-SNAPSHOT.jar
```

---

## 🧪 Testing

Run tests locally:

```bash
mvn test
```
or try the included postman tests


## 📄 API Documentation (Swagger)

👉 http://localhost:8080/swagger-ui/index.html

---

## 🗄 H2 Database Console

👉 http://localhost:8080/h2-console

```
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: password
```

---

## ⏱ JPA Auditing

Automatically sets:

| Field | Behavior |
|-------|---------|
| created | Set once on insert |
| lastModified | Updated every save |

---

## 🔁 REST Endpoints

Base path:

```
/api/repair-jobs
```

---

## 🔍 Search / Filter (with Pagination)

`GET /api/repair-jobs`

### Example Request
```bash
curl -X GET "http://localhost:8080/api/repair-jobs?status=CREATED&page=0&size=2&sort=created,desc"
```

### Example Response
```json
{
  "content": [
    {
      "id": 3,
      "name": "Oil Change",
      "userId": "user-123",
      "repairDescription": "Replace filter + oil",
      "licensePlate": "XYZ9876",
      "make": "Honda",
      "model": "Civic",
      "created": "2026-01-04T10:11:22.123",
      "lastModified": "2026-01-04T10:11:22.123",
      "status": "CREATED"
    }
  ],
  "pageable": { "... trimmed ..." },
  "totalElements": 1,
  "totalPages": 1
}
```

---

## 📍 Get Job by ID

`GET /api/repair-jobs/{id}`

### Example Request
```bash
curl -X GET http://localhost:8080/api/repair-jobs/1
```

### Example Success Response (`200 OK`)
```json
{
  "id": 1,
  "name": "Brake Pad Replacement",
  "userId": "19dfa1e6-6970-45fc-bf62-1a5e50a16888",
  "repairDescription": "Front brakes worn",
  "licensePlate": "ABC1234",
  "make": "Toyota",
  "model": "Camry",
  "created": "2026-01-01T09:00:00",
  "lastModified": "2026-01-01T09:00:00",
  "status": "CREATED"
}
```

### Example Not Found Response (`404`)
```bash
curl -X GET http://localhost:8080/api/repair-jobs/999
```

```json
{
  "timestamp": "2026-01-04T13:22:11.345",
  "status": 404,
  "error": "Not Found",
  "message": "Repair job not found with id 999",
  "path": "/api/repair-jobs/999"
}
```

---

## ➕ Create Repair Job

`POST /api/repair-jobs`

### Example Request
```bash
curl -X POST http://localhost:8080/api/repair-jobs   -H "Content-Type: application/json"   -d '{
    "name": "Brake Inspection",
    "userId": "user-123",
    "repairDescription": "Front brakes grinding",
    "licensePlate": "ABC1234",
    "make": "Toyota",
    "model": "Camry",
    "status": "CREATED"
  }'
```

### Example Response (`201 Created`)
```json
{
  "id": 5,
  "name": "Brake Inspection",
  "userId": "user-123",
  "repairDescription": "Front brakes grinding",
  "licensePlate": "ABC1234",
  "make": "Toyota",
  "model": "Camry",
  "created": "2026-01-04T14:55:21.820",
  "lastModified": "2026-01-04T14:55:21.820",
  "status": "CREATED"
}
```

---

## ✏️ Update Repair Job

`PUT /api/repair-jobs/{id}`

### Example Request
```bash
curl -X PUT http://localhost:8080/api/repair-jobs/1   -H "Content-Type: application/json"   -d '{
    "name": "Brake Inspection Updated",
    "userId": "user-123",
    "repairDescription": "Replace pads + rotors",
    "licensePlate": "ABC1234",
    "make": "Toyota",
    "model": "Camry",
    "status": "IN_PROGRESS"
  }'
```

### Example Response (`200 OK`)
```json
{
  "id": 1,
  "name": "Brake Inspection Updated",
  "userId": "user-123",
  "repairDescription": "Replace pads + rotors",
  "licensePlate": "ABC1234",
  "make": "Toyota",
  "model": "Camry",
  "created": "2026-01-01T09:00:00",
  "lastModified": "2026-01-04T15:01:10.455",
  "status": "IN_PROGRESS"
}
```

---

## ❌ Delete Repair Job

`DELETE /api/repair-jobs/{id}`

### Example Request
```bash
curl -X DELETE http://localhost:8080/api/repair-jobs/1
```

### Example Response
```
204 No Content
```

---

## 🛡 Error Handling

All errors are handled centrally in `GlobalExceptionHandler` so the API always returns a **consistent JSON error format**:

### 📌 Error Response Format

Every error follows this structure:

| Field | Description |
|------|-------------|
| `timestamp` | Time the error occurred |
| `status` | HTTP status code |
| `error` | Status text |
| `message` | Human-readable description |
| `path` | The requested URI |

```json
{
  "timestamp": "2026-01-04T13:22:11.345",
  "status": 404,
  "error": "Not Found",
  "message": "Repair job not found with id 99",
  "path": "/api/repair-jobs/99"
}
```

The API currently supports **three structured error types**:

---

### 🔴 404 — Not Found (`RepairJobNotFoundException`)

Returned when a repair job does not exist.

#### Example Request
```bash
curl -X PUT http://localhost:8080/api/repair-jobs/999
```

#### Example Response
```json
{
  "timestamp": "2026-01-04T13:22:11.345",
  "status": 404,
  "error": "Not Found",
  "message": "Repair job not found with id 999",
  "path": "/api/repair-jobs/999"
}
```

---

### 🟡 400 — Bad Request (`HttpMessageNotReadableException`)

Returned when the **request body is invalid JSON** or contains **invalid enum values**
(for example, sending `status = "DONE"` which is not valid).

#### Example Request
```bash
curl -X POST http://localhost:8080/api/repair-jobs   -H "Content-Type: application/json"   -d '{
    "name": "Test",
    "userId": "user-123",
    "repairDescription": "desc",
    "licensePlate": "ABC1234",
    "make": "Toyota",
    "model": "Camry",
    "status": "DONE"
  }'
```

#### Example Response
```json
{
  "timestamp": "2026-01-04T13:40:22.551",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid request body — check field formats and enum values",
  "path": "/api/repair-jobs"
}
```

---

### ⚫ 500 — Internal Server Error (`Exception`)

Returned for **unexpected or unhandled errors**.

#### Example Response
```json
{
  "timestamp": "2026-01-04T13:45:10.110",
  "status": 500,
  "error": "Internal Server Error",
  "message": "No static resource jobs/1",
  "path": "/api/repair-jobs"
}
```
