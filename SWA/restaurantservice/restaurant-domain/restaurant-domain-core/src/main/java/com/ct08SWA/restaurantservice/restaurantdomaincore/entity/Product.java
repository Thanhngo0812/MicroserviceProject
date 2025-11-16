package com.ct08SWA.restaurantservice.restaurantdomaincore.entity;
import java.util.Objects;

/**
 * Entity: Product (Món ăn) - Là một phần của Aggregate 'Restaurant'.
 * KHÔNG CÓ LOMBOK.
 */
public class Product extends BaseEntity<ProductId> {

    private final String name;
    private final Money price;
    private final boolean available; // Còn hàng/Hết hàng

    // Constructor (private, dùng Builder)
    private Product(Builder builder) {
        super.setId(builder.id);
        this.name = builder.name;
        this.price = builder.price;
        this.available = builder.available;
    }

    // Getters
    public String getName() {
        return name;
    }
    public Money getPrice() {
        return price;
    }
    public boolean isAvailable() {
        return available;
    }

    // Static Builder method
    public static Builder builder() {
        return new Builder();
    }

    // Builder Class (Pattern thủ công)
    public static final class Builder {
        private ProductId id;
        private String name;
        private Money price;
        private boolean available;

        private Builder() {}

        public Builder id(ProductId val) { id = val; return this; }
        public Builder name(String val) { name = val; return this; }
        public Builder price(Money val) { price = val; return this; }
        public Builder available(boolean val) { available = val; return this; }

        public Product build() {
            // (Nên thêm validation ở đây, ví dụ: price > 0)
            return new Product(this);
        }
    }
}