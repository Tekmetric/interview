# Music Application Backend - Detailed Project Plan

## Project Overview
Implementation of a Spring Boot microservice for managing a music application database (similar to Spotify) with Artist, Album, and Song entities. The system will provide RESTful APIs with real-time notifications via WebSocket and JMS messaging.

## Implementation Status

### ✅ COMPLETED
- **Java Version**: Upgraded from Java 8 to Java 11
- **Phase 1**: Data Model Foundation (Tasks 1-2)
  - ✅ Task 1: JPA Entities (Artist, Song, Album, SearchResult)
  - ✅ Task 2: Database Initialization Scripts (data.sql)
- **Phase 1.5**: Repository Integration Testing (Tasks 3-6)
  - ✅ Task 3: Repository Integration Test Infrastructure Setup
  - ✅ Task 4: Entity and Relationship Integration Tests (14 tests passing)
  - ✅ Task 5: Repository Query Integration Tests (16 tests passing)
  - ✅ Task 6: Search View Integration Tests (12 tests passing)
  - ✅ **Total: 42 repository integration tests passing**
- **Phase 3**: Data Access Layer
  - ✅ Task 9: Spring Data JPA Repositories (All 4 repositories created)

### 🔄 REMAINING
- **Phase 2**: API Layer Setup (Tasks 7-8)
- **Phase 4**: Business Logic Layer (Tasks 10-13)
- **Phase 5**: REST API Implementation (Tasks 14-16)
- **Phase 6**: Search API Implementation (Task 17)
- **Phase 7**: Real-time Communication (Tasks 18-20)
- **Phase 8**: End-to-End Testing (Tasks 21-26)

## Architecture Overview
- **Technology Stack**: Spring Boot 2.2.1, Java 11, H2 Database 2.1.210, JPA/Hibernate
- **Architecture Pattern**: Layered architecture (Controller → Service → Repository)
- **Communication**: REST APIs, WebSocket, JMS
- **Data Transfer**: DTOs with ModelMapper for entity mapping

## Implementation Phases

### Phase 1: Data Model Foundation (Tasks 1-2) ✅ COMPLETED

#### Task 1: Create JPA Entities ✅ COMPLETED
**Objective**: Implement the core domain model with proper JPA relationships

**Entities to create**:
- `Artist.java`
  - Fields: id (Long), name (String)
  - Relationships: One-to-Many with Songs and Albums
  - Cascade: CascadeType.REMOVE for Songs and Albums

- `Song.java`
  - Fields: id (Long), title (String), length (Duration), releaseDate (LocalDate)
  - Relationships: Many-to-One with Artist (required), Many-to-Many with Albums
  - JoinTable: song_album

- `Album.java`
  - Fields: id (Long), title (String), releaseDate (LocalDate)
  - Relationships: Many-to-One with Artist, Many-to-Many with Songs

- `SearchResult.java` (Read-only entity mapped to database view)
  - Fields: id (Long), entityType (String), name (String), artistName (String), releaseDate (LocalDate)
  - Annotation: @Immutable, @Subselect (maps to search_view)
  - Used for: Unified search across all entity types

**Package**: `com.interview.entity`

#### Task 2: Database Initialization Scripts ✅ COMPLETED
**Objective**: Create H2 database schema and sample data

**Note**: Database view is created via @Subselect annotation in SearchResult entity. Sample data.sql includes test data for Artists, Songs, Albums, and their relationships.

**Location**: `src/main/resources/database/data.sql`

**Scripts to include**:
- CREATE TABLE statements (if not using JPA auto-generation)
- CREATE VIEW for unified search:
  ```sql
  CREATE OR REPLACE VIEW search_view AS
  SELECT
    id,
    'ARTIST' as entity_type,
    name as name,
    name as artist_name,
    NULL as release_date
  FROM artist
  UNION ALL
  SELECT
    s.id,
    'SONG' as entity_type,
    s.title as name,
    a.name as artist_name,
    s.release_date as release_date
  FROM song s
  JOIN artist a ON s.artist_id = a.id
  UNION ALL
  SELECT
    al.id,
    'ALBUM' as entity_type,
    al.title as name,
    a.name as artist_name,
    al.release_date as release_date
  FROM album al
  JOIN artist a ON al.artist_id = a.id;
  ```
- Sample data for testing:
  - 5-10 Artists
  - 20-30 Songs
  - 10-15 Albums
  - Appropriate relationship mappings

---

### Phase 1.5: Repository Integration Testing (Tasks 3-6) ✅ COMPLETED

#### Task 3: Repository Integration Test Infrastructure Setup ✅ COMPLETED
**Objective**: Set up infrastructure for testing JPA repositories against H2 database

**Implementation Notes**:
- Created `BaseRepositoryTest.java` with `@DataJpaTest` annotation
- Configured `application-test.properties` with H2 LEGACY mode for compatibility
- Added `spring.jpa.properties.hibernate.id.new_generator_mappings=false` for proper ID generation
- Disabled data.sql loading in tests (tests create their own data)

**Base Test Class**: `BaseRepositoryTest.java`

**Setup Requirements**:
- Use `@DataJpaTest` for repository slice testing
- Automatically configures H2 test database
- Provides `TestEntityManager` for test data setup
- Transactional by default (rollback after each test)
- Excludes service, controller, and other beans

**Dependencies** (already included in spring-boot-starter-test):
```xml
<!-- @DataJpaTest includes these automatically -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

**BaseRepositoryTest.java** structure:
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseRepositoryTest {
    @Autowired
    protected TestEntityManager entityManager;

    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
```

**Test Configuration** (application-test.properties):
```properties
# Repository tests use same H2 setup as main app
spring.datasource.url=jdbc:h2:mem:testdb_repository
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:database/data.sql
```

#### Task 4: Entity and Relationship Integration Tests ✅ COMPLETED
**Objective**: Validate JPA entity mappings and relationships

**Test Class**: `EntityMappingIntegrationTest.java`

**Status**: 14 tests passing - All entity mappings, relationships, and cascade operations verified

**Test Methods**:

1. **testArtistEntityPersistence()**
   - Create and persist Artist entity
   - Flush and clear persistence context
   - Retrieve by ID
   - Verify fields persisted correctly

2. **testSongEntityPersistence()**
   - Create Artist and Song entities
   - Persist Song with Artist relationship
   - Verify Song fields and Artist relationship persisted

3. **testAlbumEntityPersistence()**
   - Create Artist and Album
   - Verify Album persists with Artist relationship

