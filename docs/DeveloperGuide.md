# Developer Guide

## Local Loop

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

## Package Map

| Package | Purpose |
| --- | --- |
| `config` | Framework configuration |
| `controller` | REST and MVC adapters |
| `dto` | Request and response models |
| `engine` | Forensics and reconstruction helpers |
| `entity` | JPA persistence model |
| `exception` | API error handling |
| `repository` | Data access |
| `security` | JWT and role infrastructure |
| `service` | Business workflows |

## Adding a Feature

1. Define the endpoint or UI workflow.
2. Add or update DTOs.
3. Implement business logic in a service.
4. Persist through repositories.
5. Add tests for behavior and authorization.
6. Update docs and screenshots when visible behavior changes.

## Testing

Prefer focused unit tests for service logic and Spring tests for integration-sensitive behavior.
