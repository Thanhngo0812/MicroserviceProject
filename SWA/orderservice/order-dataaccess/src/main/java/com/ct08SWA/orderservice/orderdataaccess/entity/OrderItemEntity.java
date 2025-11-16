package com.ct08SWA.orderservice.orderdataaccess.entity;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "order_items", schema = "\"order\"")
@IdClass(OrderItemEntityId.class)
public class OrderItemEntity {
    
    @Id
    private Long id;
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;
    
    @Column(name = "product_id", nullable = false)
    private UUID productId;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "sub_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal subTotal;

    // Default constructor
    public OrderItemEntity() {}

    // Builder constructor
    private OrderItemEntity(Builder builder) {
        this.id = builder.id;
        this.order = builder.order;
        this.productId = builder.productId;
        this.price = builder.price;
        this.quantity = builder.quantity;
        this.subTotal = builder.subTotal;
    }

    // Getters
    public Long getId() { return id; }
    public OrderEntity getOrder() { return order; }
    public UUID getProductId() { return productId; }
    public BigDecimal getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getSubTotal() { return subTotal; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setOrder(OrderEntity order) { this.order = order; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private OrderEntity order;
        private UUID productId;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subTotal;

        private Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder order(OrderEntity order) {
            this.order = order;
            return this;
        }

        public Builder productId(UUID productId) {
            this.productId = productId;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder subTotal(BigDecimal subTotal) {
            this.subTotal = subTotal;
            return this;
        }

        public OrderItemEntity build() {
            return new OrderItemEntity(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemEntity that = (OrderItemEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order);
    }

    @Override
    public String toString() {
        return "OrderItemEntity{" +
                "id=" + id +
                ", productId=" + productId +
                ", price=" + price +
                ", quantity=" + quantity +
                ", subTotal=" + subTotal +
                '}';
    }
    
}