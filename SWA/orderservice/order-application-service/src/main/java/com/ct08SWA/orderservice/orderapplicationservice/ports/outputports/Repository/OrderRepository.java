package com.ct08SWA.orderservice.orderapplicationservice.ports.outputports.Repository;

import java.util.Optional;

import com.ct08SWA.orderservice.orderdomaincore.entity.Order;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.OrderId;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.TrackingId;

public interface OrderRepository {
    
    /**
     * Save order to database
     * @param order the order to save
     * @return the saved order
     */
    Order save(Order order);
    
    /**
     * Find order by ID
     * @param orderId the order ID
     * @return Optional of order if found
     */
    Optional<Order> findById(OrderId orderId);
    
    /**
     * Find order by tracking ID
     * @param trackingId the tracking ID
     * @return Optional of order if found
     */
    Optional<Order> findByTrackingId(TrackingId trackingId);
}