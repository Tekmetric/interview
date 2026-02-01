# Music Library API - Usage Guide

## Overview

The Music Library API is a RESTful web service for managing a music catalog including artists, songs, and albums. It provides CRUD operations for all entities, a unified search capability, pagination support, and real-time notifications via WebSocket.

**Key Features:**
- Full CRUD operations for Artists, Songs, and Albums
- Unified search across all entity types
- Pagination and sorting support
- Many-to-many relationships between songs and albums
- Cascade delete for artists (removes associated songs and albums)
- Real-time WebSocket notifications for all data changes
- Interactive API documentation via Swagger UI

## Quick Start

### Running the Application

```bash
# Using Maven
mvn spring-boot:run

# Or build and run the JAR
mvn clean package
java -jar target/interview-1.0-SNAPSHOT.jar
```

The API will be available at: **http://localhost:8080**

### Accessing Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v2/api-docs

## API Endpoints

### Artists (`/api/artists`)

Artists are the top-level entity in the music library. Each artist can have multiple songs and albums.

#### List All Artists
```bash
GET /api/artists?page=0&size=20&sort=name,asc
```

**Example Response:**
```json
{
  "content": [
    {
      "id": 1,
      "name": "The Beatles",
      "songCount": 15,
      "albumCount": 5
    }
  ],
  "pageable": { ... },
  "totalElements": 1,
  "totalPages": 1
}
```

#### Get Artist by ID
```bash
GET /api/artists/1
```

**Example Response:**
```json
{
  "id": 1,
  "name": "The Beatles"
}
```

#### Create Artist
```bash
POST /api/artists
Content-Type: application/json

{
  "name": "Queen"
}
```

**Example Response:** HTTP 201 Created
```json
{
  "id": 2,
  "name": "Queen"
}
```

#### Update Artist
```bash
PUT /api/artists/2
Content-Type: application/json

{
  "name": "Queen (Updated)"
}
```

**Example Response:** HTTP 200 OK
```json
{
  "id": 2,
  "name": "Queen (Updated)"
}
```

#### Delete Artist
```bash
DELETE /api/artists/2
```

**Example Response:** HTTP 204 No Content

**Important:** Deleting an artist will cascade delete all associated songs and albums.

#### Get Artist's Songs
```bash
GET /api/artists/1/songs?page=0&size=20&sort=title,asc
```

#### Get Artist's Albums
```bash
GET /api/artists/1/albums?page=0&size=20&sort=title,asc
```

### Songs (`/api/songs`)

Songs must be associated with an artist and can optionally be associated with multiple albums.

#### List All Songs
```bash
GET /api/songs?page=0&size=20&sort=title,asc
```

**Example Response:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Hey Jude",
      "length": 431,
      "releaseDate": "1968-08-26",
      "artistId": 1,
      "artistName": "The Beatles",
      "albumIds": [1, 2]
    }
  ],
  "totalElements": 1
}
```

#### Get Song by ID
```bash
GET /api/songs/1
```

#### Create Song
```bash
POST /api/songs
Content-Type: application/json

{
  "title": "Bohemian Rhapsody",
  "artistId": 2,
  "length": 354,
  "releaseDate": "1975-10-31",
  "albumIds": [3]
}
```

**Required Fields:**
- `title` (string)
- `artistId` (long)
- `length` (integer, in seconds)
- `releaseDate` (string, format: YYYY-MM-DD)

**Optional Fields:**
- `albumIds` (array of longs)

**Example Response:** HTTP 201 Created

#### Update Song
```bash
PUT /api/songs/1
Content-Type: application/json

{
  "title": "Hey Jude (Remastered)",
  "artistId": 1,
  "length": 431,
  "releaseDate": "1968-08-26",
  "albumIds": [1]
}
```

#### Delete Song
```bash
DELETE /api/songs/1
```

**Example Response:** HTTP 204 No Content

**Note:** Deleting a song does NOT delete associated albums.

### Albums (`/api/albums`)

Albums must be associated with an artist and can contain multiple songs.

#### List All Albums
```bash
GET /api/albums?page=0&size=20&sort=title,asc
```

**Example Response:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Abbey Road",
      "releaseDate": "1969-09-26",
      "artistId": 1,
      "artistName": "The Beatles",
      "songIds": [1, 2, 3]
    }
  ],
  "totalElements": 1
}
```

