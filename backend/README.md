# Music Library API

A comprehensive RESTful API for managing a music library with artists, songs, and albums. Built with Spring Boot, featuring real-time notifications, extensive test coverage, and a full-featured command-line interface.

## Table of Contents

- [Quick Start](#quick-start)
- [Features](#features)
- [Architecture Overview](#architecture-overview)
- [For End Users](#for-end-users)
- [For Developers](#for-developers)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Design Decisions](#design-decisions)

## Quick Start

### Prerequisites
- **Java 11+** (required)
- **Maven 3.6+**
- **jq** (for CLI and demos)

### Running the Application

```bash
# Build and run
mvn spring-boot:run

# Or build and run as JAR
mvn package && java -jar target/interview-1.0-SNAPSHOT.jar
```

The API will be available at `http://localhost:8080`

### Verify Installation

```bash
# Check API health
curl http://localhost:8080/api/artists

# Or use the CLI
cd ../music-cli
./bin/music-artist list
```

### Run the Demo

```bash
# Terminal 1: Start the API
mvn spring-boot:run

# Terminal 2: Run comprehensive demo
cd ../demo
./demo-cli.sh    # CLI-based demo
# or
./demo-curl.sh   # HTTP/cURL-based demo
```

## Features

### Core Functionality
- **Full CRUD Operations** - Create, Read, Update, Delete for Artists, Songs, and Albums
- **Rich Entity Relationships** - Many-to-many relationships between Songs and Albums
- **Advanced Querying** - Pagination, sorting, filtering by multiple criteria
- **Full-Text Search** - Search across all entity types with type filtering
- **Real-time Notifications** - WebSocket-based change notifications via JMS
- **Data Validation** - Comprehensive input validation and error handling

### API Capabilities
- RESTful endpoints following best practices
- Paginated responses for list operations
- Relationship management through dedicated endpoints
- Cascade operations with proper referential integrity
- Flexible querying with sorting and filtering

### Developer Tools
- **Command-Line Interface** - Full-featured CLI for all operations (see `../music-cli/`)
- **Comprehensive Test Suite** - Unit, integration, and E2E tests
- **Demo Scripts** - Automated demonstrations of all features
- **H2 Console** - Database inspection at `http://localhost:8080/h2-console`

## Architecture Overview

### Technology Stack
- **Spring Boot 2.2.1** - Application framework
- **Spring Data JPA** - Data access layer
- **Hibernate 5.4.8** - ORM implementation
- **H2 Database** - In-memory database (MODE=LEGACY for MySQL compatibility)
- **ActiveMQ** - JMS message broker for notifications
- **WebSocket (STOMP)** - Real-time client notifications
- **JUnit 5** - Testing framework

### Project Structure

```
backend/
├── src/main/java/com/interview/
│   ├── controller/      # REST endpoints
│   ├── service/         # Business logic layer
│   ├── repository/      # Data access layer (Spring Data JPA)
│   ├── entity/          # JPA entities with bidirectional relationships
│   ├── dto/             # Data Transfer Objects
│   ├── config/          # Spring configuration (JMS, WebSocket)
│   └── Application.java # Main entry point
├── src/main/resources/
│   ├── data.sql         # Seed data
│   └── application.properties
├── src/test/
│   ├── java/.../        # Unit and integration tests
│   └── resources/       # Test configurations
└── docs/                # Architecture and requirements documentation
```

### Design Patterns
- **Service Layer Pattern** - Business logic isolated from controllers
- **Repository Pattern** - Data access abstraction via Spring Data JPA
- **DTO Pattern** - Clean API contracts separate from domain entities
- **Defensive Copying** - Entities return unmodifiable collections
- **Transaction Management** - Proper ACID guarantees for operations

## For End Users

### Using the API

#### REST API
Full API documentation with examples: [docs/Requirements.md](docs/Requirements.md)

**Quick Examples:**

```bash
# List all artists
curl http://localhost:8080/api/artists

# Create an artist
curl -X POST http://localhost:8080/api/artists \
  -H "Content-Type: application/json" \
  -d '{"name":"The Beatles"}'

# Get artist with songs
curl http://localhost:8080/api/artists/1/songs

# Search across all entities
curl http://localhost:8080/api/search?q=love&type=SONG
```

#### Command-Line Interface
A full-featured CLI is available for all operations.

**Documentation:** `../music-cli/README.md`

**Quick Examples:**

```bash
# Navigate to CLI directory
cd ../music-cli

# List artists
./bin/music-artist list

# Create a song
./bin/music-song create \
  --title "Hey Jude" \
  --length 431 \
  --release-date 1968-08-26 \
  --artist-id 1

# Search for content
./bin/music-search "Beatles"

# Get JSON output for scripting
./bin/music-artist --json list | jq '.content[].name'
```

**CLI Features:**
- Interactive and non-interactive modes
- Formatted table output or JSON
- Configuration management
- Pagination and sorting support
- Comprehensive help system

#### Demo Scripts
Comprehensive demonstrations of all API features:

**Location:** `../demo/`
- `demo-cli.sh` - CLI-based demonstration
- `demo-curl.sh` - HTTP/cURL-based demonstration

**What they demonstrate:**
- All CRUD operations
- Relationship management
- Search functionality
- Pagination and sorting
- Data validation
- Error handling

### WebSocket Notifications

Subscribe to real-time notifications for entity changes:

```javascript
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, () => {
  // Subscribe to artist changes
  stompClient.subscribe('/topic/artists', (message) => {
    console.log('Artist event:', JSON.parse(message.body));
  });

  // Subscribe to song changes
  stompClient.subscribe('/topic/songs', (message) => {
    console.log('Song event:', JSON.parse(message.body));
  });
});
```

## For Developers

### Prerequisites
- Java 11+ (required)
- Maven 3.6+
- IDE with Spring Boot support (IntelliJ IDEA recommended)

### Development Setup

```bash
# Clone and navigate to project
cd backend

# Import into IDE as Maven project
# Project root: backend/

# Install dependencies
mvn clean install

# Run in development mode
mvn spring-boot:run

# Run with debug port 5005
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ArtistServiceTest

# Run integration tests only
mvn test -Dtest=*IntegrationTest

# Run E2E tests only
mvn test -Dtest=NotificationE2ETest

# Generate coverage report
mvn test jacoco:report
# View at: target/site/jacoco/index.html
```

**Test Coverage:**
- **42 Integration Tests** - Repository and service layer testing
- **9 E2E Tests** - End-to-end WebSocket notification testing
- **Transactional Isolation** - Clean database state per test
- **Test Data Management** - Separate test profiles and data

### Building for Production

```bash
# Build JAR
mvn package

# Run JAR
java -jar target/interview-1.0-SNAPSHOT.jar

# Build with specific profile
mvn package -Pprod

# Skip tests for faster build
mvn package -DskipTests
```

### Database Access

#### H2 Console
- **URL:** http://localhost:8080/h2-console
- **JDBC URL:** `jdbc:h2:mem:testdb;MODE=LEGACY`
- **Username:** `sa`
- **Password:** `password`

#### Seed Data
Initial data is loaded from `src/main/resources/data.sql`:
- 2 Artists (Queen, The Beatles)
- 5 Songs
- 5 Albums

### Key Configuration Files

**`application.properties`** - Main configuration
- Database connection (H2 in-memory)
- JPA/Hibernate settings
- JMS configuration
- WebSocket configuration

**`application-test.properties`** - Test overrides
- Test database isolation
- Disabled data.sql loading for unit tests

**`data.sql`** - Database initialization
- Schema creation (automatic via Hibernate)
- Seed data for development and demo

### Adding New Features

#### Example: Adding a New Entity

1. **Create Entity** (`src/main/java/com/interview/entity/`)
```java
@Entity
@Table(name = "playlists")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "playlist_songs",
        joinColumns = @JoinColumn(name = "playlist_id"),
        inverseJoinColumns = @JoinColumn(name = "song_id"))
    private List<Song> songs = new ArrayList<>();

    // Getters, setters, helper methods
}
```

2. **Create Repository** (`src/main/java/com/interview/repository/`)
```java
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByNameContaining(String name);
}
```

3. **Create Service** (`src/main/java/com/interview/service/`)
```java
@Service
public class PlaylistService {
    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    public Playlist createPlaylist(Playlist playlist) {
        Playlist saved = playlistRepository.save(playlist);
        jmsTemplate.convertAndSend("playlist.queue",
            new EntityChangeMessage("CREATE", "PLAYLIST", saved.getId()));
        return saved;
    }
}
```

4. **Create Controller** (`src/main/java/com/interview/controller/`)
```java
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {
    @Autowired
    private PlaylistService playlistService;

    @GetMapping
    public Page<Playlist> getPlaylists(Pageable pageable) {
        return playlistService.getAllPlaylists(pageable);
    }
}
```

5. **Add Tests** (`src/test/java/com/interview/`)
- Unit tests for service layer
- Integration tests for repository
- E2E tests for WebSocket notifications

### Contributing Guidelines

**Code Style:**
- Follow existing patterns and conventions
- Use meaningful variable and method names
- Add JavaDoc for public APIs
- Keep methods focused and single-purpose

**Testing:**
- Write tests for new features
- Maintain test coverage above 80%
- Use transactional tests for database operations
- Test both success and error scenarios

**Commits:**
- Write clear, descriptive commit messages
- One logical change per commit
- Reference issue numbers when applicable

**Pull Requests:**
- Include description of changes
- Link to related issues
- Ensure all tests pass
- Update documentation as needed

## API Documentation

### Complete API Reference
See [docs/Requirements.md](docs/Requirements.md) for:
- All endpoints with request/response examples
- Query parameters and filters
- Error responses and status codes
- Pagination and sorting details
- WebSocket notification format

### CLI Documentation
See `../music-cli/README.md` for:
- Installation and configuration
- Command reference
- Usage examples
- Configuration management
- Scripting and automation

### Implementation Plans
See [docs/CLI-Implementation-Plan.md](docs/CLI-Implementation-Plan.md) and [docs/CLI-Requirements.md](docs/CLI-Requirements.md) for detailed specifications.

## Testing

### Test Structure

```
src/test/java/com/interview/
├── controller/              # Controller unit tests (future)
├── service/                 # Service unit tests (future)
├── repository/              # Integration tests (42 tests)
│   ├── ArtistRepositoryIntegrationTest.java
│   ├── SongRepositoryIntegrationTest.java
│   ├── AlbumRepositoryIntegrationTest.java
│   ├── EntityMappingIntegrationTest.java
│   └── SearchIntegrationTest.java
└── e2e/                     # End-to-end tests (9 tests)
    └── NotificationE2ETest.java
```

### Running Specific Test Suites

```bash
# All tests
mvn test

# Repository integration tests
mvn test -Dtest=*RepositoryIntegrationTest

# Relationship mapping tests
mvn test -Dtest=EntityMappingIntegrationTest

# E2E notification tests
mvn test -Dtest=NotificationE2ETest

# Specific test method
mvn test -Dtest=ArtistRepositoryIntegrationTest#testFindByNameContaining
```

### Test Profiles

**`application-test.properties`** - Used by integration tests
- Isolated test database
- Disabled seed data loading
- SQL logging enabled

**`application-e2e.properties`** - Used by E2E tests
- Full application context
- JMS and WebSocket enabled
- Seed data loaded

### Known Issues

**Defensive Collection Testing:**
- Entities return `Collections.unmodifiableList()` for collections
- Tests use helper methods (`addSong()`, `addAlbum()`) instead of direct collection manipulation
- This prevents `UnsupportedOperationException` and enforces proper relationship management

**JMS Queue Cleanup:**
- E2E tests clear JMS queues in `@BeforeEach` to prevent message pollution
- Critical for test isolation and preventing flaky tests

## Design Decisions

### Entity Relationship Design

**Bidirectional Many-to-Many:** Songs ↔ Albums
- Managed on both sides with helper methods
- Ensures referential integrity
- Uses `@JoinTable` on owning side (Album)

**Unidirectional Many-to-One:** Songs/Albums → Artist
- Simplified by business logic (songs/albums belong to one artist)
- Reverse navigation via service layer methods

**Defensive Copying:**
- All collection getters return `Collections.unmodifiableList()`
- Prevents external modification of internal state
- Helper methods (`addSong()`, `removeSong()`) manage relationships
- Package-private accessors (`getAlbumsInternal()`) for bidirectional updates

### Service Layer Patterns

**Transaction Management:**
- `@Transactional` on service methods
- ACID guarantees for complex operations
- Rollback on exceptions

**Event Broadcasting:**
- JMS messages sent after successful persistence
- Asynchronous notification to WebSocket subscribers
- Message format: `{"action":"CREATE|UPDATE|DELETE", "entityType":"ARTIST|SONG|ALBUM", "entityId":123}`

**Cascade Operations:**
- Artist deletion cascades to songs and albums
- Song deletion removes from albums (albums remain)
- Album deletion removes references (songs remain)

### API Design Choices

**Pagination:**
- Default page size: 20
- Configurable via `page` and `size` parameters
- Returns Spring `Page<T>` with metadata

**Sorting:**
- Format: `field,direction` (e.g., `name,asc`)
- Multi-field sorting supported
- Default sorting on primary keys

**Search:**
- Case-insensitive partial matching
- Searches across name/title fields
- Type filtering (ARTIST, SONG, ALBUM)
- Returns unified result format

**Error Handling:**
- Consistent error response format
- Proper HTTP status codes
- Descriptive error messages
- Validation errors include field details

### Database Configuration

**H2 in MODE=LEGACY:**
- MySQL compatibility mode
- Allows implicit data type conversions
- Compatible with typical SQL patterns
- Trade-off: Less strict than pure H2 mode

**Connection Pooling:**
- HikariCP (Spring Boot default)
- Fast, lightweight, production-ready
- Automatic configuration

### Testing Strategy

**Test Isolation:**
- `@Transactional` on test classes
- Automatic rollback after each test
- Clean slate for every test method

**Test Data Management:**
- Integration tests use `@BeforeEach` setup
- No dependency on seed data
- Explicit test data creation

**E2E Testing:**
- Full Spring context with WebSocket
- JMS queue cleanup prevents test pollution
- Real message broker (ActiveMQ embedded)

### Why These Choices?

**Spring Boot 2.2.1:**
- Stable LTS version
- Excellent documentation
- Wide ecosystem support

**JPA + Hibernate:**
- Industry standard ORM
- Reduces boilerplate
- Type-safe queries
- Automatic schema generation

**H2 Database:**
- Zero configuration
- Perfect for development and testing
- Fast startup and teardown
- Easy transition to production database

**JMS + WebSocket:**
- Decoupled notification system
- Scalable to multiple instances
- Standard protocols
- Easy client integration

**Command-Line Interface:**
- Developer-friendly
- Scriptable and automatable
- Testing without GUI tools
- CI/CD integration friendly

## License

This project is part of an interview coding exercise.

## Additional Resources

- [Project Requirements](docs/Requirements.md) - Original specifications
- [CLI Documentation](../music-cli/README.md) - Command-line interface guide
- [Demo Scripts](../demo/README.md) - Comprehensive feature demonstrations
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/reference/html/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
