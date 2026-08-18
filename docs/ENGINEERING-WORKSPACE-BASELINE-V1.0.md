# APMS Engineering Workspace Baseline v1.0

## Purpose
Establish the governed engineering factory for APMS implementation.

## Repository topology
- architecture/: architecture governance and baselines
- contracts/: API and event contracts
- platform/: shared enterprise platform boundaries
- domains/: domain-aligned application services
- infrastructure/: infrastructure-as-code and runtime deployment
- dev/: local developer bootstrap
- quality/: integration, security and performance validation
- docs/: engineering/runbook/evidence artifacts
- .github/: CI/CD governance

## Reference stack
Java/Spring Boot; PostgreSQL; Apache Kafka; Kong; Keycloak; Camunda 8; OpenSearch; OpenTelemetry; Kubernetes.

## Environment progression
LOCAL -> DEV -> INT/TEST -> SIT -> UAT -> STAGING -> PROD -> DR.

## Engineering rules
1. Domain services own their transactional data.
2. No cross-domain database coupling.
3. Enterprise master authority remains with MDM for agreed master objects.
4. API and event contracts are versioned and governed.
5. Secrets are externalized; no production secrets in Git.
6. Database changes are versioned migrations.
7. CI/CD is the promotion mechanism from source to runtime.
8. Architecture remains governed by the approved APMS enterprise baselines.
