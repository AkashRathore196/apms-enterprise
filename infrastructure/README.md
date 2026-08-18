# Infrastructure

Infrastructure-as-code boundary for APMS.

Planned structure:

- `infrastructure/opentofu/` — cloud and shared infrastructure provisioning
- `infrastructure/kubernetes/` — platform and domain deployment definitions
- `infrastructure/environments/` — environment overlays and configuration
- `infrastructure/security/` — network/security policy definitions

Local Docker bootstrap remains under `dev/` and is intentionally separate from shared/production infrastructure.
