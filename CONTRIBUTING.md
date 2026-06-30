# Contributing to AegisTrace

Thank you for helping improve AegisTrace. Contributions should strengthen its defensive-security capabilities while preserving the established interface and keeping changes reviewable.

By participating, you agree to follow the [Code of Conduct](CODE_OF_CONDUCT.md). Report vulnerabilities through the private process in [SECURITY.md](SECURITY.md), never through a public issue.

## Before you start

1. Search existing issues and pull requests.
2. Open an issue before substantial architectural or behavioral work.
3. Keep each contribution focused on one concern.
4. Only scan infrastructure you own or are authorized to test.

## Development setup

Requirements and database setup are documented in [docs/Installation.md](docs/Installation.md).

```bash
git clone <your-fork-url>
cd AegisTrace
cp .env.example .env
./mvnw clean verify
```

Use `Copy-Item .env.example .env` and `.\mvnw.cmd clean verify` on Windows.

## Branches and commits

Create a descriptive branch from the default branch:

```bash
git switch -c feat/incident-timeline
```

Use concise, imperative Conventional Commit messages where practical:

- `feat(scans): add result pagination`
- `fix(security): reject inactive accounts`
- `docs(api): document scan authorization`
- `test(alerts): cover critical-port detection`

## Engineering standards

- Use constructor injection and keep controllers thin.
- Put business rules in services and persistence logic in repositories.
- Use validated DTOs at API boundaries; do not bind untrusted input directly to entities.
- Avoid hardcoded dashboard values and credentials.
- Add database indexes or repository queries rather than filtering full tables in memory.
- Use SLF4J; never log passwords, tokens, raw secrets, or sensitive evidence.
- Add tests for new behavior and regressions.
- Comment intent only where the code cannot communicate it clearly.
- Do not redesign the existing UI as part of an unrelated functional change.

## Database changes

Document schema changes in `docs/Database.md`. Until versioned migrations are introduced, call out any compatibility or data-migration requirement prominently in the pull request.

## UI and screenshots

Preserve the current theme, typography, colors, layout, and page structure unless an approved issue explicitly requires otherwise. For visible changes, include redacted before/after images following [screenshots/README.md](screenshots/README.md).

## Verification

Before opening a pull request, run:

```bash
./mvnw clean verify
```

Also verify Docker changes with `docker compose config` and, when practical, `docker compose up --build`.

## Pull requests

Complete the pull-request template and include the problem, chosen approach, linked issues, tests, security and database impact, screenshots for visible changes, and documentation updates.

Maintainers may request that broad work be split into smaller pull requests. Reviews focus on correctness, security, maintainability, compatibility, and test coverage.
