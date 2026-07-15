# ADR-001: Layered Architecture over Hexagonal (Ports and Adapters)

## Context
We need a CRUD API for a repair order management system with three related entities (Customer, RepairOrder, LineItem). The application uses Spring Boot with an in-memory H2 database. We considered hexagonal architecture with a separate domain layer and repository abstractions, as well as a command-based approach for service input contracts.

## Decision
We chose a standard layered architecture (controller, service, repository) over a hexagonal (ports and adapters) approach. JPA entities serve as the domain model — there is no separate domain POJO layer, no port interfaces, and no custom repository abstractions. Spring Data repositories are used directly by services.

## Alternatives Considered
- **Hexagonal (ports and adapters):** separate domain POJOs, repository port interfaces, adapter implementations wrapping Spring Data. Provides full persistence independence but adds significant boilerplate for a single-entity CRUD scope.
- **Layered with command objects:** service accepts command/query objects instead of entities. Gives the service its own input contract but creates a redundant mapping layer when the controller already uses DTOs.
- **Layered with domain POJO layer:** separate domain model mapped to/from JPA entities. If the service owns the mapping, separation is leaky; if the repository owns it, we're back to hexagonal complexity.

## Consequences
- **Simple and readable:** fewer classes, straightforward data flow, easy to review quickly.
- **Coupled to JPA:** the service layer is aware of JPA entities. In a larger system with complex business rules or multiple persistence backends, we would extract a domain layer.
- **Mapping at the service boundary:** MapStruct handles entity-to-DTO conversion in the service layer. DTOs exist only at the API boundary — no separate domain POJO layer.
- **Tradeoff acknowledged:** this is a deliberate scope-driven decision, not a lack of awareness. For a production system with richer domain logic, a hexagonal or ports-and-adapters approach would be more appropriate.
