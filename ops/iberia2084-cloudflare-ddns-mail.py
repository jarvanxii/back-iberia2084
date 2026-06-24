#!/usr/bin/env python3
"""Keep mail.iberia2084.com pointing to the current public IPv4 address."""

from __future__ import annotations

import ipaddress
import json
import os
import sys
import urllib.error
import urllib.parse
import urllib.request


ENV_FILE = "/etc/iberia2084/cloudflare-ddns-mail.env"
API_BASE = "https://api.cloudflare.com/client/v4"
IP_CHECK_URLS = (
    "https://1.1.1.1/cdn-cgi/trace",
    "https://cloudflare.com/cdn-cgi/trace",
)


def load_env_file(path: str) -> None:
    if not os.path.exists(path):
        return

    with open(path, encoding="utf-8") as env_file:
        for raw_line in env_file:
            line = raw_line.strip()
            if not line or line.startswith("#") or "=" not in line:
                continue
            key, value = line.split("=", 1)
            os.environ.setdefault(key.strip(), value.strip().strip("\"'"))


def required_env(name: str) -> str:
    value = os.environ.get(name, "").strip()
    if not value:
        raise RuntimeError(f"Missing required environment value: {name}")
    return value


def cloudflare_request(method: str, path: str, token: str, payload: dict | None = None) -> dict:
    body = None if payload is None else json.dumps(payload).encode("utf-8")
    request = urllib.request.Request(
        f"{API_BASE}{path}",
        data=body,
        method=method,
        headers={
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json",
        },
    )

    try:
        with urllib.request.urlopen(request, timeout=20) as response:
            data = json.loads(response.read().decode("utf-8"))
    except urllib.error.HTTPError as error:
        details = error.read().decode("utf-8", errors="replace")
        raise RuntimeError(f"Cloudflare API returned HTTP {error.code}: {details}") from error
    except urllib.error.URLError as error:
        raise RuntimeError(f"Cloudflare API request failed: {error}") from error

    if not data.get("success"):
        raise RuntimeError(f"Cloudflare API returned an error: {data.get('errors')}")
    return data


def current_public_ipv4() -> str:
    errors: list[str] = []
    for url in IP_CHECK_URLS:
        try:
            with urllib.request.urlopen(url, timeout=10) as response:
                body = response.read().decode("utf-8", errors="replace")
        except urllib.error.URLError as error:
            errors.append(f"{url}: {error}")
            continue

        values = dict(
            line.split("=", 1)
            for line in body.splitlines()
            if "=" in line
        )
        ip_value = values.get("ip", "").strip()
        try:
            parsed = ipaddress.ip_address(ip_value)
        except ValueError:
            errors.append(f"{url}: invalid ip value {ip_value!r}")
            continue
        if parsed.version == 4:
            return str(parsed)
        errors.append(f"{url}: expected IPv4, got {parsed}")

    raise RuntimeError("Could not detect public IPv4: " + "; ".join(errors))


def get_zone_id(token: str, zone_name: str) -> str:
    query = urllib.parse.urlencode({"name": zone_name, "status": "active"})
    data = cloudflare_request("GET", f"/zones?{query}", token)
    zones = data.get("result", [])
    if not zones:
        raise RuntimeError(f"Cloudflare zone not found or not active: {zone_name}")
    return zones[0]["id"]


def get_a_record(token: str, zone_id: str, record_name: str) -> dict | None:
    query = urllib.parse.urlencode({"type": "A", "name": record_name})
    data = cloudflare_request("GET", f"/zones/{zone_id}/dns_records?{query}", token)
    records = data.get("result", [])
    if not records:
        return None
    return records[0]


def sync_record(token: str, zone_id: str, record_name: str, ip_value: str, ttl: int, proxied: bool) -> None:
    record = get_a_record(token, zone_id, record_name)
    payload = {
        "type": "A",
        "name": record_name,
        "content": ip_value,
        "ttl": ttl,
        "proxied": proxied,
    }

    if record is None:
        cloudflare_request("POST", f"/zones/{zone_id}/dns_records", token, payload)
        print(f"Created {record_name} A {ip_value}")
        return

    current_ip = record.get("content")
    current_ttl = int(record.get("ttl", 1))
    current_proxied = bool(record.get("proxied", False))
    if current_ip == ip_value and current_ttl == ttl and current_proxied == proxied:
        print(f"{record_name} already points to {ip_value}")
        return

    cloudflare_request("PATCH", f"/zones/{zone_id}/dns_records/{record['id']}", token, payload)
    print(f"Updated {record_name}: {current_ip} -> {ip_value}")


def main() -> int:
    load_env_file(os.environ.get("IBERIA2084_CF_DDNS_ENV", ENV_FILE))

    token = required_env("CF_API_TOKEN")
    if token.upper().startswith(("CHANGE_ME", "REPLACE_ME", "PENDIENTE")):
        raise RuntimeError("CF_API_TOKEN still contains a placeholder value")

    zone_name = required_env("CF_ZONE_NAME")
    record_name = required_env("CF_RECORD_NAME")
    ttl = int(os.environ.get("CF_TTL", "120"))
    proxied = os.environ.get("CF_PROXIED", "false").strip().lower() == "true"
    if proxied:
        raise RuntimeError("SMTP DNS records must be DNS-only. Set CF_PROXIED=false.")

    ip_value = current_public_ipv4()
    zone_id = get_zone_id(token, zone_name)
    sync_record(token, zone_id, record_name, ip_value, ttl, proxied)
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except Exception as exc:
        print(f"iberia2084-cloudflare-ddns-mail: {exc}", file=sys.stderr)
        raise SystemExit(1)
