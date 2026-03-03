# Java Spring Boot API Coding Exercise

This project now includes a complete CRUD API for a `WorkOrder` resource, backed by in-memory H2.

## Prerequisites
- Java 21
- Maven 3.9+

## Run the app
```bash
mvn spring-boot:run
```

## Verify app health
```bash
curl -X GET http://localhost:8080/api/welcome
```

## CRUD endpoints
- `POST /api/work-orders` - create a work order
- `GET /api/work-orders` - list all work orders
- `GET /api/work-orders/{id}` - fetch a single work order
- `PUT /api/work-orders/{id}` - update a work order
- `DELETE /api/work-orders/{id}` - delete a work order

## Example API-client demo (cURL)

1. Create:
```bash
curl -X POST http://localhost:8080/api/work-orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Jane Doe",
    "vin": "1HGCM82633A123456",
    "issueDescription": "Air conditioning not cooling",
    "status": "OPEN"
  }'
```

2. List:
```bash
curl -X GET http://localhost:8080/api/work-orders
```

3. Get one:
```bash
curl -X GET http://localhost:8080/api/work-orders/1
```

4. Update:
```bash
curl -X PUT http://localhost:8080/api/work-orders/1 \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Jane Doe",
    "vin": "1HGCM82633A123456",
    "issueDescription": "A/C repaired and tested",
    "status": "COMPLETED"
  }'
```

5. Delete:
```bash
curl -X DELETE http://localhost:8080/api/work-orders/1
```

## Database script
DDL and seed data are provided in:
- `src/main/resources/data.sql`

## H2 Console
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`
