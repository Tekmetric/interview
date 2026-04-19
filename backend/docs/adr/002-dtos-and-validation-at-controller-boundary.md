# ADR-002: DTOs and Validation at Controller Boundary

## Context
We need to define how data enters and exits the API, and where input validation occurs. Options included: exposing entities directly, using a single DTO for both directions, or using separate request/response DTOs. For validation, we considered Bean Validation on DTOs (controller level) versus validation in the service layer.

## Decision
- Separate request and response DTOs per operation (e.g. `CreateRepairOrderRequest`, `UpdateRepairOrderRequest`, `RepairOrderResponse`).
- DTOs are Java records (immutable, concise).
- Bean Validation annotations (`@NotNull`, `@Size`, etc.) on request DTOs, enforced at the controller via `@Valid`.
- The controller maps between DTOs and entities.

## Alternatives Considered
- **Expose entities directly:** no DTOs, entities serialized as-is. Simple but couples the API to the database schema and leaks internal structure.
- **Single DTO for both directions:** one class for request and response. Simpler but create and update often have different required fields, and the response may include computed/read-only fields.
- **Validation in the service layer:** validates domain objects rather than DTOs. More appropriate when a domain POJO layer exists, but in our architecture the controller is the system boundary.

## Consequences
- **Clear API contract:** request and response shapes are independent and can evolve separately.
- **Entities are never exposed:** internal structure changes don't break the API.
- **Validation at the boundary:** invalid input is rejected before reaching the service. The service can assume valid data.
- **Controller has mapping responsibility:** this is a small overhead but keeps the service focused on business logic.
- **In a larger system**, we would consider validating domain invariants in the service layer as well, especially for rules that go beyond field-level validation.
