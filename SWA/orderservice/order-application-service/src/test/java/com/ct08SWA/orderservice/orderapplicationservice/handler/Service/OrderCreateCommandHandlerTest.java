package com.ct08SWA.orderservice.orderapplicationservice.handler.Service;

import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order.CreateOrderCommand;
import com.ct08SWA.orderservice.orderapplicationservice.dto.ouputdto.Order.OrderCreatedResponse;
import com.ct08SWA.orderservice.orderapplicationservice.mapper.OrderDataMapper;
import com.ct08SWA.orderservice.orderapplicationservice.ports.outputports.Repository.OrderOutboxRepository;
import com.ct08SWA.orderservice.orderapplicationservice.ports.outputports.Repository.OrderRepository;
import com.ct08SWA.orderservice.orderdomaincore.entity.Order;
import com.ct08SWA.orderservice.orderdomaincore.entity.OrderItem;
import com.ct08SWA.orderservice.orderdomaincore.entity.Product;
import com.ct08SWA.orderservice.orderdomaincore.event.OrderCreatedEvent;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderCreateCommandHandler
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Order Create Command Handler Tests")
class OrderCreateCommandHandlerTest {

    @Mock
    private OrderDataMapper orderDataMapper;

    @Mock
    private OrderDomainService orderDomainService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderOutboxRepository orderOutboxRepository;

    @InjectMocks
    private OrderCreateCommandHandler orderCreateCommandHandler;

    private CreateOrderCommand createOrderCommand;
    private Order order;
    private UUID trackingId;

