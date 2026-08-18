# MDM Party/Organization Regression Baseline v1.0

This baseline defines the minimum regression suite that must remain green before advancing to Kafka eventing.

## CI checks
- Maven model validation
- Compile
- Unit tests
- Spring application-context test
- PostgreSQL Testcontainers
- Kafka Testcontainers
- Flyway + PostgreSQL initialization
- Party/Organization persistence
- DRAFT -> SUBMITTED -> ACTIVE lifecycle
- Duplicate legal-name rule
- Audit persistence
- Outbox persistence
- Package/JAR generation

## Evidence rule
A green workflow is evidence only for tests actually executed. Implementation presence alone is not acceptance evidence.

## Gate
Party/Organization regression is GREEN only when the latest GitHub Actions MDM Build run is successful and the run includes all required tests above.