# Security Policy

## Supported versions

AegisTrace is currently maintained on the default branch. Until formal release branches are established, security fixes are applied there.

| Version | Supported |
| --- | --- |
| Default branch | Yes |
| Older snapshots and forks | No |

## Reporting a vulnerability

Do not open a public issue for a suspected vulnerability.

After publication, use GitHub's **Report a vulnerability** or private security-advisory feature. If private reporting is unavailable, contact the maintainer privately through the repository owner's GitHub profile and request a secure communication channel.

Include, when safe:

- the affected component and version or commit;
- reproduction steps or a minimal proof of concept;
- expected and observed impact;
- suggested mitigation, if known;
- whether the issue is already public or actively exploited.

Do not include real credentials, personal data, production evidence, or unauthorized scan results.

## Response process

Maintainers will aim to acknowledge a complete report within five business days, validate and prioritize it, coordinate remediation, and credit the reporter if requested. Timelines vary with severity and complexity.

Please allow a reasonable remediation window before public disclosure. Good-faith research that avoids privacy violations, destructive testing, service disruption, and unauthorized access is welcome.

## Deployment responsibilities

Deployers must replace demo credentials, protect JWT and database secrets, apply least privilege, terminate TLS at the edge, restrict network exposure, and scan only authorized targets.
