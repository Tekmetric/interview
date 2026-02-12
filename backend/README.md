# Java Spring Boot API Coding Exercise

## Steps to get started:

#### Prerequisites
- Maven
- Java 25

#### Build and run the app (choose and method)
- `mvn package && java -jar target/interview-1.0-SNAPSHOT.jar`
- `mvn spring-boot:run`

#### Test that the app is running
- `curl -X GET   http://localhost:8080/listing`
- Run any example request in the [provided script](testRequests.http)

## Overview
This microservice produces a CRUD rest API for managing real estate listings and the offers submitted on them. Listings
include data such as address, realtor, listing price, etc. and offers include the buyer's suggested price, financing options
etc. One listing may have 0 to many offers and a single offer is only ever associated with one and only one listing.

### Tech stack/Tools
- Maven
- Java 25
- Spring Boot 3.5.7
  - Web
  - Data
  - Validation
- SpringDoc - Automated generation of OpenAPI spec
- Bucket4J - Rate limiting configuration
- H2
- Lombok - Easy generation of POJO boilerplate
- MapStruct - Generates mappers between POJO <--> Entities
- Instancio - Generates prepopulated POJOs for test data

### API Documentation
- Swagger UI - http://localhost:8080/swagger-ui.html
- OpenAPI Spec - http://localhost:8080/v3/api-docs

### DB Config
- Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

## Features

### RESTful API
This app supports the full suite of REST methods to manage its domain entities

- GET - List/fetch entity
- POST - Create entity and rely on the DB to auto generate ids
- PUT - Replace entire existing entity by id
- PATCH - Partially update exiting entity by id
- DELETE - Remove entity (and child entities if applicable)

All methods except POST are idempotent so subsequent identical calls will not have unknow consequences.
POST is intentionally the exception so multiple POST requests will create multiple identical entities however with
different ids.

### Rate Limiting
To protect the service from malicious or abusive behavior, incoming requests will be throttled. To help the user(s)
understand how much of the rate has been consumed, headers are supplied in all requests so consumers and self regulate
their requests.

### Global Error Handling
To prevent errors from bubbling up and being served back to consumers, a global error handler maps expected exceptions 
to user-friendly error messages so internal information is not inadvertently leaked. In the case of an unexpected exception
is thrown, that too will be handled by default.

### DTO/Entity Separation
To allow greater flexibility between what data consumers need and how its store, DB domain entities are not returned
directly to consumers. Similarly with requests, the internals of our DB schema are not known to the consumer. All DTOs
and entities are mapped and converted separating their concerns and enforcing what data is required or presented.

### Data Validation
Spring validation allows easy validation of incoming DTOs, cutting down the boilerplate of enforcing data integrity and 
validation. These annotations are also picked up by SpringDoc updating the API documentation automatically.

### Layered Architecture
Even in a simple app, it's important to separate concerns into layers. Each layer interacts with another through its
published interfaces and abstracts away their implementation details as well as other lower layers.

API <---> Service <---> Repository

### Automated Tests
Business logic is covered by unit tests as well as integration tests. Unit test focus on testing individual classes
with mocked dependencies while integration tests focus on interacting with the app as consumers would so a 
TestRestTemplate is used to fire off requests to the api to enact behavior, responses are tested against expectations, 
and queries confirm correct DB state.

## Production Read Next Steps
The following tasks should be followed to get this app deployed and ready for production
- Robust datastore - H2 is fine for local development, but a scalable and distributed one is needed. To ensure parity between prod and local, I'd recommend using docker and/or testcontainers to stand up whatever prod database is chosen locally
- Security - It should be determined if this will be a public or internal api and implement an appropriate security plan (i.e. JWT token, HMAC, etc.)
- Distributed rate limiting - To ensure fair use of the api, distributed rate limiting allows rate limit buckets to be shared across instances. Additionally, a more fine grain bucketing strategy can be implemented by identifying consumers in some way
- Monitoring and alerting - Metrics should be included to judge the application's performance. Additionally, dashboards should be created with proper alerting
- Performance testing - A baseline of the app's throughput should be determined to drive autoscaling/instance count
- Automated build pipeline - To ease developer experience, an pipeline (github actions, jenkins, etc.) should be created to build, test, deploy to lower environments, and kick off additional automated testing 
