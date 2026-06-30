# Changelog

All notable changes to AegisTrace are documented in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and the project aims to follow [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Planned

- Versioned database migrations
- OpenAPI documentation
- Expanded integration and authorization tests

## [1.0.0] - 2026-06-30

### Added

- Database-backed SOC dashboard and operational pages
- Network scanning through Nmap with TCP fallback behavior
- Persisted scan history, search, deletion, and CSV export
- Alert generation, incident reporting, evidence records, and attack reconstruction
- STOMP WebSocket security-event updates
- JWT authentication, BCrypt password hashing, validated authentication DTOs, and three roles
- MySQL and Docker Compose deployment configuration
- Maven verification workflow and open-source repository documentation

### Security

- Restricted user registration to administrators
- Prevented password hashes from being serialized
- Rejected inactive accounts during authentication

Link references will be added after the canonical GitHub repository URL is known.
