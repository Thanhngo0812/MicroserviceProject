package com.ct08SWA.orderservice.orderapplicationservice.handler.Service;

import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order.CancelOrderCommand;
import com.ct08SWA.orderservice.orderapplicationservice.ports.outputports.Repository.OrderOutboxRepository;
import com.ct08SWA.orderservice.orderapplicationservice.ports.outputports.Repository.OrderRepository;
import com.ct08SWA.orderservice.orderdomaincore.entity.Order;
import com.ct08SWA.orderservice.orderdomaincore.entity.OrderItem;
import com.ct08SWA.orderservice.orderdomaincore.entity.Product;
import com.ct08SWA.orderservice.orderdomaincore.event.OrderCancelledEvent;
import com.ct08SWA.orderservice.orderdomaincore.exception.OrderDomainException;
import com.ct08SWA.orderservice.orderdomaincore.service.OrderDomainService;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderCancelCommandHandler
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Order Cancel Command Handler Tests")
class OrderCancelCommandHandlerTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderOutboxRepository orderOutboxRepository;
    @Mock
    private OrderDomainService orderDomainService;
    @InjectMocks
    private OrderCancelCommandHandler orderCancelCommandHandler;

    private UUID orderId;
    private Order order;
    private CancelOrderCommand cancelOrderCommand;

    @BeforeEach
    void setUp() {
        // Set the topic via reflection
        ReflectionTestUtils.setField(orderCancelCommandHandler, "OrderCancelEventTopic", "order.cancel.topic");
        orderId = UUID.randomUUID();

        // FIX: Use builder pattern instead of constructor
        cancelOrderCommand = CancelOrderCommand.builder()
                .orderId(orderId)
                .build();

        // Create mock order
        Money productPrice = new Money(new BigDecimal("50.00"));
        Product product = new Product(
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
        order = Order.builder()
                .orderId(new OrderId(orderId))
                .customerId(new CustomerId(UUID.randomUUID()))
                .restaurantId(new RestaurantId(UUID.randomUUID()))
                .deliveryAddress(new StreetAddress("123 Main St", "12345", "New York"))
                .price(new Money(new BigDecimal("100.00")))
                .items(List.of(orderItem))
                .trackingId(new TrackingId(UUID.randomUUID()))
                .orderStatus(OrderStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Should cancel PENDING order safely")
    void shouldCancelPendingOrderSafely() {
        // Given
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        OrderCancelledEvent mockEvent = new OrderCancelledEvent(
                order.getId().getValue(),
                ZonedDateTime.now(),
                order.getPrice().getAmount(),
                order.getCustomerId().getValue()
        );
        doAnswer(invocation -> {
            order.addDomainEvent(mockEvent);
            return null;
        }).when(orderDomainService).cancelOrder(any(Order.class), anyList());

        // When
        orderCancelCommandHandler.cancelOrder(cancelOrderCommand);

        // Then
        verify(orderDomainService).cancelOrder(eq(order), anyList());
        verify(orderRepository).save(order);
        verify(orderOutboxRepository).save(any(), eq(orderId), eq("order.cancel.topic"));
    }

    @Test
    @DisplayName("Should cancel PAYMENT_FAILED order safely")
    void shouldCancelPaymentFailedOrderSafely() {
        // Given
        order = Order.builder()
                .orderId(new OrderId(orderId))
                .customerId(new CustomerId(UUID.randomUUID()))
                .restaurantId(new RestaurantId(UUID.randomUUID()))
                .deliveryAddress(new StreetAddress("123 Main St", "12345", "New York"))
                .price(new Money(new BigDecimal("100.00")))
                .items(List.of())
                .trackingId(new TrackingId(UUID.randomUUID()))
                .orderStatus(OrderStatus.PAYMENT_FAILED)
                .build();
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        OrderCancelledEvent mockEvent = new OrderCancelledEvent(
                order.getId().getValue(),
                ZonedDateTime.now(),
                order.getPrice().getAmount(),
                order.getCustomerId().getValue()
        );
        doAnswer(invocation -> {
            order.addDomainEvent(mockEvent);
            return null;
        }).when(orderDomainService).cancelOrder(any(Order.class), anyList());

        // When
        orderCancelCommandHandler.cancelOrder(cancelOrderCommand);

        // Then
        verify(orderDomainService).cancelOrder(eq(order), anyList());
        verify(orderRepository).save(order);
        verify(orderOutboxRepository).save(any(), eq(orderId), eq("order.cancel.topic"));
    }

    @Test
    @DisplayName("Should initiate compensation for PAID order")
    void shouldInitiateCompensationForPaidOrder() {
        // Given
        order = Order.builder()
                .orderId(new OrderId(orderId))
                .customerId(new CustomerId(UUID.randomUUID()))
                .restaurantId(new RestaurantId(UUID.randomUUID()))
                .deliveryAddress(new StreetAddress("123 Main St", "12345", "New York"))
                .price(new Money(new BigDecimal("100.00")))
                .items(List.of())
                .trackingId(new TrackingId(UUID.randomUUID()))
                .orderStatus(OrderStatus.PAID)
                .build();
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        OrderCancelledEvent mockEvent = new OrderCancelledEvent(
                order.getId().getValue(),
                ZonedDateTime.now(),
                order.getPrice().getAmount(),
                order.getCustomerId().getValue()
        );
        doAnswer(invocation -> {
            order.addDomainEvent(mockEvent);
            return null;
        }).when(orderDomainService).initiateCancel(any(Order.class), anyList());

        // When
        orderCancelCommandHandler.cancelOrder(cancelOrderCommand);

        // Then
        verify(orderDomainService).initiateCancel(eq(order), anyList());
        verify(orderRepository).save(order);
        verify(orderOutboxRepository).save(any(), eq(orderId), eq("order.cancel.topic"));
    }

    @Test
    @DisplayName("Should throw exception when cancelling APPROVED order")
    void shouldThrowExceptionWhenCancellingApprovedOrder() {
        // Given
        order = Order.builder()
                .orderId(new OrderId(orderId))
                .customerId(new CustomerId(UUID.randomUUID()))
                .restaurantId(new RestaurantId(UUID.randomUUID()))
                .deliveryAddress(new StreetAddress("123 Main St", "12345", "New York"))
                .price(new Money(new BigDecimal("100.00")))
                .items(List.of())
                .trackingId(new TrackingId(UUID.randomUUID()))
                .orderStatus(OrderStatus.APPROVED)
                .build();
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));

        // When & Then
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> orderCancelCommandHandler.cancelOrder(cancelOrderCommand)
        );
        assertTrue(exception.getMessage().contains("un-cancellable state"));
        verify(orderDomainService, never()).cancelOrder(any(), anyList());
        verify(orderDomainService, never()).initiateCancel(any(), anyList());
    }

    @Test
    @DisplayName("Should throw exception when cancelling CANCELLING order")
    void shouldThrowExceptionWhenCancellingCancellingOrder() {
        // Given
        order = Order.builder()
                .orderId(new OrderId(orderId))
                .customerId(new CustomerId(UUID.randomUUID()))
                .restaurantId(new RestaurantId(UUID.randomUUID()))
                .deliveryAddress(new StreetAddress("123 Main St", "12345", "New York"))
                .price(new Money(new BigDecimal("100.00")))
                .items(List.of())
                .trackingId(new TrackingId(UUID.randomUUID()))
                .orderStatus(OrderStatus.CANCELLING)
                .build();
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));

        // When & Then
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> orderCancelCommandHandler.cancelOrder(cancelOrderCommand)
        );
        assertTrue(exception.getMessage().contains("un-cancellable state"));
    }

    @Test
    @DisplayName("Should throw exception when order not found")
    void shouldThrowExceptionWhenOrderNotFound() {
        // Given
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.empty());

        // When & Then
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> orderCancelCommandHandler.cancelOrder(cancelOrderCommand)
        );
        assertTrue(exception.getMessage().contains("Order not found"));
        verify(orderDomainService, never()).cancelOrder(any(), anyList());
    }

    @Test
    @DisplayName("Should save event to outbox for PENDING order cancellation")
    void shouldSaveEventToOutboxForPendingOrderCancellation() {
        // Given
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        OrderCancelledEvent mockEvent = new OrderCancelledEvent(
                order.getId().getValue(),
                ZonedDateTime.now(),
                order.getPrice().getAmount(),
                order.getCustomerId().getValue()
        );
        doAnswer(invocation -> {
            order.addDomainEvent(mockEvent);
            return null;
        }).when(orderDomainService).cancelOrder(any(Order.class), anyList());
        doNothing().when(orderOutboxRepository).save(any(), any(UUID.class), anyString());

        // When
        orderCancelCommandHandler.cancelOrder(cancelOrderCommand);

        // Then
        verify(orderOutboxRepository, times(1)).save(any(), eq(orderId), eq("order.cancel.topic"));
    }

    @Test
    @DisplayName("Should find order by ID before cancelling")
    void shouldFindOrderByIdBeforeCancelling() {
        // Given
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        OrderCancelledEvent mockEvent = new OrderCancelledEvent(
                order.getId().getValue(),
                ZonedDateTime.now(),
                order.getPrice().getAmount(),
                order.getCustomerId().getValue()
        );
        doAnswer(invocation -> {
            order.addDomainEvent(mockEvent);
            return null;
        }).when(orderDomainService).cancelOrder(any(Order.class), anyList());

        // When
        orderCancelCommandHandler.cancelOrder(cancelOrderCommand);

        // Then
        verify(orderRepository, times(1)).findById(new OrderId(orderId));
    }
}