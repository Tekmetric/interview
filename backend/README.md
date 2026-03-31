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

# Solution Overview

### Java Version

The project is compiled with:

`java.version=1.8`

to align with the assignment requirements.

It can still be run locally using newer JDK versions (e.g., Java 17 or 21),
as long as Maven builds against the configured target version.

## Implementation Overview

This project implements a RESTful CRUD API for managing **Repair Orders**, a core concept in an auto repair shop workflow.

A Repair Order represents work performed on a vehicle and includes:
- Customer name
- Vehicle VIN (unique identifier)
- Description of the work to be performed
- Current status of the job
- Total cost
- Creation and update timestamps

The API supports full lifecycle operations:
- Create
- Read (single and list)
- Update
- Delete

## Architecture

The application follows a standard layered architecture.

### Responsibilities

#### Controller
- Defines REST endpoints
- Handles request/response mapping
- Delegates business logic to the service layer

#### Service
- Encapsulates business rules
- Handles validation logic beyond annotations
- Coordinates repository access
- Performs DTO-to-entity and entity-to-DTO mapping
  
#### Repository
- Extends `JpaRepository`
- Provides CRUD operations and custom existence checks
  
#### Entity / DTOs
- Entity represents the persistence model
- Request/Response DTOs decouple the API from the database schema
  
## Domain Choice

I chose **Repair Order** to align closely with Tekmetric's core product:

> Managing repair orders, technician workflows, and customer-facing service records.

### Design note

For the purpose of this single-entity exercise:
- `vehicleVin` is treated as **unique**
- This simplifies demonstrating conflict handling with `409 Conflict`

In a real system, a vehicle would have multiple repair orders over time, and uniqueness would likely be enforced via a separate identifier such as an order number.

## Key Features
- Full CRUD API
- Input validation using `javax.validation`
- Structured global error handling
- Duplicate VIN detection with proper HTTP `409 Conflict`
- Automatic timestamp management with `@PrePersist` and `@PreUpdate`
- Clean separation using DTOs
- In-memory H2 database with schema and seed data
- Multi-layer testing:
  - Controller
  - Service
  - Repository
  - Integration

## Database

### Configuration
- Engine: H2 (in-memory)
- URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

### Schema & Seed Data

Defined in:

```
src/main/resources/data.sql
```

Includes:
- Table creation for `repair_orders`
- Initial seed data for testing and demo purposes

## Running the Application

From the `backend` directory:

```bash
mvn spring-boot:run
```

Or:

```bash
mvn package
java -jar target/interview-1.0-SNAPSHOT.jar
```

## Access Points

### API Base URL
```
http://localhost:8080/api/repair-orders
```

### H2 Console
```
http://localhost:8080/h2-console
```

Use:
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## API Endpoints

| Method | Endpoint                         | Description                  |
|--------|----------------------------------|------------------------------|
| GET    | /api/repair-orders               | List all repair orders       |
| GET    | /api/repair-orders/{id}          | Get repair order by ID       |
| POST   | /api/repair-orders               | Create new repair order      |
| PUT    | /api/repair-orders/{id}          | Update existing repair order |
| DELETE | /api/repair-orders/{id}          | Delete repair order          |

## API Demo

A ready-to-use HTTP client file is included:

```
api-demo.http
```

Compatible with:
- IntelliJ HTTP Client
- VS Code REST Client extension

### Example Request

```http
POST http://localhost:8080/api/repair-orders
Content-Type: application/json

{
  "customerName": "John Smith",
  "vehicleVin": "5YJSA1E14HF123456",
  "description": "Oil change and tire rotation",
  "status": "IN_PROGRESS",
  "totalCost": 89.99
}
```

## Error Handling

The API returns structured JSON errors:

### Validation Error (400)

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": {
    "vehicleVin": "must be 17 characters",
    "customerName": "must not be blank"
  }
}
```

### Not Found (404)

```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Not Found",
  "message": "Repair order not found with id: 1"
}
```

### Conflict (409)

```json
{
  "timestamp": "...",
  "status": 409,
  "error": "Conflict",
  "message": "Repair order already exists for VIN: ..."
}
```

## Testing

The project includes multiple test layers.

### Controller Tests (`@WebMvcTest`)
- Verifies endpoint behavior
- Validates request input handling
- Ensures correct response structure
- Covers error scenarios (400, 404, 409)

### Service Tests
- Business logic validation
- Duplicate VIN handling
- Update and delete flows

### Repository Tests (`@DataJpaTest`)
Custom query methods:
- `existsByVehicleVin`
- `existsByVehicleVinAndIdNot`

### Integration Tests (`@SpringBootTest`)
- Full request → database flow
- Seed data verification
- CRUD lifecycle validation

### Run Tests
```bash
mvn test
```

## Design Decisions
- RepairOrder domain aligns with Tekmetric’s real-world use case
- DTO pattern keeps API decoupled from the persistence layer
- Global exception handler ensures consistent error responses
- Validation uses both annotations and service-level checks
- H2 database keeps setup simple and self-contained
- SQL initialization ensures a reproducible state

## Notes
- VIN uniqueness is used to demonstrate conflict handling in a simplified model
- In production, additional entities (Customer, Vehicle, LineItems) would likely be introduced
- The design prioritizes clarity, correctness, and maintainability
