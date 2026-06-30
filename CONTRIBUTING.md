# Contributing

Thanks for improving AegisTrace. Keep changes focused, tested, and aligned with the existing dark SOC interface.

## Development Flow

1. Create a branch for one feature or fix.
2. Run `.\mvnw.cmd test` before opening a pull request on Windows, or `./mvnw test` on Linux/macOS.
3. Keep security-sensitive values in environment variables.
4. Include screenshots only when UI behavior changes.

## Code Style

- Keep controllers thin and push business rules into services.
- Use DTOs for API request and response payloads.
- Prefer repository queries over manual in-memory filtering for growing datasets.
- Do not add hardcoded dashboard metrics; values must come from persisted data or backend aggregation.