#### Get Album by ID
```bash
GET /api/albums/1
```

#### Create Album
```bash
POST /api/albums
Content-Type: application/json

{
  "title": "A Night at the Opera",
  "artistId": 2,
  "releaseDate": "1975-11-21",
  "songIds": [5, 6]
}
```

**Required Fields:**
- `title` (string)
- `artistId` (long)
- `releaseDate` (string, format: YYYY-MM-DD)

**Optional Fields:**
- `songIds` (array of longs)

**Example Response:** HTTP 201 Created

#### Update Album
```bash
PUT /api/albums/1
Content-Type: application/json

{
  "title": "Abbey Road (Remastered)",
  "artistId": 1,
  "releaseDate": "1969-09-26",
  "songIds": [1, 2, 3, 4]
}
```

#### Delete Album
```bash
DELETE /api/albums/1
```

**Example Response:** HTTP 204 No Content

**Note:** Deleting an album does NOT delete associated songs.

#### Get Album's Songs
```bash
GET /api/albums/1/songs?page=0&size=20&sort=title,asc
```

### Search (`/api/search`)

The unified search endpoint provides flexible search across all entity types using a database view.

#### Search Mode 1: General Search
Search across all entity types (artists, songs, and albums):

```bash
GET /api/search?q=beatles&page=0&size=20
```

**Example Response:**
```json
{
  "content": [
    {
      "entityType": "ARTIST",
      "entityId": 1,
      "name": "The Beatles",
      "title": null,
      "artistName": null,
      "releaseDate": null
    },
    {
      "entityType": "SONG",
      "entityId": 5,
      "name": null,
      "title": "Beatles Medley",
      "artistName": "The Beatles",
      "releaseDate": "1970-05-08"
    }
  ],
  "totalElements": 2
}
```

#### Search Mode 2: Type-Filtered Search
Search within a specific entity type (ARTIST, SONG, or ALBUM):

```bash
# Search only songs
GET /api/search?q=love&type=SONG&page=0&size=20

# Search only artists
GET /api/search?q=queen&type=ARTIST&page=0&size=20

# Search only albums
GET /api/search?q=night&type=ALBUM&page=0&size=20
```

#### Search Mode 3: Artist-Based Search
Find all songs and albums by a specific artist:

```bash
GET /api/search?artist=The Beatles&page=0&size=20
```

This will return all songs and albums (but not the artist entity itself) where the artist name matches.

**Search Notes:**
- All searches are case-insensitive substring matches
- Empty results return an empty page (not an error)
- If no search parameters provided, returns empty page

## Pagination

All list endpoints support pagination using Spring Data's `Pageable` interface.

**Query Parameters:**
- `page` - Zero-based page number (default: 0)
- `size` - Number of items per page (default: 20)
- `sort` - Sort criteria in format `property,direction` (e.g., `name,asc` or `releaseDate,desc`)

**Example:**
```bash
GET /api/songs?page=2&size=10&sort=releaseDate,desc&sort=title,asc
```

**Response Structure:**
```json
{
  "content": [ ... ],
  "pageable": {
    "pageNumber": 2,
    "pageSize": 10,
    "sort": { ... }
  },
  "totalElements": 150,
  "totalPages": 15,
  "last": false,
  "first": false,
  "numberOfElements": 10
}
```

## Real-Time Notifications

The API sends real-time notifications via WebSocket for all create, update, and delete operations.

### WebSocket Connection

**Endpoint:** `ws://localhost:8080/ws`

**Protocol:** STOMP over WebSocket

### Subscription Topics

Subscribe to these topics to receive notifications:

- `/topic/artists` - Artist create/update/delete events
- `/topic/songs` - Song create/update/delete events
- `/topic/albums` - Album create/update/delete events

### Notification Message Format

```json
{
  "action": "CREATED",
  "entityType": "SONG",
  "entityId": 42,
  "timestamp": "2025-01-15T10:30:00Z",
  "data": {
    "id": 42,
    "title": "New Song",
    "artistId": 1,
    ...
  }
}
```

**Action Types:**
- `CREATED` - New entity created
- `UPDATED` - Existing entity modified
- `DELETED` - Entity removed

### JavaScript Example

