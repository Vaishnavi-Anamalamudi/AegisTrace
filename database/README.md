# Database

This folder contains database documentation and future migration assets.

Current state:

- Runtime database: MySQL 8+
- Test database: H2
- ORM: Spring Data JPA and Hibernate
- Development schema mode: `JPA_DDL_AUTO=update`

Production target:

- Add Flyway or Liquibase.
- Move schema changes into reviewed migrations.
- Use `JPA_DDL_AUTO=validate`.
- Apply least privilege to the application database user.

See [../docs/Database.md](../docs/Database.md).
