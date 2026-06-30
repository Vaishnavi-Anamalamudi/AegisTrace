# Deployment

This guide covers local, Docker Compose, and production-oriented deployment expectations.

## Docker Compose

```bash
cp .env.example .env
docker compose up --build
```

Open `http://localhost:8084` after the app and MySQL containers are healthy.

## Local JVM

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` before starting the app.

## Production Checklist

- Terminate TLS at a reverse proxy or ingress.
- Replace development credentials.
- Use a dedicated database principal with least privilege.
- Use managed secrets instead of checked-in configuration.
- Disable or isolate seed/demo data.
- Set `JPA_DDL_AUTO=validate`.
- Restrict actuator exposure.
- Add centralized logging and metrics.
- Ensure all scanning targets are authorized.
- Capture release artifact checksums.

## Environment Variables

| Variable | Required | Notes |
| --- | --- | --- |
| `DB_URL` | Yes | JDBC URL for MySQL |
| `DB_USERNAME` | Yes | Database user |
| `DB_PASSWORD` | Yes | Database password |
| `JPA_DDL_AUTO` | Recommended | Use `validate` outside development |
| `NMAP_ENABLED` | Optional | Set `false` where scanner execution is not allowed |

## Health Check

```text
GET /actuator/health
```
