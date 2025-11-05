# Commercial Kitchen Ingredient Management API - Project Summary

## Overview
This is a complete CRUD REST API built with Spring Boot for managing commercial kitchen ingredients. The application uses an in-memory H2 database and also exposes an OpenAPI definition and a swagger UI (in an enterprise environment, API definition would be decoupled from the main application)


## Technologies Used

- **Java 21**: Programming language (Temurin 21.0.5)
- **Spring Boot 3.2.0**: Application framework
- **Spring Data JPA**: Data persistence
- **Jakarta Persistence API**: JPA 3.0+ (formerly javax.persistence)
- **H2 Database 2.1.210**: In-memory database
- **Maven**: Build tool
- **Hibernate 6.3.1**: ORM framework
- **SpringDoc OpenAPI 2.2.0**: API documentation (Swagger UI)

## Features Implemented

### Core CRUD Operations
✅ **CREATE** - Add new ingredients to inventory  
✅ **READ** - Fetch all ingredients or a single ingredient by ID  
✅ **UPDATE** - Modify existing ingredient details  
✅ **DELETE** - Remove ingredients from inventory  

### Additional Features
✅ **Search by Name** - Find ingredients using partial name matching (case-insensitive)  
✅ **Filter by Category** - Get all ingredients in a specific category  
✅ **Filter by Supplier** - Get all ingredients from a specific supplier  
✅ **Low Stock Alert** - Identify ingredients that need reordering  
✅ **Refrigeration Tracking** - Track which ingredients require refrigeration  
✅ **Global Exception Handling** - Consistent error responses using Jakarta EntityNotFoundException  
✅ **Automatic Timestamps** - Track when ingredients were last updated  
✅ **OpenAPI Documentation** - Interactive API documentation with Swagger UI  

## Data Model

### Ingredient Entity
```java
- id (Long): Auto-generated primary key
- name (String): Ingredient name
- category (String): Category (Oils, Grains, Vegetables, etc.)
- quantity (Double): Current stock quantity
- unit (String): Unit of measurement (kg, liters, units)
- minimumStock (Double): Reorder threshold
- pricePerUnit (BigDecimal): Price per unit
- supplier (String): Supplier name
- expirationDate (LocalDate): Expiration date
- requiresRefrigeration (Boolean): Whether refrigeration is required
- lastUpdated (LocalDateTime): Last update timestamp
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/ingredients` | Create new ingredient |
| GET | `/api/ingredients` | Get all ingredients |
| GET | `/api/ingredients/{id}` | Get ingredient by ID |
| PUT | `/api/ingredients/{id}` | Update ingredient |
| DELETE | `/api/ingredients/{id}` | Delete ingredient |
| GET | `/api/ingredients/search?name={name}` | Search by name |
| GET | `/api/ingredients/category/{category}` | Get by category |
| GET | `/api/ingredients/supplier/{supplier}` | Get by supplier |
| GET | `/api/ingredients/low-stock` | Get low stock items |

## How to Run

### Prerequisites
- Java 21
- Maven 3.x

### Build and Run
```bash
cd backend
mvn clean package
java -jar target/interview-1.0-SNAPSHOT.jar
```

Alternatively, run with Maven:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access H2 Console
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

### Access API Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

### Quick Test
```bash
# Get all ingredients
curl -X GET http://localhost:8080/api/ingredients

# Create an ingredient
curl -X POST http://localhost:8080/api/ingredients \
  -H "Content-Type: application/json" \
  -d '{"name":"Olive Oil","category":"Oils","quantity":50.0,"unit":"liters","minimumStock":10.0,"pricePerUnit":8.50,"supplier":"Mediterranean Foods","expirationDate":"2025-12-31","requiresRefrigeration":false}'

# Search for ingredients
curl -X GET "http://localhost:8080/api/ingredients/search?name=tomato"

# Get low stock items
curl -X GET http://localhost:8080/api/ingredients/low-stock
```

## Best Practices Implemented

1. **Layered Architecture**: Clear separation between Controller, Service, and Repository layers
2. **Dependency Injection**: Constructor-based injection for better testability
3. **Exception Handling**: Global exception handler using standard Jakarta EntityNotFoundException
4. **RESTful Design**: Proper HTTP methods and status codes
5. **Data Validation**: Entity-level validation with JPA annotations
6. **Automatic Timestamps**: PreUpdate lifecycle callback for tracking changes
7. **Proper HTTP Status Codes**: 200, 201, 404, 500 as appropriate
8. **Meaningful Response Messages**: Clear error messages for debugging
9. **API Documentation**: Comprehensive OpenAPI/Swagger documentation with annotations
10. **Comprehensive Testing**: unit and integration tests with 100% passing rate

## Sample Categories

The application includes sample data in these categories:
- Oils (Olive Oil, etc.)
- Grains (Flour, Pasta, etc.)
- Vegetables (Tomatoes, Onions, Peppers, Garlic, etc.)
- Proteins (Chicken, Eggs, etc.)
- Dairy (Cheese, Cream, Butter, etc.)
- Herbs (Basil, etc.)
- Seasonings (Salt, Pepper, etc.)

## Error Handling

All errors return a structured JSON response using standard Jakarta EntityNotFoundException:
```json
{
  "timestamp": "2025-11-04T20:29:33.974079",
  "status": 404,
  "error": "Not Found",
  "message": "Ingredient not found with id: 999",
  "path": "/api/ingredients/999"
}
```

## Testing

The project includes a comprehensive test suite:
- **Unit Tests**: Service layer, Controller layer, Model validation
- **Integration Tests**: Full API endpoint testing
- **Repository Tests**: JPA repository query testing

Run tests with:
```bash
mvn test
```

All tests pass successfully with Java 21 and Spring Boot 3.2.0.

## Future Enhancements

Potential improvements for production use:
- Add authentication and authorization (Spring Security)
- Implement pagination for large datasets
- Add comprehensive input validation with Bean Validation
- Implement rate limiting for API endpoints
- Add caching for better performance (Redis/Caffeine)
- Add batch operations for bulk updates
- Include audit logging and change tracking
- Implement data backup and recovery strategies
- Add monitoring and metrics (Actuator, Prometheus)
- Deploy to cloud platform (AWS, Azure, GCP)

