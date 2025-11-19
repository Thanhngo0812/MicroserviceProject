package com.ct08SWA.orderservice.ordercontainer.saga;

import com.ct08SWA.orderservice.ordercontainer.BaseIntegrationTest;
import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order.CreateOrderCommand;
import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Payment.PaymentResponse;
import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Restaurant.RestaurantResponse;
import com.ct08SWA.orderservice.orderdataaccess.entity.OrderEntity;
import com.ct08SWA.orderservice.orderdataaccess.repository.OrderJpaRepository;
import com.ct08SWA.orderservice.orderdataaccess.entity.OrderStatus;
import com.ct08SWA.orderservice.ordermessaging.dto.DebeziumOutboxMessage;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end integration tests for Order SAGA flow
 * Tests the complete orchestration from order creation through payment and restaurant approval
 */
@DisplayName("Order SAGA Integration Tests")
class OrderSagaIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

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
    @DisplayName("Should complete full happy path: Order -> Payment -> Restaurant -> Approved")
    void shouldCompleteFullHappyPathSaga() throws Exception {
        // ============ STEP 1: CREATE ORDER ============
        // Given: A valid create order command
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateOrderCommand.OrderItem orderItem = new CreateOrderCommand.OrderItem(
            productId,
            2,
            new BigDecimal("50.00"),
            new BigDecimal("100.00")
        );

        CreateOrderCommand.OrderAddress address = new CreateOrderCommand.OrderAddress(
            "123 Main Street",
            "12345",
            "New York"
        );

        CreateOrderCommand command = new CreateOrderCommand(
            customerId,
            restaurantId,
            new BigDecimal("100.00"),
            List.of(orderItem),
            address
        );

        // When: POST request to create order
        MvcResult result = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.orderTrackingId").exists())
            .andExpect(jsonPath("$.orderStatus").value(OrderStatus.PENDING.name()))
            .andReturn();

        // Extract order tracking ID
        String responseContent = result.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseContent);
        UUID orderTrackingId = UUID.fromString(responseJson.get("orderTrackingId").asText());

        // Then: Order should be in PENDING status
        Optional<OrderEntity> pendingOrder = orderJpaRepository.findAll().stream()
            .filter(order -> order.getTrackingId().equals(orderTrackingId))
            .findFirst();
        assertThat(pendingOrder).isPresent();
        assertThat(pendingOrder.get().getOrderStatus()).isEqualTo(OrderStatus.PENDING);

        UUID orderId = pendingOrder.get().getId();

        // ============ STEP 2: PAYMENT COMPLETED ============
        // Given: Payment service processes payment successfully
        PaymentResponse paymentResponse = PaymentResponse.builder()
            .orderId(orderId)
            .paymentId(UUID.randomUUID())
            .customerId(customerId)
            .price(new BigDecimal("100.00"))
            .status("COMPLETED")
            .createdAt(ZonedDateTime.now())
            .failureMessages(List.of())
            .build();

        // When: Send payment completed event
        String paymentJson = objectMapper.writeValueAsString(paymentResponse);
        DebeziumOutboxMessage paymentOutbox = new DebeziumOutboxMessage();
        paymentOutbox.payload = paymentJson;
        String paymentMessage = objectMapper.writeValueAsString(paymentOutbox);
        kafkaTemplate.send(paymentResponseTopic, orderId.toString(), paymentMessage);

        // Then: Order should transition to PAID status
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(500, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> {
                Optional<OrderEntity> paidOrder = orderJpaRepository.findById(orderId);
                assertThat(paidOrder).isPresent();
                assertThat(paidOrder.get().getOrderStatus()).isEqualTo(OrderStatus.PAID);
            });

        // ============ STEP 3: RESTAURANT APPROVED ============
        // Given: Restaurant service approves the order
        RestaurantResponse restaurantResponse = new RestaurantResponse();
        restaurantResponse.setApprovalId(UUID.randomUUID());
        restaurantResponse.setOrderId(orderId);
        restaurantResponse.setRestaurantId(restaurantId);
        restaurantResponse.setStatus("APPROVED");
        restaurantResponse.setCreatedAt(ZonedDateTime.now());
        restaurantResponse.setFailureMessages(List.of());

        // When: Send restaurant approved event
        String restaurantJson = objectMapper.writeValueAsString(restaurantResponse);
        DebeziumOutboxMessage restaurantOutbox = new DebeziumOutboxMessage();
        restaurantOutbox.payload = restaurantJson;
        String restaurantMessage = objectMapper.writeValueAsString(restaurantOutbox);
        kafkaTemplate.send(restaurantResponseTopic, orderId.toString(), restaurantMessage);

        // Then: Order should transition to APPROVED status (final state)
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(500, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> {
                Optional<OrderEntity> approvedOrder = orderJpaRepository.findById(orderId);
                assertThat(approvedOrder).isPresent();
                assertThat(approvedOrder.get().getOrderStatus()).isEqualTo(OrderStatus.APPROVED);
            });

        // Verify final state
        Optional<OrderEntity> finalOrder = orderJpaRepository.findById(orderId);
        assertThat(finalOrder).isPresent();
        assertThat(finalOrder.get().getCustomerId()).isEqualTo(customerId);
        assertThat(finalOrder.get().getRestaurantId()).isEqualTo(restaurantId);
        assertThat(finalOrder.get().getPrice()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(finalOrder.get().getOrderStatus()).isEqualTo(OrderStatus.APPROVED);
    }

    @Test
    @DisplayName("Should handle compensation flow: Order -> Payment Failed -> Cancelled")
    void shouldHandlePaymentFailureCompensation() throws Exception {
        // ============ STEP 1: CREATE ORDER ============
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();

        CreateOrderCommand.OrderItem orderItem = new CreateOrderCommand.OrderItem(
            UUID.randomUUID(),
            1,
            new BigDecimal("100.00"),
            new BigDecimal("100.00")
        );

        CreateOrderCommand.OrderAddress address = new CreateOrderCommand.OrderAddress(
            "456 Oak Street",
            "67890",
            "Los Angeles"
        );

        CreateOrderCommand command = new CreateOrderCommand(
            customerId,
            restaurantId,
            new BigDecimal("100.00"),
            List.of(orderItem),
            address
        );

        // When: Create order
        MvcResult result = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isCreated())
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseContent);
        UUID orderTrackingId = UUID.fromString(responseJson.get("orderTrackingId").asText());

        Optional<OrderEntity> pendingOrder = orderJpaRepository.findAll().stream()
            .filter(order -> order.getTrackingId().equals(orderTrackingId))
            .findFirst();
        assertThat(pendingOrder).isPresent();
        UUID orderId = pendingOrder.get().getId();

        // ============ STEP 2: PAYMENT FAILED ============
        // Given: Payment service fails the payment
        PaymentResponse paymentResponse = PaymentResponse.builder()
            .orderId(orderId)
            .paymentId(UUID.randomUUID())
            .customerId(customerId)
            .price(new BigDecimal("100.00"))
            .status("FAILED")
            .createdAt(ZonedDateTime.now())
            .failureMessages(List.of("Insufficient funds", "Card declined"))
            .build();

        // When: Send payment failed event
        String paymentJson = objectMapper.writeValueAsString(paymentResponse);
        DebeziumOutboxMessage paymentOutbox = new DebeziumOutboxMessage();
        paymentOutbox.payload = paymentJson;
        String paymentMessage = objectMapper.writeValueAsString(paymentOutbox);
        kafkaTemplate.send(paymentResponseTopic, orderId.toString(), paymentMessage);

        // Then: Order should be cancelled
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(500, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> {
                Optional<OrderEntity> cancelledOrder = orderJpaRepository.findById(orderId);
                assertThat(cancelledOrder).isPresent();
                assertThat(cancelledOrder.get().getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
            });
    }

    @Test
    @DisplayName("Should handle compensation flow: Order -> Payment OK -> Restaurant Rejected -> Cancelled")
    void shouldHandleRestaurantRejectionCompensation() throws Exception {
        // ============ STEP 1: CREATE ORDER ============
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();

        CreateOrderCommand.OrderItem orderItem = new CreateOrderCommand.OrderItem(
            UUID.randomUUID(),
            3,
            new BigDecimal("25.00"),
            new BigDecimal("75.00")
        );

        CreateOrderCommand.OrderAddress address = new CreateOrderCommand.OrderAddress(
            "789 Pine Street",
            "54321",
            "Chicago"
        );

        CreateOrderCommand command = new CreateOrderCommand(
            customerId,
            restaurantId,
            new BigDecimal("75.00"),
            List.of(orderItem),
            address
        );

        // Create order
        MvcResult result = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isCreated())
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseContent);
        UUID orderTrackingId = UUID.fromString(responseJson.get("orderTrackingId").asText());

        Optional<OrderEntity> pendingOrder = orderJpaRepository.findAll().stream()
            .filter(order -> order.getTrackingId().equals(orderTrackingId))
            .findFirst();
        UUID orderId = pendingOrder.get().getId();

        // ============ STEP 2: PAYMENT COMPLETED ============
        PaymentResponse paymentResponse = PaymentResponse.builder()
            .orderId(orderId)
            .paymentId(UUID.randomUUID())
            .customerId(customerId)
            .price(new BigDecimal("75.00"))
            .status("COMPLETED")
            .createdAt(ZonedDateTime.now())
            .failureMessages(List.of())
            .build();

        String paymentJson = objectMapper.writeValueAsString(paymentResponse);
        DebeziumOutboxMessage paymentOutbox = new DebeziumOutboxMessage();
        paymentOutbox.payload = paymentJson;
        kafkaTemplate.send(paymentResponseTopic, orderId.toString(), objectMapper.writeValueAsString(paymentOutbox));

        // Wait for PAID status
        await()
            .atMost(10, TimeUnit.SECONDS)
            .until(() -> {
                Optional<OrderEntity> order = orderJpaRepository.findById(orderId);
                return order.isPresent() && order.get().getOrderStatus() == com.ct08SWA.orderservice.orderdataaccess.entity.OrderStatus.PAID;
            });

        // ============ STEP 3: RESTAURANT REJECTED ============
        RestaurantResponse restaurantResponse = new RestaurantResponse();
        restaurantResponse.setApprovalId(UUID.randomUUID());
        restaurantResponse.setOrderId(orderId);
        restaurantResponse.setRestaurantId(restaurantId);
        restaurantResponse.setStatus("REJECTED");
        restaurantResponse.setCreatedAt(ZonedDateTime.now());
        restaurantResponse.setFailureMessages(List.of("Restaurant is closed", "Out of ingredients"));

        String restaurantJson = objectMapper.writeValueAsString(restaurantResponse);
        DebeziumOutboxMessage restaurantOutbox = new DebeziumOutboxMessage();
        restaurantOutbox.payload = restaurantJson;
        kafkaTemplate.send(restaurantResponseTopic, orderId.toString(), objectMapper.writeValueAsString(restaurantOutbox));

        // Then: Order should trigger compensation and move to CANCELLING or CANCELLED
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(500, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> {
                Optional<OrderEntity> order = orderJpaRepository.findById(orderId);
                assertThat(order).isPresent();
                assertThat(order.get().getOrderStatus())
                    .isIn(OrderStatus.CANCELLING, OrderStatus.CANCELLED);
            });
    }

    @Test
    @DisplayName("Should handle user-initiated order cancellation")
    void shouldHandleUserInitiatedCancellation() throws Exception {
        // ============ STEP 1: CREATE ORDER ============
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();

        CreateOrderCommand.OrderItem orderItem = new CreateOrderCommand.OrderItem(
            UUID.randomUUID(),
            1,
            new BigDecimal("50.00"),
            new BigDecimal("50.00")
        );

        CreateOrderCommand.OrderAddress address = new CreateOrderCommand.OrderAddress(
            "321 Elm Street",
            "11111",
            "Boston"
        );

        CreateOrderCommand command = new CreateOrderCommand(
            customerId,
            restaurantId,
            new BigDecimal("50.00"),
            List.of(orderItem),
            address
        );

        // Create order
        MvcResult createResult = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isCreated())
            .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseContent);
        UUID orderTrackingId = UUID.fromString(responseJson.get("orderTrackingId").asText());

        Optional<OrderEntity> pendingOrder = orderJpaRepository.findAll().stream()
            .filter(order -> order.getTrackingId().equals(orderTrackingId))
            .findFirst();
        UUID orderId = pendingOrder.get().getId();

        // ============ STEP 2: USER CANCELS ORDER ============
        // When: User initiates order cancellation
        mockMvc.perform(post("/orders/{orderId}/cancel", orderId))
            .andExpect(status().isAccepted());

        // Then: Order should move to CANCELLING status
        await()
            .atMost(5, TimeUnit.SECONDS)
            .pollInterval(500, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> {
                Optional<OrderEntity> order = orderJpaRepository.findById(orderId);
                assertThat(order).isPresent();
                assertThat(order.get().getOrderStatus())
                    .isIn(OrderStatus.CANCELLING, OrderStatus.CANCELLED);
            });
    }

    @Test
    @DisplayName("Should handle multiple concurrent order creations")
    void shouldHandleMultipleConcurrentOrders() throws Exception {
        // Given: Multiple order creation requests
        int numberOfOrders = 5;

        for (int i = 0; i < numberOfOrders; i++) {
            CreateOrderCommand.OrderItem orderItem = new CreateOrderCommand.OrderItem(
                UUID.randomUUID(),
                1,
                new BigDecimal("20.00"),
                new BigDecimal("20.00")
            );

            CreateOrderCommand.OrderAddress address = new CreateOrderCommand.OrderAddress(
                "Street " + i,
                "ZIP" + i,
                "City" + i
            );

            CreateOrderCommand command = new CreateOrderCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("20.00"),
                List.of(orderItem),
                address
            );

            // When: Create multiple orders
            mockMvc.perform(post("/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated());
        }

        // Then: All orders should be created in PENDING status
        await()
            .atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                List<OrderEntity> orders = orderJpaRepository.findAll();
                assertThat(orders).hasSize(numberOfOrders);
                assertThat(orders)
                    .allMatch(order -> order.getOrderStatus() == OrderStatus.PENDING);
            });
    }
}
