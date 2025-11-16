package com.ct08SWA.paymentservice.paymentdomaincore.event;

// Import các kiểu dữ liệu "phẳng" (simple types)
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;
// Bỏ import các Entity "sạch" (Payment, CreditEntry, CreditHistory)

/**
 * Lớp cha (Refactored) cho các Event phản hồi.
 * Đã được "làm phẳng" (flat) và tuân thủ POJO (JSON-able).
 */
public abstract class PaymentEvent {

    // Các trường "phẳng" chung
    private UUID paymentId;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal price;
    private ZonedDateTime createdAt;
    private  String Status;
    // Constructor rỗng cho Jackson
    public PaymentEvent() {
    }

    // Constructor (để các lớp con gọi)
    protected PaymentEvent(UUID paymentId, UUID orderId, UUID customerId, BigDecimal price, ZonedDateTime createdAt, String Status) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.price = price;
        this.createdAt = createdAt;
        this.Status = Status;
    }

    // --- Getters and Setters (BẮT BUỘC cho Jackson) ---

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}

