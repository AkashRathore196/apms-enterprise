# MDM Eventing Stage 3 — Outbox to Kafka

Stage 3 remains isolated on `feature/mdm-eventing-integration`.

Acceptance boundary:

1. Create one `PENDING` outbox event.
2. Claim it with a replica-safe database update/lock.
3. Publish the claimed event to Kafka.
4. Mark it `PUBLISHED` only after broker acknowledgement.
5. Retry a failed publish without losing the event.
6. Verify no concurrent publisher can claim the same event.

No merge to `main` until this stage has bounded, reproducible CI evidence.
