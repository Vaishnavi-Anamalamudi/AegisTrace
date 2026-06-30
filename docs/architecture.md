# Architecture

AegisTrace is a Spring Boot MVC application backed by MySQL. Thymeleaf renders the existing dark SOC pages, while REST APIs power scan execution and operational data access.

```mermaid
flowchart LR
    Browser[Thymeleaf SOC UI] --> Controllers[Spring MVC Controllers]
    Browser --> WebSocket[STOMP WebSocket /topic/security-events]
    Controllers --> Services[Service Layer]
    Services --> Repositories[Spring Data Repositories]
    Repositories --> MySQL[(MySQL)]
    Services --> Nmap[Nmap or TCP Probe]
    Services --> Alerts[Alert Engine]
    Alerts --> MySQL
    Services --> WebSocket
```

## Layers

- Controller layer: validates requests, applies role rules, returns DTOs or views.
- Service layer: owns scan orchestration, alert creation, events, risk scoring, and audit-friendly logging.
- Repository layer: persists normalized SOC entities through Spring Data JPA.
- UI layer: keeps the existing dark cyber theme and receives live event updates over WebSockets.
