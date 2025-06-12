# tekmetric-backend-interview

The project is a SpringBoot v3.5.0 application that serves as a very basic backend for a car service management system. 
It allows users to manage customer, vehicle and repair order records, including creating, updating, and deleting them via REST API endpoints.

## Prerequisites

- Java 21 or higher
- Maven 3.9 or higher

## Building the Project

### Source Code

To build the project source code, run the following command in the backend directory:

```bash
./mvnw clean install
```

### Docker Image

To build the Docker image, run the following command in the backend directory:

```bash
docker build -t tekmetric-backend-interview:latest .
```

## Running the Project

### 

To run the project, execute the following command in the backend directory:

```bash
DB_PASSWORD=password API_PASSWORD=password ./mvnw spring-boot:run 
# Or if you want to use the dev profile to enable H2 Console and Swagger UI and set the default database and API user passwords:
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

### Docker Container

To run the project in a Docker container, execute the following command in the backend directory:

```bash
docker run -p 8080:8080 -e DB_PASSWORD=password -e API_PASSWORD=password tekmetric-backend-interview:latest
# Or if you want to use the dev profile to enable H2 Console and Swagger UI and set the default database and API user passwords:
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev tekmetric-backend-interview:latest
```

### Local Development Tools

* Swagger URI is available at: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* H2 Console is available at: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
* OpenAPI Specification is available at: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> Note: The H2 Console and the Swagger URI is **only** available in the "dev" profile or when running the project with the correct environment variables. Username is `sa` and the password is set via environment variables (default is `password` when "dev" profile is active).

## Running Tests

To run the project, execute the following command in the backend directory:

```bash
./mvnw clean verify
```

## Environment Variables

- `API_USERNAME`: The username for the default user. Default is `sa`.
- `API_PASSWORD`: The password for the default user. Default is `password`.
  This is required to run the project and automatically set as default value when using the dev profile.
- `DB_USERNAME`: The username for the H2 database. Default is `sa`.
- `DB_PASSWORD`: The password for the H2 database. Default is `password`. 
  This is required to run the project and automatically set as default value when using the dev profile.
- `SPRING_PROFILES_ACTIVE`: The active Spring profile. Default profile is configured for prod. 
  Set to `dev` to enable H2 Console and Swagger UI.
- `H2_CONSOLE_ENABLED`: Set to `true` to enable the H2 Console. Default is `false`. 
  This is automatically enabled when the `dev` profile is active.
- `SWAGGER_ENABLED`: Set to `true` to enable Swagger UI. Default is `false`. 
  This is automatically enabled when the `dev` profile is active.
- `SERVER_PORT`: The port on which the application will run. Default is `8080`. 
  This can be set to any available port.
- `LOG_BEAN_NAMES`: Set to `true` to log bean names at startup. Default is `false`. 
  This can be useful for debugging purposes.
- `LOG_REQUESTS_ENABLED`: Set to `true` to enable logging of HTTP requests. Default is `false`. 
  This can be useful for debugging purposes.
- `LOG_REQUESTS_INCLUDE_HEADERS`: Set to `true` to include headers in the request logs. Default is `false`. 
  This can be useful for debugging purposes, but may expose sensitive information, so use with caution!

## Health Check & Info Endpoints

* **/actuator/info**: Info about service version and git commit id.
* **/actuator/health/liveness**: Endpoint for liveness probe (can be used by k8s).
* **/actuator/health/readiness**: Endpoint for readiness probe (can be used by k8s and docker-compose).
* **/actuator/metrics**: Lists all metric names.
* **/actuator/metrics/{metricName}**: Shows specified metric (can be used by prometheus).

## Possible Future Improvements

In order to keep the interview demo project simple and constrained to a specific timeline, 
some features have been intentionally left out or simplified and could be improved if the project were to be developed for a real-world application:

- To ensure discoverability in the REST API, HATEOAS support should be considered.
- To prevent "lost updates" in the database, the persistence layer should use versioning (i.e. version column) if necessary.
- Migration to a more robust database (e.g. PostgreSQL, MySQL) should be considered.
- After a database migration, the project may use Testcontainers to run integration tests against the database.
- To further optimize the Docker image size, the image might be built using native compilation or carefully picking modules/JRE via JLink.
- Logging should be further improved to output structured logs in JSON format, which is more suitable for log aggregation and analysis (e.g. ELK or Grafana stacks)
- REST API should be secured using OAuth2 or JWT authentication instead of basic authentication.
- REST API versioning strategy should be reconsidered as URL-based versioning can become cumbersome in the long run.
