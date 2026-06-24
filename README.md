# Iberia 2084 API

Backend de Iberia 2084, juego de estrategia política, territorial y satírica construido con Spring Boot, Java 25, MariaDB, JPA y Flyway.

## Requisitos

- JDK 25.
- MariaDB local o remota.
- Maven Wrapper incluido en el repositorio.

No hace falta instalar Maven globalmente. Usa siempre `mvnw.cmd` en Windows.

## JDK 25

Hay un ZIP de Temurin 25 en:

```text
C:\Users\Jarva\Downloads\OpenJDK25U-jdk_x64_windows_hotspot_25.0.3_9.zip
```

Para extraerlo en `C:\Users\Jarva\.jdks\jdk-25.0.3+9` y activar Java 25 solo en la terminal actual:

```powershell
.\scripts\setup-jdk25.ps1
```

Comprueba que Maven ya usa Java 25:

```powershell
.\mvnw.cmd -version
```

## Entornos

El perfil por defecto es `local`.

Variables comunes:

```properties
SPRING_PROFILES_ACTIVE=local
IBERIA2084_SERVER_PORT=18081
IBERIA2084_DB_URL=jdbc:mariadb://localhost:3306/iberia2084
IBERIA2084_DB_USER=iberia2084
IBERIA2084_DB_PASSWORD=iberia2084
```

Archivos de ejemplo:

```text
.env.example
.env.local.example
.env.remoto.example
```

Spring Boot no carga `.env` automáticamente. Úsalos como plantilla para IntelliJ, PowerShell o el sistema de despliegue.

## Base de Datos Local

El perfil local usa MariaDB en:

```text
jdbc:mariadb://localhost:3306/iberia2084
```

Para crear la base de datos y el usuario local, ejecuta como usuario administrador de MariaDB el contenido de:

```text
docs/dev/mariadb-local.sql
```

## Ejecutar En Local

Con Java 25 activo y MariaDB preparada:

```powershell
$env:SPRING_PROFILES_ACTIVE="local"
.\mvnw.cmd spring-boot:run
```

El backend arranca en:

```text
http://localhost:18081
```

## Ejecutar Contra Entorno Remoto

Para arrancar con perfil remoto deben existir variables reales de entorno. No uses los valores de ejemplo tal cual en producción.

```powershell
$env:SPRING_PROFILES_ACTIVE="remoto"
$env:IBERIA2084_DB_URL="jdbc:mariadb://localhost:3306/iberia2084"
$env:IBERIA2084_DB_USER="iberia2084"
$env:IBERIA2084_DB_PASSWORD="change-me"
.\mvnw.cmd spring-boot:run
```

## Verificar

Compilación sin tests:

```powershell
.\mvnw.cmd -DskipTests package
```

Tests:

```powershell
.\mvnw.cmd test
```

## Migraciones

El esquema de base de datos debe evolucionar con Flyway. Las migraciones van en:

```text
src/main/resources/db/migration
```

Usa nombres como:

```text
V1__init.sql
V2__create_players.sql
```

No uses `ddl-auto=update`. Los perfiles están configurados con `validate` para que el modelo Java y la base de datos no diverjan en silencio.

## Beta Jugable

La primera vertical jugable incluye:

- Registro y login con token local.
- Mundos `Iberia Beta 1` y `Iberia Beta 2`, cada uno preparado para hasta 200 jugadores.
- Partidos satíricos ficticios que representan vicios políticos de todo el espectro sin usar partidos reales.
- Partidos canónicos: Pantomima Popular (PP), Unión Progresista Nacional (PISOE), Grupo Independiente Liberal (GIL), Partido Unido Feminista Federal (PUFF...), Votantes obreros con Xilófono (VOX) y Junts Usuaris de Noves Tarifes Sobiranes (JUNTS).
- Recursos simplificados: pesetas, votos y favores. Cada recurso debe tener un uso jugable claro.
- Acciones con temporizador: conquista territorial, presión territorial, corrupción y gestión de crisis.
- Corrupción con porcentaje real de pillada y penalización expresada en votos, pesetas o favores.
- Catástrofes aleatorias: DANA, pandemia, terremoto, apagón, crisis de precios, huelga y plaga burocrática.
- Investigaciones y coaliciones con foro interno.
- Ciudad propia con 12 edificios políticos: palacio, oficinas, redacción, plató, BOE, favores, promesas, crisis, antenas, rotondas, concejalía y alcaldía.
- Tropas políticas con cola de generación: funcionario raso, administrativo, periodista, presentador, inspector, asesor, concejal y alcalde.
- Divisas políticas: pesetas para construir, entrenar e investigar; votos para presión territorial y músculo político; favores para corrupción, alianzas, atajos y defensa burocrática.
- Sistema nacional con ministerios, gobiernos autonómicos e intercambio de recursos.

### Reglas De Juego Actuales

- El endpoint `GET /api/game/state` cobra producción pendiente, resuelve acciones vencidas y puede generar una catástrofe nueva.
- Las catástrofes tienen territorio, gravedad, caducidad y estado. Ahora mismo la probabilidad de aparición es del 18% por refresco de estado mientras haya menos de 4 crisis activas en el mundo.
- Los planes de crisis consumen recursos, tienen porcentaje de éxito y devuelven consecuencias en votos, pesetas o favores si el jugador hace teatro y sale mal.
- La corrupción nunca debe ser decorativa: toda operación debe tener coste, duración, riesgo visible de pillada, recompensa si sale bien y daño político si sale mal.
- Las acciones de conquista y presión territorial cambian control territorial real cuando se resuelven con éxito.
- Las tropas se generan mediante cola temporal persistente. El jugador paga recursos, espera el temporizador y recibe unidades al completar.
- Los edificios de ciudad tienen nivel, coste escalado, tiempo de mejora, posición en mapa urbano y desbloquean unidades o ventajas.
- No añadas recursos decorativos. Si una mecánica necesita coste o recompensa, debe encajar en pesetas, votos o favores.
- Las pesetas explican el retorno monetario ficticio de Iberia; los favores son la moneda informal del poder y los votos representan apoyo público accionable.
- Los ministerios se asignan por partido y apoyo institucional; si el jugador pertenece al bloque adecuado, sus mejoras se aplican a producción.
- El mapa político global se expone por comunidades y provincias para que el front pueda colorearlo según el gobierno territorial.
- Las coaliciones tienen membresía y foro interno persistente; cualquier mejora cooperativa debe partir de estas tablas.

### Endpoints Principales

```text
POST /api/auth/signup
POST /api/auth/login
GET  /api/bootstrap
GET  /api/worlds
GET  /api/factions
GET  /api/game/state
POST /api/game/collect
POST /api/game/actions/conquer
POST /api/game/actions/influence
POST /api/game/actions/corruption
POST /api/game/actions/disaster
POST /api/game/research
POST /api/game/troops/train
POST /api/game/city/buildings/upgrade
POST /api/game/resources/exchange
GET  /api/alliances
POST /api/alliances
POST /api/alliances/join
POST /api/alliances/messages
```

## Normas

- Mantener este backend separado de la antigua plataforma de películas.
- No copiar código, SQL, assets ni documentación de La Pipa de Gandalf.
- No commitear `.env`, contraseñas ni dumps reales.
- Toda estructura persistente debe entrar por Flyway.
- Las decisiones de seguridad futuras van a documentación de roadmap; las migraciones reales deben describir pasos, impacto y verificación.
