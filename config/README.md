# Configuration

Project-level runtime configuration is stored in:

```text
src/main/resources/application.properties
src/main/resources/application-docker.properties
.env.example
docker-compose.yml
```

Do not commit local secrets. Use `.env` for Docker Compose development and environment variables for deployment.
