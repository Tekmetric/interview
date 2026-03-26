# Concreete Requirements / Decisions
# Overarching Principles
Create some initial foundational patterns for building more endpoints 
Aim primarily for simplicity and out of the box functionality leveraging spring libraries e.g. pagination and filter support
## Resources
- Create Vehicle as the resource/object to align with tekmetric domain
- VIN is a unique field
- use UUIDs ids
- Headers
- Location header for create requests
- PUT vs PATCH
## Fetching Lists
- pagination - limit/offsets
- filtering

## Datastore
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
- soft delete
- More advanced Authentication + Authorization scheme
- Adding any external dependencies beyond H2
- Adding more libraries beyond H2 or testing or bumping java version target
- Other potential longer term concerns like Caching, Multi-tenancy, Rate limiting
