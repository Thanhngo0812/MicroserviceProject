package com.ct08SWA.orderservice.ordercontainer.messaging;

import com.ct08SWA.orderservice.ordercontainer.BaseIntegrationTest;
import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Payment.PaymentResponse;
import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Restaurant.RestaurantResponse;
import com.ct08SWA.orderservice.orderdataaccess.entity.OrderEntity;
import com.ct08SWA.orderservice.orderdataaccess.repository.OrderJpaRepository;
import com.ct08SWA.orderservice.orderdataaccess.entity.OrderStatus;
import com.ct08SWA.orderservice.ordermessaging.dto.DebeziumOutboxMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration tests for Kafka message listeners
 * Tests the complete flow from Kafka message to domain logic execution
 */
@DisplayName("Order Kafka Integration Tests")
@TestPropertySource(properties = {
    "spring.kafka.consumer.auto-offset-reset=earliest"
})
class OrderKafkaIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${order-service.kafka.listen-payment-response-topic}")
    private String paymentResponseTopic;

    @Value("${order-service.kafka.listen-restaurant-response-topic}")
    private String restaurantResponseTopic;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        orderJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should handle payment completed event successfully")
    void shouldHandlePaymentCompletedEvent() throws Exception {
        // Given: An existing order in PENDING status
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderEntity order = createTestOrderEntity(orderId, customerId, OrderStatus.PENDING);
        orderJpaRepository.save(order);

        // And: A payment completed response
        PaymentResponse paymentResponse = PaymentResponse.builder()
            .orderId(orderId)
            .paymentId(UUID.randomUUID())
            .customerId(customerId)
            .price(new BigDecimal("100.00"))
            .status("COMPLETED")
            .createdAt(ZonedDateTime.now())
            .failureMessages(List.of())
            .build();

        // When: Send payment completed message to Kafka
        String paymentJson = objectMapper.writeValueAsString(paymentResponse);
        DebeziumOutboxMessage outboxMessage = new DebeziumOutboxMessage();
        outboxMessage.payload = paymentJson;
        String message = objectMapper.writeValueAsString(outboxMessage);

        kafkaTemplate.send(paymentResponseTopic, orderId.toString(), message);

        // Then: Order status should be updated to PAID
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(500, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> {
                Optional<OrderEntity> updatedOrder = orderJpaRepository.findById(orderId);
                assertThat(updatedOrder).isPresent();
                assertThat(updatedOrder.get().getOrderStatus()).isEqualTo(OrderStatus.PAID);
            });
    }

    @Test
    @DisplayName("Should handle payment failed event successfully")
    void shouldHandlePaymentFailedEvent() throws Exception {
        // Given: An existing order in PENDING status
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderEntity order = createTestOrderEntity(orderId, customerId, OrderStatus.PENDING);
        orderJpaRepository.save(order);

        // And: A payment failed response
        PaymentResponse paymentResponse = PaymentResponse.builder()
            .orderId(orderId)
            .paymentId(UUID.randomUUID())
            .customerId(customerId)
            .price(new BigDecimal("100.00"))
            .status("FAILED")
            .createdAt(ZonedDateTime.now())
            .failureMessages(List.of("Insufficient funds"))
            .build();

        // When: Send payment failed message to Kafka
        String paymentJson = objectMapper.writeValueAsString(paymentResponse);
        DebeziumOutboxMessage outboxMessage = new DebeziumOutboxMessage();
        outboxMessage.payload = paymentJson;
        String message = objectMapper.writeValueAsString(outboxMessage);

        kafkaTemplate.send(paymentResponseTopic, orderId.toString(), message);

        // Then: Order status should be updated to CANCELLED
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(500, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> {
                Optional<OrderEntity> updatedOrder = orderJpaRepository.findById(orderId);
                assertThat(updatedOrder).isPresent();
                assertThat(updatedOrder.get().getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
            });
    }

    @Test
    @DisplayName("Should handle payment cancelled event successfully")
    void shouldHandlePaymentCancelledEvent() throws Exception {
        // Given: An existing order in CANCELLING status
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderEntity order = createTestOrderEntity(orderId, customerId, OrderStatus.CANCELLING);
        orderJpaRepository.save(order);

        // And: A payment cancelled response
        PaymentResponse paymentResponse = PaymentResponse.builder()
            .orderId(orderId)
            .paymentId(UUID.randomUUID())
            .customerId(customerId)
            .price(new BigDecimal("100.00"))
            .status("CANCELLED")
            .createdAt(ZonedDateTime.now())
            .failureMessages(List.of())
            .build();

        // When: Send payment cancelled message to Kafka
        String paymentJson = objectMapper.writeValueAsString(paymentResponse);
        DebeziumOutboxMessage outboxMessage = new DebeziumOutboxMessage();
        outboxMessage.payload = paymentJson;
        String message = objectMapper.writeValueAsString(outboxMessage);

        kafkaTemplate.send(paymentResponseTopic, orderId.toString(), message);

        // Then: Order status should be updated to CANCELLED
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(500, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> {
                Optional<OrderEntity> updatedOrder = orderJpaRepository.findById(orderId);
                assertThat(updatedOrder).isPresent();
                assertThat(updatedOrder.get().getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
            });
    }

    @Test
    @DisplayName("Should handle restaurant approved event successfully")
    void shouldHandleRestaurantApprovedEvent() throws Exception {
        // Given: An existing order in PAID status
        UUID orderId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        OrderEntity order = createTestOrderEntity(orderId, UUID.randomUUID(), OrderStatus.PAID);
        order.setRestaurantId(restaurantId);
        orderJpaRepository.save(order);

        // And: A restaurant approved response
        RestaurantResponse restaurantResponse = new RestaurantResponse();
        restaurantResponse.setApprovalId(UUID.randomUUID());
        restaurantResponse.setOrderId(orderId);
        restaurantResponse.setRestaurantId(restaurantId);
        restaurantResponse.setStatus("APPROVED");
        restaurantResponse.setCreatedAt(ZonedDateTime.now());
        restaurantResponse.setFailureMessages(List.of());

        // When: Send restaurant approved message to Kafka
        String restaurantJson = objectMapper.writeValueAsString(restaurantResponse);
        DebeziumOutboxMessage outboxMessage = new DebeziumOutboxMessage();
        outboxMessage.payload = restaurantJson;
        String message = objectMapper.writeValueAsString(outboxMessage);

        kafkaTemplate.send(restaurantResponseTopic, orderId.toString(), message);

        // Then: Order status should be updated to APPROVED
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(500, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> {
                Optional<OrderEntity> updatedOrder = orderJpaRepository.findById(orderId);
                assertThat(updatedOrder).isPresent();
                assertThat(updatedOrder.get().getOrderStatus()).isEqualTo(OrderStatus.APPROVED);
            });
    }

    @Test
    @DisplayName("Should handle restaurant rejected event successfully")
    void shouldHandleRestaurantRejectedEvent() throws Exception {
        // Given: An existing order in PAID status
        UUID orderId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        OrderEntity order = createTestOrderEntity(orderId, UUID.randomUUID(), OrderStatus.PAID);
        order.setRestaurantId(restaurantId);
        orderJpaRepository.save(order);

        // And: A restaurant rejected response
        RestaurantResponse restaurantResponse = new RestaurantResponse();
        restaurantResponse.setApprovalId(UUID.randomUUID());
        restaurantResponse.setOrderId(orderId);
        restaurantResponse.setRestaurantId(restaurantId);
        restaurantResponse.setStatus("REJECTED");
        restaurantResponse.setCreatedAt(ZonedDateTime.now());
        restaurantResponse.setFailureMessages(List.of("Restaurant is not available"));

        // When: Send restaurant rejected message to Kafka
        String restaurantJson = objectMapper.writeValueAsString(restaurantResponse);
        DebeziumOutboxMessage outboxMessage = new DebeziumOutboxMessage();
        outboxMessage.payload = restaurantJson;
        String message = objectMapper.writeValueAsString(outboxMessage);

        kafkaTemplate.send(restaurantResponseTopic, orderId.toString(), message);

        // Then: Order should be cancelled (trigger compensation)
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(500, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> {
                Optional<OrderEntity> updatedOrder = orderJpaRepository.findById(orderId);
                assertThat(updatedOrder).isPresent();
                // The order should be in CANCELLING status to trigger payment compensation
                assertThat(updatedOrder.get().getOrderStatus())
                    .isIn(OrderStatus.CANCELLING, OrderStatus.CANCELLED);
            });
    }

    @Test
    @DisplayName("Should ignore payment event for non-existent order")
    void shouldIgnorePaymentEventForNonExistentOrder() throws Exception {
        // Given: A payment response for a non-existent order
        UUID nonExistentOrderId = UUID.randomUUID();
        PaymentResponse paymentResponse = PaymentResponse.builder()
            .orderId(nonExistentOrderId)
            .paymentId(UUID.randomUUID())
            .customerId(UUID.randomUUID())
            .price(new BigDecimal("100.00"))
            .status("COMPLETED")
            .createdAt(ZonedDateTime.now())
            .failureMessages(List.of())
            .build();

        // When: Send payment message to Kafka
        String paymentJson = objectMapper.writeValueAsString(paymentResponse);
        DebeziumOutboxMessage outboxMessage = new DebeziumOutboxMessage();
        outboxMessage.payload = paymentJson;
        String message = objectMapper.writeValueAsString(outboxMessage);

        kafkaTemplate.send(paymentResponseTopic, nonExistentOrderId.toString(), message);

        // Then: No order should be created or modified
        await()
            .pollDelay(2, TimeUnit.SECONDS)
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThat(orderJpaRepository.count()).isZero();
            });
    }

    /**
     * Helper method to create a test order entity
     */
    private OrderEntity createTestOrderEntity(UUID orderId, UUID customerId, OrderStatus status) {
        return OrderEntity.builder()
            .id(orderId)
            .customerId(customerId)
            .restaurantId(UUID.randomUUID())
            .trackingId(UUID.randomUUID())
            .price(new BigDecimal("100.00"))
            .orderStatus(status)
            .createdAt(ZonedDateTime.now())
            .items(List.of())
            .build();
    }
}
