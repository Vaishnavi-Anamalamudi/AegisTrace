# Architecture

AegisTrace is a layered Spring Boot SOC platform. The application combines server-rendered operations pages, REST APIs, WebSocket updates, JPA persistence, and scanner orchestration behind Spring Security.

## System Context

```mermaid
flowchart LR
    Analyst[Security Analyst] --> Browser[Browser]
    Browser --> UI[Thymeleaf Views]
    Integrator[API Client] --> API[REST API]
    UI --> App[AegisTrace Spring Boot]
    API --> App
    App --> MySQL[(MySQL 8)]
    App --> Scanner[Nmap or TCP Probe]
    App --> WebSocket[STOMP Broker]
    WebSocket --> Browser
```

## Runtime Components

```mermaid
flowchart TB
    Controllers[Controllers] --> DTOs[DTO Validation]
    Controllers --> Services[Application Services]
    Services --> Engines[Forensics and Timeline Engines]
    Services --> Repositories[Spring Data Repositories]
    Services --> Scanner[Scan Adapter]
    Services --> Publisher[Realtime Event Publisher]
    Repositories --> Entities[JPA Entities]
    Entities --> DB[(MySQL)]
    Security[JWT Filter + Spring Security] --> Controllers
```

## Layer Responsibilities

| Layer | Responsibility |
| --- | --- |
| `controller` | Accept HTTP requests, apply endpoint contracts, return DTOs or views |
| `dto` | Keep API payloads explicit and validation-ready |
| `service` | Own business workflows, scan orchestration, alerting, event handling, authentication |
| `repository` | Encapsulate persistence through Spring Data JPA |
| `entity` | Represent relational state and schema ownership |
| `security` | JWT generation, token filtering, and roles |
| `engine` | Domain-specific reconstruction and forensics helpers |
| `config` | Security, WebSocket, seed data, and infrastructure beans |

## Authentication Flow

```mermaid
sequenceDiagram
    actor User
    participant API as AuthController
    participant Auth as AuthenticationManager
    participant Users as UserRepository
    participant JWT as JwtUtil

    User->>API: POST /api/auth/login
    API->>Auth: authenticate username/password
    Auth->>Users: load user details
    Users-->>Auth: enabled account + authorities
    Auth-->>API: authenticated principal
    API->>JWT: generate token
    JWT-->>API: signed JWT
    API-->>User: AuthResponse
```

## Scan Flow

```mermaid
sequenceDiagram
    actor Analyst
    participant API as ScanController
    participant Service as ScanService
    participant Scanner as Nmap/TCP Probe
    participant DB as ScanResultRepository
    participant Alerts as AlertService

    Analyst->>API: POST /api/scans/run
    API->>Service: runTargetScan(target, user)
    Service->>Scanner: execute authorized scan
    Scanner-->>Service: ports, services, fingerprint
    Service->>Alerts: create findings for risky exposure
    Service->>DB: persist scan result
    Service-->>API: ScanResult
    API-->>Analyst: ScanResponse
```

## Deployment Diagram

```mermaid
flowchart TB
    Internet[Authorized Users] --> TLS[TLS Termination / Reverse Proxy]
    TLS --> App[AegisTrace Container]
    App --> MySQL[(MySQL Container or Managed DB)]
    App --> Nmap[Nmap Binary]
    App --> Logs[Application Logs]
    App --> Health[Actuator Health]
```

## Design Principles

- Keep controllers thin and service-oriented.
- Keep API payloads in DTOs instead of binding directly to entities.
- Treat scanner execution as privileged defensive functionality.
- Keep seed/demo data away from test profile and disable it for hardened deployments.
- Prefer versioned migrations before production schema evolution.
- Add tests around security-sensitive behavior before broad feature work.
