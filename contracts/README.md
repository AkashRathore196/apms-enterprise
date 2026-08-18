# Contracts

API and event contracts are enterprise-controlled integration artifacts.

## Rules

- REST contracts use OpenAPI.
- Kafka events are versioned and backward-compatibility aware.
- Producers and consumers validate compatibility before integration.
- Event contracts must not expose internal database structures unnecessarily.
