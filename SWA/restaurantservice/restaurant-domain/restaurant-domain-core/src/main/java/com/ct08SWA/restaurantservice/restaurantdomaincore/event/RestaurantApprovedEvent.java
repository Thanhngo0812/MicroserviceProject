package com.ct08SWA.restaurantservice.restaurantdomaincore.event;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Event (POJO "phẳng") - Đơn hàng được DUYỆT (APPROVED).
 */
public class RestaurantApprovedEvent extends RestaurantApprovalEvent {

    // Constructor rỗng cho Jackson
    public RestaurantApprovedEvent() {
        super();
    }

    // Constructor (để Domain Service gọi)
    public RestaurantApprovedEvent(UUID approvalId, UUID orderId, UUID restaurantId,String Status, ZonedDateTime createdAt) {
        super(approvalId, orderId, restaurantId,Status, createdAt);
    }
}