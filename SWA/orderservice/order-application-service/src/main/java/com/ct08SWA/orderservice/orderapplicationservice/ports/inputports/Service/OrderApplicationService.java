package com.ct08SWA.orderservice.orderapplicationservice.ports.inputports.Service;
import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order.CancelOrderCommand;
import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order.CreateOrderCommand;
import com.ct08SWA.orderservice.orderapplicationservice.dto.ouputdto.Order.OrderCreatedResponse;

import java.util.UUID;

public interface OrderApplicationService {
	OrderCreatedResponse createOrder(CreateOrderCommand createOrderCommand, UUID tokenUserId);
    void cancelOrder(CancelOrderCommand cancelOrderCommand);

}
