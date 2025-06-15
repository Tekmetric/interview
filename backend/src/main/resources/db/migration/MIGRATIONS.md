# Database Migrations Guide

This project uses Flyway for database migration management. Migrations are essential for tracking database schema changes over time in a version-controlled manner.

## Migration Files Location

All migration files are stored in:
```
src/main/resources/db/migration
```

## Naming Convention

Flyway uses a specific naming convention for migration files:
```
V{version}__{description}.sql
```

- `{version}`: A version number, such as 1, 2, 2.1, etc.
- `{description}`: A description with words separated by underscores

Examples:
- `V1__Create_running_event_table.sql`
- `V2__Insert_sample_data.sql`
- `V3__Add_participant_count_column.sql`

## Creating a New Migration

When you need to make schema changes, follow these steps:

1. Create a new SQL file in the migrations directory with the next version number
2. Write your SQL statements in the file
3. Run the application - Flyway will automatically apply the migration

## Best Practices

1. **Never modify existing migration files** once they have been committed to version control or run on any environment.
2. **Keep migrations small and focused** on a specific change.
3. **Separate schema changes from data changes** into different migration files when possible.
4. **Test migrations thoroughly** before deploying them.
5. **Include rollback steps in comments** for critical migrations.

## Example: Adding a New Column

If you need to add a new column to the `running_event` table:

1. Create a new file: `V3__Add_difficulty_level_column.sql`
2. Add SQL:
```sql
ALTER TABLE running_event ADD COLUMN difficulty_level VARCHAR(20);
```

## Checking Migration Status

To check what migrations have been applied, run:
```
./mvnw flyway:info
```

## Manually Running Migrations

To manually run migrations:
```
./mvnw flyway:migrate
```

## Additional Resources

- [Flyway Documentation](https://flywaydb.org/documentation/)