```javascript
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);

    // Subscribe to song notifications
    stompClient.subscribe('/topic/songs', function(message) {
        const notification = JSON.parse(message.body);
        console.log('Song ' + notification.action + ':', notification.data);
    });

    // Subscribe to all entity types
    stompClient.subscribe('/topic/artists', handleArtistNotification);
    stompClient.subscribe('/topic/albums', handleAlbumNotification);
});
```

## Error Handling

The API uses standard HTTP status codes:

**Success Codes:**
- `200 OK` - Successful GET or PUT request
- `201 Created` - Successful POST request
- `204 No Content` - Successful DELETE request

**Error Codes:**
- `400 Bad Request` - Invalid input data (missing required fields, invalid format)
- `404 Not Found` - Requested entity does not exist
- `500 Internal Server Error` - Unexpected server error

**Error Response Format:**
```json
{
  "timestamp": "2025-01-15T10:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Artist not found with id: 999",
  "path": "/api/artists/999"
}
```

## Data Models

### Artist
```json
{
  "id": 1,
  "name": "Artist Name"
}
```

### Song
```json
{
  "id": 1,
  "title": "Song Title",
  "artistId": 1,
  "length": 240,
  "releaseDate": "2020-01-01",
  "albumIds": [1, 2]
}
```

**Field Details:**
- `length` - Duration in seconds (integer)
- `releaseDate` - Format: YYYY-MM-DD
- `albumIds` - Optional array of album IDs

### Album
```json
{
  "id": 1,
  "title": "Album Title",
  "artistId": 1,
  "releaseDate": "2020-01-01",
  "songIds": [1, 2, 3]
}
```

**Field Details:**
- `releaseDate` - Format: YYYY-MM-DD
- `songIds` - Optional array of song IDs

### SearchResult
```json
{
  "entityType": "SONG",
  "entityId": 1,
  "name": null,
  "title": "Song Title",
  "artistName": "Artist Name",
  "releaseDate": "2020-01-01"
}
```

**Field Details:**
- `entityType` - One of: ARTIST, SONG, ALBUM
- `name` - Populated for ARTIST results only
- `title` - Populated for SONG and ALBUM results
- `artistName` - Populated for SONG and ALBUM results
- `releaseDate` - Populated for SONG and ALBUM results

## Common Workflows

### Creating a Complete Album

```bash
# Step 1: Create the artist
curl -X POST http://localhost:8080/api/artists \
  -H "Content-Type: application/json" \
  -d '{"name": "Pink Floyd"}'
# Response: {"id": 10, "name": "Pink Floyd"}

# Step 2: Create the album
curl -X POST http://localhost:8080/api/albums \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Dark Side of the Moon",
    "artistId": 10,
    "releaseDate": "1973-03-01"
  }'
# Response: {"id": 20, "title": "The Dark Side of the Moon", ...}

# Step 3: Create songs and associate with album
curl -X POST http://localhost:8080/api/songs \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Time",
    "artistId": 10,
    "length": 413,
    "releaseDate": "1973-03-01",
    "albumIds": [20]
  }'

curl -X POST http://localhost:8080/api/songs \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Money",
    "artistId": 10,
    "length": 382,
    "releaseDate": "1973-03-01",
    "albumIds": [20]
  }'
```

### Finding All Content by an Artist

```bash
# Search for everything by artist name
curl http://localhost:8080/api/search?artist=Pink+Floyd

# Or get artist ID first, then use relationship endpoints
curl http://localhost:8080/api/artists/10/songs
curl http://localhost:8080/api/artists/10/albums
```

### Updating Song-Album Associations

```bash
# Get current song details
curl http://localhost:8080/api/songs/5

# Update to add/remove album associations
curl -X PUT http://localhost:8080/api/songs/5 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Time",
    "artistId": 10,
    "length": 413,
    "releaseDate": "1973-03-01",
    "albumIds": [20, 21, 22]
  }'
```

## Testing

The API includes comprehensive test coverage:

- **Repository Tests** - Unit tests for data access layer
- **End-to-End Tests** - Integration tests for all API endpoints
- **Notification Tests** - WebSocket notification verification

Run tests:
```bash
mvn test
```

## Additional Resources

- **Swagger UI**: Interactive API testing interface at http://localhost:8080/swagger-ui.html
- **SWAGGER.md**: Documentation about Swagger configuration and usage
- **README.md**: Project setup and technical architecture
- **docs/Requirements.md**: Original project requirements and specifications
