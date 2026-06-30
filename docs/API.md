# API Reference

All endpoints except login require authentication. Use a bearer token returned from `/api/auth/login` or HTTP Basic where supported by the active security configuration.

## Authentication

### Login

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "username": "analyst",
  "password": "Analyst123!"
}
```

### Register User

```http
POST /api/auth/register
Authorization: Bearer <admin-token>
Content-Type: application/json
```

Admin only.

## Scans

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/scans/run` | Run a target scan |
| `GET` | `/api/scans?q=value` | Search scan history |
| `DELETE` | `/api/scans/{id}` | Delete a scan, admin only |
| `GET` | `/api/scans/export.csv?q=value` | Export scan history |

Run request:

```json
{
  "target": "scanme.nmap.org"
}
```

## Events

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/api/events` | List security events |
| `GET` | `/api/events/{id}` | Read one security event |
| `POST` | `/api/events` | Create security event |
| `DELETE` | `/api/events/{id}` | Delete event |
| `POST` | `/api/ingest/events` | Ingest event |

## Alerts

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/api/alerts` | List alerts |
| `GET` | `/api/alerts/{id}` | Read alert |
| `POST` | `/api/alerts` | Create alert |
| `POST` | `/api/alerts/{id}/resolve` | Mark alert resolved |

## Incidents and Evidence

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/api/incidents` | List incidents |
| `POST` | `/api/incidents` | Create incident |
| `GET` | `/api/evidence` | List evidence records |
| `POST` | `/api/evidence` | Create evidence record |
| `GET` | `/api/forensics/{id}` | Reconstruct attack timeline |

## Intelligence

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/api/iocs` | List indicators |
| `POST` | `/api/iocs` | Create indicator |
| `DELETE` | `/api/iocs/{id}` | Delete indicator |
| `GET` | `/api/threats` | List threat intel |
| `GET` | `/api/threats/ip/{ip}` | Lookup threat intel by IP |
| `POST` | `/api/threats` | Create threat intel |

## Realtime

Security event updates are published over STOMP to:

```text
/topic/security-events
```
