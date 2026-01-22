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

## Application Overview

This application implements a RESTful API for an automotive service domain, focusing on managing **Customers**, their **Vehicles**, and associated **Service Jobs**. It demonstrates a robust backend architecture using Spring Boot, JPA, and an in-memory H2 database.

Key features and architectural choices include:

*   **Domain-Driven Design:** Clear separation of concerns with dedicated entities (`Customer`, `Vehicle`, `ServiceJob`), DTOs (Data Transfer Objects), and service layers.
*   **RESTful API Design:** Adherence to REST principles with resource-oriented URLs (e.g., `/api/customers`), appropriate HTTP methods (GET, POST, PUT, PATCH, DELETE), and meaningful status codes.
*   **Data Persistence:** Utilizes Spring Data JPA for seamless interaction with the H2 database, including `@GeneratedValue(strategy = GenerationType.IDENTITY)` for automatic ID assignment and proper handling of one-to-many relationships.
*   **Relationship Management:** Enforces data integrity by validating the existence of parent entities when creating or updating child entities (e.g., a `ServiceJob` requires an existing `Vehicle`).
*   **Global Exception Handling:** Implements a `@ControllerAdvice` to centralize error handling, providing consistent and informative JSON error responses for exceptions like `ResourceNotFoundException`.
*   **Layered Architecture:** Clear division into controller, service, repository, and model layers to promote maintainability and testability.
*   **Comprehensive Unit Testing:** Extensive unit tests for service and mapper layers, and `MockMvc`-based tests for the web layer, ensuring high code quality and reliability.
*   **Development-Friendly Setup:** Uses an in-memory H2 database with pre-loaded sample data via `data.sql` for easy local development and testing, accessible via an H2 console.

---

## API Usage with cURL Examples

The application exposes a RESTful API on `http://localhost:8080`. Ensure the application is running (`mvn package && java -jar target/interview-1.0-SNAPSHOT.jar`) before trying these commands.

The H2 Console is available at `http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:testdb`, Username `sa`, and Password `password`.

---

**Sample cURLs:**

*(Note: IDs for existing resources are based on the `data.sql` script. For new resources, replace placeholders like `{NEW_CUSTOMER_ID}` with actual IDs returned from `POST` requests.)*

**A. Customer Endpoints (`/api/customers`)**

*   **Get all Customers:**
    ```bash
    curl -X GET http://localhost:8080/api/customers
    ```
*   **Get Customer by ID (e.g., ID 1):**
    ```bash
    curl -X GET http://localhost:8080/api/customers/1
    ```
*   **Create a new Customer:**
    ```bash
    curl -X POST http://localhost:8080/api/customers \
         -H "Content-Type: application/json" \
         -d \
             '{
               "firstName": "Alice",
               "lastName": "Johnson",
               "email": "alice.j@example.com"
             }'
    ```
*   **Update an existing Customer (e.g., ID 1):**
    ```bash
    curl -X PUT http://localhost:8080/api/customers/1 \
         -H "Content-Type: application/json" \
         -d \
             '{
               "id": 1,
               "firstName": "John",
               "lastName": "D. Doe",
               "email": "john.doe@example.com"
             }'
    ```
*   **Partially update a Customer (e.g., ID 1):**
    ```bash
    curl -X PATCH http://localhost:8080/api/customers/1 \
         -H "Content-Type: application/json" \
         -d \
             '{
               "lastName": "Smith-Doe"
             }'
    ```
*   **Delete a Customer (e.g., ID 1):**
    ```bash
    curl -X DELETE http://localhost:8080/api/customers/1
    ```
*   **Test Non-Existent Customer (Expect 404):**
    ```bash
    curl -X GET http://localhost:8080/api/customers/999
    ```

**B. Vehicle Endpoints (`/api/vehicles`)**

*   **Get all Vehicles:**
    ```bash
    curl -X GET http://localhost:8080/api/vehicles
    ```
*   **Get Vehicle by ID (e.g., ID 1):**
    ```bash
    curl -X GET http://localhost:8080/api/vehicles/1
    ```
*   **Create a new Vehicle (for Customer ID 1):**
    ```bash
    curl -X POST http://localhost:8080/api/vehicles \
         -H "Content-Type: application/json" \
         -d \
             '{
               "vin": "VINXYZ789",
               "make": "Nissan",
               "model": "Altima",
               "modelYear": 2023,
               "customerId": 1
             }'
    ```
*   **Test Create Vehicle with Non-Existent Customer (Expect 404):**
    ```bash
    curl -X POST http://localhost:8080/api/vehicles \
         -H "Content-Type: application/json" \
         -d \
             '{
               "vin": "VINBADID123",
               "make": "Chevy",
               "model": "Malibu",
               "modelYear": 2020,
               "customerId": 999
             }'
    ```

**C. Service Job Endpoints (`/api/service-jobs`)**

*   **Get all Service Jobs:**
    ```bash
    curl -X GET http://localhost:8080/api/service-jobs
    ```
*   **Get Service Job by ID (e.g., ID 1):**
    ```bash
    curl -X GET http://localhost:8080/api/service-jobs/1
    ```
*   **Create a new Service Job (for Vehicle ID 1):**
    ```bash
    curl -X POST http://localhost:8080/api/service-jobs \
         -H "Content-Type: application/json" \
         -d \
             '{
               "description": "Brake pad replacement",
               "creationDate": "2024-01-22T10:00:00Z",
               "status": "PENDING",
               "cost": 350.75,
               "vehicleId": 1
             }'
    ```
*   **Test Create Service Job with Non-Existent Vehicle (Expect 404):**
    ```bash
    curl -X POST http://localhost:8080/api/service-jobs \
         -H "Content-Type: application/json" \
         -d \
             '{
               "description": "Oil Change",
               "creationDate": "2024-01-22T11:00:00Z",
               "status": "IN_PROGRESS",
               "cost": 120.00,
               "vehicleId": 999
             }'
    ```