# Database

The application currently uses Spring Data JPA with `ddl-auto=update` for local development. For production, set `JPA_DDL_AUTO=validate` and manage migrations with a migration tool such as Flyway or Liquibase.

See `docs/database-schema.md` for the current entity relationship diagram.
