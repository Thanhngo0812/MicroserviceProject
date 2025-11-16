package com.ct08SWA.orderservice.orderapplicationservice.handler.Service;

import com.ct08SWA.orderservice.orderapplicationservice.ports.outputports.Repository.OrderOutboxRepository;
import com.ct08SWA.orderservice.orderapplicationservice.ports.outputports.Repository.OrderRepository;
import com.ct08SWA.orderservice.orderdomaincore.entity.Order;
import com.ct08SWA.orderservice.orderdomaincore.service.OrderDomainService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order.CreateOrderCommand;
import com.ct08SWA.orderservice.orderapplicationservice.dto.ouputdto.Order.OrderCreatedResponse;
import lombok.extern.slf4j.Slf4j;
import com.ct08SWA.orderservice.orderapplicationservice.mapper.OrderDataMapper;

@Slf4j
@Component
public class OrderCreateCommandHandler {
    private final OrderDataMapper orderDataMapper;
    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderOutboxRepository orderOutboxRepository;
    @Value("${order-service.kafka.order-create-topic}")
    private String orderCreateTopic;
    public OrderCreateCommandHandler(
                                   OrderDataMapper orderDataMapper,OrderDomainService orderDomainService,OrderRepository orderRepository,OrderOutboxRepository orderOutboxRepository) {
        this.orderDataMapper = orderDataMapper;
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.orderOutboxRepository = orderOutboxRepository;
    }

    public OrderCreatedResponse createOrder(CreateOrderCommand createOrderCommand) {
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        orderDomainService.validateAndInitiateOrder(order);
        orderRepository.save(order);
        log.info("Order is saved with id: {}", order.getId().getValue());
        orderOutboxRepository.save(order.getDomainEvents().get(0),order.getId().getValue(),orderCreateTopic);
        log.info("Order is created with id: {}", order.getId().getValue().toString());
        return orderDataMapper.orderToCreateOrderResponse(order.getTrackingId().getValue());
    }
}