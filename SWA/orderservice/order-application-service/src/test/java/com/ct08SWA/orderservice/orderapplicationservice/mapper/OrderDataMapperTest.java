package com.ct08SWA.orderservice.orderapplicationservice.mapper;

import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order.CreateOrderCommand;
import com.ct08SWA.orderservice.orderapplicationservice.dto.ouputdto.Order.OrderCreatedResponse;
import com.ct08SWA.orderservice.orderdomaincore.entity.Order;
import com.ct08SWA.orderservice.orderdomaincore.entity.OrderItem;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OrderDataMapper
 * Tests DTO to Entity mapping and Entity to DTO mapping
 */
@DisplayName("OrderDataMapper Tests")
class OrderDataMapperTest {

    private OrderDataMapper orderDataMapper;

    @BeforeEach
    void setUp() {
        orderDataMapper = new OrderDataMapper();
    }

    @Nested
    @DisplayName("CreateOrderCommand to Order Entity Mapping Tests")
    class CreateOrderCommandToOrderTests {

        @Test
        @DisplayName("Should map CreateOrderCommand to Order entity with all fields")
        void shouldMapCreateOrderCommandToOrderEntityWithAllFields() {
            // Given
            UUID customerId = UUID.randomUUID();
            UUID restaurantId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();
            BigDecimal price = new BigDecimal("100.00");

            CreateOrderCommand.OrderItem orderItemDto = new CreateOrderCommand.OrderItem(
                    productId,
                    2,
                    new BigDecimal("50.00"),
                    new BigDecimal("100.00")
            );

            CreateOrderCommand.OrderAddress addressDto = new CreateOrderCommand.OrderAddress(
                    "123 Main Street",
                    "12345",
                    "New York"
            );

            CreateOrderCommand command = new CreateOrderCommand(
                    customerId,
                    restaurantId,
                    price,
                    List.of(orderItemDto),
                    addressDto
            );

            // When
            Order order = orderDataMapper.createOrderCommandToOrder(command);

            // Then
            assertNotNull(order);
            assertEquals(customerId, order.getCustomerId().getValue());
            assertEquals(restaurantId, order.getRestaurantId().getValue());
            assertEquals(price, order.getPrice().getAmount());

            // Verify address mapping
            assertNotNull(order.getDeliveryAddress());
            assertEquals("123 Main Street", order.getDeliveryAddress().getStreet());
            assertEquals("12345", order.getDeliveryAddress().getPostalCode());
            assertEquals("New York", order.getDeliveryAddress().getCity());

            // Verify items mapping
            assertNotNull(order.getItems());
            assertEquals(1, order.getItems().size());
        }

        @Test
        @DisplayName("Should map order items correctly")
        void shouldMapOrderItemsCorrectly() {
            // Given
            UUID productId = UUID.randomUUID();
            CreateOrderCommand.OrderItem orderItemDto = new CreateOrderCommand.OrderItem(
                    productId,
                    3,
                    new BigDecimal("25.00"),
                    new BigDecimal("75.00")
            );

            CreateOrderCommand command = createValidCreateOrderCommand(List.of(orderItemDto));

            // When
            Order order = orderDataMapper.createOrderCommandToOrder(command);

            // Then
            OrderItem mappedItem = order.getItems().get(0);
            assertNotNull(mappedItem);
            assertEquals(productId, mappedItem.getProduct().getId().getValue());
            assertEquals(3, mappedItem.getQuantity());
            assertEquals(new BigDecimal("25.00"), mappedItem.getPrice().getAmount());
            assertEquals(new BigDecimal("75.00"), mappedItem.getSubTotal().getAmount());
        }

