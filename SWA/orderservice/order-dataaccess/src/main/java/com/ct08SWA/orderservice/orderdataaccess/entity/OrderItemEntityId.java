package com.ct08SWA.orderservice.orderdataaccess.entity;


import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class OrderItemEntityId implements Serializable {
    private Long id;
    private UUID order;

    public OrderItemEntityId() {}

    public OrderItemEntityId(Long id, UUID order) {
        this.id = id;
        this.order = order;
    }

    public Long getId() { return id; }
    public UUID getOrder() { return order; }

    public void setId(Long id) { this.id = id; }
    public void setOrder(UUID order) { this.order = order; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemEntityId that = (OrderItemEntityId) o;
        return Objects.equals(id, that.id) && Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order);
    }
}