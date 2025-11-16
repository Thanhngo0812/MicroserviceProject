package com.ct08SWA.orderservice.orderdomaincore.entity;

import com.ct08SWA.orderservice.orderdomaincore.valueobject.Money;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.OrderId;


public class OrderItem {
    private Long id;
    private OrderId orderId;
    private final Product product;
    private final int quantity;
    private final Money price;
    private final Money subTotal;

    public void initializeOrderItem(OrderId orderId, Long itemId) {
        this.orderId = orderId;
        this.id = itemId;
    }

    public boolean isPriceValid() {
        return price.isGreaterThanZero() &&
                price.equals(product.getPrice()) &&
                price.multiply(quantity).equals(subTotal);
    }

    // Constructor, getters, builder...
    private OrderItem(Builder builder) {
        product = builder.product;
        quantity = builder.quantity;
        price = builder.price;
        subTotal = builder.subTotal;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() { return id; }
    public void setId(Long id){
        this.id = id;
    }
    public OrderId getOrderId() { return orderId; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public Money getPrice() { return price; }
    public Money getSubTotal() { return subTotal; }
    public static final class Builder {
        private Product product;
        private int quantity;
        private Money price;
        private Money subTotal;

        private Builder() {}

        public Builder product(Product val) {
            product = val;
            return this;
        }

        public Builder quantity(int val) {
            quantity = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder subTotal(Money val) {
            subTotal = val;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}