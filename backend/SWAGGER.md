# Swagger API Documentation

This project includes interactive API documentation powered by Swagger/Springfox.

## Accessing Swagger UI

Once the application is running, you can access the Swagger UI at:

**Swagger UI**: http://localhost:8080/swagger-ui.html

## API Documentation Endpoints

- **Swagger UI (Interactive)**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v2/api-docs
- **OpenAPI YAML**: http://localhost:8080/v2/api-docs?format=yaml

## Starting the Application

```bash
mvn spring-boot:run
```

Or build and run the JAR:

```bash
mvn clean package
java -jar target/interview-1.0-SNAPSHOT.jar
```

## What's Documented

The Swagger UI provides interactive documentation for all REST endpoints:

### Artists API (`/api/artists`)
- GET all artists (paginated)
- GET artist by ID
- POST create artist
- PUT update artist
- DELETE artist (with cascade)
- GET artist's songs
- GET artist's albums

### Songs API (`/api/songs`)
- GET all songs (paginated)
- GET song by ID
- POST create song
- PUT update song
- DELETE song

### Albums API (`/api/albums`)
- GET all albums (paginated)
- GET album by ID
- POST create album
- PUT update album
- DELETE album
- GET album's songs

### Search API (`/api/search`)
- Search across all entity types
- Filter by entity type
- Search by artist name

## Features

- **Try it out**: Execute API calls directly from the browser
- **Request/Response examples**: See sample JSON payloads
- **Parameter descriptions**: Understand what each parameter does
- **Response codes**: See all possible HTTP status codes
- **Model schemas**: View the structure of DTOs

## Configuration

Swagger is configured in `com.interview.config.SwaggerConfig.java`:
- Scans controllers in `com.interview.controller` package
- Documents all endpoints under `/api/**`
- Provides API metadata (title, description, version)

## Annotations Used

Controllers use Swagger annotations for better documentation:
- `@Api`: Describes the controller
- `@ApiOperation`: Describes each endpoint
- `@ApiParam`: Describes parameters
- `@ApiResponse`: Documents response codes

## Example: Testing the API

1. Start the application
2. Open http://localhost:8080/swagger-ui.html
3. Find the "Artists" section
4. Click "POST /api/artists" to expand
5. Click "Try it out"
6. Enter sample data:
   ```json
   {
     "name": "Queen"
   }
   ```
7. Click "Execute"
8. View the response with HTTP 201 Created

## Real-time Notifications

Note: WebSocket endpoints are not documented in Swagger as they use a different protocol (STOMP over WebSocket).

For WebSocket documentation, see:
- WebSocket endpoint: `ws://localhost:8080/ws`
- Topics:
  - `/topic/artists` - Artist notifications
  - `/topic/songs` - Song notifications
  - `/topic/albums` - Album notifications
