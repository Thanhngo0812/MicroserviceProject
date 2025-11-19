package com.ct08SWA.orderservice.orderdomaincore.service;

import com.ct08SWA.orderservice.orderdomaincore.entity.Order;
import com.ct08SWA.orderservice.orderdomaincore.entity.OrderItem;
import com.ct08SWA.orderservice.orderdomaincore.entity.Product;
import com.ct08SWA.orderservice.orderdomaincore.event.OrderCancelledEvent;
import com.ct08SWA.orderservice.orderdomaincore.event.OrderCreatedEvent;
import com.ct08SWA.orderservice.orderdomaincore.event.OrderPaidEvent;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OrderDomainServiceImpl
 */
@DisplayName("Order Domain Service Tests")
class OrderDomainServiceImplTest {
    private OrderDomainService orderDomainService;
    private CustomerId customerId;
    private RestaurantId restaurantId;
    private StreetAddress deliveryAddress;
    private Product product;
    private List<OrderItem> orderItems;
    private Money totalPrice;

    @BeforeEach
    void setUp() {
        orderDomainService = new OrderDomainServiceImpl();
        customerId = new CustomerId(UUID.randomUUID());
        restaurantId = new RestaurantId(UUID.randomUUID());
        deliveryAddress = new StreetAddress("123 Main St", "12345", "New York");
        Money productPrice = new Money(new BigDecimal("50.00"));
        product = new Product(
                new ProductId(UUID.randomUUID()),
                "Pizza",
                productPrice
        );
        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(2)
                .price(productPrice)
                .subTotal(new Money(new BigDecimal("100.00")))
                .build();
        orderItems = List.of(orderItem);
        totalPrice = new Money(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Should validate and initiate order successfully")
    void shouldValidateAndInitiateOrderSuccessfully() {
        // Given
        Order order = Order.builder()
                .customerId(customerId)
                .restaurantId(restaurantId)
                .deliveryAddress(deliveryAddress)
                .price(totalPrice)
                .items(orderItems)
                .build();

        // When
        orderDomainService.validateAndInitiateOrder(order);

        // Then
        assertNotNull(order.getId());
        assertNotNull(order.getTrackingId());
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        assertEquals(1, order.getDomainEvents().size());
        assertTrue(order.getDomainEvents().get(0) instanceof OrderCreatedEvent);
    }

    @Test
    @DisplayName("Should add OrderCreatedEvent when order is initiated")
    void shouldAddOrderCreatedEventWhenOrderIsInitiated() {
        // Given
        Order order = Order.builder()
                .customerId(customerId)
                .restaurantId(restaurantId)
                .deliveryAddress(deliveryAddress)
                .price(totalPrice)
                .items(orderItems)
                .build();

        // When
        orderDomainService.validateAndInitiateOrder(order);

        // Then
        OrderCreatedEvent event = (OrderCreatedEvent) order.getDomainEvents().get(0);
        assertNotNull(event);
        assertEquals(order.getId().getValue(), event.getOrderId());
        assertEquals(customerId.getValue(), event.getCustomerId());
        assertEquals(restaurantId.getValue(), event.getRestaurantId());
        assertEquals(totalPrice.getAmount(), event.getPrice());
        assertNotNull(event.getTrackingId());
    }

    @Test
    @DisplayName("Should pay order and add OrderPaidEvent")
    void shouldPayOrderAndAddOrderPaidEvent() {
        // Given
        Order order = Order.builder()
                .customerId(customerId)
                .restaurantId(restaurantId)
                .deliveryAddress(deliveryAddress)
                .price(totalPrice)
                .items(orderItems)
                .build();
        order.initializeOrder();
        order.clearDomainEvents(); // Clear initialization events

        // When
        orderDomainService.payOrder(order);

        // Then
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
        assertEquals(1, order.getDomainEvents().size());
        assertTrue(order.getDomainEvents().get(0) instanceof OrderPaidEvent);
    }

    @Test
    @DisplayName("Should approve order without adding event")
    void shouldApproveOrderWithoutAddingEvent() {
        // Given
        Order order = Order.builder()
                .customerId(customerId)
                .restaurantId(restaurantId)
                .deliveryAddress(deliveryAddress)
                .price(totalPrice)
                .items(orderItems)
                .build();
        order.initializeOrder();
        order.pay(); // Set status to PAID

        // When
        orderDomainService.approveOrder(order);

        // Then
        assertEquals(OrderStatus.APPROVED, order.getOrderStatus());
    }

    @Test
    @DisplayName("Should cancel order and add OrderCancelledEvent")
    void shouldCancelOrderAndAddOrderCancelledEvent() {
        // Given
        Order order = Order.builder()
                .customerId(customerId)
                .restaurantId(restaurantId)
                .deliveryAddress(deliveryAddress)
                .price(totalPrice)
                .items(orderItems)
                .build();
        order.initializeOrder(); // Initialize to set ID
        order.clearDomainEvents(); // Clear initialization events
        List<String> failureMessages = List.of("Payment failed", "Card declined");

        // When
        orderDomainService.cancelOrder(order, failureMessages);

        // Then
        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
        assertEquals(2, order.getFailureMessages().size());
        assertEquals(1, order.getDomainEvents().size());
        assertTrue(order.getDomainEvents().get(0) instanceof OrderCancelledEvent);
    }

    @Test
    @DisplayName("Should initiate cancel and add OrderCancelledEvent")
    void shouldInitiateCancelAndAddOrderCancelledEvent() {
        // Given
        Order order = Order.builder()
                .customerId(customerId)
                .restaurantId(restaurantId)
                .deliveryAddress(deliveryAddress)
                .price(totalPrice)
                .items(orderItems)
                .build();
        order.initializeOrder(); // Initialize to set ID
        order.pay(); // Set status to PAID
        order.clearDomainEvents(); // Clear previous events
        List<String> failureMessages = List.of("Restaurant rejected");

        // When
        orderDomainService.initiateCancel(order, failureMessages);

        // Then
        assertEquals(OrderStatus.CANCELLING, order.getOrderStatus());
        assertEquals(1, order.getFailureMessages().size());
        assertEquals(1, order.getDomainEvents().size());
        assertTrue(order.getDomainEvents().get(0) instanceof OrderCancelledEvent);
    }

    @Test
    @DisplayName("Should initialize order items with correct IDs")
    void shouldInitializeOrderItemsWithCorrectIds() {
        // Given
        OrderItem item1 = OrderItem.builder()
                .product(product)
                .quantity(1)
                .price(new Money(new BigDecimal("50.00")))
                .subTotal(new Money(new BigDecimal("50.00")))
                .build();
        OrderItem item2 = OrderItem.builder()
                .product(product)
                .quantity(2)
                .price(new Money(new BigDecimal("50.00")))
                .subTotal(new Money(new BigDecimal("100.00")))
                .build();
        Order order = Order.builder()
                .customerId(customerId)
                .restaurantId(restaurantId)
                .deliveryAddress(deliveryAddress)
                .price(new Money(new BigDecimal("150.00")))
                .items(List.of(item1, item2))
                .build();

        // When
        orderDomainService.validateAndInitiateOrder(order);

        // Then
        assertEquals(1L, order.getItems().get(0).getId());
        assertEquals(2L, order.getItems().get(1).getId());
        assertNotNull(order.getItems().get(0).getOrderId());
        assertNotNull(order.getItems().get(1).getOrderId());
    }

    @Test
    @DisplayName("Should create event with correct order item data")
    void shouldCreateEventWithCorrectOrderItemData() {
        // Given
        Order order = Order.builder()
                .customerId(customerId)
                .restaurantId(restaurantId)
                .deliveryAddress(deliveryAddress)
                .price(totalPrice)
                .items(orderItems)
                .build();

        // When
        orderDomainService.validateAndInitiateOrder(order);

        // Then
        OrderCreatedEvent event = (OrderCreatedEvent) order.getDomainEvents().get(0);
        assertEquals(1, event.getItems().size());
        OrderCreatedEvent.OrderItemData itemData = event.getItems().get(0);
        assertEquals(product.getId().getValue(), itemData.getProductId());
        assertEquals(2, itemData.getQuantity());
        assertEquals(new BigDecimal("50.00"), itemData.getPrice());
        assertEquals(new BigDecimal("100.00"), itemData.getSubTotal());
    }
}