package com.ct08SWA.restaurantservice.restaurantdomaincore.entity;

import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.*;
/**
 * Entity: OrderProduct (Món ăn trong đơn hàng).
 * Đây là phiên bản "rút gọn", chỉ chứa thông tin mà Restaurant Service cần
 * để xác thực (validate) đơn hàng.
 * KHÔNG CÓ LOMBOK.
 */
public class OrderProduct extends BaseEntity<ProductId> {

    private final Money price;
    private final int quantity;
    // (Có thể thêm 'name' nếu cần, nhưng 'id' là đủ để validate)

    // Constructor (private, dùng Builder)
    private OrderProduct(Builder builder) {
        super.setId(builder.id);
        this.price = builder.price;
        this.quantity = builder.quantity;
    }

    // Getters
    public Money getPrice() { return price; }
    public int getQuantity() { return quantity; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private ProductId id;
        private Money price;
        private int quantity;

        private Builder() {}

        public Builder id(ProductId val) { id = val; return this; }
        public Builder price(Money val) { price = val; return this; }
        public Builder quantity(int val) { quantity = val; return this; }

        public OrderProduct build() {
            return new OrderProduct(this);
        }
    }
}