        @Test
        @DisplayName("Should map multiple order items")
        void shouldMapMultipleOrderItems() {
            // Given
            CreateOrderCommand.OrderItem item1 = new CreateOrderCommand.OrderItem(
                    UUID.randomUUID(),
                    2,
                    new BigDecimal("30.00"),
                    new BigDecimal("60.00")
            );

            CreateOrderCommand.OrderItem item2 = new CreateOrderCommand.OrderItem(
                    UUID.randomUUID(),
                    1,
                    new BigDecimal("40.00"),
                    new BigDecimal("40.00")
            );

            CreateOrderCommand command = createValidCreateOrderCommand(List.of(item1, item2));

            // When
            Order order = orderDataMapper.createOrderCommandToOrder(command);

            // Then
            assertEquals(2, order.getItems().size());

            OrderItem mappedItem1 = order.getItems().get(0);
            assertEquals(2, mappedItem1.getQuantity());
            assertEquals(new BigDecimal("30.00"), mappedItem1.getPrice().getAmount());

            OrderItem mappedItem2 = order.getItems().get(1);
            assertEquals(1, mappedItem2.getQuantity());
            assertEquals(new BigDecimal("40.00"), mappedItem2.getPrice().getAmount());
        }

        @Test
        @DisplayName("Should map address to StreetAddress correctly")
        void shouldMapAddressToStreetAddressCorrectly() {
            // Given
            CreateOrderCommand.OrderAddress addressDto = new CreateOrderCommand.OrderAddress(
                    "456 Oak Avenue",
                    "67890",
                    "Los Angeles"
            );

            CreateOrderCommand command = new CreateOrderCommand(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    new BigDecimal("100.00"),
                    List.of(createValidOrderItem()),
                    addressDto
            );

            // When
            Order order = orderDataMapper.createOrderCommandToOrder(command);

            // Then
            assertEquals("456 Oak Avenue", order.getDeliveryAddress().getStreet());
            assertEquals("67890", order.getDeliveryAddress().getPostalCode());
            assertEquals("Los Angeles", order.getDeliveryAddress().getCity());
        }

        @Test
        @DisplayName("Should map product with null name (name not in command)")
        void shouldMapProductWithNullName() {
            // Given
            UUID productId = UUID.randomUUID();
            CreateOrderCommand.OrderItem orderItemDto = new CreateOrderCommand.OrderItem(
                    productId,
                    1,
                    new BigDecimal("50.00"),
                    new BigDecimal("50.00")
            );

            CreateOrderCommand command = createValidCreateOrderCommand(List.of(orderItemDto));

            // When
            Order order = orderDataMapper.createOrderCommandToOrder(command);

            // Then
            OrderItem mappedItem = order.getItems().get(0);
            assertEquals(productId, mappedItem.getProduct().getId().getValue());
            assertNull(mappedItem.getProduct().getName()); // Name is null because not in command
            assertEquals(new BigDecimal("50.00"), mappedItem.getProduct().getPrice().getAmount());
        }

        @Test
        @DisplayName("Should handle decimal prices correctly")
        void shouldHandleDecimalPricesCorrectly() {
            // Given
            CreateOrderCommand.OrderItem orderItemDto = new CreateOrderCommand.OrderItem(
                    UUID.randomUUID(),
                    3,
                    new BigDecimal("15.99"),
                    new BigDecimal("47.97")
            );

            CreateOrderCommand command = new CreateOrderCommand(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    new BigDecimal("47.97"),
                    List.of(orderItemDto),
                    createValidAddress()
            );

            // When
            Order order = orderDataMapper.createOrderCommandToOrder(command);

            // Then
            assertEquals(new BigDecimal("47.97"), order.getPrice().getAmount());
            assertEquals(new BigDecimal("15.99"), order.getItems().get(0).getPrice().getAmount());
            assertEquals(new BigDecimal("47.97"), order.getItems().get(0).getSubTotal().getAmount());
        }

