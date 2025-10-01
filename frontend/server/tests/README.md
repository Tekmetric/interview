# Server Tests

Comprehensive test suite for the frontend/server using Vitest and in-memory SQLite.

## Quick Start

```bash
# Run all tests
pnpm test

# Run tests in watch mode
pnpm test

# Run tests once
pnpm test:run

# Run tests with UI
pnpm test:ui

# Run tests with coverage
pnpm test:coverage
```

## Test Structure

```
server/
├── tests/
│   ├── test-utils.ts          # Shared test utilities
│   └── README.md              # This file
├── core/
│   └── middleware/
│       └── tests/
│           └── validate.test.ts (11 tests)
├── domains/
│   ├── technicians/
│   │   └── tests/
│   │       └── repository.test.ts (12 tests)
│   └── repair-orders/
│       └── tests/
│           ├── repository.test.ts (20 tests)
│           └── transforms.test.ts (7 tests)
└── shared/
    └── tests/
        └── transitions.test.ts (25 tests)
```

## Test Coverage

**Total: 75 tests, all passing**

### By Category

1. **Business Logic** (25 tests)
   - Status transitions and business rules
   - Critical path validation

2. **Validation Middleware** (11 tests)
   - Request data validation (body, query, params)
   - Error handling and formatting

3. **Data Layer** (39 tests)
   - Repository CRUD operations
   - Data transformations
   - Database integrity

## Test Utilities

### `createTestDb()`

Creates an in-memory SQLite database with full schema for isolated tests.

```typescript
import { createTestDb, cleanupDb } from '@server/tests/test-utils'

let db: Database

beforeEach(() => {
  db = createTestDb()
})

afterEach(() => {
  cleanupDb(db)
})
```

### `seedTestTechnicians(db)`

Seeds database with 3 test technicians (2 active, 1 inactive).

### `seedTestRepairOrders(db)`

Seeds database with 2 test repair orders with different statuses.

## Testing Patterns

### Repository Tests

- Use in-memory database with mocked db import
- Test CRUD operations with fresh DB for each test
- Verify data persistence and retrieval

### Transform Tests

- Test pure transformation functions
- Verify JSON parsing (services, specialties)
- Test type conversions (boolean, nullable fields)

### Validation Tests

- Test valid data passes through
- Test invalid data returns proper 400 errors
- Test error message format and details

## Key Testing Principles

1. **Isolation**: Each test uses a fresh in-memory database
2. **Independence**: Tests don't depend on each other
3. **Fast**: All 75 tests run in <400ms
4. **Comprehensive**: Cover happy path, edge cases, and error scenarios

## Found Issues

Tests have already caught production bugs:

- ✅ Fixed: `datetime("now")` SQL syntax error in updates
- ✅ Fixed: WAITING_PARTS→IN_PROGRESS requires tech assignment

## Next Steps (Optional)

Lower priority items not yet implemented:

- Route integration tests (using supertest)
- Performance tests for critical paths
- Coverage reporting setup
