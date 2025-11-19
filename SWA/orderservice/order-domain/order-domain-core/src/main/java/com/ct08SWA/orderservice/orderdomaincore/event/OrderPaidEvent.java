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
public class OrderPaidEvent implements OrderEvent {

    // Các trường "phẳng"
    private UUID orderId;
    private UUID customerId;
    private UUID restaurantId;
    private BigDecimal price;
    private ZonedDateTime createdAt; // Đổi tên từ occurredOn
    private UUID trackingId;

    // Constructor rỗng (BẮT BUỘC cho Jackson Deserialize trong Poller)
    public OrderPaidEvent() {}

    // Constructor để tạo (GHI) (Dùng bởi Domain Service)
    public OrderPaidEvent(UUID orderId, UUID customerId, UUID restaurantId, BigDecimal price, ZonedDateTime createdAt,
                             UUID trackingId) { // Thêm trackingId
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.price = price;
        this.createdAt = createdAt;
        this.trackingId = trackingId; // Gán trackingId
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
    public UUID getTrackingId() { return trackingId; }
    public void setTrackingId(UUID trackingId) { this.trackingId = trackingId; }
}