        @Test
        @DisplayName("Should map empty items list")
        void shouldMapEmptyItemsList() {
            // Given
            CreateOrderCommand command = new CreateOrderCommand(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    new BigDecimal("0.00"),
                    List.of(), // Empty items
                    createValidAddress()
            );

            // When
            Order order = orderDataMapper.createOrderCommandToOrder(command);

            // Then
            assertNotNull(order.getItems());
            assertTrue(order.getItems().isEmpty());
        }
    }

    @Nested
    @DisplayName("Order to OrderCreatedResponse Mapping Tests")
    class OrderToOrderCreatedResponseTests {

        @Test
        @DisplayName("Should map tracking ID to OrderCreatedResponse")
        void shouldMapTrackingIdToOrderCreatedResponse() {
            // Given
            UUID trackingId = UUID.randomUUID();

            // When
            OrderCreatedResponse response = orderDataMapper.orderToCreateOrderResponse(trackingId);

            // Then
            assertNotNull(response);
            assertEquals(trackingId, response.orderTrackingId());
            assertEquals(OrderStatus.PENDING, response.orderStatus());
        }

        @Test
        @DisplayName("Should include default message in OrderCreatedResponse")
        void shouldIncludeDefaultMessageInOrderCreatedResponse() {
            // Given
            UUID trackingId = UUID.randomUUID();

            // When
            OrderCreatedResponse response = orderDataMapper.orderToCreateOrderResponse(trackingId);

            // Then
            assertEquals("Order created successfully", response.message());
        }

        @Test
        @DisplayName("Should set status to PENDING")
        void shouldSetStatusToPending() {
            // Given
            UUID trackingId = UUID.randomUUID();

            // When
            OrderCreatedResponse response = orderDataMapper.orderToCreateOrderResponse(trackingId);

            // Then
            assertEquals(OrderStatus.PENDING, response.orderStatus());
        }

        @Test
        @DisplayName("Should handle multiple responses with different tracking IDs")
        void shouldHandleMultipleResponsesWithDifferentTrackingIds() {
            // Given
            UUID trackingId1 = UUID.randomUUID();
            UUID trackingId2 = UUID.randomUUID();

            // When
            OrderCreatedResponse response1 = orderDataMapper.orderToCreateOrderResponse(trackingId1);
            OrderCreatedResponse response2 = orderDataMapper.orderToCreateOrderResponse(trackingId2);

            // Then
            assertEquals(trackingId1, response1.orderTrackingId());
            assertEquals(trackingId2, response2.orderTrackingId());
            assertNotEquals(response1.orderTrackingId(), response2.orderTrackingId());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Complex Scenarios")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle large number of items")
        void shouldHandleLargeNumberOfItems() {
            // Given: 100 items
            List<CreateOrderCommand.OrderItem> items = new java.util.ArrayList<>();
            for (int i = 0; i < 100; i++) {
                items.add(new CreateOrderCommand.OrderItem(
                        UUID.randomUUID(),
                        1,
                        new BigDecimal("10.00"),
                        new BigDecimal("10.00")
                ));
            }

            CreateOrderCommand command = new CreateOrderCommand(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    new BigDecimal("1000.00"),
                    items,
                    createValidAddress()
            );

            // When
            Order order = orderDataMapper.createOrderCommandToOrder(command);

            // Then
            assertEquals(100, order.getItems().size());
        }

        @Test
        @DisplayName("Should handle very large prices")
        void shouldHandleVeryLargePrices() {
            // Given
            BigDecimal largePrice = new BigDecimal("999999.99");
            CreateOrderCommand.OrderItem orderItemDto = new CreateOrderCommand.OrderItem(
                    UUID.randomUUID(),
                    1,
                    largePrice,
                    largePrice
            );

            CreateOrderCommand command = createValidCreateOrderCommand(List.of(orderItemDto));

            // When
            Order order = orderDataMapper.createOrderCommandToOrder(command);

            // Then
            assertEquals(largePrice, order.getItems().get(0).getPrice().getAmount());
        }

