package com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.outputports;

import com.ct08SWA.restaurantservice.restaurantdomaincore.event.RestaurantApprovalEvent;

import java.util.UUID;

public interface RestaurantOutboxRepository {
    public void save(RestaurantApprovalEvent restaurantApprovalEvent, UUID SagaId, String Topic);
}
