# Google OAuth en Iberia 2084

Este documento describe la operativa de acceso con Google para Iberia 2084. No contiene secretos reales; las credenciales viven en `C:\Users\Jarva\Desktop\OPERACION AGENTICA.md` y en `/etc/iberia2084/api.env` en el Servidor 2.

## Flujo

1. El front llama a `GET /api/auth/oauth/google?return_to=https://iberia2084.com/home`.
2. El backend valida que Google este configurado y redirige a `/oauth2/authorization/google`.
3. Google devuelve al backend en `/login/oauth2/code/google`.
4. El backend vincula la cuenta por email:
   - si el email ya existe, reutiliza ese usuario y marca el correo como verificado;
   - si no existe, crea un usuario nuevo con password aleatoria y correo verificado.
5. El backend crea un token normal de Iberia 2084 y lo guarda en `auth_oauth_handoffs` con caducidad corta.
6. El usuario vuelve a `/home?oauth=success&provider=google&handoff=...`.
7. El front consume el handoff con `POST /api/auth/oauth/handoff/{handoffId}`, guarda el token normal y limpia la URL.

No hay pantalla intermedia: tras Google se entra directamente al home si el handoff es valido.

## Variables de entorno

En produccion deben estar en `/etc/iberia2084/api.env`:

```properties
IBERIA2084_OAUTH_GOOGLE_CLIENT_ID=<client-id-de-google>
IBERIA2084_OAUTH_GOOGLE_CLIENT_SECRET=<client-secret-de-google>
IBERIA2084_AUTH_ALLOWED_REDIRECT_ORIGINS=https://iberia2084.com,https://www.iberia2084.com
IBERIA2084_OAUTH_HANDOFF_TTL_SECONDS=120
```

No se commitean credenciales reales.

## Google Cloud

En el cliente OAuth de Google hay que registrar este URI de redireccion autorizado:

```text
https://iberia2084.com/login/oauth2/code/google
```

Para pruebas locales, si se desea usar el mismo flujo contra el backend local, se puede anadir:

```text
http://localhost:18081/login/oauth2/code/google
```

## Nginx

El virtual host de Iberia 2084 debe proxyar hacia el backend interno `127.0.0.1:8081` estas rutas:

```text
/api/
/oauth2/
/login/oauth2/
```

Tambien debe enviar cabeceras `X-Forwarded-Proto`, `X-Forwarded-Host` y `X-Forwarded-Port`, porque Spring las usa para generar el callback publico con HTTPS.

## Verificacion

Comprobaciones utiles:

```bash
curl https://iberia2084.com/api/auth/providers
curl -I https://iberia2084.com/api/auth/oauth/google?return_to=https://iberia2084.com/home
```

La primera debe devolver `google.configured=true`. La segunda debe acabar redirigiendo a Google.
