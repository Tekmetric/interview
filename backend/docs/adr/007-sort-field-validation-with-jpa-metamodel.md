# ADR-007: Sort Field Validation with JPA Metamodel

## Context
The paginated list endpoint accepts a `sort` query parameter. Passing a non-existent field name causes Spring Data to throw a `PropertyReferenceException`, resulting in an unstructured 500 error. We needed to validate the sort field before it reaches the repository.

## Decision
We use the Hibernate-generated JPA static metamodel (`RepairOrder_`) to define the set of allowed sort fields at compile time. The service validates the `sort` parameter against this set before constructing the `PageRequest`. Invalid fields throw an `IllegalArgumentException`, caught by the global exception handler as a 400.

## Alternatives Considered
- **Catch `PropertyReferenceException` in a global handler:** reactive, not proactive — the error message exposes entity internals and the query still executes partially.
- **Hardcoded string set:** works but drifts when entity fields are renamed or added. The metamodel keeps the set in sync with the entity automatically.
- **Enum of sortable fields:** type-safe but requires manual maintenance and doesn't leverage existing JPA tooling.

## Consequences
- **Compile-time safety:** if a field is removed from the entity, the metamodel constant disappears and the code fails to compile.
- **Clear error messages:** the 400 response lists all allowed sort values, giving the client actionable feedback.
- **Build dependency:** requires `hibernate-processor` as an annotation processor. Adds a build step but no runtime dependency.
