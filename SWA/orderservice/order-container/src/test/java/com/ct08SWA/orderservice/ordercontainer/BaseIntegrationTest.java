package com.ct08SWA.orderservice.ordercontainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Base class for integration tests providing:
 * - H2 in-memory database for fast testing
 * - Embedded Kafka for messaging
 * - Spring Boot test context
 * - MockMvc for REST API testing
 */
@SpringBootTest(classes = com.ct08SWA.orderservice.ordercontainer.container.OrderserviceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EmbeddedKafka(
    partitions = 1,
    topics = {
        "order.create.topic",
        "order.cancel.topic",
        "order.paid.topic",
        "order.state.topic",
        "order.payment.response",
        "restaurant.approval.response"
    }
)
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    void baseSetUp() {
        // Common setup for all integration tests
        // Can be overridden in subclasses
    }
}
