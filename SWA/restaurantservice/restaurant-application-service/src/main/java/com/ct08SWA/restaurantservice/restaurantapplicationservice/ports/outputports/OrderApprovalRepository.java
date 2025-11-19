package com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.outputports;

import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.OrderApproval;

import java.util.Optional;
import java.util.UUID;

public interface OrderApprovalRepository {
    OrderApproval save(OrderApproval orderApproval);
    Optional<OrderApproval> findByOrderId(UUID orderId);
}