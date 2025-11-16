package com.ct08SWA.paymentservice.paymentdomaincore.event;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Event (POJO "phẳng") cho thanh toán THẤT BẠI.
 * (JSON-able)
 */
public class PaymentFailedEvent extends PaymentEvent {

    // Trường riêng
    private List<String> failureMessages;

    /**
     * Constructor rỗng cho Jackson (Deserialize).
     */
    public PaymentFailedEvent() {
        super(); // Gọi constructor rỗng của cha
    }

    /**
     * Constructor (để Application Service gọi).
     * @param paymentId ID của Payment (có thể là null nếu Payment chưa được tạo)
     * @param orderId ID của Order
     * @param customerId ID của Customer
     * @param price Giá
     * @param createdAt Thời gian tạo
     * @param failureMessages Danh sách lý do thất bại
     */
    public PaymentFailedEvent(UUID paymentId, UUID orderId, UUID customerId, BigDecimal price, ZonedDateTime createdAt, List<String> failureMessages,String status) {
        // Gọi constructor cha
        super(paymentId, orderId, customerId, price, createdAt,status);
        this.failureMessages = failureMessages;
    }

    // --- Getters and Setters (BẮT BUỘC cho Jackson) ---

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public void setFailureMessages(List<String> failureMessages) {
        this.failureMessages = failureMessages;
    }
}

