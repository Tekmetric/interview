# Java Spring Boot API Coding Exercise

This project now includes a complete CRUD API for a `WorkOrder` resource, backed by in-memory H2.

## Prerequisites
- Java 21
- Maven 3.9+

## Run the app
```bash
mvn spring-boot:run
```

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
curl -X GET http://localhost:8080/api/welcome
```

## Actuator endpoints
- `GET /actuator/health`
- `GET /actuator/info`
- `GET /actuator/metrics`
- Demo file: `demo/actuator-api-demo.http`

## API demo files
- `demo/work-order-api-demo.http`

## CRUD endpoints
- `POST /api/customers/{customerId}/work-orders` - create a work order
- `GET /api/customers/{customerId}/work-orders` - list all work orders for a customer
- `GET /api/customers/{customerId}/work-orders/{id}` - fetch a single work order for a customer
- `PUT /api/customers/{customerId}/work-orders/{id}` - update a work order
- `DELETE /api/customers/{customerId}/work-orders/{id}` - delete a work order

## Example API-client demo (cURL)

1. Create:
```bash
curl -X POST http://localhost:8080/api/customers/1/work-orders \
  -H "Content-Type: application/json" \
  -d '{
    "vin": "1HGCM82633A123456",
    "issueDescription": "Air conditioning not cooling",
    "status": "OPEN"
  }'
```

2. List:
```bash
curl -X GET http://localhost:8080/api/customers/1/work-orders
```

3. Get one:
```bash
curl -X GET http://localhost:8080/api/customers/1/work-orders/1
```

4. Update:
```bash
curl -X PUT http://localhost:8080/api/customers/1/work-orders/1 \
  -H "Content-Type: application/json" \
  -d '{
    "vin": "1HGCM82633A123456",
    "issueDescription": "A/C repaired and tested",
    "status": "COMPLETED"
  }'
```

5. Delete:
```bash
curl -X DELETE http://localhost:8080/api/customers/1/work-orders/1
```

## Database migration
Schema is managed by Flyway migration:
- `src/main/resources/db/migration/V1__init_work_orders.sql`

`work_orders` now has a FK relation to `customers` (`work_orders.customer_id -> customers.id`).
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
