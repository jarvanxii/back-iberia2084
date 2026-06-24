# Iberia 2084: correo transaccional con Cloudflare y DDNS

Esta guia deja preparado el envio desde `no-reply@iberia2084.com` usando el
Postfix local del Servidor 2 y registros propios de `iberia2084.com`.

## Estado actual

Comprobado el 2026-06-25:

- `iberia2084-api` esta activo.
- La app usa SMTP local: `127.0.0.1:25`.
- Remitente configurado: `no-reply@iberia2084.com`.
- OpenDKIM esta activo y preparado para firmar `*@iberia2084.com`.
- Selector DKIM: `mail2026`.
- La clave privada DKIM de Iberia queda bajo `root:root` con permisos
  estrictos (`/etc/opendkim/keys/iberia2084.com` a `700` y
  `mail2026.private` a `600`). Si queda como `opendkim:opendkim`, OpenDKIM
  rechaza el mensaje con `key data is not secure` y la API muestra
  `No se pudo enviar el correo de acceso`.
- Registros DNS de correo creados en Cloudflare.
- Token DNS limitado instalado en `/etc/iberia2084/cloudflare-ddns-mail.env`.
- `iberia2084-cloudflare-ddns-mail.timer` activo y habilitado.
- `mail.iberia2084.com` resuelve a `47.59.154.222` desde `1.1.1.1` y
  `8.8.8.8`.

Tambien se reviso la zona y no quedan registros `c4ligo...` dentro de
`iberia2084.com`. Los unicos tuneles esperados en esta zona son
`iberia2084.com` y `www.iberia2084.com`.

## Registros que hay que crear en Cloudflare

En Cloudflare, dominio `iberia2084.com`, abre `DNS` > `Registros`.

### 1. Registro A del servidor SMTP

| Campo | Valor |
| --- | --- |
| Tipo | `A` |
| Nombre | `mail` |
| Direccion IPv4 | `47.59.154.222` |
| Estado de proxy | `Solo DNS` |
| TTL | `Auto` o `2 min` |

Este valor inicial puede cambiar. El timer del servidor actualizara solo
`mail.iberia2084.com` cuando cambie la IP WAN.

### 2. SPF del dominio raiz

| Campo | Valor |
| --- | --- |
| Tipo | `TXT` |
| Nombre | `@` |
| Contenido | `v=spf1 a:mail.iberia2084.com -all` |
| TTL | `Auto` o `2 min` |

Debe existir un solo SPF. Si Cloudflare tiene otro TXT que empieza por
`v=spf1`, hay que borrarlo o sustituirlo.

### 3. DKIM de Iberia 2084

| Campo | Valor |
| --- | --- |
| Tipo | `TXT` |
| Nombre | `mail2026._domainkey` |
| Contenido | `v=DKIM1;h=sha256;k=rsa;p=MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw5C2hXXyjvjsCSvhsMY3AYfLq8HAXtQe0lULRj11iWpT4+yfORRodI0o7cNt7N6izjNF6lMS88/eEiIuGIsiRYZLc/XG3NOJSCOvx6op4HjyKkXhtR1sPOY+nr+I6MGGS1eO0V7pz+gzR6wpoKnJ9+fK/ZqcmVzljQDcihVT7csywxB1jDA8P57+Xb0h/pnLPdsI7dRFXiH0HlqYVjt1TTIRteGfw3YxYAaaZ6N32/So8mDWOUBbs93uB/Fypo2Bsu0BeMBbEHJ5zMGwrY/0AYen89UlRGCySBTrexkfYyvcBISIr4XS7W1i5L80/L3IZex3W7cpnbcXWNTxGtpu6QIDAQAB` |
| TTL | `Auto` o `2 min` |

Cloudflare puede partir visualmente el valor, pero hay que pegarlo como un unico
TXT.

### 4. DMARC inicial

