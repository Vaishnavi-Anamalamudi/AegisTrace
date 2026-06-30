# Installation

## Requirements

- Java 17+
- Git
- MySQL 8+ or Docker Desktop
- Nmap for enhanced scanning

## Clone

```bash
git clone https://github.com/Vaishnavi/AegisTrace.git
cd AegisTrace
```

## Configure

```bash
cp .env.example .env
```

Edit `.env` and replace every `change-me` value.

## Verify

```bash
./mvnw clean verify
```

Windows:

```powershell
.\mvnw.cmd clean verify
```

## Start

```bash
docker compose up --build
```

or:

```bash
./mvnw spring-boot:run
```

## Troubleshooting

| Symptom | Check |
| --- | --- |
| MySQL connection fails | Confirm `DB_URL`, credentials, and container health |
| Port already in use | Change `server.port` or stop the existing process |
| Nmap unavailable | Install Nmap or set `NMAP_ENABLED=false` |
| Login fails | Use development accounts only in local non-test profile |
