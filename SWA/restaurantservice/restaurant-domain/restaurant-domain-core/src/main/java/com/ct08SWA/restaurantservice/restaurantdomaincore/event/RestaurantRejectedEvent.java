package com.ct08SWA.restaurantservice.restaurantdomaincore.event;


import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Event (POJO "phẳng") - Đơn hàng bị TỪ CHỐI (REJECTED).
 */
public class RestaurantRejectedEvent extends RestaurantApprovalEvent {

    private List<String> failureMessages;

    // Constructor rỗng cho Jackson
    public RestaurantRejectedEvent() {
        super();
    }

    // Constructor (để Domain Service gọi)
    public RestaurantRejectedEvent(UUID approvalId, UUID orderId, UUID restaurantId, ZonedDateTime createdAt,String Status, List<String> failureMessages) {
        super(approvalId, orderId, restaurantId,Status, createdAt);
        this.failureMessages = failureMessages;
    }

    // Getters/Setters
    public List<String> getFailureMessages() { return failureMessages; }
    public void setFailureMessages(List<String> failureMessages) { this.failureMessages = failureMessages; }
}