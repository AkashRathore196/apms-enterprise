# MDM CI Trigger

This file is intentionally non-functional and exists to trigger the MDM Build workflow on push to `main` while execution evidence is being established.

The workflow should run:
- Java 21 setup
- Maven test
- Maven package
- Artifact upload

Once CI execution is confirmed, this file can be removed as part of repository cleanup.
