# Goals
- Design a CRUD API with data store using Spring Boot and in memory H2 database (pre-configured, see below)
- API should include one object with create, read, update, and delete operations. Read should include fetching a single item and list of items.
- Provide SQL create scripts for your object(s) in resources/data.sql
- Demo API functionality using API client tool

# Requirements / Decisions

# Overarching Principles
Create foundational patterns for building more endpoints - focus on concerns that might be harder to change later e.g. id scheme, soft vs hard delete etc
Aim primarily for simplicity and out of the box functionality leveraging spring libraries e.g. pagination and filter support

## Resources
- Create Vehicle as the resource/object to align with tekmetric domain
- VIN is a unique field
- use UUIDs ids
- Headers
- Location header for create requests

## Fetching Lists
- pagination - limit/offsets
- filtering

## Datastore
- Use soft delete
- Basic auditing

## Security
- Implement basic security using API key

## Testing + Demo
- Integration tests
- Open API UI - http://localhost:8080/swagger-ui/index.html
- Postman Collection

# Out of Scope 
- trace ids
- idempotency keys
- 
- More advanced Authentication + Authorization scheme
- Adding any external dependencies beyond H2
- Adding more libraries beyond H2 or testing or bumping java version target
- Other potential longer term concerns like Caching, Multi-tenancy, Rate limiting