4. **testArtistToSongsOneToManyRelationship()**
   - Create Artist with multiple Songs
   - Persist Artist (cascade to Songs)
   - Flush and clear context
   - Retrieve Artist
   - Verify Songs collection loaded correctly
   - Verify bidirectional relationship (song.getArtist() points back)

5. **testArtistToAlbumsOneToManyRelationship()**
   - Create Artist with multiple Albums
   - Verify Albums collection persisted and loaded
   - Verify bidirectional relationship

6. **testSongToAlbumsManyToManyRelationship()**
   - Create Song and multiple Albums
   - Add Song to Albums
   - Persist
   - Verify many-to-many relationship in both directions
   - Verify join table created correctly

7. **testAlbumToSongsManyToManyRelationship()**
   - Create Album with multiple Songs
   - Verify Songs collection on Album
   - Add another Song to Album
   - Verify join table updated

8. **testSongRequiresArtist()**
   - Attempt to persist Song without Artist
   - Verify constraint violation exception
   - Verify @NotNull validation works

9. **testCascadeDeleteArtistToSongs()**
   - Create Artist with 3 Songs
   - Persist and flush
   - Delete Artist
   - Flush and clear
   - Verify Songs deleted (cascade remove)
   - Query Song table directly to confirm

10. **testCascadeDeleteArtistToAlbums()**
    - Create Artist with 3 Albums
    - Delete Artist
    - Verify Albums deleted (cascade remove)

11. **testCascadeDeleteArtistToSongsAndAlbums()**
    - Create Artist with 5 Songs and 3 Albums
    - Associate Songs with Albums (many-to-many)
    - Delete Artist
    - Verify all Songs deleted
    - Verify all Albums deleted
    - Verify join table entries cleaned up

12. **testDeleteSongDoesNotDeleteAlbum()**
    - Create Song on Album
    - Delete Song
    - Verify Album still exists (no cascade)

13. **testDeleteAlbumDoesNotDeleteSongs()**
    - Create Album with Songs
    - Delete Album
    - Verify Songs still exist (no cascade)

14. **testMultipleSongsOnMultipleAlbums()**
    - Create complex graph: 2 Albums, 3 Songs
    - Song 1 on both Albums
    - Song 2 on Album 1 only
    - Song 3 on Album 2 only
    - Verify all relationships persisted correctly
    - Query from both sides to verify

#### Task 5: Repository Query Integration Tests ✅ COMPLETED
**Objective**: Test Spring Data JPA repository query methods

**Status**: 16 tests passing across 3 test classes
- `ArtistRepositoryIntegrationTest.java` - 7 tests
- `SongRepositoryIntegrationTest.java` - 6 tests
- `AlbumRepositoryIntegrationTest.java` - 3 tests

**Test Class**: `ArtistRepositoryIntegrationTest.java`

**Test Methods**:

1. **testSaveArtist()**
   - Save Artist via repository
   - Verify ID generated
   - Verify saved entity returned

2. **testFindById()**
   - Save Artist
   - Find by ID
   - Verify correct Artist returned

3. **testFindAll()**
   - Save 10 Artists
   - Find all
   - Verify count is 10

4. **testFindByNameContainingIgnoreCase()**
   - Save Artists: "Queen", "The Beatles", "The Rolling Stones"
   - Search for "the" (lowercase)
   - Verify "The Beatles" and "The Rolling Stones" returned
   - Verify case-insensitive

5. **testFindByNameContainingIgnoreCaseWithPagination()**
   - Save 25 Artists with "Band" in name
   - Query with PageRequest (page 0, size 10)
   - Verify page has 10 items
   - Verify total elements is 25
   - Query page 1
   - Verify next 10 items returned

6. **testExistsByName()**
   - Save Artist "Queen"
   - Verify existsByName("Queen") returns true
   - Verify existsByName("NonExistent") returns false

7. **testDeleteArtist()**
   - Save Artist
   - Delete by ID
   - Verify findById returns empty

**Test Class**: `SongRepositoryIntegrationTest.java`

**Test Methods**:

1. **testFindByArtistId()**
   - Create Artist with 5 Songs
   - Find Songs by Artist ID
   - Verify 5 Songs returned

2. **testFindByArtistIdWithPagination()**
   - Create Artist with 30 Songs
   - Query with pagination (page 0, size 10)
   - Verify correct page returned

3. **testFindByTitleContainingIgnoreCase()**
   - Save Songs with various titles
   - Search by partial title
   - Verify substring matching works (case-insensitive)

4. **testFindByAlbumsId()**
   - Create Album with multiple Songs
   - Find Songs by Album ID
   - Verify correct Songs returned

5. **testFindByAlbumsIdWithPagination()**
   - Create Album with 20 Songs
   - Query with pagination
   - Verify paginated results

6. **testSongWithMultipleAlbums()**
   - Create Song on 3 different Albums
   - Query each Album
   - Verify Song appears in all 3 Album queries

**Test Class**: `AlbumRepositoryIntegrationTest.java`

**Test Methods**:

1. **testFindByArtistId()**
   - Create Artist with 5 Albums
   - Find Albums by Artist ID
   - Verify 5 Albums returned

2. **testFindByArtistIdWithPagination()**
   - Create Artist with 25 Albums
   - Query with pagination
   - Verify paginated results correct

3. **testFindByTitleContainingIgnoreCase()**
   - Save Albums with various titles
   - Search by partial title
   - Verify substring matching works

#### Task 6: Search View Integration Tests ✅ COMPLETED
**Objective**: Test database view and SearchResult entity

**Test Class**: `SearchResultRepositoryIntegrationTest.java`

**Status**: 12 tests passing - All search view queries and entity type discrimination verified

**Setup**:
- Ensure search_view is created by data.sql
- Use @Sql annotation if needed to ensure view exists

**Test Methods**:

1. **testSearchViewExists()**
   - Query SearchResultRepository
   - Verify no errors (view exists and is queryable)

2. **testSearchArtistInView()**
   - Create Artist "Queen"
   - Query SearchResultRepository for "Queen"
   - Verify one result returned
   - Verify entityType = "ARTIST"
   - Verify name = "Queen"
   - Verify artistName = "Queen"

3. **testSearchSongInView()**
   - Create Artist "Queen" and Song "Bohemian Rhapsody"
   - Query for "Bohemian"
   - Verify result has entityType = "SONG"
   - Verify artistName = "Queen"
   - Verify releaseDate populated

4. **testSearchAlbumInView()**
   - Create Artist and Album
   - Query for album title
   - Verify result has entityType = "ALBUM"
   - Verify artistName populated correctly

5. **testSearchAllEntityTypes()**
   - Create Artist "Queen", Song "Bohemian Rhapsody", Album "A Night at the Opera"
   - Query for "Queen"
   - Verify 3 results returned (Artist, Song via artistName, Album via artistName)
   - Verify entity types correctly differentiated

