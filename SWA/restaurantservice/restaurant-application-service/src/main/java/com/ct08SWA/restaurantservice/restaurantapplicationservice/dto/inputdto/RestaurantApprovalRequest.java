package com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO (Input) "sạch" cho Yêu cầu Duyệt đơn hàng (SAGA Bước 3).
 * (Không Lombok)
 */
public class RestaurantApprovalRequest {

    private final UUID orderId;
    private final UUID restaurantId;
    private final BigDecimal price; // Giá của Order
    private final ZonedDateTime createdAt; // Thời gian Order được tạo
    private final List<ProductRequest> products; // Danh sách món ăn

    // Constructor (private, dùng Builder)
    private RestaurantApprovalRequest(Builder builder) {
        this.orderId = builder.orderId;
        this.restaurantId = builder.restaurantId;
        this.price = builder.price;
        this.createdAt = builder.createdAt;
        this.products = builder.products;
    }

    // Getters
    public UUID getOrderId() { return orderId; }
    public UUID getRestaurantId() { return restaurantId; }
    public BigDecimal getPrice() { return price; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public List<ProductRequest> getProducts() { return products; }

    // Static Builder method
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Lớp con (nested) "sạch" cho Product (Món ăn)
     */
    public static class ProductRequest {
        private final UUID id;
        private final int quantity;
        private final BigDecimal price; // Giá của món ăn (Item)

        // Constructor (private, dùng Builder)
        private ProductRequest(Builder builder) {
            this.id = builder.id;
            this.quantity = builder.quantity;
            this.price = builder.price;
        }

        // Getters
        public UUID getId() { return id; }
        public int getQuantity() { return quantity; }
        public BigDecimal getPrice() { return price; }

        // Builder cho ProductRequest
        public static Builder builder() { return new Builder(); }

        public static final class Builder {
            private UUID id;
            private int quantity;
            private BigDecimal price;

            private Builder() {}
            public Builder id(UUID val) { id = val; return this; }
            public Builder quantity(int val) { quantity = val; return this; }
            public Builder price(BigDecimal val) { price = val; return this; }
            public ProductRequest build() { return new ProductRequest(this); }
        }
    }

    // Builder cho RestaurantApprovalRequest
    public static final class Builder {
        private UUID orderId;
        private UUID restaurantId;
        private BigDecimal price;
        private ZonedDateTime createdAt;
        private List<ProductRequest> products;

        private Builder() {}
        public Builder orderId(UUID val) { orderId = val; return this; }
        public Builder restaurantId(UUID val) { restaurantId = val; return this; }
        public Builder price(BigDecimal val) { price = val; return this; }
        public Builder createdAt(ZonedDateTime val) { createdAt = val; return this; }
        public Builder products(List<ProductRequest> val) { products = val; return this; }
        public RestaurantApprovalRequest build() { return new RestaurantApprovalRequest(this); }
    }
}