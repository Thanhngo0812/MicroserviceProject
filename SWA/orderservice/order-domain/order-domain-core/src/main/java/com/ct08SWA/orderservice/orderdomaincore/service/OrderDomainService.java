package com.ct08SWA.orderservice.orderdomaincore.service;

import com.ct08SWA.orderservice.orderdomaincore.entity.Order;
import com.ct08SWA.orderservice.orderdomaincore.event.OrderCreatedEvent;

public interface OrderDomainService {
    
    /**
     * Validate and initialize order
     * @param order the order to validate and initialize
     * @return OrderCreatedEvent
     */
    void validateAndInitiateOrder(Order order);
    
    /**
     * Pay order
     * @param order the order to pay
     */
    void payOrder(Order order);
    
    /**
     * Approve order
     * @param order the order to approve
     */
    void approveOrder(Order order);
    
    /**
     * Cancel order
     * @param order the order to cancel
     * @param failureMessages failure messages
     */
    void cancelOrder(Order order, java.util.List<String> failureMessages);
    void cancelOrderAfterApproved(Order order, java.util.List<String> failureMessages);

        /**
         * Initiate cancel order
         * @param order the order to initiate cancel
         * @param failureMessages failure messages
         */
    void initiateCancel(Order order, java.util.List<String> failureMessages);
}