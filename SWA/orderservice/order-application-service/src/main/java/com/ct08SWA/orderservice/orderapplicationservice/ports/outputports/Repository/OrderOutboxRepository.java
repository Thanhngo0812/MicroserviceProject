package com.ct08SWA.orderservice.orderapplicationservice.ports.outputports.Repository;

import com.ct08SWA.orderservice.orderdomaincore.event.OrderEvent;

import java.util.UUID;

public interface OrderOutboxRepository {
    void save(OrderEvent orderEvent, UUID SagaId, String topic);
}