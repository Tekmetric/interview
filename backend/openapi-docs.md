# OpenAPI Documentation Guide

## Overview

The Commercial Kitchen Ingredient Management API now includes comprehensive OpenAPI 3.0 documentation. This provides interactive API documentation, schema definitions, and the ability to test endpoints directly from your browser.

## Accessing the Documentation

### 1. Swagger UI (Interactive Documentation)

The Swagger UI provides an interactive interface to explore and test the API.

**URL:** `http://localhost:8080/swagger-ui/index.html`

**Features:**
- Interactive API explorer
- Try out API endpoints directly from the browser
- View request/response schemas
- See example values
- Execute requests with custom parameters

### 2. OpenAPI JSON Document

The raw OpenAPI specification in JSON format.

**URL:** `http://localhost:8080/v3/api-docs`

**Use Cases:**
- Import into API testing tools (Postman, Insomnia, etc.)
- Generate client SDKs
- API contract validation
- Integration with CI/CD pipelines

### 3. OpenAPI YAML Document

The OpenAPI specification in YAML format (more human-readable).

**URL:** `http://localhost:8080/v3/api-docs.yaml`

## API Documentation Structure

### API Information

- **Title:** Commercial Kitchen Ingredient Management API
- **Version:** 1.0.0
- **Description:** REST API for managing ingredients in a commercial kitchen environment
- **License:** MIT License
- **Contact:** support@kitchenmanagement.com

### Base URL

- **Development Server:** `http://localhost:8080`

### Available Endpoints

#### Ingredient Management

1. **GET /api/ingredients** - Get all ingredients
   - Returns a list of all ingredients in the inventory

2. **GET /api/ingredients/{id}** - Get ingredient by ID
   - Retrieves a specific ingredient by its unique identifier
   - Parameters: `id` (path, required)

3. **POST /api/ingredients** - Create new ingredient
   - Adds a new ingredient to the inventory
   - Request body: Ingredient object (required)

4. **PUT /api/ingredients/{id}** - Update ingredient
   - Updates an existing ingredient's information
   - Parameters: `id` (path, required)
   - Request body: Updated ingredient data (required)

5. **DELETE /api/ingredients/{id}** - Delete ingredient
   - Removes an ingredient from the inventory
   - Parameters: `id` (path, required)

#### Search and Filter

6. **GET /api/ingredients/category/{category}** - Get ingredients by category
   - Parameters: `category` (path, required)
   - Example: `/api/ingredients/category/Dairy`

7. **GET /api/ingredients/supplier/{supplier}** - Get ingredients by supplier
   - Parameters: `supplier` (path, required)
   - Example: `/api/ingredients/supplier/Local%20Farm%20Direct`

8. **GET /api/ingredients/search?name={name}** - Search ingredients by name
   - Parameters: `name` (query, required)
   - Example: `/api/ingredients/search?name=tomato`

9. **GET /api/ingredients/low-stock** - Get low stock ingredients
   - Returns ingredients where quantity is at or below minimum stock level

## Ingredient Schema

### Properties

| Field | Type | Required | Description | Example |
|-------|------|----------|-------------|---------|
| `id` | Long | Auto-generated | Unique identifier | `1` |
| `name` | String | Yes | Ingredient name | `"Fresh Tomatoes"` |
| `category` | String | Yes | Category | `"Vegetables"` |
| `quantity` | Double | Yes | Current quantity | `25.0` |
| `unit` | String | Yes | Unit of measurement | `"kg"` |
| `minimumStock` | Double | Yes | Minimum stock level | `15.0` |
| `pricePerUnit` | BigDecimal | No | Price per unit | `3.50` |
| `supplier` | String | No | Supplier name | `"Local Farm Direct"` |
| `expirationDate` | LocalDate | No | Expiration date | `"2025-12-31"` |
| `requiresRefrigeration` | Boolean | Yes | Needs refrigeration | `true` |
| `lastUpdated` | LocalDateTime | Auto-generated | Last update timestamp | `"2025-11-04T20:00:00"` |
| `lowStock` | Boolean | Computed | Is below minimum stock | `false` |

### Example Request Body (POST/PUT)

```json
{
  "name": "Fresh Tomatoes",
  "category": "Vegetables",
  "quantity": 25.0,
  "unit": "kg",
  "minimumStock": 15.0,
  "pricePerUnit": 3.50,
  "supplier": "Local Farm Direct",
  "expirationDate": "2025-12-31",
  "requiresRefrigeration": true
}
```

### Example Response

```json
{
  "id": 1,
  "name": "Fresh Tomatoes",
  "category": "Vegetables",
  "quantity": 25.0,
  "unit": "kg",
  "minimumStock": 15.0,
  "pricePerUnit": 3.50,
  "supplier": "Local Farm Direct",
  "expirationDate": "2025-12-31",
  "requiresRefrigeration": true,
  "lastUpdated": "2025-11-04T20:00:00.123456",
  "lowStock": false
}
```

## Using the Documentation with Tools

### Postman

1. Open Postman
2. Click **Import**
3. Select **Link** tab
4. Enter: `http://localhost:8080/v3/api-docs`
5. Click **Continue** and then **Import**

### Insomnia

1. Open Insomnia
2. Click **Create** → **Import From**
3. Select **URL**
4. Enter: `http://localhost:8080/v3/api-docs`
5. Click **Fetch and Import**

### Swagger Editor

1. Go to [editor.swagger.io](https://editor.swagger.io/)
2. Click **File** → **Import URL**
3. Enter: `http://localhost:8080/v3/api-docs`
4. View and edit the specification

## Testing with Swagger UI

### Example: Creating a New Ingredient

1. Navigate to `http://localhost:8080/swagger-ui/index.html`
2. Find the **POST /api/ingredients** endpoint
3. Click **Try it out**
4. Edit the request body with your ingredient data:
   ```json
   {
     "name": "Mozzarella Cheese",
     "category": "Dairy",
     "quantity": 15.0,
     "unit": "kg",
     "minimumStock": 5.0,
     "pricePerUnit": 9.75,
     "supplier": "Dairy Delight",
     "expirationDate": "2024-11-20",
     "requiresRefrigeration": true
   }
   ```
5. Click **Execute**
6. View the response below

### Example: Searching for Ingredients

1. Find the **GET /api/ingredients/search** endpoint
2. Click **Try it out**
3. Enter a search term in the `name` parameter (e.g., "cheese")
4. Click **Execute**
5. View matching ingredients in the response

## Response Codes

| Code | Description |
|------|-------------|
| 200 | Successful operation |
| 201 | Successfully created |
| 400 | Bad request / Invalid input |
| 404 | Resource not found |
| 500 | Internal server error |

## Dependencies

The OpenAPI documentation is powered by:
- **springdoc-openapi-starter-webmvc-ui** (version 2.2.0)
- Provides both Swagger UI and OpenAPI specification generation

## Configuration

OpenAPI configuration can be found in:
```
src/main/java/com/interview/config/OpenApiConfig.java
```

## Annotations Used

- `@Tag` - Groups endpoints
- `@Operation` - Describes endpoint operation
- `@ApiResponses` - Documents possible responses
- `@Parameter` - Describes path/query parameters
- `@Schema` - Documents model properties
- `@io.swagger.v3.oas.annotations.parameters.RequestBody` - Documents request body

## Additional Resources

- [OpenAPI Specification](https://swagger.io/specification/)
- [Springdoc OpenAPI Documentation](https://springdoc.org/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)

## Notes

- The documentation is automatically generated from code annotations
- The Swagger UI is available only when the application is running
- All endpoints return JSON formatted data
- The API follows RESTful conventions
