package com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto;

import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.ApprovalStatus;

public class OrderApprovalCommand {
    private String orderId;
    private String restaurantId;
    private ApprovalStatus status; // APPROVED hoặc REJECTED
    private String failureMessages; // Lý do (nếu REJECTED)

    public OrderApprovalCommand() {}

    public OrderApprovalCommand(String orderId, String restaurantId, ApprovalStatus status, String failureMessages) {
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.status = status;
        this.failureMessages = failureMessages;
    }

    // Getters / Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
    public ApprovalStatus getStatus() { return status; }
    public void setStatus(ApprovalStatus status) { this.status = status; }
    public String getFailureMessages() { return failureMessages; }
    public void setFailureMessages(String failureMessages) { this.failureMessages = failureMessages; }
}
