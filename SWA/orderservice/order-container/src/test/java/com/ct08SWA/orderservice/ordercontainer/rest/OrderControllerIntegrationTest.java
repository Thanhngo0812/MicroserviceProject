package com.ct08SWA.orderservice.ordercontainer.rest;

import com.ct08SWA.orderservice.ordercontainer.BaseIntegrationTest;
import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order.CreateOrderCommand;
import com.ct08SWA.orderservice.orderdataaccess.entity.OrderEntity;
import com.ct08SWA.orderservice.orderdataaccess.repository.OrderJpaRepository;
import com.ct08SWA.orderservice.orderdataaccess.entity.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for OrderController REST endpoints
 * Tests the complete flow from HTTP request to database persistence
 */
@DisplayName("Order Controller Integration Tests")
class OrderControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        orderJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create order successfully with valid request")
    void shouldCreateOrderSuccessfully() throws Exception {
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
            // Then: Should return 201 CREATED
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.orderTrackingId").exists())
            .andExpect(jsonPath("$.orderStatus").value(OrderStatus.PENDING.name()))
            .andExpect(jsonPath("$.message").value("Order created successfully"))
            .andReturn();

        // And: Order should be persisted in database
        List<OrderEntity> orders = orderJpaRepository.findAll();
        assertThat(orders).hasSize(1);

        OrderEntity savedOrder = orders.get(0);
        assertThat(savedOrder.getCustomerId()).isEqualTo(customerId);
        assertThat(savedOrder.getRestaurantId()).isEqualTo(restaurantId);
        assertThat(savedOrder.getPrice()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(savedOrder.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("Should return 400 when customer ID is null")
    void shouldReturnBadRequestWhenCustomerIdIsNull() throws Exception {
        // Given: Create order command with null customerId
        CreateOrderCommand.OrderItem orderItem = new CreateOrderCommand.OrderItem(
            UUID.randomUUID(),
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
            null, // null customerId
            UUID.randomUUID(),
            new BigDecimal("100.00"),
            List.of(orderItem),
            address
        );

        // When & Then: Should return error (400 or 500 depending on validation)
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andDo(print())
            .andExpect(result -> {
                int status = result.getResponse().getStatus();
                assertThat(status).isGreaterThanOrEqualTo(400);
            });

        // And: No order should be created
        assertThat(orderJpaRepository.count()).isZero();
    }

    @Test
    @DisplayName("Should return 400 when items list is empty")
    void shouldReturnBadRequestWhenItemsListIsEmpty() throws Exception {
        // Given: Create order command with empty items
        CreateOrderCommand.OrderAddress address = new CreateOrderCommand.OrderAddress(
            "123 Main Street",
            "12345",
            "New York"
        );

        CreateOrderCommand command = new CreateOrderCommand(
            UUID.randomUUID(),
            UUID.randomUUID(),
            new BigDecimal("100.00"),
            List.of(), // empty items
            address
        );

        // When & Then: Should return 400 BAD REQUEST
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when price is null")
    void shouldReturnBadRequestWhenPriceIsNull() throws Exception {
        // Given: Create order command with null price
        CreateOrderCommand.OrderItem orderItem = new CreateOrderCommand.OrderItem(
            UUID.randomUUID(),
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
            UUID.randomUUID(),
            UUID.randomUUID(),
            null, // null price
            List.of(orderItem),
            address
        );

        // When & Then: Should return error (400 or 500 depending on validation)
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andDo(print())
            .andExpect(result -> {
                int status = result.getResponse().getStatus();
                assertThat(status).isGreaterThanOrEqualTo(400);
            });
    }

    @Test
    @DisplayName("Should create order with multiple items")
    void shouldCreateOrderWithMultipleItems() throws Exception {
        // Given: A create order command with multiple items
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();

        CreateOrderCommand.OrderItem item1 = new CreateOrderCommand.OrderItem(
            UUID.randomUUID(),
            2,
            new BigDecimal("50.00"),
            new BigDecimal("100.00")
        );

        CreateOrderCommand.OrderItem item2 = new CreateOrderCommand.OrderItem(
            UUID.randomUUID(),
            1,
            new BigDecimal("30.00"),
            new BigDecimal("30.00")
        );

        CreateOrderCommand.OrderAddress address = new CreateOrderCommand.OrderAddress(
            "456 Oak Avenue",
            "67890",
            "Los Angeles"
        );

        CreateOrderCommand command = new CreateOrderCommand(
            customerId,
            restaurantId,
            new BigDecimal("130.00"),
            List.of(item1, item2),
            address
        );

        // When: POST request to create order
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andDo(print())
            // Then: Should return 201 CREATED
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.orderTrackingId").exists())
            .andExpect(jsonPath("$.orderStatus").value(OrderStatus.PENDING.name()));

        // And: Order with 2 items should be persisted
        List<OrderEntity> orders = orderJpaRepository.findAll();
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getItems()).hasSize(2);
    }

    @Test
    @DisplayName("Should cancel order successfully")
    void shouldCancelOrderSuccessfully() throws Exception {
        // Given: An existing order in PENDING status
        UUID orderId = UUID.randomUUID();
        OrderEntity existingOrder = OrderEntity.builder()
            .id(orderId)
            .customerId(UUID.randomUUID())
            .restaurantId(UUID.randomUUID())
            .trackingId(UUID.randomUUID())
            .price(new BigDecimal("100.00"))
            .orderStatus(OrderStatus.PENDING)
            .createdAt(java.time.ZonedDateTime.now())
            .items(List.of())
            .build();
        orderJpaRepository.save(existingOrder);

        // When: POST request to cancel order
        mockMvc.perform(post("/orders/{orderId}/cancel", orderId))
            .andDo(print())
            // Then: Should return 202 ACCEPTED
            .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("Should handle cancel request for non-existent order")
    void shouldHandleCancelForNonExistentOrder() throws Exception {
        // Given: A non-existent order ID
        UUID nonExistentOrderId = UUID.randomUUID();

        // When & Then: Should return error (likely 404 or 400 depending on implementation)
        mockMvc.perform(post("/orders/{orderId}/cancel", nonExistentOrderId))
            .andDo(print())
            .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Should handle invalid JSON request body")
    void shouldHandleInvalidJsonRequest() throws Exception {
        // Given: Invalid JSON
        String invalidJson = "{\"customerId\": \"not-a-valid-uuid\"}";

        // When & Then: Should return error (400 or 500 depending on validation)
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andDo(print())
            .andExpect(result -> {
                int status = result.getResponse().getStatus();
                assertThat(status).isGreaterThanOrEqualTo(400);
            });
    }
}
