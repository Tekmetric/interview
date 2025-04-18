# Basic Repair Shop Management Service

This is Spring boot service which allows management of jobs & tasks within a repair shop.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Data JPA
- H2 Database
- Flyway (schema migration)
- Lombok & MapStruct (for reducing boilerplate)
- Zipkin (distributed tracing)
- Micrometer Prometheus (exposes application metrics)
- Grafana (for metrics dashboard)
- Spring Boot Test

## Setup for local dev

Can be found [here](SETUP_LOCAL_DEV.md)

## DB Diagram

```mermaid
erDiagram
    CAR ||--|{ JOB : has
    CAR {
        int id
        text vin
        text make
        text model
        int model_year
        text customer
        timestamp createdAt
        timestamp updatedAt
    }
    JOB ||--|{ TASK : has
    JOB {
        int id
        int fk_car_id
        text status
        timestamp scheduled_at
        timestamp createdAt
        timestamp updatedAt
    }
    TASK {
        int id
        int fk_job_id
        text status
        text title
        text type
        text description
        text mechanic_name
    }
```

## API

TODO

## Security

This application leverages Spring Security with OAuth2 Resource Server capabilities to secure its endpoints. It relies on JWT (JSON Web Tokens) for authentication and role-based authorization.

### Authentication

All requests except initial resource found at `/api/welcome` are required to provide a valid JWT in `Authorization` header.
The service will validate the JWT signature using the public key found at `resources/keys/public.pem` directory.

***Note***: for production we would configure an external Authorization Provider (okta, google etc.) to obtain the keys from. 

### Authorization

Role-based authorization is set up such that the server will read roles from JWT claim `roles`.
We use method-level security on the endpoints using `@PreAuthorize("hasRole({ROLE})")` to enforce that a user needs to have a specific role (ADMIN or USER) in order to access those resources.