6. **testFindByNameContainingIgnoreCase()**
   - Create multiple entities
   - Search with partial name (case-insensitive)
   - Verify substring matching across all entity types

7. **testFindByEntityTypeAndNameContainingIgnoreCase()**
   - Create mixed entity types with similar names
   - Filter by entityType = "SONG"
   - Verify only songs returned

8. **testFindByArtistNameContainingIgnoreCase()**
   - Create multiple Songs and Albums by "Queen"
   - Create other entities by "The Beatles"
   - Search artistName for "Queen"
   - Verify only Queen's songs and albums returned (not artist itself)

9. **testSearchPagination()**
   - Create 50 entities matching search term
   - Query with PageRequest (page 0, size 20)
   - Verify correct pagination metadata
   - Query page 1
   - Verify next page returned

10. **testSearchViewRefreshesWithNewData()**
    - Query search view (baseline)
    - Create new Artist
    - Flush
    - Query search view again
    - Verify new Artist appears in view (view updates dynamically)

11. **testSearchResultIsReadOnly()**
    - Query SearchResult entity
    - Attempt to modify and save
    - Verify operation fails or is ignored (read-only entity)

12. **testCascadeDeleteUpdatesSearchView()**
    - Create Artist with Songs and Albums
    - Query search view - verify all entities present
    - Delete Artist
    - Flush
    - Query search view for deleted artist name
    - Verify Artist, Songs, and Albums no longer in view

---

### Phase 2: API Layer Setup (Tasks 7-8)

#### Task 7: Create DTO Classes
**Objective**: Implement Data Transfer Objects for API communication

**Main Entity DTOs** (for CRUD operations):
- `ArtistDto.java`
  - Fields: id, name
  - Usage: Create/Update operations, single entity GET responses

- `SongDto.java`
  - Fields: id, title, length, releaseDate, artistId, albumIds
  - Usage: Create/Update operations, single entity GET responses

- `AlbumDto.java`
  - Fields: id, title, releaseDate, artistId, songIds
  - Usage: Create/Update operations, single entity GET responses

**Reference DTOs** (lightweight objects for relationships):
- `ArtistRefDto.java`
  - Fields: id, name
  - Usage: Embedded in list DTOs to represent related artists

- `SongRefDto.java`
  - Fields: id, title
  - Usage: Embedded in list DTOs to represent related songs

- `AlbumRefDto.java`
  - Fields: id, title
  - Usage: Embedded in list DTOs to represent related albums

**List/Search DTOs** (for list and search responses):
- `ArtistListDto.java`
  - Fields: id, name, songCount, albumCount
  - Usage: List endpoints, search results

- `SongListDto.java`
  - Fields: id, title, length, releaseDate, artist (ArtistRefDto), albums (List<AlbumRefDto>)
  - Usage: List endpoints, search results

- `AlbumListDto.java`
  - Fields: id, title, releaseDate, artist (ArtistRefDto), songCount, songs (List<SongRefDto>)
  - Usage: List endpoints, search results

**Search DTOs** (for unified search results):
- `SearchResultDto.java`
  - Fields: id, entityType (enum: ARTIST/SONG/ALBUM), name, artistName, releaseDate
  - Usage: Unified search endpoint responses
  - Maps from: SearchResult entity (database view)

**Package**: `com.interview.dto`
**Sub-packages**:
- `com.interview.dto.entity` - Main entity DTOs
- `com.interview.dto.reference` - Reference DTOs
- `com.interview.dto.list` - List/Search DTOs
- `com.interview.dto.search` - Search result DTOs

#### Task 8: Configure ModelMapper
**Objective**: Set up automatic DTO-Entity mapping

**Configuration class**: `ModelMapperConfig.java`
**Package**: `com.interview.config`

**Setup**:
- Bean definition for ModelMapper
- Custom mappings for complex relationships
- Bidirectional mapping configurations
- Separate mapping configurations for:
  - Entity ↔ Entity DTO mappings (for CRUD operations)
  - Entity → Reference DTO mappings (for lightweight references)
  - Entity → List DTO mappings (for list/search results)
  - Custom converters for:
    - Converting entity collections to Reference DTO lists
    - Aggregated fields (counts)
    - Nested reference resolution

---

### Phase 3: Data Access Layer (Task 9) ✅ COMPLETED

#### Task 9: Create Spring Data JPA Repositories ✅ COMPLETED
**Objective**: Implement repository interfaces with custom query methods

**Status**: All 4 repositories created and tested via integration tests

**Repositories**:
- `ArtistRepository.java`
  ```java
  - findByNameContainingIgnoreCase(String name, Pageable pageable)
  - existsByName(String name)
  ```

- `SongRepository.java`
  ```java
  - findByArtistId(Long artistId, Pageable pageable)
  - findByTitleContainingIgnoreCase(String title, Pageable pageable)
  - findByAlbumsId(Long albumId, Pageable pageable)
  ```

- `AlbumRepository.java`
  ```java
  - findByArtistId(Long artistId, Pageable pageable)
  - findByTitleContainingIgnoreCase(String title, Pageable pageable)
  ```

- `SearchResultRepository.java` (Read-only repository for database view)
  ```java
  - findByNameContainingIgnoreCase(String query, Pageable pageable)
  - findByEntityTypeAndNameContainingIgnoreCase(String entityType, String query, Pageable pageable)
  - findByArtistNameContainingIgnoreCase(String artistName, Pageable pageable)
  ```

**Package**: `com.interview.repository`

---

### Phase 4: Business Logic Layer (Tasks 10-13)

#### Task 10: Artist Service Implementation
**Objective**: Implement business logic for Artist operations

**Class**: `ArtistService.java`
**Package**: `com.interview.service`

**Methods**:
- `createArtist(ArtistDto dto)` → Returns ArtistDto
- `updateArtist(Long id, ArtistDto dto)` → Returns ArtistDto
- `deleteArtist(Long id)` - with cascade delete
- `getArtist(Long id)` → Returns ArtistDto
- `getAllArtists(Pageable pageable)` → Returns Page<ArtistListDto>

#### Task 11: Song Service Implementation
**Objective**: Implement business logic for Song operations

**Class**: `SongService.java`

**Methods**:
- `createSong(SongDto dto)` → Returns SongDto
- `updateSong(Long id, SongDto dto)` → Returns SongDto
- `deleteSong(Long id)`
- `getSong(Long id)` → Returns SongDto
- `getSongsByArtist(Long artistId, Pageable pageable)` → Returns Page<SongListDto>
- `getSongsByAlbum(Long albumId, Pageable pageable)` → Returns Page<SongListDto>

