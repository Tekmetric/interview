## 2.0.0 - 2025-10-30

- DB migrations via Liquibase
- Swagger/OpenAPI support via Springdoc
- Consistent code formatting via Spotless
- Test coverage check using JaCoCo
- Coding rules check using ArchUnit
- Added QueryDsl to support type-safe query building
- Added MapStruct to generate mappers between DTO and ORM models
- Added Lombok to reduce boilerplate
- Added Spring actuator for monitoring
- Initial implementation of `Product` entity
- Added `Money` utility type to support product "price"
  - Basically lightweight alternative to [JSR-354](https://github.com/JavaMoney/jsr354-api)
