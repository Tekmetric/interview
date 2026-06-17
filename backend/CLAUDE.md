# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot backend API for a music application database management system. It's an interview exercise template where candidates implement a microservice following specific architectural patterns.

## Essential Commands

### Build and Run
```bash
# Build the application
mvn package

# Run the application
java -jar target/interview-1.0-SNAPSHOT.jar

# Combined build and run
mvn package && java -jar target/interview-1.0-SNAPSHOT.jar

# Verify application is running
curl -X GET http://localhost:8080/api/welcome
```

### Database Access
- H2 Console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## Architecture & Implementation Guidelines

### Domain Model
The system manages three interconnected entities:

1. **Artist** - Simple entity with name field
2. **Song** - Associated to Artist (required) and Albums (optional). Fields: title, length (Duration), release date
3. **Album** - Collection of songs. Fields: title, release date

Key relationships:
- Songs can belong to multiple albums or be singles (no album)
- Deleting an Artist cascades to delete all associated Songs and Albums

### Required Architecture Patterns

1. **Layered Architecture** (mandatory):
   - REST Controllers → Service Layer → Repository Layer
   - Never access repositories directly from controllers
   - Use DTOs for API layer, separate from JPA entities
   - Use ModelMapper for DTO ↔ Entity translation

2. **Spring Patterns**:
   - Spring Data JPA with Repository pattern
   - H2 in-memory database
   - Spring Boot 2.2.1 with Java 8

3. **Real-time Features**:
   - WebSocket notifications on all write operations (POST, PUT, DELETE)
   - JMS messaging for internal system notifications
   - Embedded lightweight message broker

### API Requirements

Each entity needs:
- CRUD endpoints (GET, POST, PUT, DELETE)
- List endpoints with paging support
- Search endpoint with substring matching across artist names, song titles, and album titles

Specific list APIs required:
- All artists
- All albums for an artist
- All songs for an artist
- All songs on an album

### Project Structure

```
src/main/java/com/interview/
├── entity/          # JPA entities (Artist, Album, Song)
├── dto/             # DTOs for API layer
├── repository/      # Spring Data JPA repositories
├── service/         # Business logic layer
├── controller/      # REST controllers
└── config/          # Configuration (WebSocket, JMS, ModelMapper)

src/main/resources/
├── application.properties  # H2 and Spring configuration
└── database/data.sql      # SQL initialization scripts
```

## Current Implementation Status

The project is a starter template with:
- Basic Spring Boot setup configured
- H2 database configured but no schema
- Single welcome endpoint at `/api/welcome`
- Empty SQL initialization file

All domain logic, entities, services, and APIs need to be implemented according to the requirements in `docs/Requirements.md`.

## Technology Stack

- Java 1.8
- Spring Boot 2.2.1.RELEASE
- Spring Data JPA 2.1.4.RELEASE
- H2 Database 2.1.210
- Maven 3.9.5