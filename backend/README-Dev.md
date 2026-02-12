# Repair Order Management API - Developer Guide

## Tech Stack

- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Data JPA**
- **H2 Database** (in-memory)
- **Maven**
- **SpringDoc OpenAPI 2.2.0**

## Exposed Endpoints

### Application
- **Base URL**: `http://localhost:8080/api/v1`

### Swagger UI
- **URL**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### H2 Database Console
- **URL**: `http://localhost:8080/h2-console`

## REST API Endpoints

### Get All Orders (Paginated)
```bash
curl -X GET "http://localhost:8080/api/v1/orders?page=0&size=10"
```

### Get Order by ID
```bash
curl -X GET "http://localhost:8080/api/v1/orders/1"
```

### Create Order
```bash
curl -X POST "http://localhost:8080/api/v1/orders" -H "Content-Type: application/json" -d "{\"orderDate\": \"2024-11-06\", \"status\": \"PLACED\", \"car\": {\"id\": 0, \"licensePlate\": \"XYZ-999\", \"make\": \"BMW\", \"model\": \"X5\"}, \"mechanicIds\": [1, 2]}"
```

### Update Order
```bash
curl -X PUT "http://localhost:8080/api/v1/orders/1" -H "Content-Type: application/json" -d "{\"orderDate\": \"2024-11-06\", \"status\": \"IN_PROGRESS\",\"car\": {\"id\": 1},\"mechanicIds\": [2, 3]}"
```

### Delete Order
```bash
curl -X DELETE "http://localhost:8080/api/v1/orders/1"
```
