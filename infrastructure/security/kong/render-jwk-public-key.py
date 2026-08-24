#!/usr/bin/env python3
import base64
import json
import os
from pathlib import Path

from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives.asymmetric import rsa


def b64u_int(value: str) -> int:
    padded = value + "=" * (-len(value) % 4)
    return int.from_bytes(base64.urlsafe_b64decode(padded), "big")


key = json.loads(os.environ["KEY_JSON"])
issuer = os.environ["ISSUER"]

public_key = rsa.RSAPublicNumbers(
    b64u_int(key["e"]),
    b64u_int(key["n"]),
).public_key()

pem = public_key.public_bytes(
    serialization.Encoding.PEM,
    serialization.PublicFormat.SubjectPublicKeyInfo,
).decode()

lines = [
    '_format_version: "3.0"',
    "_transform: true",
    "consumers:",
    "  - username: mdm-security-keycloak",
    "    jwt_secrets:",
    f"      - key: {issuer}",
    "        algorithm: RS256",
    "        rsa_public_key: |",
]
lines.extend("          " + line for line in pem.rstrip().splitlines())
lines.extend(
    [
        "services:",
        "  - name: mdm",
        "    url: http://host.docker.internal:8080",
        "    routes:",
        "      - name: mdm-api",
        "        paths:",
        "          - /mdm",
        "        strip_path: false",
        "    plugins:",
        "      - name: jwt",
        "        config:",
        "          claims_to_verify:",
        "            - exp",
        "          key_claim_name: iss",
        "          maximum_expiration: 3600",
        "          run_on_preflight: true",
    ]
)

Path("/tmp/kong-rendered.yml").write_text("\n".join(lines) + "\n", encoding="utf-8")
print(f"issuer={issuer}")
print(f"kid={key['kid']}")
