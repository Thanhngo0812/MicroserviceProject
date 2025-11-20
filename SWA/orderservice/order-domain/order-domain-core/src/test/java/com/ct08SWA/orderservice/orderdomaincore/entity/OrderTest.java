package com.ct08SWA.orderservice.orderdomaincore.entity;

import com.ct08SWA.orderservice.orderdomaincore.exception.OrderDomainException;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Order entity
 * Tests domain logic, validation rules, and state transitions
 */
@DisplayName("Order Entity Tests")
class OrderTest {

    private UUID customerId;
    private UUID restaurantId;
    private UUID productId;
    private StreetAddress deliveryAddress;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();
        productId = UUID.randomUUID();
        deliveryAddress = new StreetAddress("123 Main St", "12345", "New York");
    }

    @Nested
    @DisplayName("Order Initialization Tests")
    class InitializationTests {

        @Test
        @DisplayName("Should initialize order with generated IDs and PENDING status")
        void shouldInitializeOrderSuccessfully() {
            // Given
            Order order = createValidOrder();

            // When
            order.initializeOrder();

            // Then
            assertNotNull(order.getId());
            assertNotNull(order.getTrackingId());
            assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        }

        @Test
        @DisplayName("Should initialize order items with sequential IDs")
        void shouldInitializeOrderItemsWithSequentialIds() {
            // Given
            Product product1 = new Product(new ProductId(UUID.randomUUID()), "Pizza", new Money(new BigDecimal("10.00")));
            Product product2 = new Product(new ProductId(UUID.randomUUID()), "Burger", new Money(new BigDecimal("15.00")));

            OrderItem item1 = createOrderItem(product1, 1, new BigDecimal("10.00"), new BigDecimal("10.00"));
            OrderItem item2 = createOrderItem(product2, 1, new BigDecimal("15.00"), new BigDecimal("15.00"));

            Order order = Order.builder()
                    .customerId(new CustomerId(customerId))
                    .restaurantId(new RestaurantId(restaurantId))
                    .deliveryAddress(deliveryAddress)
                    .price(new Money(new BigDecimal("25.00")))
                    .items(List.of(item1, item2))
                    .build();

            // When
            order.initializeOrder();

            // Then
            assertEquals(1L, order.getItems().get(0).getId());
            assertEquals(2L, order.getItems().get(1).getId());
            assertEquals(order.getId(), order.getItems().get(0).getOrderId());
            assertEquals(order.getId(), order.getItems().get(1).getOrderId());
        }

        @Test
        @DisplayName("Should initialize failure messages as empty list")
        void shouldInitializeFailureMessagesAsEmptyList() {
            // Given
            Order order = createValidOrder();

            // When
            order.initializeOrder();

            // Then
            assertNotNull(order.getFailureMessages());
            assertTrue(order.getFailureMessages().isEmpty());
        }
    }

    @Nested
    @DisplayName("Order Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate order successfully with correct data")
        void shouldValidateOrderSuccessfully() {
            // Given
            Order order = createValidOrder();
            order.initializeOrder();

            // When & Then
            assertDoesNotThrow(() -> order.validateOrder());
        }

        @Test
        @DisplayName("Should throw exception when total price is null")
        void shouldThrowExceptionWhenPriceIsNull() {
            // Given
            Product product = createProduct(new BigDecimal("50.00"));
            OrderItem item = createOrderItem(product, 2, new BigDecimal("50.00"), new BigDecimal("100.00"));

            Order order = Order.builder()
                    .customerId(new CustomerId(customerId))
                    .restaurantId(new RestaurantId(restaurantId))
                    .deliveryAddress(deliveryAddress)
                    .price(null) // null price
                    .items(List.of(item))
                    .build();
            order.initializeOrder();

            // When & Then
            OrderDomainException exception = assertThrows(OrderDomainException.class,
                    () -> order.validateOrder());
            assertTrue(exception.getMessage().contains("Total price must be greater than zero"));
        }

        @Test
        @DisplayName("Should throw exception when total price is zero")
        void shouldThrowExceptionWhenPriceIsZero() {
            // Given
            Product product = createProduct(new BigDecimal("50.00"));
            OrderItem item = createOrderItem(product, 2, new BigDecimal("50.00"), new BigDecimal("100.00"));

            Order order = Order.builder()
                    .customerId(new CustomerId(customerId))
                    .restaurantId(new RestaurantId(restaurantId))
                    .deliveryAddress(deliveryAddress)
                    .price(new Money(BigDecimal.ZERO)) // zero price
                    .items(List.of(item))
                    .build();
            order.initializeOrder();

            // When & Then
            OrderDomainException exception = assertThrows(OrderDomainException.class,
                    () -> order.validateOrder());
            assertTrue(exception.getMessage().contains("Total price must be greater than zero"));
        }

        @Test
        @DisplayName("Should throw exception when total price is negative")
        void shouldThrowExceptionWhenPriceIsNegative() {
            // Given
            Product product = createProduct(new BigDecimal("50.00"));
            OrderItem item = createOrderItem(product, 2, new BigDecimal("50.00"), new BigDecimal("100.00"));

            Order order = Order.builder()
                    .customerId(new CustomerId(customerId))
                    .restaurantId(new RestaurantId(restaurantId))
                    .deliveryAddress(deliveryAddress)
                    .price(new Money(new BigDecimal("-100.00"))) // negative price
                    .items(List.of(item))
                    .build();
            order.initializeOrder();

            // When & Then
            OrderDomainException exception = assertThrows(OrderDomainException.class,
                    () -> order.validateOrder());
            assertTrue(exception.getMessage().contains("Total price must be greater than zero"));
        }

        @Test
        @DisplayName("Should throw exception when order total does not match items total")
        void shouldThrowExceptionWhenTotalPriceDoesNotMatchItemsTotal() {
            // Given
            Product product = createProduct(new BigDecimal("50.00"));
            OrderItem item = createOrderItem(product, 2, new BigDecimal("50.00"), new BigDecimal("100.00"));

            Order order = Order.builder()
                    .customerId(new CustomerId(customerId))
                    .restaurantId(new RestaurantId(restaurantId))
                    .deliveryAddress(deliveryAddress)
                    .price(new Money(new BigDecimal("150.00"))) // Wrong total: should be 100
                    .items(List.of(item))
                    .build();
            order.initializeOrder();

            // When & Then
            OrderDomainException exception = assertThrows(OrderDomainException.class,
                    () -> order.validateOrder());
            assertTrue(exception.getMessage().contains("Total price"));
            assertTrue(exception.getMessage().contains("is not equal to Order items total"));
        }

        @Test
        @DisplayName("Should throw exception when item price does not match product price")
        void shouldThrowExceptionWhenItemPriceDoesNotMatchProductPrice() {
            // Given
            Product product = createProduct(new BigDecimal("50.00"));
            // Item price is 40, but product price is 50
            OrderItem item = createOrderItem(product, 2, new BigDecimal("40.00"), new BigDecimal("80.00"));

            Order order = Order.builder()
                    .customerId(new CustomerId(customerId))
                    .restaurantId(new RestaurantId(restaurantId))
                    .deliveryAddress(deliveryAddress)
                    .price(new Money(new BigDecimal("80.00")))
                    .items(List.of(item))
                    .build();
            order.initializeOrder();

            // When & Then
            OrderDomainException exception = assertThrows(OrderDomainException.class,
                    () -> order.validateOrder());
            assertTrue(exception.getMessage().contains("Order item price"));
            assertTrue(exception.getMessage().contains("is not valid for product"));
        }

        @Test
        @DisplayName("Should throw exception when item subtotal is incorrect")
        void shouldThrowExceptionWhenItemSubtotalIsIncorrect() {
            // Given
            Product product = createProduct(new BigDecimal("50.00"));
            // Subtotal is 80, but should be 50 * 2 = 100
            OrderItem item = createOrderItem(product, 2, new BigDecimal("50.00"), new BigDecimal("80.00"));

            Order order = Order.builder()
                    .customerId(new CustomerId(customerId))
                    .restaurantId(new RestaurantId(restaurantId))
                    .deliveryAddress(deliveryAddress)
                    .price(new Money(new BigDecimal("80.00")))
                    .items(List.of(item))
                    .build();
            order.initializeOrder();

            // When & Then
            OrderDomainException exception = assertThrows(OrderDomainException.class,
                    () -> order.validateOrder());
            assertTrue(exception.getMessage().contains("Order item price"));
        }

        @Test
        @DisplayName("Should validate order with multiple items successfully")
        void shouldValidateOrderWithMultipleItems() {
            // Given
            Product product1 = createProduct(new BigDecimal("30.00"));
            Product product2 = createProduct(new BigDecimal("70.00"));

            OrderItem item1 = createOrderItem(product1, 2, new BigDecimal("30.00"), new BigDecimal("60.00"));
            OrderItem item2 = createOrderItem(product2, 1, new BigDecimal("70.00"), new BigDecimal("70.00"));

            Order order = Order.builder()
                    .customerId(new CustomerId(customerId))
                    .restaurantId(new RestaurantId(restaurantId))
                    .deliveryAddress(deliveryAddress)
                    .price(new Money(new BigDecimal("130.00"))) // 60 + 70 = 130
                    .items(List.of(item1, item2))
                    .build();
            order.initializeOrder();

            // When & Then
            assertDoesNotThrow(() -> order.validateOrder());
        }
    }

    @Nested
    @DisplayName("State Transition Tests")
    class StateTransitionTests {

        @Test
        @DisplayName("Should transition from PENDING to PAID successfully")
        void shouldTransitionFromPendingToPaid() {
            // Given
            Order order = createValidOrder();
            order.initializeOrder();

            // When
            order.pay();

            // Then
            assertEquals(OrderStatus.PAID, order.getOrderStatus());
        }

        @Test
        @DisplayName("Should throw exception when paying non-PENDING order")
        void shouldThrowExceptionWhenPayingNonPendingOrder() {
            // Given
            Order order = createValidOrder();
            order.initializeOrder();
            order.pay(); // Already PAID

            // When & Then
            OrderDomainException exception = assertThrows(OrderDomainException.class,
                    () -> order.pay());
            assertTrue(exception.getMessage().contains("Order is not in correct state for pay operation"));
        }

        @Test
        @DisplayName("Should transition from PAID to APPROVED successfully")
        void shouldTransitionFromPaidToApproved() {
            // Given
            Order order = createValidOrder();
            order.initializeOrder();
            order.pay();

            // When
            order.approve();

            // Then
            assertEquals(OrderStatus.APPROVED, order.getOrderStatus());
        }

        @Test
        @DisplayName("Should throw exception when approving non-PAID order")
        void shouldThrowExceptionWhenApprovingNonPaidOrder() {
            // Given
            Order order = createValidOrder();
            order.initializeOrder(); // PENDING

            // When & Then
            OrderDomainException exception = assertThrows(OrderDomainException.class,
                    () -> order.approve());
            assertTrue(exception.getMessage().contains("Order is not in correct state for approve operation"));
        }

        @Test
        @DisplayName("Should transition from PENDING to CANCELLED successfully")
        void shouldTransitionFromPendingToCancelled() {
            // Given
            Order order = createValidOrder();
            order.initializeOrder(); // PENDING
            List<String> failureMessages = List.of("User cancelled");

            // When
            order.cancel(failureMessages);

            // Then
            assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
            assertTrue(order.getFailureMessages().contains("User cancelled"));
        }

        @Test
        @DisplayName("Should transition from CANCELLING to CANCELLED successfully")
        void shouldTransitionFromCancellingToCancelled() {
            // Given
            Order order = createValidOrder();
            order.initializeOrder();
            order.pay();
            order.initCancel(List.of("Restaurant rejected"));

            // When
            order.cancel(List.of("Payment refunded"));

            // Then
            assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
            assertEquals(2, order.getFailureMessages().size());
            assertTrue(order.getFailureMessages().contains("Restaurant rejected"));
            assertTrue(order.getFailureMessages().contains("Payment refunded"));
        }

        @Test
        @DisplayName("Should throw exception when cancelling PAID order")
        void shouldThrowExceptionWhenCancellingPaidOrder() {
            // Given
            Order order = createValidOrder();
            order.initializeOrder();
            order.pay(); // PAID

            // When & Then
            OrderDomainException exception = assertThrows(OrderDomainException.class,
                    () -> order.cancel(List.of("Cannot cancel")));
            assertTrue(exception.getMessage().contains("Order is not in correct state for cancel operation"));
        }

        @Test
        @DisplayName("Should transition from PAID to CANCELLING successfully")
        void shouldTransitionFromPaidToCancelling() {
            // Given
            Order order = createValidOrder();
            order.initializeOrder();
            order.pay();
            List<String> failureMessages = List.of("Restaurant unavailable");

            // When
            order.initCancel(failureMessages);

            // Then
            assertEquals(OrderStatus.CANCELLING, order.getOrderStatus());
            assertTrue(order.getFailureMessages().contains("Restaurant unavailable"));
        }

        @Test
        @DisplayName("Should throw exception when init cancel on non-PAID order")
        void shouldThrowExceptionWhenInitCancelOnNonPaidOrder() {
            // Given
            Order order = createValidOrder();
            order.initializeOrder(); // PENDING

            // When & Then
            OrderDomainException exception = assertThrows(OrderDomainException.class,
                    () -> order.initCancel(List.of("Error")));
            assertTrue(exception.getMessage().contains("Order is not in correct state for initCancel operation"));
        }

        @Test
        @DisplayName("Should set PAYMENT_FAILED status")
        void shouldSetPaymentFailedStatus() {
            // Given
            Order order = createValidOrder();
            order.initializeOrder();
            List<String> failureMessages = List.of("Insufficient funds", "Card declined");

            // When
            order.paymentfailed(failureMessages);

            // Then
            assertEquals(OrderStatus.PAYMENT_FAILED, order.getOrderStatus());
            assertEquals(2, order.getFailureMessages().size());
            assertTrue(order.getFailureMessages().contains("Insufficient funds"));
            assertTrue(order.getFailureMessages().contains("Card declined"));
        }
    }

    @Nested
    @DisplayName("Failure Messages Tests")
    class FailureMessagesTests {

        @Test
        @DisplayName("Should add failure messages correctly")
        void shouldAddFailureMessagesCorrectly() {
            // Given
            Order order = createValidOrder();
            order.initializeOrder();
            order.paymentfailed(List.of("Error 1"));

            // When
            order.paymentfailed(List.of("Error 2", "Error 3"));

            // Then
            assertEquals(3, order.getFailureMessages().size());
            assertTrue(order.getFailureMessages().contains("Error 1"));
            assertTrue(order.getFailureMessages().contains("Error 2"));
            assertTrue(order.getFailureMessages().contains("Error 3"));
        }

        @Test
        @DisplayName("Should filter out empty failure messages")
        void shouldFilterOutEmptyFailureMessages() {
            // Given
            Order order = createValidOrder();
            order.initializeOrder();

            // When
            order.paymentfailed(List.of("Error 1", "", "Error 2", ""));

            // Then
            assertEquals(2, order.getFailureMessages().size());
            assertTrue(order.getFailureMessages().contains("Error 1"));
            assertTrue(order.getFailureMessages().contains("Error 2"));
        }

        @Test
        @DisplayName("Should handle null failure messages gracefully")
        void shouldHandleNullFailureMessagesGracefully() {
            // Given
            Order order = createValidOrder();
            order.initializeOrder();

            // When & Then - Should not throw exception
            assertDoesNotThrow(() -> order.cancel(null));
        }
    }

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should build order with all fields")
        void shouldBuildOrderWithAllFields() {
            // Given
            Product product = createProduct(new BigDecimal("100.00"));
            OrderItem item = createOrderItem(product, 1, new BigDecimal("100.00"), new BigDecimal("100.00"));

            // When
            Order order = Order.builder()
                    .customerId(new CustomerId(customerId))
                    .restaurantId(new RestaurantId(restaurantId))
                    .deliveryAddress(deliveryAddress)
                    .price(new Money(new BigDecimal("100.00")))
                    .items(List.of(item))
                    .build();

            // Then
            assertNotNull(order);
            assertEquals(customerId, order.getCustomerId().getValue());
            assertEquals(restaurantId, order.getRestaurantId().getValue());
            assertEquals(deliveryAddress, order.getDeliveryAddress());
            assertEquals(new BigDecimal("100.00"), order.getPrice().getAmount());
            assertEquals(1, order.getItems().size());
        }

        @Test
        @DisplayName("Should copy failure messages list to prevent external modifications")
        void shouldCopyFailureMessagesListToPreventExternalModifications() {
            // Given
            List<String> externalList = new ArrayList<>();
            externalList.add("Error 1");

            Product product = createProduct(new BigDecimal("100.00"));
            OrderItem item = createOrderItem(product, 1, new BigDecimal("100.00"), new BigDecimal("100.00"));

            Order order = Order.builder()
                    .customerId(new CustomerId(customerId))
                    .restaurantId(new RestaurantId(restaurantId))
                    .deliveryAddress(deliveryAddress)
                    .price(new Money(new BigDecimal("100.00")))
                    .items(List.of(item))
                    .failureMessages(externalList)
                    .build();

            // When - Modify external list
            externalList.add("Error 2");

            // Then - Order's failure messages should not be affected
            assertEquals(1, order.getFailureMessages().size());
            assertTrue(order.getFailureMessages().contains("Error 1"));
            assertFalse(order.getFailureMessages().contains("Error 2"));
        }
    }

    // Helper methods
    private Order createValidOrder() {
        Product product = createProduct(new BigDecimal("50.00"));
        OrderItem item = createOrderItem(product, 2, new BigDecimal("50.00"), new BigDecimal("100.00"));

        return Order.builder()
                .customerId(new CustomerId(customerId))
                .restaurantId(new RestaurantId(restaurantId))
                .deliveryAddress(deliveryAddress)
                .price(new Money(new BigDecimal("100.00")))
                .items(List.of(item))
                .build();
    }

    private Product createProduct(BigDecimal price) {
        return new Product(new ProductId(productId), "Test Product", new Money(price));
    }

    private OrderItem createOrderItem(Product product, int quantity, BigDecimal price, BigDecimal subTotal) {
        return OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .price(new Money(price))
                .subTotal(new Money(subTotal))
                .build();
    }
}
