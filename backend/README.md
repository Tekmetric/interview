# Java Spring Boot API Coding Exercise

This project now includes a complete CRUD API for a `WorkOrder` resource, backed by in-memory H2.

## Prerequisites
- Java 21
- Maven 3.9+

## Run the app
```bash
mvn spring-boot:run
```

## Run with dev profile (auto seed data)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
When `dev` profile is active, SQL in `src/test/resources/sql/test-data.sql` is executed automatically.

## Run tests
- Unit tests only:
```bash
mvn test
```
- Integration tests (and unit tests in prior phase):
```bash
mvn verify
```

## Verify app health
```bash
curl -X GET http://localhost:8080/actuator/health
```

## Actuator endpoints
- `GET /actuator/health`
- `GET /actuator/info`
- `GET /actuator/metrics`
- Demo file: `demo/actuator-api-demo.http`

## API Security
- HTTP Basic authentication is enabled.
- `GET /api/**` requires `ROLE_USER` or `ROLE_ADMIN`.
- `POST/PUT/DELETE /api/**` requires `ROLE_ADMIN`.
- `GET /actuator/health` is public; other actuator endpoints require `ROLE_ADMIN`.
- Users are loaded from DB table `users`.
- Passwords are stored as BCrypt hashes (salted) in DB.
- `ROLE_USER` accounts are linked to a customer via `users.customer_id`.
- `ROLE_ADMIN` accounts can have `customer_id` as `NULL`.
- No default users are seeded by Flyway migration.
- Create users manually (or use test-scope SQL seeds during integration tests).

## Logging
- Structured JSON logging is enabled via Logback.
- Configuration file: `src/main/resources/logback-spring.xml`
- Request correlation id is supported with `X-Request-Id`.
- If not provided, API generates one and returns it in response header.
- Logs include this id as `requestId` (MDC) for request-level filtering.

## API demo files
- `demo/work-order-api-demo.http`

## CRUD endpoints
- `POST /api/customers/{customerId}/work-orders` - create a work order
- `GET /api/customers/{customerId}/work-orders` - list work orders for a customer (paged, sortable, filterable)
- `GET /api/customers/{customerId}/work-orders/{id}` - fetch a single work order for a customer
- `PUT /api/customers/{customerId}/work-orders/{id}` - update a work order
- `DELETE /api/customers/{customerId}/work-orders/{id}` - delete a work order

### List query params
- `status` (optional): `OPEN`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`
- `page` (optional): zero-based page index, default `0`
- `size` (optional): page size, default `20`
- `sort` (optional): `<field>,<direction>`, default `id,asc`

## Example API-client demo (cURL)

0. Prepare credentials:
```bash
BASIC_AUTH=$(printf '%s' 'api-admin:changeit-admin' | base64)
```

1. Create:
```bash
curl -X POST http://localhost:8080/api/customers/1/work-orders \
  -H "Authorization: Basic $BASIC_AUTH" \
  -H "Content-Type: application/json" \
  -d '{
    "vin": "1HGCM82633A123456",
    "issueDescription": "Air conditioning not cooling",
    "status": "OPEN"
  }'
```

2. List:
```bash
curl -X GET http://localhost:8080/api/customers/1/work-orders \
  -H "Authorization: Basic $BASIC_AUTH"
```

2a. List with filter + pagination + sorting:
```bash
curl -X GET "http://localhost:8080/api/customers/1/work-orders?status=OPEN&page=0&size=2&sort=id,asc" \
  -H "Authorization: Basic $BASIC_AUTH"
```

3. Get one:
```bash
curl -X GET http://localhost:8080/api/customers/1/work-orders/1 \
  -H "Authorization: Basic $BASIC_AUTH"
```

4. Update:
```bash
curl -X PUT http://localhost:8080/api/customers/1/work-orders/1 \
  -H "Authorization: Basic $BASIC_AUTH" \
  -H "Content-Type: application/json" \
  -d '{
    "vin": "1HGCM82633A123456",
    "issueDescription": "A/C repaired and tested",
    "status": "COMPLETED"
  }'
```

5. Delete:
```bash
curl -X DELETE http://localhost:8080/api/customers/1/work-orders/1 \
  -H "Authorization: Basic $BASIC_AUTH"
```

## Database migration
Schema is managed by Flyway migration:
- `src/main/resources/db/migration/V1__init_work_orders.sql`
- `src/main/resources/db/migration/V2__add_users.sql`

`work_orders` now has a FK relation to `customers` (`work_orders.customer_id -> customers.id`).
`users` has an optional FK relation to `customers` (`users.customer_id -> customers.id`) with role-based check constraint.
Work order create/update requires an existing `customerId` in URL path; customer records are not auto-created from work order requests.

## Test data
Integration-test data is managed in test scope SQL scripts:
- `src/test/resources/sql/cleanup.sql`
- `src/test/resources/sql/test-data.sql`

## H2 Console
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`
