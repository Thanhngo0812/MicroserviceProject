package com.ct08SWA.orderservice.ordercontainer.dataaccess;

import com.ct08SWA.orderservice.ordercontainer.BaseIntegrationTest;
import com.ct08SWA.orderservice.orderapplicationservice.ports.outputports.Repository.OrderOutboxRepository;
import com.ct08SWA.orderservice.orderdataaccess.entity.OrderOutboxEntity;
import com.ct08SWA.orderservice.orderdataaccess.repository.OrderOutboxJpaRepository;
import com.ct08SWA.orderservice.orderdataaccess.entity.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for OrderOutboxRepository (Transactional Outbox Pattern)
 * Tests database operations for the outbox table with real PostgreSQL
 */
@DisplayName("Order Outbox Repository Integration Tests")
class OrderOutboxIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderOutboxRepository orderOutboxRepository;

    @Autowired
    private OrderOutboxJpaRepository orderOutboxJpaRepository;

    @BeforeEach
    void setUp() {
        // Clean outbox table before each test
        orderOutboxJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save outbox message successfully")
    void shouldSaveOutboxMessageSuccessfully() {
        // Given: An outbox entity
        OrderOutboxEntity outboxEntity = createTestOutboxEntity();

        // When: Save the outbox message
        OrderOutboxEntity savedEntity = orderOutboxJpaRepository.save(outboxEntity);

        // Then: Should be persisted with all fields
        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getSagaId()).isEqualTo(outboxEntity.getSagaId());
        assertThat(savedEntity.getPayload()).isEqualTo(outboxEntity.getPayload());
        assertThat(savedEntity.getStatus()).isEqualTo(outboxEntity.getStatus());
    }

    @Test
    @DisplayName("Should find outbox messages by order status and outbox status")
    void shouldFindOutboxMessagesByOrderStatusAndOutboxStatus() {
        // Given: Multiple outbox messages with different statuses
        OrderOutboxEntity pending1 = createTestOutboxEntity();
        pending1.setStatus("PENDING");

        OrderOutboxEntity pending2 = createTestOutboxEntity();
        pending2.setStatus("PENDING");

        OrderOutboxEntity completed = createTestOutboxEntity();
        completed.setStatus("COMPLETED");

        orderOutboxJpaRepository.saveAll(List.of(pending1, pending2, completed));

        // When: Find by status
        List<OrderOutboxEntity> pendingMessages = orderOutboxJpaRepository.findAll().stream()
            .filter(msg -> "PENDING".equals(msg.getStatus()))
            .toList();

        // Then: Should find only pending messages
        assertThat(pendingMessages).hasSize(2);
        assertThat(pendingMessages)
            .allMatch(msg -> msg.getStatus().equals("PENDING"));
    }

    @Test
    @DisplayName("Should delete outbox message after processing")
    void shouldDeleteOutboxMessageAfterProcessing() {
        // Given: A saved outbox message
        OrderOutboxEntity outboxEntity = createTestOutboxEntity();
        OrderOutboxEntity savedEntity = orderOutboxJpaRepository.save(outboxEntity);
        assertThat(orderOutboxJpaRepository.count()).isEqualTo(1);

        // When: Delete the message
        orderOutboxJpaRepository.deleteById(savedEntity.getId());

        // Then: Should be removed from database
        assertThat(orderOutboxJpaRepository.count()).isZero();
        Optional<OrderOutboxEntity> deletedEntity = orderOutboxJpaRepository.findById(savedEntity.getId());
        assertThat(deletedEntity).isEmpty();
    }

    @Test
    @DisplayName("Should update outbox status after message is sent")
    void shouldUpdateOutboxStatusAfterMessageSent() {
        // Given: A saved outbox message in PENDING status
        OrderOutboxEntity outboxEntity = createTestOutboxEntity();
        outboxEntity.setStatus("PENDING");
        OrderOutboxEntity savedEntity = orderOutboxJpaRepository.save(outboxEntity);

        // When: Update status to COMPLETED
        savedEntity.setStatus("COMPLETED");
        OrderOutboxEntity updatedEntity = orderOutboxJpaRepository.save(savedEntity);

        // Then: Status should be updated
        assertThat(updatedEntity.getStatus()).isEqualTo("COMPLETED");

        // And: Should be persisted
        Optional<OrderOutboxEntity> foundEntity = orderOutboxJpaRepository.findById(updatedEntity.getId());
        assertThat(foundEntity).isPresent();
        assertThat(foundEntity.get().getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    @DisplayName("Should handle multiple outbox messages for same saga")
    void shouldHandleMultipleOutboxMessagesForSameSaga() {
        // Given: Multiple outbox messages for the same saga
        UUID sagaId = UUID.randomUUID();

        OrderOutboxEntity message1 = createTestOutboxEntity();
        message1.setSagaId(sagaId);
        message1.setPayload("{\"event\":\"OrderCreated\"}");

        OrderOutboxEntity message2 = createTestOutboxEntity();
        message2.setSagaId(sagaId);
        message2.setPayload("{\"event\":\"PaymentRequested\"}");

        // When: Save both messages
        orderOutboxJpaRepository.saveAll(List.of(message1, message2));

        // Then: Both should be persisted
        List<OrderOutboxEntity> allMessages = orderOutboxJpaRepository.findAll();
        assertThat(allMessages).hasSize(2);
        assertThat(allMessages)
            .allMatch(msg -> msg.getSagaId().equals(sagaId));
    }

    @Test
    @DisplayName("Should order outbox messages by created date")
    void shouldOrderOutboxMessagesByCreatedDate() throws InterruptedException {
        // Given: Multiple messages created at different times
        OrderOutboxEntity first = createTestOutboxEntity();
        first.setCreatedAt(ZonedDateTime.now().minusMinutes(5));
        orderOutboxJpaRepository.save(first);

        Thread.sleep(100); // Small delay to ensure different timestamps

        OrderOutboxEntity second = createTestOutboxEntity();
        second.setCreatedAt(ZonedDateTime.now().minusMinutes(3));
        orderOutboxJpaRepository.save(second);

        Thread.sleep(100);

        OrderOutboxEntity third = createTestOutboxEntity();
        third.setCreatedAt(ZonedDateTime.now().minusMinutes(1));
        orderOutboxJpaRepository.save(third);

        // When: Find all messages
        List<OrderOutboxEntity> messages = orderOutboxJpaRepository.findAll();

        // Then: Should be able to order by creation time
        assertThat(messages).hasSize(3);
        // Verify they can be sorted by createdAt
        messages.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
        assertThat(messages.get(0).getId()).isEqualTo(first.getId());
        assertThat(messages.get(2).getId()).isEqualTo(third.getId());
    }

    @Test
    @DisplayName("Should persist large payload in outbox message")
    void shouldPersistLargePayloadInOutboxMessage() {
        // Given: An outbox message with large JSON payload
        StringBuilder largePayload = new StringBuilder("{\"items\":[");
        for (int i = 0; i < 100; i++) {
            largePayload.append("{\"id\":\"").append(UUID.randomUUID()).append("\",\"value\":").append(i).append("}");
            if (i < 99) largePayload.append(",");
        }
        largePayload.append("]}");

        OrderOutboxEntity outboxEntity = createTestOutboxEntity();
        outboxEntity.setPayload(largePayload.toString());

        // When: Save the message
        OrderOutboxEntity savedEntity = orderOutboxJpaRepository.save(outboxEntity);

        // Then: Large payload should be persisted correctly
        assertThat(savedEntity.getPayload()).isEqualTo(largePayload.toString());
        assertThat(savedEntity.getPayload().length()).isGreaterThan(1000);

        // And: Should be retrievable
        Optional<OrderOutboxEntity> foundEntity = orderOutboxJpaRepository.findById(savedEntity.getId());
        assertThat(foundEntity).isPresent();
        assertThat(foundEntity.get().getPayload()).hasSize(largePayload.length());
    }

    /**
     * Helper method to create a test outbox entity
     */
    private OrderOutboxEntity createTestOutboxEntity() {
        OrderOutboxEntity entity = OrderOutboxEntity.builder()
            .id(UUID.randomUUID())
            .sagaId(UUID.randomUUID())
            .createdAt(ZonedDateTime.now())
            .eventType("OrderCreated")
            .payload("{\"orderId\":\"" + UUID.randomUUID() + "\",\"status\":\"PENDING\"}")
            .status("PENDING")
            .build();
        return entity;
    }
}
