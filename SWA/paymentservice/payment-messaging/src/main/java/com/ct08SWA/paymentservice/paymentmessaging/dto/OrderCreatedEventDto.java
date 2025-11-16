package com.ct08SWA.paymentservice.paymentmessaging.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

public class OrderCreatedEventDto {

    private UUID orderId;
    private UUID customerId;
    private UUID restaurantId;
    private BigDecimal price;
    private ZonedDateTime createdAt;
    private List<OrderItemDto> items;
    private UUID trackingId;

    public OrderCreatedEventDto() {}

    public OrderCreatedEventDto(UUID orderId, UUID customerId, UUID restaurantId,
                                  BigDecimal price, ZonedDateTime createdAt,
                                  List<OrderItemDto> items, UUID trackingId) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.price = price;
        this.createdAt = createdAt;
        this.items = items;
        this.trackingId = trackingId;
    }

    public static class OrderItemDto {
        private UUID productId;
        private int quantity;
        private BigDecimal price;
        private BigDecimal subTotal;

        public OrderItemDto() {}

        public OrderItemDto(UUID productId, int quantity, BigDecimal price, BigDecimal subTotal) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
            this.subTotal = subTotal;
        }

        public UUID getProductId() { return productId; }
        public void setProductId(UUID productId) { this.productId = productId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public BigDecimal getSubTotal() { return subTotal; }
        public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }
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

    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }

    public UUID getTrackingId() { return trackingId; }
    public void setTrackingId(UUID trackingId) { this.trackingId = trackingId; }
}
