# Inventory Management System

A comprehensive inventory management REST API built with Spring Boot 3.5.4 and Java 21.

## Features

- **Product Management**: CRUD operations for products with SKU tracking
- **Inventory Tracking**: Real-time stock levels across multiple warehouses
- **Stock Movements**: Complete audit trail of all inventory changes
- **Search & Filtering**: Advanced search capabilities across products and inventory
- **Load Testing**: Built-in performance testing with Artillery.js
- **Monitoring**: Health checks and metrics via Spring Actuator

## Tech Stack

- **Backend**: Spring Boot 3.5.4, Java 21
- **Database**: PostgreSQL with Flyway migrations
- **Security**: Spring Security with Basic Authentication
- **Containerization**: Docker & Docker Compose
- **Testing**: Artillery.js for load testing
- **CI/CD**: GitHub Actions

## Quick Start

### Using Docker Compose (Recommended)

```bash
# Start the application and database
docker-compose up -d

# Check health
curl http://localhost:8080/actuator/health

# Access API (credentials: admin/admin123)
curl -u admin:admin123 http://localhost:8080/api/products/active
```

## API Documentation

### Products API
- `GET /api/products` - List all products (paginated)
- `GET /api/products/active` - List active products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/search` - Search products
- `POST /api/products` - Create new product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Inventory API
- `GET /api/inventory` - List all inventory items
- `GET /api/inventory/low-stock` - Get low stock items
- `POST /api/inventory/movements` - Record stock movement
- `GET /api/inventory/movements/product/{id}` - Movement history

### Authentication
All API endpoints require Basic Authentication:
- Username: `admin` (configurable via `SPRING_SECURITY_USER_NAME`)
- Password: `admin123` (configurable via `SPRING_SECURITY_USER_PASSWORD`)

## Load Testing

Run performance tests against the application:

```bash
# Quick test
npm install -g artillery
artillery run quick-test.yml

# Full load test
artillery run load-test.yml

# Docker environment test
./load-test-docker.sh
```

## License

This project is for interview purposes.