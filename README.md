# AegisTrace

AegisTrace is a final-year cybersecurity web project for cyber forensics, SOC monitoring, attack reconstruction, evidence tracking, and exposure scanning.

## Website Name

**AegisTrace - Cyber Forensics and Attack Reconstruction Platform**

## Main Features

- Public project home page at `http://localhost:8084/`
- Secure SOC dashboard with event metrics, charts, alert queue, and live scan form
- Incident reconstruction page with impact and remediation details
- Digital evidence vault with chain-of-custody records
- Threat intelligence page with saved scan history and detected exposure findings
- JWT-based API login and Spring Security protection for analyst pages
- MySQL persistence with first-run demo data for presentations

## Demo Login

- Username: `admin`
- Password: `ChangeMe123!`

## Run Locally

Make sure MySQL is running, then update `src/main/resources/application.properties` if your MySQL username or password is different.

```powershell
.\mvnw.cmd spring-boot:run
```

Open:

```text
http://localhost:8084/
```

## Tech Stack

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Thymeleaf
- MySQL
- Chart.js
- Bootstrap
