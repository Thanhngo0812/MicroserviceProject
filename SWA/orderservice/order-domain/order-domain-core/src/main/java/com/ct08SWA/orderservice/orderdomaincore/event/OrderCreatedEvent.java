package com.ct08SWA.orderservice.orderdomaincore.event;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
// Bỏ import Order (Entity "sạch")
// import com.ct08SWA.orderservice.orderdomaincore.entity.Order;

/**
 * SỬA LẠI: Domain Event "Phẳng" (Flat).
 * Không chứa Entity "sạch" (Order), chỉ chứa dữ liệu "an toàn" cho serialization.
 * Jackson (ObjectMapper) có thể serialize/deserialize class này (JSON-able).
 */
public class OrderCreatedEvent implements OrderEvent {

    // Các trường "phẳng"
    private UUID orderId;
    private UUID customerId;
    private UUID restaurantId;
    private BigDecimal price;
    private ZonedDateTime createdAt; // Đổi tên từ occurredOn
    private List<OrderItemData> items; // Dùng DTO "phẳng" cho items
    private UUID trackingId;

    // Constructor rỗng (BẮT BUỘC cho Jackson Deserialize trong Poller)
    public OrderCreatedEvent() {}

    // Constructor để tạo (GHI) (Dùng bởi Domain Service)
    public OrderCreatedEvent(UUID orderId, UUID customerId, UUID restaurantId, BigDecimal price, ZonedDateTime createdAt, List<OrderItemData> items,
                             UUID trackingId) { // Thêm trackingId
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.price = price;
        this.createdAt = createdAt;
        this.items = items;
        this.trackingId = trackingId; // Gán trackingId
    }

    // Lớp con (nested) "phẳng" cho OrderItem
    // (Jackson cũng cần constructor rỗng và getters/setters cho lớp này)
    public static class OrderItemData {
        private UUID productId;
        private int quantity;
        private BigDecimal price;
        private BigDecimal subTotal;

        public OrderItemData() {} // Rỗng

        public OrderItemData(UUID productId, int quantity, BigDecimal price, BigDecimal subTotal) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
            this.subTotal = subTotal;
        }

        // Getters/Setters cho Jackson
        public UUID getProductId() { return productId; }
        public void setProductId(UUID productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public BigDecimal getSubTotal() { return subTotal; }
        public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }
    }

    // Getters/Setters cho Jackson
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
    public List<OrderItemData> getItems() { return items; }
    public void setItems(List<OrderItemData> items) { this.items = items; }
    public UUID getTrackingId() { return trackingId; }
    public void setTrackingId(UUID trackingId) { this.trackingId = trackingId; }
}