# ADR-002: DTOs, Mapping, and Service-Level Validation

## Context
We need to define how data enters and exits the API, where input validation occurs, and how entities map to DTOs. Options included: exposing entities directly, using a single DTO for both directions, or using separate DTOs. For validation, we considered the controller boundary versus the service layer.

## Decision
- **Naming:** command DTOs for writes (`CreateRepairOrderCommand`), Dto suffix for reads (`RepairOrderSummaryDto`, `RepairOrderDetailDto`). Separate summary/detail DTOs where list and get-by-id return different shapes.
- **DTOs are Java records** (immutable, concise). `PageDto<T>` wraps paginated results with no Spring Data dependency.
- **Validation at the service layer:** `@Validated` on service classes, Bean Validation annotations (`@PositiveOrZero`, `@Range`, `@NotBlank`, `@NotNull`) on service method parameters. Controllers stay thin and Spring Data agnostic.
- **MapStruct for mapping:** mapper interfaces live in `service/`, annotated with `@Mapper(componentModel = "spring")`. Service owns the entity-to-DTO conversion.
- **Controllers accept primitives** (`int page`, `String sort`) with `@RequestParam` defaults — no `Pageable` or `Page` types leak into the API layer.

## Alternatives Considered
- **Validation at controller boundary:** `@Valid` on request DTOs. Simpler, but puts the validation contract on the API shape rather than the service contract. Service-level validation is more resilient to multiple entry points.
- **Manual mapping in controllers:** controller maps between DTOs and entities. Works but scatters mapping logic and couples controllers to entity structure.
- **Expose Spring `Page`/`Pageable` in controllers:** convenient but couples the API contract to Spring Data internals.

## Consequences
- **Service is the validation boundary:** any caller (controller, event handler, scheduled task) gets the same validation guarantees.
- **Controllers are thin:** they accept primitives, delegate to the service, and return DTOs. No mapping, no validation, no Spring Data types.
- **MapStruct eliminates boilerplate:** compile-time code generation, type-safe, integrates with Lombok via `lombok-mapstruct-binding`.
- **Clear API contract:** summary and detail DTOs can evolve independently. `PageDto` is a plain record decoupled from Spring.