| Campo | Valor |
| --- | --- |
| Tipo | `TXT` |
| Nombre | `_dmarc` |
| Contenido | `v=DMARC1; p=none; adkim=s; aspf=s` |
| TTL | `Auto` o `2 min` |

`p=none` es deliberado para las primeras pruebas. Cuando el envio este probado,
se puede endurecer a `quarantine` o `reject`.

## Token de Cloudflare para DDNS

El token de Thorondor no sirve si esta limitado a `thorondor.app`. Iberia usa
su propio token limitado a `iberia2084.com`, instalado en el servidor con
permisos `root:root 600`.

Si hay que rotarlo en el futuro, en Cloudflare:

1. `Mi perfil` > `Tokens de API`.
2. `Crear token`.
3. Plantilla `Editar DNS de zona`.
4. Nombre: `iberia2084-ddns-mail`.
5. Permisos:
   - `Zona` / `DNS` / `Editar`
   - `Zona` / `Zona` / `Leer`
6. Recursos de zona:
   - `Incluir` / `Zona especifica` / `iberia2084.com`
7. No rellenar `Filtro de direcciones IP del cliente`.
8. No poner caducidad mientras estemos probando.
9. Crear token y copiarlo una sola vez.

## Archivos instalados en servidor

El despliegue operativo usa:

- `/etc/iberia2084/cloudflare-ddns-mail.env`
- `/usr/local/sbin/iberia2084-cloudflare-ddns-mail.py`
- `/etc/systemd/system/iberia2084-cloudflare-ddns-mail.service`
- `/etc/systemd/system/iberia2084-cloudflare-ddns-mail.timer`

El entorno final queda asi:

```ini
CF_API_TOKEN=<token limitado de Cloudflare>
CF_ZONE_NAME=iberia2084.com
CF_RECORD_NAME=mail.iberia2084.com
CF_TTL=120
CF_PROXIED=false
```

Si se rota el token, despues de pegar el nuevo:

```bash
sudo systemctl daemon-reload
sudo systemctl start iberia2084-cloudflare-ddns-mail.service
sudo systemctl enable --now iberia2084-cloudflare-ddns-mail.timer
```

## Comprobaciones

```bash
dig +short A mail.iberia2084.com @1.1.1.1
dig +short TXT iberia2084.com @1.1.1.1
dig +short TXT mail2026._domainkey.iberia2084.com @1.1.1.1
dig +short TXT _dmarc.iberia2084.com @1.1.1.1
sudo opendkim-testkey -d iberia2084.com -s mail2026 -vvv
sudo journalctl -u iberia2084-cloudflare-ddns-mail.service -n 50 --no-pager
```

Si el registro o la recuperacion fallan al enviar el correo, revisar primero
Postfix y OpenDKIM:

```bash
sudo journalctl -u opendkim -u postfix --since "15 minutes ago" --no-pager
```

Si aparece `key data is not secure` para `mail2026._domainkey.iberia2084.com`,
reaplicar los permisos seguros y reiniciar OpenDKIM:

```bash
sudo chown root:root /etc/opendkim/keys /etc/opendkim/keys/iberia2084.com
sudo chown root:root /etc/opendkim/keys/iberia2084.com/mail2026.private
sudo chmod 700 /etc/opendkim/keys /etc/opendkim/keys/iberia2084.com
sudo chmod 600 /etc/opendkim/keys/iberia2084.com/mail2026.private
sudo systemctl restart opendkim
```

Una prueba correcta deja dos senales en logs: `DKIM-Signature field added
(s=mail2026, d=iberia2084.com)` y `status=sent`.

## Notas

No se crea `MX` porque ahora mismo solo queremos enviar correos
transaccionales desde `no-reply@iberia2084.com`, no recibir correo en ese
dominio. Si mas adelante se quiere recibir respuestas o formularios en un
buzon real, se configurara un proveedor de entrada o Cloudflare Email Routing.
