# Unified Error Handling System

This document describes the unified error handling system implemented using Spring's `@RestControllerAdvice`.

## Overview

The application uses a centralized error handling approach that provides consistent, structured error responses across all REST endpoints.

## Components

### 1. Custom Exceptions

#### ResourceNotFoundException
Located at: `com.interview.common.exception.ResourceNotFoundException`

Used when a requested resource cannot be found.

**Usage:**
```java
throw new ResourceNotFoundException("Widget", "id", id);
// Results in: "Widget not found with id: '123'"
```

### 2. Standardized Error Response

#### ApiError
Located at: `com.interview.common.exception.ApiError`

All error responses use this consistent format:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Widget not found with id: '123'",
  "timestamp": "2025-12-09T16:49:52",
  "path": "/api/widgets/123",
  "validationErrors": {
    "fieldName": "error message"
  }
}
```

**Fields:**
- `status`: HTTP status code (e.g., 404, 400, 500)
- `error`: HTTP status reason phrase (e.g., "Not Found", "Bad Request")
- `message`: Human-readable error description
- `timestamp`: When the error occurred (ISO-8601 format)
- `path`: The request URI that caused the error
- `validationErrors`: Field-level validation errors (optional, only for validation failures)

### 3. Global Exception Handler

Located at: `com.interview.common.config.GlobalExceptionHandler`

Handles all exceptions thrown by REST controllers and provides unified responses.

## Handled Exception Types

### 1. Validation Errors (400 Bad Request)
**Exception:** `MethodArgumentNotValidException`

Triggered when `@Valid` annotation validation fails.

**Example Response:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields",
  "timestamp": "2025-12-09T16:49:52",
  "path": "/api/widgets",
  "validationErrors": {
    "name": "Name is required",
    "description": "Description must not exceed 500 characters"
  }
}
```

### 2. Resource Not Found (404 Not Found)
**Exception:** `ResourceNotFoundException`

Triggered when a requested resource doesn't exist.

**Example Response:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Widget not found with id: '123'",
  "timestamp": "2025-12-09T16:49:52",
  "path": "/api/widgets/123"
}
```

### 3. Optimistic Locking Failures (409 Conflict)
**Exception:** `ObjectOptimisticLockingFailureException`

Triggered when concurrent updates conflict (Widget entity uses `@Version`).

**Example Response:**
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "The resource was modified by another user. Please refresh and try again.",
  "timestamp": "2025-12-09T16:49:52",
  "path": "/api/widgets/123"
}
```

### 4. Data Integrity Violations (409 Conflict)
**Exception:** `DataIntegrityViolationException`

Triggered when database constraints are violated (e.g., unique constraints).

**Example Response:**
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "A record with this value already exists.",
  "timestamp": "2025-12-09T16:49:52",
  "path": "/api/widgets"
}
```

### 5. Invalid HTTP Method (405 Method Not Allowed)
**Exception:** `HttpRequestMethodNotSupportedException`

Triggered when using an unsupported HTTP method on an endpoint.

**Example Response:**
```json
{
  "status": 405,
  "error": "Method Not Allowed",
  "message": "HTTP method DELETE is not supported for this endpoint",
  "timestamp": "2025-12-09T16:49:52",
  "path": "/api/widgets"
}
```

### 6. Malformed Request Body (400 Bad Request)
**Exception:** `HttpMessageNotReadableException`

Triggered when JSON parsing fails or data format is invalid.

**Example Response:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Malformed JSON request body or invalid data format",
  "timestamp": "2025-12-09T16:49:52",
  "path": "/api/widgets"
}
```

### 7. Type Mismatch (400 Bad Request)
**Exception:** `MethodArgumentTypeMismatchException`

Triggered when path variables or parameters have invalid types.

**Example Response:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid value 'abc' for parameter 'id'",
  "timestamp": "2025-12-09T16:49:52",
  "path": "/api/widgets/abc"
}
```

### 8. Non-Existent Endpoints (404 Not Found)
**Exception:** `NoResourceFoundException`

Triggered when requesting a non-existent endpoint.

**Example Response:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "The requested endpoint does not exist",
  "timestamp": "2025-12-09T16:49:52",
  "path": "/api/nonexistent"
}
```

### 9. Unexpected Errors (500 Internal Server Error)
**Exception:** `Exception` (catch-all)

Handles any unexpected exceptions.

**Example Response:**
```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred. Please try again later.",
  "timestamp": "2025-12-09T16:49:52",
  "path": "/api/widgets/123"
}
```

## Logging

All exceptions are logged with appropriate severity levels:

- **WARN**: Business logic issues (not found, validation, conflicts, client errors)
- **ERROR**: Unexpected system errors (stack trace included)

Log format includes:
- Request URI
- Exception message
- Additional context (field names, parameter values, etc.)
- Full stack trace for unexpected errors

## Controller Usage

Controllers now throw exceptions instead of returning error ResponseEntity objects:

### Before:
```java
@PutMapping("/{id}")
public ResponseEntity<Widget> updateWidget(@PathVariable Long id,
                                          @Valid @RequestBody UpdateWidgetCommand command) {
    return updateWidgetHandler.handle(id, command)
            .map(widget -> ResponseEntity.ok(widget))
            .orElseGet(() -> ResponseEntity.notFound().build());
}
```

### After:
```java
@PutMapping("/{id}")
public ResponseEntity<Widget> updateWidget(@PathVariable Long id,
                                          @Valid @RequestBody UpdateWidgetCommand command) {
    Widget widget = updateWidgetHandler.handle(id, command)
            .orElseThrow(() -> new ResourceNotFoundException("Widget", "id", id));
    return ResponseEntity.ok(widget);
}
```

## Benefits

1. **Consistency**: All error responses follow the same structure
2. **Maintainability**: Centralized error handling logic
3. **Clarity**: Clear, descriptive error messages
4. **Debugging**: Comprehensive logging with request context
5. **Simplicity**: Controllers focus on business logic, not error formatting
6. **Type Safety**: Structured error responses are strongly typed
7. **Extensibility**: Easy to add new exception handlers

## Testing

All error scenarios are thoroughly tested:

- Unit tests verify exception handling logic
- Integration tests verify end-to-end error responses
- Tests cover all HTTP status codes and error types

## Future Enhancements

Potential improvements:
- Add error codes for client-side error categorization
- Implement internationalized error messages
- Add correlation IDs for distributed tracing
- Create custom exceptions for domain-specific errors
