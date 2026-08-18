package com.apms.mdm.integration;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class MdmIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    private static final DockerImageName KAFKA_IMAGE =
            DockerImageName.parse("apache/kafka:3.8.0")
                    .asCompatibleSubstituteFor("confluentinc/cp-kafka");

    @Container
    static KafkaContainer kafka = new KafkaContainer(KAFKA_IMAGE);

    @Test
    void platformContainersStart() {
        assertTrue(postgres.isRunning());
        assertTrue(kafka.isRunning());
        assertTrue(postgres.getJdbcUrl().startsWith("jdbc:postgresql://"));
        assertTrue(kafka.getBootstrapServers().startsWith("PLAINTEXT://"));
    }
}
