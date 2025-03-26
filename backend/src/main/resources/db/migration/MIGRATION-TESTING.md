# Database Migration Testing Guide

This project includes a comprehensive testing strategy for database migrations. This document explains the approach and how to run the tests.

## Migration Testing Strategy

The migration testing approach includes:

1. **Schema Verification**: Tests that verify the database schema is correct after migrations are applied
2. **Data Verification**: Tests that verify sample data is correctly populated
3. **Integration Testing**: Tests that verify the application works correctly with the migrated schema

## Running Migration Tests Locally

### Using Maven

```bash
# Clean the database, apply all migrations, and run verification tests
./mvnw clean test -P migration-test
```

### Manual Testing Steps

1. Clean the database:
   ```
   ./mvnw flyway:clean -P migration-test
   ```

2. Apply migrations:
   ```
   ./mvnw flyway:migrate -P migration-test
   ```

3. Run verification tests:
   ```
   ./mvnw test -P migration-test
   ```

## CI/CD Integration

Migration tests run automatically in the CI/CD pipeline when:

1. Changes are made to migration files (`src/main/resources/db/migration/**`)
3. Code is pushed to the master branch

### CI Pipeline Steps

1. Clean the test database
2. Apply all migrations from scratch
3. Run schema and data verification tests
4. Run all application tests to ensure compatibility

## Adding New Migrations

When adding new migrations:

1. Create a new migration file in `src/main/resources/db/migration` following the naming convention `V{version}__{description}.sql`
2. Run the migration tests locally to verify your changes
3. Update the `DatabaseMigrationVerificationTest` if your migration adds new tables, columns, or indices

## Troubleshooting

### Common Issues

1. **Validation Errors**: If you see validation errors in the tests, it usually means your migration script doesn't match your JPA entities. Update either the migration script or the entities to fix.

2. **CI Pipeline Failure**: If the migration test fails in CI but works locally:
    - Check if all migration files are committed
    - Verify environment-specific configuration is properly handled

3. **Version Conflicts**: If you see Flyway errors about "already applied" migrations, you may need to clean the database first or update the version number.

## Best Practices

1. Always include both "up" migrations (changes) and "down" migrations (rollbacks) when possible
2. Keep migrations small and focused
3. Separate schema changes from data changes
4. Test all migrations thoroughly before deploying to production