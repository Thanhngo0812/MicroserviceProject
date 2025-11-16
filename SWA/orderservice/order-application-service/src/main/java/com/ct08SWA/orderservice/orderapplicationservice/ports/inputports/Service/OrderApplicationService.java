package com.ct08SWA.orderservice.orderapplicationservice.ports.inputports.Service;
import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order.CancelOrderCommand;
import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order.CreateOrderCommand;
import com.ct08SWA.orderservice.orderapplicationservice.dto.ouputdto.Order.OrderCreatedResponse;

public interface OrderApplicationService {
	OrderCreatedResponse createOrder(CreateOrderCommand createOrderCommand);
    void cancelOrder(CancelOrderCommand cancelOrderCommand);

}
