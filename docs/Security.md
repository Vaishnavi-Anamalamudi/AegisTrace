# Security

AegisTrace is a defensive security application and should be operated with strict authorization boundaries.

## Controls

- BCrypt password hashing
- JWT bearer token generation
- Spring Security filter chain
- Method security with role checks
- Admin-only user registration
- Admin/Analyst scan execution
- Admin-only scan deletion
- Disabled inactive accounts
- Password hash serialization protection
- Limited actuator exposure

## Roles

| Role | Intended user |
| --- | --- |
| `ROLE_ADMIN` | Maintainers and administrators |
| `ROLE_ANALYST` | SOC analysts and incident responders |
| `ROLE_VIEWER` | Read-only reviewers |

## Responsible Scanning

Only scan assets you own or are explicitly authorized to test. Unauthorized scanning can be illegal and harmful.

## Hardening Backlog

- Add production profile without seed accounts.
- Use external secret management.
- Add controller-level authorization tests.
- Expand write endpoint role policies.
- Add rate limiting for authentication and scan execution.
- Add audit entries for privileged changes.
- Introduce migration tooling and database least privilege.

## Vulnerability Reports

Follow [../SECURITY.md](../SECURITY.md). Do not open public issues for vulnerabilities.
