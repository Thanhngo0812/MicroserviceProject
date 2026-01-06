package com.ct08SWA.restaurantservice.restaurantmessaging.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * DTO phẳng dùng cho Kafka consumer để nhận OrderCancelledEvent.
 * Serializable bởi Jackson.
 */
public class OrderCancelledEventDto {

    private UUID orderId;
    private ZonedDateTime createdAt;
    private BigDecimal price;
    private UUID customerId;

    // --- Constructor rỗng (BẮT BUỘC cho Jackson) ---
    public OrderCancelledEventDto() {}

    // --- Constructor đầy đủ (optional nếu bạn muốn dùng) ---
    public OrderCancelledEventDto(UUID orderId, ZonedDateTime createdAt,
                                  BigDecimal price, UUID customerId) {
        this.orderId = orderId;
        this.createdAt = createdAt;
        this.price = price;
        this.customerId = customerId;
    }

    // --- Getter / Setter (BẮT BUỘC) ---
    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }
}