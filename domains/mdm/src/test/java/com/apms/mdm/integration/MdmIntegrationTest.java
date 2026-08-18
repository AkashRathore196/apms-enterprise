package com.apms.mdm.integration;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class MdmIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withStartupTimeout(Duration.ofMinutes(2));

    // Apache Kafka 3.8 is not a Confluent image. Use GenericContainer so
    // Testcontainers does not apply Confluent-specific startup scripts.
    @Container
    static final GenericContainer<?> kafka =
            new GenericContainer<>(DockerImageName.parse("apache/kafka:3.8.0"))
                    .withExposedPorts(9092)
                    .withEnv("KAFKA_NODE_ID", "1")
                    .withEnv("KAFKA_PROCESS_ROLES", "broker,controller")
                    .withEnv("KAFKA_LISTENERS", "PLAINTEXT://:9092,CONTROLLER://:9093")
                    .withEnv("KAFKA_ADVERTISED_LISTENERS", "PLAINTEXT://localhost:9092")
                    .withEnv("KAFKA_CONTROLLER_LISTENER_NAMES", "CONTROLLER")
                    .withEnv("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT")
                    .withEnv("KAFKA_CONTROLLER_QUORUM_VOTERS", "1@localhost:9093")
                    .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
                    .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
                    .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1")
                    .withEnv("CLUSTER_ID", "MkU3OEVBNTcwNTJENDM2Qk")
                    .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(2)));

    @Test
    void platformContainersStart() {
        assertTrue(postgres.isRunning(), "PostgreSQL container must be running");
        assertTrue(kafka.isRunning(), "Kafka container must be running");
        assertTrue(postgres.getJdbcUrl().startsWith("jdbc:postgresql://"));
        assertTrue(kafka.getHost() != null);
        assertTrue(kafka.getMappedPort(9092) > 0);
    }
}