#### Task 12: Album Service Implementation
**Objective**: Implement business logic for Album operations

**Class**: `AlbumService.java`

**Methods**:
- `createAlbum(AlbumDto dto)` → Returns AlbumDto
- `updateAlbum(Long id, AlbumDto dto)` → Returns AlbumDto
- `deleteAlbum(Long id)`
- `getAlbum(Long id)` → Returns AlbumDto
- `getAlbumsByArtist(Long artistId, Pageable pageable)` → Returns Page<AlbumListDto>

#### Task 13: Search Service Implementation
**Objective**: Implement unified search across all entities using database view

**Class**: `SearchService.java`

**Methods**:
- `search(String query, Pageable pageable)` → Returns Page<SearchResultDto>
- `searchByType(String query, String entityType, Pageable pageable)` → Returns Page<SearchResultDto>
- `searchByArtist(String artistName, Pageable pageable)` → Returns Page<SearchResultDto>

**Note**: Uses SearchResultRepository to query the database view for efficient unified search

---

### Phase 5: REST API Implementation (Tasks 14-16)

#### Task 14: Artist REST Controller
**Objective**: Expose Artist operations via REST endpoints

**Class**: `ArtistController.java`
**Package**: `com.interview.controller`
**Base path**: `/api/artists`

**Endpoints**:
- `GET /api/artists` → Page<ArtistListDto> - List all artists (paginated)
- `GET /api/artists/{id}` → ArtistDto - Get specific artist
- `POST /api/artists` → ArtistDto - Create new artist
- `PUT /api/artists/{id}` → ArtistDto - Update artist
- `DELETE /api/artists/{id}` → void - Delete artist (cascade)
- `GET /api/artists/{id}/songs` → Page<SongListDto> - Get artist's songs
- `GET /api/artists/{id}/albums` → Page<AlbumListDto> - Get artist's albums

#### Task 15: Song REST Controller
**Class**: `SongController.java`
**Base path**: `/api/songs`

**Endpoints**:
- `GET /api/songs` → Page<SongListDto> - List all songs (paginated)
- `GET /api/songs/{id}` → SongDto - Get specific song
- `POST /api/songs` → SongDto - Create new song
- `PUT /api/songs/{id}` → SongDto - Update song
- `DELETE /api/songs/{id}` → void - Delete song

#### Task 16: Album REST Controller
**Class**: `AlbumController.java`
**Base path**: `/api/albums`

**Endpoints**:
- `GET /api/albums` → Page<AlbumListDto> - List all albums (paginated)
- `GET /api/albums/{id}` → AlbumDto - Get specific album
- `POST /api/albums` → AlbumDto - Create new album
- `PUT /api/albums/{id}` → AlbumDto - Update album
- `DELETE /api/albums/{id}` → void - Delete album
- `GET /api/albums/{id}/songs` → Page<SongRefDto> - Get album's songs

---

### Phase 6: Search API Implementation (Task 17)

#### Task 17: Search Controller Implementation
**Objective**: Implement unified search using database view

**Class**: `SearchController.java`
**Base path**: `/api/search`

**Endpoints**:
- `GET /api/search?q={query}` → Page<SearchResultDto> - Search all entity types
- `GET /api/search?q={query}&type={entityType}` → Page<SearchResultDto> - Filter by entity type
- `GET /api/search?artist={artistName}` → Page<SearchResultDto> - Search by artist name

**Features**:
- Leverages database view for efficient searching
- Substring matching (case-insensitive) via view
- Unified result format with entity type discrimination
- Paginated results
- Returns SearchResultDto with: id, entityType, name, artistName, releaseDate

---

### Phase 7: Real-time Communication (Tasks 18-20)

#### Task 18: WebSocket Configuration
**Objective**: Set up WebSocket for real-time client notifications

**Configuration class**: `WebSocketConfig.java`

**Components**:
- WebSocket message broker configuration
- STOMP protocol setup
- Client subscription endpoints:
  - `/topic/artists` - Artist changes
  - `/topic/songs` - Song changes
  - `/topic/albums` - Album changes

#### Task 19: JMS Broker Setup
**Objective**: Configure embedded JMS broker for internal messaging

**Configuration class**: `JmsConfig.java`

**Components**:
- Embedded ActiveMQ broker
- JMS template configuration
- Message queues:
  - `artist.queue`
  - `song.queue`
  - `album.queue`

#### Task 20: Notification Integration
**Objective**: Add notifications to all write operations

**Implementation**:
- Create `NotificationService.java`
- Inject into all service classes
- Send notifications on:
  - Create operations
  - Update operations
  - Delete operations
- Message format:
  ```json
  {
    "action": "CREATE|UPDATE|DELETE",
    "entityType": "ARTIST|SONG|ALBUM",
    "entityId": 123,
    "timestamp": "2024-02-01T12:00:00Z"
  }
  ```

---

### Phase 8: End-to-End Testing (Tasks 21-26)

#### Task 21: E2E Testing Infrastructure Setup
**Objective**: Configure WebTestClient for comprehensive API testing

**Test Configuration Class**: `TestConfig.java`
**Base Test Class**: `BaseE2ETest.java`

**Setup Requirements**:
- Use `@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)`
- Configure WebTestClient bean
- Set up test data initialization
- Configure H2 test database (separate from main)
- Mock/configure JMS and WebSocket for testing

**Dependencies** (add to pom.xml):
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
    <scope>test</scope>
</dependency>
```

**BaseE2ETest.java** structure:
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseE2ETest {
    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected ArtistRepository artistRepository;

    @Autowired
    protected SongRepository songRepository;

    @Autowired
    protected AlbumRepository albumRepository;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        albumRepository.deleteAll();
        songRepository.deleteAll();
        artistRepository.deleteAll();
    }
}
```

#### Task 22: Artist E2E Tests
**Objective**: Test all Artist endpoints and cascade delete functionality

**Test Class**: `ArtistE2ETest.java`

**Test Methods**:
1. **testCreateArtist()**
   - POST /api/artists with valid ArtistDto
   - Verify 201 Created status
   - Verify response contains id and name
   - Verify artist exists in database

2. **testGetArtist()**
   - Create artist via repository
   - GET /api/artists/{id}
   - Verify 200 OK status
   - Verify correct ArtistDto returned

3. **testUpdateArtist()**
   - Create artist
   - PUT /api/artists/{id} with updated data
   - Verify 200 OK status
   - Verify database updated

4. **testDeleteArtist()**
   - Create artist
   - DELETE /api/artists/{id}
   - Verify 204 No Content status
   - Verify artist removed from database

