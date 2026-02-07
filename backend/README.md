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

---

## Vehicle CRUD API — Implementation

### Design Decisions
- **Vehicle entity**: Domain relevant to Tekmetric's auto repair platform
- **DTO pattern**: Separated API contract (request/response) from the JPA entity
- **Record for Response**: `VehicleResponse` and `VehiclePageResponse` use Java 17 Records for immutable output data
- **Constructor injection**: Over field injection, following Spring best practices
- **Global exception handler**: Consistent error responses with proper HTTP status codes. Validation handler safely distinguishes between field-level and object-level errors to prevent ClassCastException.
- **Service interface**: Enables easy testing and follows Dependency Inversion Principle
- **Pagination**: A busy auto repair shop can accumulate thousands of vehicles over time. Returning all records in a single response would degrade performance and waste bandwidth. The paginated endpoint supports page, size, and sorting parameters, giving API consumers full control over data retrieval.
- **Input validation**: Sort field whitelist on the paginated endpoint prevents invalid field errors. Unrecognized fields fallback to `id` instead of returning a 500.
- **SLF4J Logging**: Added structured logging at the service layer using `info` for normal operations and `warn` for not-found scenarios. Uses `{}` placeholders for performance over string concatenation. In a production environment, these logs enable monitoring, debugging, and auditing of API activity.

### Changes from Original Project
- Upgraded Spring Boot from 2.2.1 to 2.7.18 to support Java 17 (as suggested in the README)
- Added `spring-boot-starter-validation` and `spring-boot-starter-test` dependencies

### API Endpoints

| Method | Endpoint                 | Description              | Status Codes     |
|--------|--------------------------|--------------------------|------------------|
| GET    | /api/vehicles            | List all vehicles        | 200              |
| GET    | /api/vehicles/paged      | List vehicles (paginated)| 200              |
| GET    | /api/vehicles/{id}       | Get vehicle by ID        | 200, 404         |
| POST   | /api/vehicles            | Create new vehicle       | 201, 400         |
| PUT    | /api/vehicles/{id}       | Update vehicle           | 200, 400, 404    |
| DELETE | /api/vehicles/{id}       | Delete vehicle           | 204, 404         |

#### Pagination Parameters

| Parameter | Default | Description            |
|-----------|---------|------------------------|
| page      | 0       | Page number (0-indexed) |
| size      | 10      | Items per page          |
| sortBy    | id      | Field to sort by        |

### How to Run
```bash
mvn clean package && java -jar target/interview-1.0-SNAPSHOT.jar
```

### Demo — cURL Examples

**List all vehicles:**
```bash
curl -s -X GET http://localhost:8080/api/vehicles | python3 -m json.tool
```

**List vehicles with pagination (page 0, 2 per page):**
```bash
curl -s -X GET "http://localhost:8080/api/vehicles/paged?page=0&size=2" | python3 -m json.tool
```

**List vehicles with pagination (page 1, 2 per page):**
```bash
curl -s -X GET "http://localhost:8080/api/vehicles/paged?page=1&size=2" | python3 -m json.tool
```

**List vehicles sorted by make:**
```bash
curl -s -X GET "http://localhost:8080/api/vehicles/paged?page=0&size=10&sortBy=make" | python3 -m json.tool
```

**Get single vehicle:**
```bash
curl -s -X GET http://localhost:8080/api/vehicles/1 | python3 -m json.tool
```

**Create a new vehicle:**
```bash
curl -s -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "make": "Tesla",
    "model": "Model 3",
    "year": 2024,
    "vin": "5YJ3E1EA5LF123456",
    "color": "White",
    "mileage": 5000,
    "ownerName": "Alice Johnson",
    "ownerPhone": "555-0999"
  }' | python3 -m json.tool
```

**Update a vehicle:**
```bash
curl -s -X PUT http://localhost:8080/api/vehicles/1 \
  -H "Content-Type: application/json" \
  -d '{
    "make": "Toyota",
    "model": "Camry",
    "year": 2021,
    "color": "Red",
    "mileage": 45000,
    "ownerName": "John Smith",
    "ownerPhone": "555-0101"
  }' | python3 -m json.tool
```

**Delete a vehicle:**
```bash
curl -s -X DELETE http://localhost:8080/api/vehicles/5 -w "\nHTTP Status: %{http_code}\n"
```

**Error handling — not found:**
```bash
curl -s -X GET http://localhost:8080/api/vehicles/999 | python3 -m json.tool
```

**Error handling — validation:**
```bash
curl -s -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{"color": "Blue"}' | python3 -m json.tool
```

### Running Tests
```bash
mvn test
```

- **9 unit tests** — Service layer with Mockito
- **10 integration tests** — Full HTTP cycle with MockMvc

### H2 Console

Available at http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`