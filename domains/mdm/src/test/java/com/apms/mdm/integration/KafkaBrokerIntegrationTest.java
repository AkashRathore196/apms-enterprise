package com.apms.mdm.integration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class KafkaBrokerIntegrationTest {

    @Container
    static final GenericContainer<?> kafka =
            new GenericContainer<>(DockerImageName.parse("apache/kafka:3.8.0"))
                    .withExposedPorts(9092)
                    .withEnv("KAFKA_NODE_ID", "1")
                    .withEnv("KAFKA_PROCESS_ROLES", "broker,controller")
                    .withEnv("KAFKA_LISTENERS", "PLAINTEXT://:9092,CONTROLLER://:9093")
                    .withEnv("KAFKA_ADVERTISED_LISTENERS", "PLAINTEXT://:9092")
                    .withEnv("KAFKA_CONTROLLER_LISTENER_NAMES", "CONTROLLER")
                    .withEnv("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT")
                    .withEnv("KAFKA_CONTROLLER_QUORUM_VOTERS", "1@localhost:9093")
                    .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
                    .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
                    .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1")
                    .withEnv("CLUSTER_ID", "MkU3OEVBNTcwNTJENDM2Qk")
                    .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(2)));

    @Test
    void publishesAndConsumesDuplicateEvents() throws Exception {
        String topic = "mdm-party-approved-test-" + UUID.randomUUID();
        String bootstrap = kafka.getHost() + ":" + kafka.getMappedPort(9092);
        String group = "mdm-test-" + UUID.randomUUID();

        Map<String, Object> producerProps = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.ACKS_CONFIG, "all"
        );

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {
            producer.send(new ProducerRecord<>(topic, "party-1", "event-1")).get();
            producer.send(new ProducerRecord<>(topic, "party-1", "event-1")).get();
        }

        Map<String, Object> consumerProps = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap,
                ConsumerConfig.GROUP_ID_CONFIG, group,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"
        );

        int observed = 0;
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
            consumer.subscribe(List.of(topic));
            long deadline = System.currentTimeMillis() + 15_000;
            while (System.currentTimeMillis() < deadline && observed < 2) {
                for (ConsumerRecord<String, String> record : consumer.poll(Duration.ofMillis(500))) {
                    if ("event-1".equals(record.value())) observed++;
                }
            }
        }

        assertEquals(2, observed, "Expected exactly two Kafka deliveries");
    }
}