5. **testGetAllArtists()**
   - Create multiple artists
   - GET /api/artists
   - Verify 200 OK status
   - Verify Page<ArtistListDto> returned with correct count

6. **testCascadeDelete()**
   - Create artist with songs and albums
   - DELETE /api/artists/{id}
   - Verify artist deleted
   - Verify all associated songs deleted
   - Verify all associated albums deleted
   - Verify no orphaned records

7. **testGetArtistSongs()**
   - Create artist with multiple songs
   - GET /api/artists/{id}/songs
   - Verify Page<SongListDto> returned
   - Verify songs have correct artist reference

8. **testGetArtistAlbums()**
   - Create artist with multiple albums
   - GET /api/artists/{id}/albums
   - Verify Page<AlbumListDto> returned
   - Verify albums have correct artist reference

9. **testArtistNotFound()**
   - GET /api/artists/999999
   - Verify 404 Not Found status

10. **testCreateArtistValidation()**
    - POST /api/artists with invalid data (null name, empty name)
    - Verify 400 Bad Request status
    - Verify error response contains validation messages

#### Task 23: Song and Album E2E Tests
**Objective**: Test Song and Album CRUD operations and relationships

**Test Class**: `SongE2ETest.java`

**Test Methods**:
1. **testCreateSong()**
   - Create artist first
   - POST /api/songs with SongDto (artistId, title, length, releaseDate)
   - Verify 201 Created
   - Verify song has correct artist relationship

2. **testCreateSongWithAlbums()**
   - Create artist and albums
   - POST /api/songs with albumIds
   - Verify song associated with multiple albums

3. **testGetSong()**
   - Create song with artist
   - GET /api/songs/{id}
   - Verify SongDto returned with correct data

4. **testUpdateSong()**
   - Create song
   - PUT /api/songs/{id} with updated data
   - Verify updates persisted
   - Verify relationships maintained

5. **testDeleteSong()**
   - Create song
   - DELETE /api/songs/{id}
   - Verify song deleted
   - Verify albums still exist (no cascade)

6. **testGetAllSongs()**
   - Create multiple songs
   - GET /api/songs
   - Verify Page<SongListDto> with artist references

7. **testGetSongsByArtist()**
   - Create artist with multiple songs
   - GET /api/artists/{artistId}/songs
   - Verify correct songs returned

8. **testGetSongsByAlbum()**
   - Create album with multiple songs
   - GET /api/albums/{albumId}/songs
   - Verify correct songs returned

**Test Class**: `AlbumE2ETest.java`

**Test Methods**:
1. **testCreateAlbum()**
   - Create artist
   - POST /api/albums with AlbumDto
   - Verify album created with artist relationship

2. **testCreateAlbumWithSongs()**
   - Create artist and songs
   - POST /api/albums with songIds
   - Verify album associated with songs

3. **testGetAlbum()**
   - Create album
   - GET /api/albums/{id}
   - Verify AlbumDto returned

4. **testUpdateAlbum()**
   - Create album
   - PUT /api/albums/{id}
   - Verify updates persisted

5. **testDeleteAlbum()**
   - Create album with songs
   - DELETE /api/albums/{id}
   - Verify album deleted
   - Verify songs still exist

6. **testGetAllAlbums()**
   - Create multiple albums
   - GET /api/albums
   - Verify Page<AlbumListDto> returned

7. **testGetAlbumsByArtist()**
   - Create artist with albums
   - GET /api/artists/{artistId}/albums
   - Verify correct albums returned

#### Task 24: Search E2E Tests
**Objective**: Test unified search functionality using database view

**Test Class**: `SearchE2ETest.java`

**Test Methods**:
1. **testSearchAllEntities()**
   - Create artist "Queen", song "Bohemian Rhapsody", album "A Night at the Opera"
   - GET /api/search?q=queen
   - Verify all three entities returned in SearchResultDto list
   - Verify correct entityType for each result

2. **testSearchCaseInsensitive()**
   - Create entities with mixed case names
   - GET /api/search?q=QUEEN (uppercase)
   - Verify case-insensitive matching works

3. **testSearchByEntityType()**
   - Create multiple entity types
   - GET /api/search?q=music&type=SONG
   - Verify only songs returned

4. **testSearchByArtistName()**
   - Create songs/albums by different artists
   - GET /api/search?artist=Queen
   - Verify only entities by Queen returned

5. **testSearchPagination()**
   - Create 50 entities matching search term
   - GET /api/search?q=test&page=0&size=20
   - Verify page metadata correct (totalElements, totalPages, etc.)
   - GET /api/search?q=test&page=1&size=20
   - Verify second page contains different results

6. **testSearchSubstringMatching()**
   - Create "Bohemian Rhapsody"
   - GET /api/search?q=rhapsody
   - Verify partial match works

7. **testSearchNoResults()**
   - GET /api/search?q=nonexistent
   - Verify empty results with correct page structure

8. **testSearchResultFormat()**
   - Create and search for entities
   - Verify SearchResultDto contains: id, entityType, name, artistName, releaseDate
   - Verify artistName populated for songs and albums
   - Verify artistName equals name for artists

#### Task 25: Pagination E2E Tests
**Objective**: Comprehensive pagination testing across all list endpoints

**Test Class**: `PaginationE2ETest.java`

**Test Methods**:
1. **testArtistsPagination()**
   - Create 50 artists
   - GET /api/artists?page=0&size=20
   - Verify first page (20 items, page 0)
   - GET /api/artists?page=1&size=20
   - Verify second page (20 items, page 1)
   - GET /api/artists?page=2&size=20
   - Verify third page (10 items, page 2)

2. **testSongsPagination()**
   - Create 30 songs
   - Test pagination with different page sizes (10, 20, 50)
   - Verify page metadata accuracy

3. **testAlbumsPagination()**
   - Create 25 albums
   - Test pagination
   - Verify totalElements, totalPages correct

4. **testSongsByArtistPagination()**
   - Create artist with 40 songs
   - GET /api/artists/{id}/songs?page=0&size=15
   - Verify pagination works for nested resources

5. **testSearchPaginationWithFilters()**
   - Create many entities
   - GET /api/search?q=test&type=SONG&page=0&size=10
   - Verify filtered pagination works

6. **testSortingWithPagination()**
   - Create artists with different names
   - GET /api/artists?page=0&size=10&sort=name,asc
   - Verify ascending sort order
   - GET /api/artists?page=0&size=10&sort=name,desc
   - Verify descending sort order

7. **testDefaultPagination()**
   - GET /api/artists (no pagination params)
   - Verify default page size applied (20)

