# APMS Environment Bootstrap

## Local
Disposable Docker-based developer runtime.

## DEV
Shared Kubernetes environment with platform services and domain workloads.

## INT/TEST
Automated API/event/integration validation.

## SIT
Cross-domain system integration.

## UAT
Business acceptance.

## STAGING
Production-like final validation.

## PROD / DR
Controlled operational and recovery environments.

Production and shared-environment secrets must be injected through approved secret management; no real credentials belong in Git.
