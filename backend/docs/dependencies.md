# Dependencies
Documentation for 3rd party dependencies. Not including spring-boot bundled dependencies.

## UUID creator
com.github.f4b6a3:uuid-creator
https://github.com/f4b6a3/uuid-creator

### Purpose
Provide a v7 UUID implementation.
UUID v7 is preferred over UUID v4 to improve db index performance.
### Notes
Can be replaced with built-in JDK methods in the next java release which will support UUID v7.

## MapStruct
org.mapstruct:mapstruct
https://mapstruct.org/

### Purpose
Compile-time code generation for type-safe bean mapping between layers.
Alternatives exist, but none of them are as good as MapStruct(less boilerplate, compile time code generation).

### Notes
Requires the `mapstruct-processor` annotation processor configured in `maven-compiler-plugin`.


# Future enhancements
Flyway for database migrations.
Postgres for database.
Testcontainers instead of embedded db.
Archunit enforce layer architecture and other best practices. 