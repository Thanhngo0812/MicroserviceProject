package com.ct08SWA.orderservice.orderapplicationservice.handler.Service;

import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order.CancelOrderCommand;
import com.ct08SWA.orderservice.orderapplicationservice.ports.inputports.Service.OrderApplicationService;
import org.springframework.stereotype.Service;

import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order.CreateOrderCommand;
import com.ct08SWA.orderservice.orderapplicationservice.dto.ouputdto.Order.OrderCreatedResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderApplicationServiceImpl implements OrderApplicationService {

    private final OrderCreateCommandHandler orderCreateCommandHandler;
    private final OrderCancelCommandHandler orderCancelCommandHandler; // 1. Inject Cancel Handler

    public OrderApplicationServiceImpl(OrderCreateCommandHandler orderCreateCommandHandler,
                                       OrderCancelCommandHandler orderCancelCommandHandler) { // 2. Sửa Constructor
        this.orderCreateCommandHandler = orderCreateCommandHandler;
        this.orderCancelCommandHandler = orderCancelCommandHandler; // Gán
    }

    @Override
    public OrderCreatedResponse createOrder(CreateOrderCommand createOrderCommand) {
        log.info("Received create order command for customer: {} and restaurant: {}",
                createOrderCommand.customerId(), createOrderCommand.restaurantId());
        return orderCreateCommandHandler.createOrder(createOrderCommand);
    }

    /**
     * 3. TRIỂN KHAI PHƯƠNG THỨC CANCEL (từ Input Port).
     * Ủy thác (delegate) cho OrderCancelCommandHandler.
     */
    @Override
    public void cancelOrder(CancelOrderCommand cancelOrderCommand) {
        log.info("Received cancel order command for ID: {}", cancelOrderCommand.getOrderId());
        orderCancelCommandHandler.cancelOrder(cancelOrderCommand);
    }
}