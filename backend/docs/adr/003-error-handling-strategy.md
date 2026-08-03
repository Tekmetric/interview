# ADR-003: Error Handling Strategy

## Context
We need consistent, structured error responses across the API. Options included relying on Spring's default error handling, using `@ResponseStatus` on exceptions only, or a combination of controller-level and global exception handlers.

## Decision
- Domain-specific exceptions (e.g. `RepairOrderNotFoundException`) are handled via `@ExceptionHandler` methods on the respective controller.
- A global `@ControllerAdvice` handles cross-cutting concerns: Bean Validation errors, unexpected exceptions, and any unhandled cases.
- All error responses use a structured JSON format with status code and message.

## Alternatives Considered
- **Spring defaults only:** no custom handling. Returns inconsistent, verbose error bodies that expose internal details (stack traces, class names).
- **`@ResponseStatus` on exceptions only:** minimal effort but no structured error body — clients get Spring's default JSON shape which varies by error type.
- **Global `@ControllerAdvice` only:** all exceptions handled in one place. Works but domain exceptions lose locality — harder to see what errors a controller can produce.

## Consequences
- **Locality:** domain exceptions are handled close to where they originate, making controllers self-documenting.
- **Consistency:** the global handler ensures no exception leaks as an unstructured Spring default error.
- **Validation errors** are automatically formatted with field-level details, giving API consumers actionable feedback.
- **Tradeoff:** for a single entity this split is minor. In a larger system with many controllers, the pattern scales well — each controller owns its domain errors while shared concerns stay centralized.
