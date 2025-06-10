# Owner/Car CRUD Application


This is a Spring boot service which allows management of owners and cars.

## Technologies Used
- Java 21
- Spring Boot 3.5 (with Virtual Threads)
- Maven enforcer plugin for maven [3.9.0,)
- Maven spotless plugin for code style
- Spring Boot validations and JSR 380 for bean validation
- Docker maven plugin for building docker images and Integration testing
- Spring Data JPA 
- Fuzzy search through JPA Specifications including on child elements
- Avoid N+1 problem through join fetch
- H2 in-memory database
- JWT for authentication
- Spring Security 6.x
- Swagger UI for API documentation
- JUnit 5 for unit testing and Spring Boot Tests
- Mockito for mocking in tests and MockMvc for testing controllers
- Flyway for database versioning and migrations
- K6 for load testing
- Grafana for load test results visualization
- Lombok & MapStruct for code generation
- JsonPath for testing JSON responses
- Micrometer for metrics and prometheus exporter
- HTTP2 for better performance(multiplexing and header compression)



## Swagger UI

Swagger UI is available at:
```
http://localhost:8080/swagger-ui/index.html
```

It supports also authentication in the UI.
The following credentials can be used to authenticate with a **WRITE** role:
```
{
  "username": "sorin",
  "password": "pass123"
}
````

![Swagger UI](images/swagger-ui-auth.png)


## Entity diagram and relationships

```mermaid
erDiagram
    OWNER {
        BIGINT id PK
        VARCHAR name
        VARCHAR personal_number "Unique"
        TIMESTAMP birth_date
        VARCHAR address
        BIGINT version
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    CAR {
        BIGINT id PK
        VARCHAR model
        VARCHAR vin "Unique"
        BIGINT owner_id FK
        BIGINT version
    }
    OWNER ||--o{ CAR : "owns"
```


## Github actions and apt
- Github actions are used for CI/CD.
- Usually the workflow is triggered on push to the main branch or on pull request.
- However, we can still run the workflow manually through **act** (brew install act) with the following command:
```
act
```

The command **needs to be issued in the top level directory**, next to the **.github** directory.

This will trigger the workflow and run all the steps defined in the **.github/workflows/ci.ym** file.

### Act running the github actions workflow
![Act running](images/act-running.png)


### Act completing the workflow
![Act Completed](images/act-completed.png)


## K6 load testing

- K6 is used for load testing the application.
You can simply run it with the following command:
```
docker-compose up --build
```

For example, we hit **15k requests per second** with 50 virtual users for 60 seconds with a **p95** of **7.72ms**

![K6 Running](images/k6-running.png)



## Grafana Dashboard
- The Grafana dashboard is available at:
```
http://localhost:3000/d/k6/k6-load-testing-results?orgId=1&refresh=5s
```

This is an example dashboard that shows the results of the load tests run by K6.

![Grafana Dashboard](images/grafana-dashboard.png)


## HTTP2

- The application is configured to use HTTP2 for better performance.
This can be tested with the following command:
```
curl -v --location 'http://localhost:8080/owners' \
--http2-prior-knowledge \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzb3JpbiIsImF1dGhvcml0aWVzIjpbIlJFQUQiLCJXUklURSJdfQ.0TBa_EilUxZFJeQ-m5XLhR7AefNNzexAXftc8B5kqAI' \
--data '{
    "name" : "Sorin second",
    "personalNumber" : "234242423232223",
    "birthDate" : "1990-01-01T00:00:00Z",
    "address" : "some street number 2"
}'
```

![Http2 working](images/http-2-working.png)


## Spotless and Code Style

Spotless is used to enforce a code style and format the code automatically.
It is configured as a check in the Maven build process to fail the build otherwise.

We can automatically apply the code style with the following command:
```
mvn spotless:apply
```

We can check apply the code style for violations with the following command:
```
mvn spotless:check
```

## Maven Docker plugin
- The Maven Docker plugin is used to build the Docker image for the application.
- It is also used to run the integration tests. Before the integration tests maven lifecycle phase, tests that are picked up by the **maven-failsafe-plugin** (ones that end in **IT**), it will build the Docker image, run the container, execute the tests against the dockerized application container(blackbox) and then stop the application container.

## Other considerations and improvements
- I would have chosen **gradle** as a build system as it offers more flexibility in terms of the the tasks that we can define and execute.
- Use of **Webflux for reactive programming** and a better performance and scalability.  
However this enforces the use of a reactive database like H2 R2DBC and also increases the overall complexity of the application with Flux/Mono and subscription management.

- Use of code generation tools like OpenAPI Generator to generate the API client and server stubs. This can help to ensure that the API is well-defined and can be easily consumed by clients. However, it also increases the complexity of the build process and requires additional configuration.

- Add a K8s deployment configuration to deploy the application through helm to a Kubernetes cluster. This can help to ensure that the application is scalable and can be easily managed in a production environment.
This should involve a replicaSet, HPA, and a service to expose the application.

- Security is a just a simple PoC with a JWT authentication with stored credentials in memory.
This should be replaced with a more robust solution like OAuth2 or OpenID Connect with a proper user management system.

- Considered the **maven jib plugin** for building and publishing docker images, but I wanted more control on the Dockerfile build and also wanted to have a multi stage build.

- Maybe more flexibility in terms of the JPA specification queries like passing a sort parameter and a column or using derived queries, projections, etc.