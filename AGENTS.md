# Repository Guidelines

## Project Structure

This repository contains the Spring Boot backend for España 2084. Java sources live under `src/main/java/com/espana2084/api`. Runtime configuration lives in `src/main/resources`. Flyway migrations belong in `src/main/resources/db/migration`.

## Commands

Use Java 25 and the Maven Wrapper:

```powershell
.\scripts\setup-jdk25.ps1
.\mvnw.cmd spring-boot:run
.\mvnw.cmd -DskipTests package
.\mvnw.cmd test
```

## Environment

The default profile is `local`. Supported profiles are currently `local` and `remoto`.

Environment variables:

```properties
SPRING_PROFILES_ACTIVE=local
ESPANA2084_SERVER_PORT=8080
ESPANA2084_DB_URL=jdbc:mariadb://localhost:3306/espana2084
ESPANA2084_DB_USER=espana2084
ESPANA2084_DB_PASSWORD=espana2084
```

Commit only `.env.example`, `.env.local.example`, and `.env.remoto.example`. Do not commit real secrets.

## Database

Use MariaDB. Schema changes must be expressed as Flyway migrations under `src/main/resources/db/migration`. Do not rely on Hibernate `ddl-auto=update`; keep `validate` unless there is a deliberate, documented reason to change it.

## Scope

This backend belongs only to España 2084. Do not bring back code, data, SQL, routes, or documentation from La Pipa de Gandalf. The old movie platform and the strategy game must stay separated.

## Implementation Notes

Prefer explicit domain packages once features appear. Keep controllers thin, put game rules in services/domain classes, validate input DTOs, and add focused tests for turn resolution, resources, actions, and persistence rules.
