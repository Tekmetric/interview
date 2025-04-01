# Java Spring Boot API Coding Exercise


### Implementation Notes
#### Application Architecture
This application follows the Standard 3-Layer Architecture in Spring Boot:

Controller Layer: Handles incoming HTTP requests and returns responses. It maps HTTP requests to service methods.

Service Layer: Contains the business logic. It is responsible for processing the data and interacting with the repository layer.

Repository/Data Layer: Manages database operations using Spring Data JPA to persist and retrieve entities.

#### Key Features:
DTOs for Separation of Concerns: We use DTOs (Data Transfer Objects) to decouple the domain model (entities) from the API layer. This allows both the internal data model and the external-facing API to evolve independently without tight coupling.

Database Initialization Script: A script is provided to initialize the database with predefined data and enforce data integrity by setting uniqueness constraints on keys to maintain consistent data.

Unit and Integration Testing:

Unit Tests are written for the service layer, using Mockito for mocking database interactions, ensuring business logic works as expected.

Integration Tests verify the correctness of API endpoints by testing the controller and service layers in conjunction.

OpenAPI Documentation (Swagger): The API is documented using Swagger for easy visualization of available endpoints, parameters, and responses. This makes the API easier to consume and maintain.
Available at http://localhost:8080/swagger-ui/index.html

Sorted & Paginated Endpoint: A sorted and paginated endpoint is provided for entity retrieval, allowing clients to query large datasets efficiently and with flexibility in sorting.
GET customers/

Actuator for Health & Metrics: The Spring Boot Actuator is included for monitoring the application's health and providing essential metrics. This ensures the application is running smoothly and provides insights into its performance.
GET localhost:8080/actuator/metrics

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
