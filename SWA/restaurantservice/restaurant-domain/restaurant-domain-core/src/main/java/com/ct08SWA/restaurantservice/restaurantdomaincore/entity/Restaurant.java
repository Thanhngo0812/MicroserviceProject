package com.ct08SWA.restaurantservice.restaurantdomaincore.entity;


import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Aggregate Root: Restaurant (Nhà hàng).
 * Chứa thông tin nhà hàng và Menu (danh sách Products).
 */
public class Restaurant extends AggregateRoot<RestaurantId> {

    private final List<Product> products;
    private boolean active;
    private final String name;

    // Map để truy cập Product nhanh (không phải là trường, chỉ là cache)
    private transient Map<ProductId, Product> productMap;

    // --- Constructor và Builder (Không Lombok) ---
    private Restaurant(Builder builder) {
        super.setId(builder.id);
        this.products = builder.products;
        this.active = builder.active;
        this.name = builder.name;
    }
    public static Builder builder() { return new Builder(); }

    // --- Logic nghiệp vụ (Business Logic) ---

    /**
     * Xác thực (Validate) một đơn hàng (OrderApproval).
     * Kiểm tra xem nhà hàng có active không và các món ăn có sẵn không.
     * Ném (throw) RestaurantDomainException nếu thất bại.
     */
    public void validateOrder(OrderApproval orderApproval) {
        if (!this.active) {
            throw new com.ct08SWA.restaurant.domain.exception.RestaurantDomainException(
                    "Nhà hàng " + this.name + " (ID: " + this.getId().getValue() + ") không hoạt động (inactive)!"
            );
        }

        // Tải map (nếu chưa có) để truy cập O(1)
        if (this.productMap == null) {
            this.productMap = this.products.stream()
                    .collect(Collectors.toMap(Product::getId, Function.identity()));
        }

        // Duyệt qua các món ăn trong đơn hàng
        for (OrderProduct item : orderApproval.getProducts()) {
            Product productInMenu = this.productMap.get(item.getId());

            // 1. Kiểm tra Món ăn có tồn tại trong Menu không
            if (productInMenu == null) {
                throw new com.ct08SWA.restaurant.domain.exception.RestaurantDomainException(
                        "Món ăn (Product ID: " + item.getId().getValue() + ") không tìm thấy trong menu của nhà hàng."
                );
            }

            // 2. Kiểm tra Món ăn có Sẵn hàng (Available) không
            if (!productInMenu.isAvailable()) {
                throw new com.ct08SWA.restaurant.domain.exception.RestaurantDomainException(
                        "Món ăn '" + productInMenu.getName() + "' (ID: " + item.getId().getValue() + ") đã hết hàng (unavailable)."
                );
            }

            // 3. (Tùy chọn) Kiểm tra giá (Price) có khớp không
            if (!productInMenu.getPrice().equals(item.getPrice())) {
                log.warn("Giá của món ăn {} đã thay đổi (Request: {}, Menu: {}).",
                        item.getId().getValue(), item.getPrice().getAmount(), productInMenu.getPrice().getAmount());
                // Tạm thời cho qua, chỉ cảnh báo
            }
        }

        log.info("Đơn hàng (Order ID: {}) đã được xác thực (validated) thành công bởi nhà hàng.", orderApproval.getOrderId().getValue());
    }


    // --- Getters ---
    public List<Product> getProducts() { return products; }
    public boolean isActive() { return active; }
    public String getName() { return name; }

    // --- Builder ---
    public static final class Builder {
        private RestaurantId id;
        private List<Product> products;
        private boolean active;
        private String name;

        private Builder() {}
        public Builder id(RestaurantId val) { id = val; return this; }
        public Builder products(List<Product> val) { products = val; return this; }
        public Builder active(boolean val) { active = val; return this; }
        public Builder name(String val) { name = val; return this; }

        public Restaurant build() {
            return new Restaurant(this);
        }
    }
}