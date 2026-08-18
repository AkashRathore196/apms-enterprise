# APMS Enterprise

Enterprise engineering repository for the APMS platform, shared capabilities, domain services, contracts, infrastructure and delivery foundation.

## Current build gate

**Engineering Workspace Foundation → MDM Golden Path execution**

## Repository topology

```text
architecture/   Enterprise architecture and governance references
contracts/      API and event contracts
platform/       Shared enterprise platform capabilities
domains/        Domain-aligned application services
infrastructure/Infrastructure-as-code and deployment definitions
dev/            Local developer runtime/bootstrap
quality/        Integration, performance and security validation
docs/           Engineering and operational documentation
.github/        CI/CD and repository governance
```

## Reference stack

Java/Spring Boot · PostgreSQL · Apache Kafka · Kong · Keycloak · Camunda 8 · OpenSearch · OpenTelemetry · Kubernetes

## Engineering rule

Architecture and domain ownership remain governed by the APMS Enterprise Architecture and baseline registers. Domain implementations must not create duplicate enterprise authorities or cross domain database coupling.
