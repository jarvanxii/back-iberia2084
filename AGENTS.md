# Repository Guidelines

## Project Structure

This repository contains the Spring Boot backend for Iberia 2084. Java sources live under `src/main/java/com/iberia2084/api`. Runtime configuration lives in `src/main/resources`. Flyway migrations belong in `src/main/resources/db/migration`.

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
IBERIA2084_SERVER_PORT=18081
IBERIA2084_DB_URL=jdbc:mariadb://localhost:3306/iberia2084
IBERIA2084_DB_USER=iberia2084
IBERIA2084_DB_PASSWORD=iberia2084
```

Commit only `.env.example`, `.env.local.example`, and `.env.remoto.example`. Do not commit real secrets.

## Database

Use MariaDB. Schema changes must be expressed as Flyway migrations under `src/main/resources/db/migration`. Do not rely on Hibernate `ddl-auto=update`; keep `validate` unless there is a deliberate, documented reason to change it.

## Scope

This backend belongs only to Iberia 2084. Do not bring back code, data, SQL, routes, or documentation from La Pipa de Gandalf. The old movie platform and the strategy game must stay separated.

## AI Agent Rules

- Treat `iberia2084` as the canonical project and database name. Do not reintroduce old project names or paths/packages with `ñ`.
- Keep all Spanish text in UTF-8 with correct accents and `ñ`. Never commit mojibake or replacement characters.
- Use fictional parties and satirical systems. Do not hardcode real party names, logos, slogans, or identifiable current politicians.
- The tone may be sharp and ridiculous across the whole political spectrum, but game data must stay fictional and mechanically useful.
- Canonical parties are: Pantomima Popular (PP), Unión Progresista Nacional (PISOE), Grupo Independiente Liberal (GIL), Partido Unido Feminista Federal (PUFF...), Votantes obreros con Xilófono (VOX) and Junts Usuaris de Noves Tarifes Sobiranes (JUNTS). The API may keep technical `faction` names internally until a deliberate contract migration, but all user-facing Spanish must say "partido".
- The separated Gandalfpolis extraction may be used as a gameplay reference when the user asks for it. Translate the mechanics to Iberia 2084 names, resources, satire and schema; do not reintroduce the old movie platform domain.

## Game Design Rules

- Corruption must always be a real risk mechanic: cost, duration, visible percentage of being caught, upside, and political downside.
- Catastrophes must always be actionable: type, territory, severity, expiry, management plans, success percentage, reward, and failure penalty.
- Keep crisis types broad and replayable: DANA, pandemic, earthquake, blackout, price crisis, strikes, bureaucratic collapse, and future variants that affect resources or public opinion.
- City and troop systems should keep the strong Gandalfpolis pattern: visual city, building levels, costs, timers, training queues, unit stats and persisted completion on tick.
- Troops are political units, not fantasy soldiers: officials, journalists, presenters, inspectors, advisers, councillors, mayors and future satirical public-power archetypes.
- Currency design is intentionally small. Use only pesetas, votos and favores.
- Each resource needs a clear job: pesetas fund buildings, troops and upgrades; votos drive territorial pressure and political muscle; favores pay for internal party deals, corruption, coalition shortcuts and defensive bureaucracy.
- Do not add decorative resources. Reframe flavor ideas as copy or convert their mechanics into one of the three core resources.
- National politics must stay fictional but mechanically meaningful: ministries grant bonuses and regional governments color the global map.
- Conquest and influence actions must update territory ownership only after their timers resolve.
- Cooperative features should use alliances/coalitions first: membership, internal messages, shared planning, and later shared operations.
- If a rule affects persistence, model it in Flyway and expose it through DTOs before wiring frontend assumptions.

## Implementation Notes

Prefer explicit domain packages once features appear. Keep controllers thin, put game rules in services/domain classes, validate input DTOs, and add focused tests for turn resolution, resources, actions, and persistence rules.
