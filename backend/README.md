# Java Spring Boot Vehicles API

Spring Boot application that provides a RESTful API for managing vehicle data.

#### Features

- CRUD operations for vehicle data
- Swagger UI for API documentation and testing
- Pagination and sorting support
- Extensive search and filtering capabilities
- Caching for improved performance
- Global exception handling
- Bean validation for request payloads
- In-memory H2 database for data storage
- Unit and integration tests

#### Considerations for Production

- Use a persistent database (e.g. PostgreSQL, MySQL) instead of H2
- Use a data migration tool (e.g. Liquibase, Flyway)
- Implement authentication and authorization (e.g. OAuth2, JWT)
- Use a distributed caching solution (e.g. Redis, Memcached)
- Use a CI/CD pipeline for automated testing and deployment
- Containerize the application using Docker and orchestrate with ECS\ Kubernetes
- Implement logging and monitoring (e.g. Prometheus, Grafana)
- Integrate spring boot config with secret manager for managing sensitive configurations

#### Prerequisites

- Java 17+

#### Build and run your app

- `./gradlew bootRun`

#### Run tests

- `./gradlew test`

#### Test that your app is running

- `curl -X GET   http://localhost:8080/actuator/health`

#### API Endpoints

Use Swagger UI to explore and test the API endpoints:

- Swagger UI: http://localhost:8080/swagger-ui/index.html

#### Generate Vehicle Data

Prerequisites:

- Python 3.x

To generate vehicle data you can run the following script in your terminal:

```bash
python3 ./scripts/generate_vehicle_data.py
```

Optional argument: `--num <NUMBER_OF_VEHICLES>` (default is 100)

This will create a data.sql file local to the current working directory.
Move the data.sql file to `backend/src/main/resources` and Spring Boot will automatically load this
data into the H2 in-memory database when the application starts.

#### H2 Configuration

- Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: sa

### Submitting your coding exercise

Once you have finished the coding exercise please create a PR into Tekmetric/interview
