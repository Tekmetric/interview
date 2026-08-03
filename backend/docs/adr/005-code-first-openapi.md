# ADR-005: Code-First OpenAPI over Contract-First

## Context
We need API documentation for the repair order management system. We considered a contract-first approach (write OpenAPI YAML spec, generate controller interfaces via `openapi-generator-maven-plugin`) versus a code-first approach (write controllers, auto-generate the spec at runtime).

## Alternatives Considered
- **Contract-first (spec-driven):** define the OpenAPI spec upfront, generate server stubs. Useful when multiple teams develop against the same API contract in parallel or when the spec must be agreed upon before implementation begins. Adds tooling overhead and a separate YAML file to maintain.
- **No API documentation:** rely on the HTTP client file and README. Quick but gives the reviewer no interactive way to explore the API.

## Decision
We use springdoc-openapi to auto-generate the OpenAPI spec and Swagger UI from annotated controllers. The spec is derived from the code, not the other way around.

## Consequences
- **Zero spec maintenance:** the documentation is always in sync with the code.
- **Swagger UI available at `/swagger-ui.html`:** the reviewer can explore and test endpoints interactively.
- **No contract guarantee:** the spec follows the code, so there is no upfront agreement on the API shape. For a solo project this is acceptable; in a multi-team environment, contract-first would be preferred.
