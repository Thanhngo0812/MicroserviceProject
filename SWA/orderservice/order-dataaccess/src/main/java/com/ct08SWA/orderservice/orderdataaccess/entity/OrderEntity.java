package com.ct08SWA.orderservice.orderdataaccess.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "orders", schema = "\"order\"")
public class OrderEntity {
    
    @Id
    private UUID id;
    
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;
    
    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;
    
    @Column(name = "tracking_id", nullable = false, unique = true)
    private UUID trackingId;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;
    
    @Column(name = "failure_messages")
    private String failureMessages;
    
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItemEntity> items;

    // Default constructor
    public OrderEntity() {}

    // Builder constructor
    private OrderEntity(Builder builder) {
        this.id = builder.id;
        this.customerId = builder.customerId;
        this.restaurantId = builder.restaurantId;
        this.trackingId = builder.trackingId;
        this.price = builder.price;
        this.orderStatus = builder.orderStatus;
        this.failureMessages = builder.failureMessages;
        this.createdAt = builder.createdAt;
        this.items = builder.items;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public UUID getRestaurantId() { return restaurantId; }
    public UUID getTrackingId() { return trackingId; }
    public BigDecimal getPrice() { return price; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public String getFailureMessages() { return failureMessages; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public List<OrderItemEntity> getItems() { return items; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public void setRestaurantId(UUID restaurantId) { this.restaurantId = restaurantId; }
    public void setTrackingId(UUID trackingId) { this.trackingId = trackingId; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }
    public void setFailureMessages(String failureMessages) { this.failureMessages = failureMessages; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
    public void setItems(List<OrderItemEntity> items) { this.items = items; }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID id;
        private UUID customerId;
        private UUID restaurantId;
        private UUID trackingId;
        private BigDecimal price;
        private OrderStatus orderStatus;
        private String failureMessages;
        private ZonedDateTime createdAt;
        private List<OrderItemEntity> items;

        private Builder() {}

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder customerId(UUID customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder restaurantId(UUID restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }

        public Builder trackingId(UUID trackingId) {
            this.trackingId = trackingId;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder orderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        public Builder failureMessages(String failureMessages) {
            this.failureMessages = failureMessages;
            return this;
        }

        public Builder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder items(List<OrderItemEntity> items) {
            this.items = items;
            return this;
        }

        public OrderEntity build() {
            return new OrderEntity(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntity that = (OrderEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OrderEntity{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", restaurantId=" + restaurantId +
                ", trackingId=" + trackingId +
                ", price=" + price +
                ", orderStatus=" + orderStatus +
                ", createdAt=" + createdAt +
                '}';
    }
}