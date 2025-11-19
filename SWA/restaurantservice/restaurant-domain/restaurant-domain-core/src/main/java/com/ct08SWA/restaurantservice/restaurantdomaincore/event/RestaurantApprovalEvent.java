package com.ct08SWA.restaurantservice.restaurantdomaincore.event;


import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Lớp cha (POJO "phẳng") cho các Event phản hồi của Restaurant.
 * (JSON-able, có constructor rỗng và getters/setters).
 */
public abstract class RestaurantApprovalEvent {

    private UUID approvalId;
    private UUID orderId;
    private UUID restaurantId;
    private String Status;
    private ZonedDateTime createdAt;

    // Constructor rỗng cho Jackson
    public RestaurantApprovalEvent() {
    }

    // Constructor (để các lớp con gọi)
    protected RestaurantApprovalEvent(UUID approvalId, UUID orderId, UUID restaurantId,String Status, ZonedDateTime createdAt) {
        this.approvalId = approvalId;
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.createdAt = createdAt;
        this.Status = Status;
    }

    // --- Getters and Setters (BẮT BUỘC cho Jackson) ---

    public UUID getApprovalId() { return approvalId; }
    public void setApprovalId(UUID approvalId) { this.approvalId = approvalId; }

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }

    public UUID getRestaurantId() { return restaurantId; }
    public void setRestaurantId(UUID restaurantId) { this.restaurantId = restaurantId; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return Status; }
    public void setStatus(String status) { this.Status = status; }
}