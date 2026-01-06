package com.ct08SWA.restaurantservice.restaurantmessaging.dto;


import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

public class OrderPaidEventDto {

    private UUID orderId;
    private UUID customerId;
    private UUID restaurantId;
    private BigDecimal price;
    private ZonedDateTime createdAt;
    private UUID trackingId;

    public OrderPaidEventDto() {}

    public OrderPaidEventDto(UUID orderId, UUID customerId, UUID restaurantId,
                                BigDecimal price, ZonedDateTime createdAt,
                                 UUID trackingId) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.price = price;
        this.createdAt = createdAt;
        this.trackingId = trackingId;
    }


    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }

    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }

    public UUID getRestaurantId() { return restaurantId; }
    public void setRestaurantId(UUID restaurantId) { this.restaurantId = restaurantId; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }


    public UUID getTrackingId() { return trackingId; }
    public void setTrackingId(UUID trackingId) { this.trackingId = trackingId; }
}