#### Task 26: WebSocket and JMS E2E Tests
**Objective**: Test real-time notification functionality

**Test Class**: `NotificationE2ETest.java`

**Setup**:
- Use `StompSession` for WebSocket testing
- Use `JmsTemplate` to verify JMS messages

**Test Methods**:
1. **testWebSocketConnectionAndSubscription()**
   - Connect to WebSocket endpoint
   - Subscribe to /topic/artists
   - Verify successful connection

2. **testArtistCreateNotification()**
   - Subscribe to /topic/artists
   - POST /api/artists (create new artist)
   - Verify WebSocket message received
   - Verify message contains: action=CREATE, entityType=ARTIST, entityId

3. **testArtistUpdateNotification()**
   - Subscribe to /topic/artists
   - PUT /api/artists/{id}
   - Verify WebSocket notification for UPDATE action

4. **testArtistDeleteNotification()**
   - Subscribe to /topic/artists
   - DELETE /api/artists/{id}
   - Verify WebSocket notification for DELETE action

5. **testSongNotifications()**
   - Subscribe to /topic/songs
   - Create, update, delete song
   - Verify appropriate notifications for each operation

6. **testAlbumNotifications()**
   - Subscribe to /topic/albums
   - Create, update, delete album
   - Verify notifications

7. **testJmsMessageOnCreate()**
   - POST /api/artists
   - Verify JMS message sent to artist.queue
   - Verify message format correct

8. **testJmsMessageOnUpdate()**
   - PUT /api/songs/{id}
   - Verify JMS message sent to song.queue

9. **testJmsMessageOnDelete()**
   - DELETE /api/albums/{id}
   - Verify JMS message sent to album.queue

10. **testCascadeDeleteNotifications()**
    - Create artist with songs and albums
    - Subscribe to all topics
    - DELETE /api/artists/{id}
    - Verify notifications for artist, songs, and albums deletion

---

## E2E Testing Best Practices

### Test Data Helper
Create a `TestDataHelper.java` class to encapsulate test data creation:

```java
@Component
public class TestDataHelper {
    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private AlbumRepository albumRepository;

    public Artist createArtist(String name) {
        Artist artist = new Artist();
        artist.setName(name);
        return artistRepository.save(artist);
    }

    public Song createSong(String title, Artist artist, Duration length, LocalDate releaseDate) {
        Song song = new Song();
        song.setTitle(title);
        song.setArtist(artist);
        song.setLength(length);
        song.setReleaseDate(releaseDate);
        return songRepository.save(song);
    }

    public Album createAlbum(String title, Artist artist, LocalDate releaseDate) {
        Album album = new Album();
        album.setTitle(title);
        album.setArtist(artist);
        album.setReleaseDate(releaseDate);
        return albumRepository.save(album);
    }

    public void createArtistWithSongsAndAlbums(String artistName, int songCount, int albumCount) {
        Artist artist = createArtist(artistName);

        for (int i = 0; i < songCount; i++) {
            createSong("Song " + i, artist, Duration.ofMinutes(3), LocalDate.now());
        }

        for (int i = 0; i < albumCount; i++) {
            createAlbum("Album " + i, artist, LocalDate.now());
        }
    }
}
```

### Test Configuration (application-test.properties)
```properties
# Use separate H2 database for tests
spring.datasource.url=jdbc:h2:mem:testdb_e2e
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop

# Disable WebSocket/JMS for tests (or configure test brokers)
spring.activemq.broker-url=vm://localhost?broker.persistent=false
```

### WebTestClient Usage Pattern
```java
// POST request
webTestClient.post()
    .uri("/api/artists")
    .contentType(MediaType.APPLICATION_JSON)
    .bodyValue(artistDto)
    .exchange()
    .expectStatus().isCreated()
    .expectBody(ArtistDto.class)
    .value(response -> {
        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("Queen");
    });

// GET request with pagination
webTestClient.get()
    .uri(uriBuilder -> uriBuilder
        .path("/api/artists")
        .queryParam("page", 0)
        .queryParam("size", 20)
        .build())
    .exchange()
    .expectStatus().isOk()
    .expectBody(new ParameterizedTypeReference<PageImpl<ArtistListDto>>() {})
    .value(page -> {
        assertThat(page.getTotalElements()).isEqualTo(50);
        assertThat(page.getContent()).hasSize(20);
    });

// DELETE request
webTestClient.delete()
    .uri("/api/artists/{id}", artistId)
    .exchange()
    .expectStatus().isNoContent();

// Verify database state
assertThat(artistRepository.findById(artistId)).isEmpty();
```

### E2E Testing Strategy Summary

**Why E2E Testing with WebTestClient:**
- Tests the complete request/response cycle through the actual HTTP layer
- Validates JSON serialization/deserialization of DTOs
- Ensures proper HTTP status codes and response formats
- Tests pagination metadata and page navigation
- Validates error responses and exception handling
- Verifies entity relationships and cascade operations
- Tests real-time features (WebSocket, JMS)

**Test Organization:**
- **BaseE2ETest**: Common setup, database cleanup, shared utilities
- **TestDataHelper**: Reusable methods for creating test data
- **Dedicated test classes**: One per major feature area (Artist, Song, Album, Search, Pagination, Notifications)
- **Isolated tests**: Each test method is independent with its own setup/cleanup

**Test Coverage:**
- ✅ All CRUD endpoints (Create, Read, Update, Delete)
- ✅ All list endpoints with pagination
- ✅ Relationship management (songs on albums, cascade deletes)
- ✅ Search functionality across all entity types
- ✅ Pagination edge cases (empty results, single page, multiple pages)
- ✅ Sorting with pagination
- ✅ Error scenarios (404, 400, validation errors)
- ✅ WebSocket notification delivery
- ✅ JMS message publishing
- ✅ Database view queries (SearchResult)

**Running Tests:**
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ArtistE2ETest

