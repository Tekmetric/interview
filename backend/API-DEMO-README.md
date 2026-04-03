# Athlete CRUD API Demo

This repository contains a comprehensive CRUD API for athlete management built with Spring Boot and demonstrated using Postman collections.

## 🏃‍♂️ API Overview

The Athlete API provides full CRUD operations with advanced features including:
- **Pagination** - Navigate through large datasets
- **Filtering** - Filter by nationality, discipline, and name search
- **Sorting** - Sort by multiple fields in ascending/descending order
- **Validation** - Comprehensive input validation using Jakarta Bean Validation
- **Error Handling** - Proper HTTP status codes and error messages
- **Rate Limiting** - Global rate limiting (1000 requests/minute - configurable)

## 🚀 Quick Start

### 1. Start the Application

```bash
cd backend
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### 2. Import Postman Collection

1. Open Postman
2. Click **Import** button
3. Navigate to the `backend` folder and import:
   - `Athlete-API.postman_collection.json` - The complete API collection with embedded variables

The collection includes all necessary variables (`base_url`, `athlete_id`, `created_athlete_id`) and will work immediately after import without needing a separate environment file.

## 📚 API Endpoints

### Base URL
```
http://localhost:8080/api/v1/athletes
```

### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/athletes` | Get all athletes (paginated, filterable, sortable) |
| `GET` | `/api/v1/athletes/{id}` | Get athlete by ID |
| `POST` | `/api/v1/athletes` | Create new athlete |
| `PUT` | `/api/v1/athletes/{id}` | Update existing athlete |
| `DELETE` | `/api/v1/athletes/{id}` | Delete athlete |

## 🎯 Demo Workflow

The Postman collection is organized into logical groups:

### 1. READ Operations
- **Get All Athletes** - Basic pagination example
- **Filter by Nationality** - Filter Jamaican athletes
- **Search by Name** - Find athletes by name (e.g., "Bolt")
- **Filter by Discipline** - Filter by sport discipline (e.g., "100m")
- **Multiple Filters** - Combine nationality and discipline filters
- **Advanced Sorting** - Sort by different fields and directions
- **Get Athlete by ID** - Retrieve specific athlete
- **404 Test Cases** - Verify error handling

### 2. CREATE Operations
- **Create New Athlete** - Add a new athlete with complete data
- **Validation Error** - Test input validation with invalid data
- **Duplicate Prevention** - Test business rule preventing duplicates (409 Conflict)

### 3. UPDATE Operations
- **Update Athlete** - Modify existing athlete data
- **404 Test** - Update non-existent athlete
- **Validation Error** - Test validation during updates

### 4. DELETE Operations
- **Delete Athlete** - Remove athlete from database
- **404 Test** - Delete non-existent athlete
- **Verification** - Confirm deletion by trying to retrieve deleted athlete

### 5. Advanced Features
- **Pagination Examples** - Different page sizes and navigation
- **Health Check** - Application health monitoring
- **Rate Limiting** - Test rate limiting functionality

## 📋 API Parameters

### GET /api/v1/athletes Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `page` | int | No | 0 | Page number (0-based) |
| `size` | int | No | 10 | Page size (1-100) |
| `sortBy` | string | No | id | Sort field (id, firstName, lastName, nationality, discipline) |
| `direction` | string | No | ASC | Sort direction (ASC, DESC) |
| `nationality` | string | No | - | Filter by nationality (partial match) |
| `discipline` | string | No | - | Filter by discipline (partial match) |
| `search` | string | No | - | Search by first or last name (partial match) |

### Request/Response Format

#### Create/Update Athlete Request Body
```json
{
    "firstName": "John",
    "lastName": "Doe",
    "birthDate": "1990-01-15",
    "nationality": "USA",
    "discipline": "100m",
    "personalBest": "9.99s",
    "bio": "Athlete description"
}
```

#### Athlete Response
```json
{
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "birthDate": "1990-01-15",
    "nationality": "USA",
    "discipline": "100m",
    "personalBest": "9.99s",
    "bio": "Athlete description"
}
```

#### Paginated Response
```json
{
    "content": [...],
    "number": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10,
    "first": true,
    "last": false
}
```

## 🧪 Testing Features

The Postman collection includes automated tests for:

- ✅ Status code validation
- ✅ Response structure validation
- ✅ Data integrity checks
- ✅ Error message verification
- ✅ Pagination metadata validation
- ✅ Filter result validation

### Running Tests

#### Option 1: Postman GUI
1. Execute individual requests to run their tests
2. Use **Collection Runner** to run all tests in sequence

#### Option 2: Command Line with Newman
Newman is Postman's command-line collection runner. Install it first:

```bash
# Install Newman globally
npm install -g newman

# Verify installation
newman --version
```

Then run the collection:
```bash
cd backend
newman run Athlete-API.postman_collection.json
```

**Note**: The collection is self-contained with embedded variables, so no separate environment file is needed.

## 🔧 Configuration

### Environment Variables

The Postman environment includes:

- `base_url` - API base URL (default: http://localhost:8080)
- `athlete_id` - Default athlete ID for testing
- `created_athlete_id` - Dynamically set when creating athletes

### Application Settings

The Spring Boot application is configured with:
- **Database**: H2 in-memory database with sample data
- **Port**: 8080
- **Rate Limiting**: 1000 requests per minute (configurable)
- **Validation**: Jakarta Bean Validation enabled

## 📊 Sample Data

The application includes pre-loaded sample data with athletes from various disciplines:
- Sprinters (100m, 200m)
- Distance runners (5000m, 10000m, Marathon)
- Middle distance (800m, 1500m)
- Field events (Javelin, Pole Vault, High Jump, etc.)

## 🚨 Error Handling

The API returns appropriate HTTP status codes:

- `200 OK` - Successful GET, PUT
- `201 Created` - Successful POST
- `204 No Content` - Successful DELETE
- `400 Bad Request` - Validation errors
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate athlete creation attempt
- `429 Too Many Requests` - Rate limit exceeded

## 📖 Documentation

- **Swagger UI**: Available at `http://localhost:8080/swagger-ui.html` when running
- **API Documentation**: Generated automatically from JavaDoc and annotations
- **Health Endpoint**: `http://localhost:8080/actuator/health`

## 🛠️ Development

### Prerequisites
- Java 21+
- Maven 3.6+
- Postman (for GUI testing) **OR** Node.js + npm (for CLI testing with Newman)

### Running Tests
```bash
cd backend
./mvnw test
```

### Building
```bash
cd backend
./mvnw clean package
```

## 📝 Notes

- The application uses H2 in-memory database - data resets on restart
- Rate limiting is global (not per-client)
- All dates are in ISO 8601 format (YYYY-MM-DD)
- Filtering and searching are case-insensitive and support partial matches
- The API includes comprehensive validation and error handling
