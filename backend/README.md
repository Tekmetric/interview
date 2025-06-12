# tekmetric-backend-interview

The project is a SpringBoot v3.5.0 application that serves as a very basic backend for a car service management system. 
It allows users to manage customer, vehicle and repair order records, including creating, updating, and deleting them via REST API endpoints.

## Prerequisites

- Java 21 or higher
- Maven 3.9 or higher

## Building the Project

### Source Code

To build the project source code, run the following command in the root directory:

```bash
./mvnw clean install
```

### Docker Image

To build the Docker image, run the following command in the backend directory:

```bash
docker build -t tekmetric-backend-interview .
```

## Running the Project

### 

To run the project, execute the following command in the root directory:

```bash
TODO
```

### Docker Container

To run the project in a Docker container, execute the following command in the root directory:

```bash
docker run -p 8080:8080 tekmetric-backend-interview
```

### Local Development Tools

* Swagger URI is available at: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* H2 Console is available at: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

> Note: The H2 Console and the Swagger URI is only available in the "dev" profile.

## Running Tests

To run the project, execute the following command in the root directory:

```bash
./mvnw clean verify
```

## Environment Variables

TODO

## Health Check & Info Endpoints

TODO

## ER Diagram

![ER diagram](./docs/er-diagram.png)

## Future Improvements

In order to keep the interview demo project simple and constrained to a specific timeline, 
some features have been left out or simplified and could be improved in the future:

- To ensure discoverability in the REST API, HATEOAS support should be considered.
- To prevent "lost updates" in the database, the persistence layer should use versioning (i.e. version column) if necessary.
- Migration to a more robust database (e.g. PostgreSQL, MySQL) should be considered.
