package com.ct08SWA.orderservice.orderdomaincore.event;

import com.ct08SWA.orderservice.orderdomaincore.entity.Order;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Sự kiện được kích hoạt khi một Order được hủy.
 */
public class OrderCancelledEvent implements OrderEvent {
    private UUID orderId;
    private ZonedDateTime createdAt; // Đổi tên từ occurredOn
    private BigDecimal Price;
    private UUID customerId;
    public OrderCancelledEvent() {

    }

    public OrderCancelledEvent(UUID orderId, ZonedDateTime createdAt, BigDecimal Price, UUID customerId) {
        this.orderId = orderId;
        this.createdAt = createdAt;
        this.Price = Price;
        this.customerId = customerId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getPrice() {
        return Price;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public void setPrice(BigDecimal price) {
        Price = price;
    }

    // Hiện tại, lớp này không cần thêm thuộc tính gì đặc biệt
    // vì tất cả thông tin (Order) đã có ở lớp cha.
    // Nếu bạn cần thêm, ví dụ "lý do hủy", bạn có thể thêm ở đây:
    // private final String cancellationReason;
}