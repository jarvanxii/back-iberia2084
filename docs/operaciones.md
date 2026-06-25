# Operaciones Backend Iberia 2084

## Repositorios y servidor

- Repo local: `C:\Users\Jarva\Desktop\git-repos\back-iberia2084`.
- Servidor asignado: Servidor 2, `192.168.0.253`.
- Ruta en servidor: `/var/www/iberia2084/back`.
- Servicio systemd: `iberia2084-api`.
- Entorno del servicio: `/etc/iberia2084/api.env`.
- Configuración Nginx: `/etc/nginx/sites-available/iberia2084`.
- Guía SMTP/Cloudflare: `docs/iberia2084-smtp-cloudflare.md`.
- Entorno DDNS SMTP Cloudflare: `/etc/iberia2084/cloudflare-ddns-mail.env`.
- Desplegar solo en el Servidor 2 asignado a Iberia 2084.

## Puertos y entornos

- Desarrollo local del repo: `IBERIA2084_SERVER_PORT=18081`.
- API interna en servidor: `127.0.0.1:18081`.
- Perfil remoto: `SPRING_PROFILES_ACTIVE=remoto`, con secretos reales fuera de git.
- Nginx sirve el front en `http://192.168.0.253:8083/` y proxya `/api` hacia la API interna.
- `/etc/iberia2084/api.env`, Nginx y las comprobaciones de salud deben usar el mismo puerto `18081`.

## Despliegue en Servidor 2

```bash
cd /var/www/iberia2084/back
JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 PATH=$JAVA_HOME/bin:$PATH ./mvnw -DskipTests package
sudo systemctl restart iberia2084-api
curl http://127.0.0.1:18081/actuator/health
```

El servicio systemd debe arrancar con `IBERIA2084_SERVER_PORT=18081`.

## Correo transaccional

- Remitente de producción: `no-reply@iberia2084.com`.
- SMTP local del servidor: `127.0.0.1:25`, sin auth y sin STARTTLS.
- OpenDKIM firma `*@iberia2084.com` con selector `mail2026`.
- Los permisos DKIM seguros son obligatorios: `/etc/opendkim/keys` y `/etc/opendkim/keys/iberia2084.com` en `root:root 700`; `mail2026.private` en `root:root 600`.
- Registros esperados en Cloudflare: `A mail` actualizado por DDNS, SPF raíz `v=spf1 a:mail.iberia2084.com -all`, DKIM `mail2026._domainkey` y DMARC `_dmarc` con `p=quarantine`.
- Los túneles esperados para la zona son `iberia2084.com` y `www.iberia2084.com`.

Los pasos completos de rotación, verificación y resolución de incidencias están en `docs/iberia2084-smtp-cloudflare.md`.

## Base de datos

MariaDB usa la base canónica `iberia2084`. Los cambios de esquema deben entrar por Flyway en `src/main/resources/db/migration`. El SQL local base está en `docs/dev/mariadb-local.sql`.

## Sincronización

Antes de desplegar cambios locales, comprobar `git status`, commitear y pushear lo que corresponda, y después hacer `git pull --ff-only` en el servidor.

## Credenciales

Las credenciales de acceso al servidor se consultan en `C:\Users\Jarva\Desktop\OPERACION AGENTICA.md`. No se imprimen ni se commitean.
