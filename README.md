# España 2084 API

Backend de España 2084, juego de estrategia política y territorial construido con Spring Boot, Java 25, MariaDB, JPA y Flyway.

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
ESPANA2084_SERVER_PORT=8080
ESPANA2084_DB_URL=jdbc:mariadb://localhost:3306/espana2084
ESPANA2084_DB_USER=espana2084
ESPANA2084_DB_PASSWORD=espana2084
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
jdbc:mariadb://localhost:3306/espana2084
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
http://localhost:8080
```

## Ejecutar Contra Entorno Remoto

Para arrancar con perfil remoto deben existir variables reales de entorno. No uses los valores de ejemplo tal cual en producción.

```powershell
$env:SPRING_PROFILES_ACTIVE="remoto"
$env:ESPANA2084_DB_URL="jdbc:mariadb://localhost:3306/espana2084"
$env:ESPANA2084_DB_USER="espana2084"
$env:ESPANA2084_DB_PASSWORD="change-me"
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

## Normas

- Mantener este backend separado de la antigua plataforma de películas.
- No copiar código, SQL, assets ni documentación de La Pipa de Gandalf.
- No commitear `.env`, contraseñas ni dumps reales.
- Toda estructura persistente debe entrar por Flyway.
- Las decisiones de seguridad futuras van a documentación de roadmap; las migraciones reales deben describir pasos, impacto y verificación.
