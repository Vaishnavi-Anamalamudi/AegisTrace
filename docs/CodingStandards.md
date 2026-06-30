# Coding Standards

## Java

- Use Java 17 language features conservatively.
- Prefer constructor injection.
- Keep controllers thin.
- Keep business logic in services.
- Use DTOs at API boundaries.
- Avoid leaking entity internals in public contracts when a DTO is appropriate.
- Use clear names over comments.
- Add comments only for non-obvious security, scanner, or persistence decisions.

## Security

- Never log passwords, tokens, secrets, or sensitive evidence.
- Validate external input.
- Treat scan targets as privileged input.
- Add role checks for privileged operations.

## Tests

- Cover authentication and authorization behavior.
- Cover scanners with mockable boundaries where possible.
- Add regression tests for defects.

## Documentation

- Update README for major user-facing changes.
- Update docs for API, schema, workflow, deployment, or security changes.
- Include screenshots for visible UI changes.
