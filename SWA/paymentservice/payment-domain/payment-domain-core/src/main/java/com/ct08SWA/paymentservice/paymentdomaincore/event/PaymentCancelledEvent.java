package com.ct08SWA.paymentservice.paymentdomaincore.event;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Event (POJO "phẳng") cho thanh toán BỊ HỦY (Hoàn tiền).
 * (JSON-able)
 */
public class PaymentCancelledEvent extends PaymentEvent {

    // Trường riêng (ID của Lịch sử Giao dịch HOÀN TIỀN)
    private UUID creditHistoryId;

    /**
     * Constructor rỗng cho Jackson (Deserialize).
     */
    public PaymentCancelledEvent() {
        super(); // Gọi constructor rỗng của cha
    }

    /**
     * Constructor (để Domain Service gọi).
     * @param paymentId ID của Payment
     * @param orderId ID của Order
     * @param customerId ID của Customer
     * @param price Giá (số tiền đã hoàn)
     * @param createdAt Thời gian tạo
     * @param creditHistoryId ID của CreditHistory (CREDIT) mới
     */
    public PaymentCancelledEvent(UUID paymentId, UUID orderId, UUID customerId, BigDecimal price, ZonedDateTime createdAt, UUID creditHistoryId,String status) {
        // Gọi constructor cha
        super(paymentId, orderId, customerId, price, createdAt,status);
        this.creditHistoryId = creditHistoryId;
    }

    // --- Getters and Setters (BẮT BUỘC cho Jackson) ---

    public UUID getCreditHistoryId() {
        return creditHistoryId;
    }

    public void setCreditHistoryId(UUID creditHistoryId) {
        this.creditHistoryId = creditHistoryId;
    }
}