        @Test
        @DisplayName("Should handle very small prices")
        void shouldHandleVerySmallPrices() {
            // Given
            BigDecimal smallPrice = new BigDecimal("0.01");
            CreateOrderCommand.OrderItem orderItemDto = new CreateOrderCommand.OrderItem(
                    UUID.randomUUID(),
                    1,
                    smallPrice,
                    smallPrice
            );

            CreateOrderCommand command = createValidCreateOrderCommand(List.of(orderItemDto));

            // When
            Order order = orderDataMapper.createOrderCommandToOrder(command);

            // Then
            assertEquals(smallPrice, order.getItems().get(0).getPrice().getAmount());
        }

        @Test
        @DisplayName("Should handle international characters in address")
        void shouldHandleInternationalCharactersInAddress() {
            // Given
            CreateOrderCommand.OrderAddress addressDto = new CreateOrderCommand.OrderAddress(
                    "Rue de l'Église #45",
                    "75001",
                    "Paris"
            );

            CreateOrderCommand command = new CreateOrderCommand(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    new BigDecimal("100.00"),
                    List.of(createValidOrderItem()),
                    addressDto
            );

            // When
            Order order = orderDataMapper.createOrderCommandToOrder(command);

            // Then
            assertEquals("Rue de l'Église #45", order.getDeliveryAddress().getStreet());
            assertEquals("Paris", order.getDeliveryAddress().getCity());
        }

        @Test
        @DisplayName("Should handle large quantity")
        void shouldHandleLargeQuantity() {
            // Given
            CreateOrderCommand.OrderItem orderItemDto = new CreateOrderCommand.OrderItem(
                    UUID.randomUUID(),
                    1000, // Large quantity
                    new BigDecimal("1.00"),
                    new BigDecimal("1000.00")
            );

            CreateOrderCommand command = createValidCreateOrderCommand(List.of(orderItemDto));

            // When
            Order order = orderDataMapper.createOrderCommandToOrder(command);

            // Then
            assertEquals(1000, order.getItems().get(0).getQuantity());
        }
    }

    @Nested
    @DisplayName("Immutability and Independence Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should create independent Order instance")
        void shouldCreateIndependentOrderInstance() {
            // Given
            CreateOrderCommand command = createValidCreateOrderCommand(
                    List.of(createValidOrderItem())
            );

            // When
            Order order1 = orderDataMapper.createOrderCommandToOrder(command);
            Order order2 = orderDataMapper.createOrderCommandToOrder(command);

            // Then
            assertNotSame(order1, order2);
            assertEquals(order1.getCustomerId(), order2.getCustomerId());
            assertEquals(order1.getPrice(), order2.getPrice());
        }

        @Test
        @DisplayName("Should create independent OrderCreatedResponse instances")
        void shouldCreateIndependentOrderCreatedResponseInstances() {
            // Given
            UUID trackingId = UUID.randomUUID();

            // When
            OrderCreatedResponse response1 = orderDataMapper.orderToCreateOrderResponse(trackingId);
            OrderCreatedResponse response2 = orderDataMapper.orderToCreateOrderResponse(trackingId);

            // Then
            assertNotSame(response1, response2);
            assertEquals(response1.orderTrackingId(), response2.orderTrackingId());
        }
    }

    // Helper methods
    private CreateOrderCommand createValidCreateOrderCommand(List<CreateOrderCommand.OrderItem> items) {
        return new CreateOrderCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100.00"),
                items,
                createValidAddress()
        );
    }

    private CreateOrderCommand.OrderItem createValidOrderItem() {
        return new CreateOrderCommand.OrderItem(
                UUID.randomUUID(),
                2,
                new BigDecimal("50.00"),
                new BigDecimal("100.00")
        );
    }

    private CreateOrderCommand.OrderAddress createValidAddress() {
        return new CreateOrderCommand.OrderAddress(
                "123 Main Street",
                "12345",
                "New York"
        );
    }
}
