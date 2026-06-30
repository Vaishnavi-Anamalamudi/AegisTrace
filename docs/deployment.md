# Deployment Guide

## Docker Compose

1. Copy `.env.example` to `.env`.
2. Change the database passwords.
3. Run:

```bash
docker compose up --build
```

Open `http://localhost:8084`.

## Local MySQL

Set these environment variables or edit local run configuration:

```text
DB_URL=jdbc:mysql://localhost:3306/aegistrace_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=your-password
```

Then run:

```bash
./mvnw spring-boot:run
```

## Nmap

The scanner uses Nmap when available. If Nmap is not installed or exits unsuccessfully, AegisTrace falls back to a common TCP port probe and still persists the scan result.