    @BeforeEach
    void setUp() {
        // Set the topic via reflection
        ReflectionTestUtils.setField(orderCreateCommandHandler, "orderCreateTopic", "order.create.topic");

        // Setup test data
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        trackingId = UUID.randomUUID();

        CreateOrderCommand.OrderItem orderItemCommand = new CreateOrderCommand.OrderItem(
            productId,
            2,
            new BigDecimal("50.00"),
            new BigDecimal("100.00")
        );

        CreateOrderCommand.OrderAddress addressCommand = new CreateOrderCommand.OrderAddress(
            "123 Main Street",
            "12345",
            "New York"
        );

        createOrderCommand = new CreateOrderCommand(
            customerId,
            restaurantId,
            new BigDecimal("100.00"),
            List.of(orderItemCommand),
            addressCommand
        );

        // Create mock order
        Money productPrice = new Money(new BigDecimal("50.00"));
        Product product = new Product(
            new ProductId(productId),
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
            .orderId(new OrderId(UUID.randomUUID()))
            .customerId(new CustomerId(customerId))
            .restaurantId(new RestaurantId(restaurantId))
            .deliveryAddress(new StreetAddress("123 Main Street", "12345", "New York"))
            .price(new Money(new BigDecimal("100.00")))
            .items(List.of(orderItem))
            .trackingId(new TrackingId(trackingId))
            .orderStatus(OrderStatus.PENDING)
            .build();
    }

    @Test
    @DisplayName("Should create order successfully")
    void shouldCreateOrderSuccessfully() {
        // Given
        when(orderDataMapper.createOrderCommandToOrder(createOrderCommand)).thenReturn(order);
        when(orderDataMapper.orderToCreateOrderResponse(trackingId))
            .thenReturn(new OrderCreatedResponse(trackingId, OrderStatus.PENDING, "Order created successfully"));

        // Create a mock event
        OrderCreatedEvent mockEvent = new OrderCreatedEvent(
            order.getId().getValue(),
            order.getCustomerId().getValue(),
            order.getRestaurantId().getValue(),
            order.getPrice().getAmount(),
            ZonedDateTime.now(),
            List.of(),
            trackingId
        );

        // Add event to order
        order.addDomainEvent(mockEvent);

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(orderOutboxRepository).save(any(), any(UUID.class), anyString());

        // When
        OrderCreatedResponse response = orderCreateCommandHandler.createOrder(createOrderCommand);

        // Then
        assertNotNull(response);
        assertEquals(trackingId, response.orderTrackingId());
        assertEquals(OrderStatus.PENDING, response.orderStatus());

        // Verify interactions
        verify(orderDataMapper).createOrderCommandToOrder(createOrderCommand);
        verify(orderDomainService).validateAndInitiateOrder(order);
        verify(orderRepository).save(order);
        verify(orderOutboxRepository).save(any(), eq(order.getId().getValue()), eq("order.create.topic"));
        verify(orderDataMapper).orderToCreateOrderResponse(trackingId);
    }

    @Test
    @DisplayName("Should call domain service to validate and initiate order")
    void shouldCallDomainServiceToValidateAndInitiateOrder() {
        // Given
        when(orderDataMapper.createOrderCommandToOrder(createOrderCommand)).thenReturn(order);
        when(orderDataMapper.orderToCreateOrderResponse(any(UUID.class)))
            .thenReturn(new OrderCreatedResponse(trackingId, OrderStatus.PENDING, "Order created successfully"));

        OrderCreatedEvent mockEvent = new OrderCreatedEvent(
            order.getId().getValue(),
            order.getCustomerId().getValue(),
            order.getRestaurantId().getValue(),
            order.getPrice().getAmount(),
            ZonedDateTime.now(),
            List.of(),
            trackingId
        );
        order.addDomainEvent(mockEvent);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        orderCreateCommandHandler.createOrder(createOrderCommand);

        // Then
        verify(orderDomainService, times(1)).validateAndInitiateOrder(order);
    }

    @Test
    @DisplayName("Should save order to repository")
    void shouldSaveOrderToRepository() {
        // Given
        when(orderDataMapper.createOrderCommandToOrder(createOrderCommand)).thenReturn(order);
        when(orderDataMapper.orderToCreateOrderResponse(any(UUID.class)))
            .thenReturn(new OrderCreatedResponse(trackingId, OrderStatus.PENDING, "Order created successfully"));

        OrderCreatedEvent mockEvent = new OrderCreatedEvent(
            order.getId().getValue(),
            order.getCustomerId().getValue(),
            order.getRestaurantId().getValue(),
            order.getPrice().getAmount(),
            ZonedDateTime.now(),
            List.of(),
            trackingId
        );
        order.addDomainEvent(mockEvent);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        orderCreateCommandHandler.createOrder(createOrderCommand);

        // Then
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should save order created event to outbox")
    void shouldSaveOrderCreatedEventToOutbox() {
        // Given
        when(orderDataMapper.createOrderCommandToOrder(createOrderCommand)).thenReturn(order);
        when(orderDataMapper.orderToCreateOrderResponse(any(UUID.class)))
            .thenReturn(new OrderCreatedResponse(trackingId, OrderStatus.PENDING, "Order created successfully"));

        OrderCreatedEvent mockEvent = new OrderCreatedEvent(
            order.getId().getValue(),
            order.getCustomerId().getValue(),
            order.getRestaurantId().getValue(),
            order.getPrice().getAmount(),
            ZonedDateTime.now(),
            List.of(),
            trackingId
        );
        order.addDomainEvent(mockEvent);

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(orderOutboxRepository).save(any(), any(UUID.class), anyString());

        // When
        orderCreateCommandHandler.createOrder(createOrderCommand);

        // Then
        verify(orderOutboxRepository, times(1)).save(
            any(),
            eq(order.getId().getValue()),
            eq("order.create.topic")
        );
    }

    @Test
    @DisplayName("Should map command to order entity")
    void shouldMapCommandToOrderEntity() {
        // Given
        when(orderDataMapper.createOrderCommandToOrder(createOrderCommand)).thenReturn(order);
        when(orderDataMapper.orderToCreateOrderResponse(any(UUID.class)))
            .thenReturn(new OrderCreatedResponse(trackingId, OrderStatus.PENDING, "Order created successfully"));

        OrderCreatedEvent mockEvent = new OrderCreatedEvent(
            order.getId().getValue(),
            order.getCustomerId().getValue(),
            order.getRestaurantId().getValue(),
            order.getPrice().getAmount(),
            ZonedDateTime.now(),
            List.of(),
            trackingId
        );
        order.addDomainEvent(mockEvent);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        orderCreateCommandHandler.createOrder(createOrderCommand);

        // Then
        verify(orderDataMapper, times(1)).createOrderCommandToOrder(createOrderCommand);
    }

    @Test
    @DisplayName("Should return order created response with tracking ID")
    void shouldReturnOrderCreatedResponseWithTrackingId() {
        // Given
        OrderCreatedResponse expectedResponse = new OrderCreatedResponse(
            trackingId,
            OrderStatus.PENDING,
            "Order created successfully"
        );

        when(orderDataMapper.createOrderCommandToOrder(createOrderCommand)).thenReturn(order);
        when(orderDataMapper.orderToCreateOrderResponse(trackingId)).thenReturn(expectedResponse);

        OrderCreatedEvent mockEvent = new OrderCreatedEvent(
            order.getId().getValue(),
            order.getCustomerId().getValue(),
            order.getRestaurantId().getValue(),
            order.getPrice().getAmount(),
            ZonedDateTime.now(),
            List.of(),
            trackingId
        );
        order.addDomainEvent(mockEvent);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        OrderCreatedResponse actualResponse = orderCreateCommandHandler.createOrder(createOrderCommand);

        // Then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.orderTrackingId(), actualResponse.orderTrackingId());
        assertEquals(expectedResponse.orderStatus(), actualResponse.orderStatus());
    }
}