# Run tests with coverage report
mvn test jacoco:report
```

---

## DTO Usage Examples

### Entity DTO (for single GET/POST/PUT):
```json
// GET /api/songs/1 response (SongDto):
{
  "id": 1,
  "title": "Bohemian Rhapsody",
  "length": "PT5M55S",
  "releaseDate": "1975-10-31",
  "artistId": 1,
  "albumIds": [1, 5]
}
```

### List DTO with Reference DTOs (for list/search responses):
```json
// GET /api/songs response (Page<SongListDto>):
{
  "content": [
    {
      "id": 1,
      "title": "Bohemian Rhapsody",
      "length": "PT5M55S",
      "releaseDate": "1975-10-31",
      "artist": {
        "id": 1,
        "name": "Queen"
      },
      "albums": [
        {
          "id": 1,
          "title": "A Night at the Opera"
        },
        {
          "id": 5,
          "title": "Greatest Hits"
        }
      ]
    }
  ],
  "totalElements": 100,
  "totalPages": 5,
  "currentPage": 0,
  "pageSize": 20
}
```

### Album List DTO Example:
```json
// GET /api/albums/1/songs response (Page<SongRefDto>):
{
  "content": [
    {
      "id": 1,
      "title": "Bohemian Rhapsody"
    },
    {
      "id": 2,
      "title": "You're My Best Friend"
    }
  ],
  "totalElements": 12,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20
}
```

### Search Result DTO Example:
```json
// GET /api/search?q=queen response (Page<SearchResultDto>):
{
  "content": [
    {
      "id": 1,
      "entityType": "ARTIST",
      "name": "Queen",
      "artistName": "Queen",
      "releaseDate": null
    },
    {
      "id": 1,
      "entityType": "SONG",
      "name": "Bohemian Rhapsody",
      "artistName": "Queen",
      "releaseDate": "1975-10-31"
    },
    {
      "id": 1,
      "entityType": "ALBUM",
      "name": "A Night at the Opera",
      "artistName": "Queen",
      "releaseDate": "1975-11-21"
    }
  ],
  "totalElements": 3,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20
}
```

---

## API Endpoint Summary

### Artist Endpoints
- `GET /api/artists` - List all artists
- `GET /api/artists/{id}` - Get artist details
- `POST /api/artists` - Create artist
- `PUT /api/artists/{id}` - Update artist
- `DELETE /api/artists/{id}` - Delete artist
- `GET /api/artists/{id}/songs` - Get artist's songs
- `GET /api/artists/{id}/albums` - Get artist's albums

### Song Endpoints
- `GET /api/songs` - List all songs
- `GET /api/songs/{id}` - Get song details
- `POST /api/songs` - Create song
- `PUT /api/songs/{id}` - Update song
- `DELETE /api/songs/{id}` - Delete song

### Album Endpoints
- `GET /api/albums` - List all albums
- `GET /api/albums/{id}` - Get album details
- `POST /api/albums` - Create album
- `PUT /api/albums/{id}` - Update album
- `DELETE /api/albums/{id}` - Delete album
- `GET /api/albums/{id}/songs` - Get album's songs

### Search Endpoints
- `GET /api/search?q={query}` - Search across all entity types
- `GET /api/search?q={query}&type={entityType}` - Search filtered by entity type
- `GET /api/search?artist={artistName}` - Search by artist name

---

## Technical Decisions

### Database Design
- **Many-to-Many**: Song-Album relationship via join table
- **Cascade Delete**: Artist deletion removes all associated content
- **Indexes**: On searchable fields (name, title)

### API Design
- **RESTful conventions**: Standard HTTP methods and status codes
- **DTO Pattern**: Separate API models from database entities
- **Pagination**: Consistent across all list endpoints
- **Error handling**: Standardized error response format

### DTO Architecture
- **Separation of Concerns**: Three types of DTOs for different use cases
  - **Entity DTOs** (`ArtistDto`, `SongDto`, `AlbumDto`):
    - Used for CRUD operations (Create, Update, single GET)
    - Contain only fields from the entity itself
    - Use IDs to reference related entities
    - Clean separation prevents circular dependencies

  - **Reference DTOs** (`ArtistRefDto`, `SongRefDto`, `AlbumRefDto`):
    - Lightweight objects containing just id and name/title
    - Used within List DTOs to represent related entities
    - Enable clients to display names and navigate to related resources
    - Prevent N+1 query problems by providing essential data upfront

  - **List DTOs** (`ArtistListDto`, `SongListDto`, `AlbumListDto`):
    - Used for list endpoints and search results
    - Embed Reference DTOs for related entities (not just IDs or names)
    - Contain aggregated data (counts, computed fields)
    - Optimized for display in lists with navigation capabilities

### Real-time Features
- **WebSocket**: For browser/client notifications
- **JMS**: For internal service communication
- **Event-driven**: All write operations trigger notifications

### Benefits of Reference DTO Pattern
- **Client Navigation**: Clients can easily fetch related entities using provided IDs
- **Display-Ready**: Names/titles are included for immediate display without additional API calls
- **Performance**: Reduces the number of API calls needed by the client
- **Consistency**: Standardized way to represent entity relationships across the API
- **Flexibility**: Clients can choose to use just the name for display or the ID for navigation

### Database View for Search
- **Unified Search**: Single query across all entity types using UNION ALL
- **Database-Level Optimization**: Leverage database indexing and query optimization
- **Consistency**: Search logic is defined in SQL, not scattered across multiple repositories
- **Performance**: Avoids multiple queries and in-memory merging of results
- **Simplicity**: Read-only entity with @Immutable and @Subselect annotations
- **Type Discrimination**: entity_type field allows filtering and client-side routing
- **Denormalized Data**: Pre-joins artist names for efficient searching and display

---

## Dependencies to Add to pom.xml

```xml
<!-- WebSocket Support -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>

<!-- JMS/ActiveMQ -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-activemq</artifactId>
</dependency>

<!-- ModelMapper -->
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>2.3.9</version>
</dependency>

<!-- Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Testing Dependencies -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- WebTestClient for E2E Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
    <scope>test</scope>
</dependency>

<!-- WebSocket Testing -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-websocket</artifactId>
    <scope>test</scope>
</dependency>
```

---

## Project Structure

```
src/main/java/com/interview/
├── entity/
│   ├── Artist.java
│   ├── Song.java
│   ├── Album.java
│   └── SearchResult.java              # Read-only entity for search view
├── dto/
│   ├── entity/
│   │   ├── ArtistDto.java
│   │   ├── SongDto.java
│   │   └── AlbumDto.java
│   ├── reference/
│   │   ├── ArtistRefDto.java
│   │   ├── SongRefDto.java
│   │   └── AlbumRefDto.java
│   ├── list/
│   │   ├── ArtistListDto.java
│   │   ├── SongListDto.java
│   │   └── AlbumListDto.java
│   └── search/
│       └── SearchResultDto.java       # DTO for unified search results
├── repository/
│   ├── ArtistRepository.java
│   ├── SongRepository.java
│   ├── AlbumRepository.java
│   └── SearchResultRepository.java    # Read-only repository for search view
├── service/
│   ├── ArtistService.java
│   ├── SongService.java
│   ├── AlbumService.java
│   ├── SearchService.java             # Unified search service
│   └── NotificationService.java
├── controller/
│   ├── ArtistController.java
│   ├── SongController.java
│   ├── AlbumController.java
│   └── SearchController.java
├── config/
│   ├── ModelMapperConfig.java
│   ├── WebSocketConfig.java
│   └── JmsConfig.java
└── Application.java

