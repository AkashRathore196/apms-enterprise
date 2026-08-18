# Platform

Shared APMS platform capabilities:

- IAM: Keycloak
- API Gateway: Kong
- Event Streaming: Apache Kafka
- Workflow/Orchestration: Camunda 8
- Search: OpenSearch
- Observability: OpenTelemetry

Domain services consume shared capabilities and retain their own transactional ownership.
