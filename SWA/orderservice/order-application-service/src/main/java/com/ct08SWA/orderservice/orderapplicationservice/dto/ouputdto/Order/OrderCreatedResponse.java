package com.ct08SWA.orderservice.orderapplicationservice.dto.ouputdto.Order;
import java.util.UUID;

import com.ct08SWA.orderservice.orderdomaincore.valueobject.OrderStatus;

public record OrderCreatedResponse(
    UUID orderTrackingId,
    OrderStatus orderStatus,
    String message
) {
    public OrderCreatedResponse(UUID orderTrackingId, OrderStatus orderStatus) {
        this(orderTrackingId, orderStatus, "Order created successfully");
    }
}