src/main/resources/
├── application.properties
└── database/
    └── data.sql                       # Includes search_view creation

src/test/java/com/interview/
├── config/
│   └── TestConfig.java                # Test-specific configuration
├── base/
│   ├── BaseRepositoryTest.java       # Base class for repository integration tests
│   └── BaseE2ETest.java              # Base class for E2E tests
├── repository/
│   ├── EntityMappingIntegrationTest.java        # JPA entity mapping tests
│   ├── ArtistRepositoryIntegrationTest.java     # Artist repository query tests
│   ├── SongRepositoryIntegrationTest.java       # Song repository query tests
│   ├── AlbumRepositoryIntegrationTest.java      # Album repository query tests
│   └── SearchResultRepositoryIntegrationTest.java  # Search view tests
├── e2e/
│   ├── ArtistE2ETest.java            # Artist CRUD and cascade delete tests
│   ├── SongE2ETest.java              # Song CRUD and relationship tests
│   ├── AlbumE2ETest.java             # Album CRUD and relationship tests
│   ├── SearchE2ETest.java            # Unified search functionality tests
│   ├── PaginationE2ETest.java        # Comprehensive pagination tests
│   └── NotificationE2ETest.java      # WebSocket and JMS notification tests
└── helper/
    └── TestDataHelper.java           # Helper methods for creating test data

src/test/resources/
└── application-test.properties        # Test-specific configuration
```

---

## Success Criteria

1. **All CRUD operations functional** for Artist, Song, and Album
2. **Cascade delete working** - Artist deletion removes all associated data
3. **Pagination implemented** on all list endpoints
4. **Search API functional** with database view and substring matching
5. **WebSocket notifications working** for all write operations
6. **JMS messages publishing** for internal communication
7. **Clean separation** between DTOs (Entity, Reference, List, Search)
8. **Service layer** properly abstracting business logic
9. **No direct repository access** from controllers
10. **Proper error handling** and validation
11. **Comprehensive repository integration test suite** covering JPA mappings, queries, and database view
12. **All repository integration tests passing** validating data access layer
13. **Comprehensive E2E test suite** covering all endpoints and features
14. **All E2E tests passing** with >90% code coverage

---

## Testing Checklist

### Repository Integration Tests ✅ COMPLETED
- [x] BaseRepositoryTest infrastructure set up
- [x] EntityMappingIntegrationTest: 14 test methods covering JPA mappings
- [x] ArtistRepositoryIntegrationTest: 7 test methods covering queries
- [x] SongRepositoryIntegrationTest: 6 test methods covering queries
- [x] AlbumRepositoryIntegrationTest: 3 test methods covering queries
- [x] SearchResultRepositoryIntegrationTest: 12 test methods covering search view
- [x] All repository integration tests passing (42/42)
- [x] Cascade delete operations verified
- [x] Many-to-many relationships verified
- [x] Database view queries working correctly

### Implementation Tests
- [ ] Create, read, update, delete Artist (via E2E tests)
- [ ] Create, read, update, delete Song (via E2E tests)
- [ ] Create, read, update, delete Album (via E2E tests)
- [ ] Artist cascade delete removes Songs and Albums (via E2E tests)
- [ ] List all artists with pagination (via E2E tests)
- [ ] List all songs for an artist with pagination (via E2E tests)
- [ ] List all albums for an artist with pagination (via E2E tests)
- [ ] List all songs on an album with pagination (via E2E tests)
- [ ] Search across all entities using database view (via E2E tests)
- [ ] Search filtered by entity type (ARTIST, SONG, ALBUM) (via E2E tests)
- [ ] Search by artist name (via E2E tests)
- [ ] Search results include correct entity type discrimination (via E2E tests)
- [ ] Search results properly paginated (via E2E tests)
- [ ] WebSocket notifications received on write operations (via E2E tests)
- [ ] JMS messages published on write operations (via E2E tests)
- [ ] Proper error responses for invalid requests (via E2E tests)
- [ ] Data validation working correctly (via E2E tests)

### E2E Test Suite
- [ ] BaseE2ETest and TestDataHelper implemented
- [ ] ArtistE2ETest: 10 test methods covering all Artist endpoints
- [ ] SongE2ETest: 8 test methods covering Song CRUD and relationships
- [ ] AlbumE2ETest: 7 test methods covering Album CRUD and relationships
- [ ] SearchE2ETest: 8 test methods covering unified search
- [ ] PaginationE2ETest: 7 test methods covering pagination across all endpoints
- [ ] NotificationE2ETest: 10 test methods covering WebSocket and JMS
- [ ] All E2E tests passing
- [ ] Test coverage > 90% for controllers and services

---

## Timeline Estimate

- **Phase 1** (Data Model + Search View): ✅ COMPLETED
  - JPA entities, database view, SearchResult entity, SQL scripts
- **Phase 1.5** (Repository Integration Testing): ✅ COMPLETED
  - Test infrastructure setup
  - Entity mapping tests
  - Repository query tests
  - Search view tests
- **Phase 3** (Data Access): ✅ COMPLETED
  - All repositories including SearchResultRepository
- **Phase 2** (API Layer Setup): 🔄 REMAINING - 2-3 hours
  - Entity DTOs, Reference DTOs, List DTOs, SearchResultDto, ModelMapper config
- **Phase 4** (Business Logic): 🔄 REMAINING - 4-5 hours
  - Artist, Song, Album services + SearchService
- **Phase 5** (REST APIs): 🔄 REMAINING - 2-3 hours
  - Artist, Song, Album controllers
- **Phase 6** (Search API): 🔄 REMAINING - 1-2 hours
  - SearchController implementation
- **Phase 7** (Real-time): 🔄 REMAINING - 3-4 hours
  - WebSocket, JMS configuration and integration
- **Phase 8** (End-to-End Testing): 🔄 REMAINING - 6-8 hours
  - Test infrastructure setup (1 hour)
  - Artist E2E tests (1-1.5 hours)
  - Song and Album E2E tests (1.5-2 hours)
  - Search E2E tests (1-1.5 hours)
  - Pagination E2E tests (1 hour)
  - WebSocket/JMS notification tests (1.5-2 hours)

**Total Estimate**: 26-36 hours for complete implementation with comprehensive testing
**Completed**: ~8-11 hours (Phases 1, 1.5, 3)
**Remaining**: ~18-25 hours (Phases 2, 4-8)