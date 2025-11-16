package com.ct08SWA.paymentservice.paymentdomaincore.entity;


import com.ct08SWA.paymentservice.paymentdomaincore.exception.PaymentDomainException;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.CustomerId;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.Money;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.OrderId;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.PaymentId;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.PaymentStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

// Aggregate Root cho Payment
public class Payment extends AggregateRoot<PaymentId> {
    private final OrderId orderId;
    private final CustomerId customerId;
    private final Money price;

    private PaymentStatus status;
    private ZonedDateTime createdAt;

    // Logic nghiệp vụ để khởi tạo
    public void initializePayment() {
        setId(new PaymentId(UUID.randomUUID()));
        createdAt = ZonedDateTime.now(ZoneId.of("UTC")); // Luôn dùng UTC
        status = PaymentStatus.PENDING;
    }

    // Logic nghiệp vụ để validate
    public void validatePayment() {
        if (price == null || !price.isGreaterThanZero()) {
            throw new com.ct08SWA.paymentservice.paymentdomaincore.exception.PaymentDomainException("Total price must be greater than zero!");
        }
    }

    // Cập nhật trạng thái
    public void updateStatus(PaymentStatus newStatus) {
        this.status = newStatus;
    }


    /**
     * Chuyển trạng thái sang COMPLETED.
     * Chỉ được gọi khi thanh toán thành công.
     */
    public void completePayment() {
        if (this.status != PaymentStatus.PENDING) {
            throw new PaymentDomainException("Payment is not in correct state for complete operation!");
        }
        this.status = PaymentStatus.COMPLETED;
    }

    /**
     * Chuyển trạng thái sang CANCELLED.
     * Chỉ được gọi khi cần hoàn tiền (SAGA Compensation).
     */
    public void cancelPayment() {
        // Thường thì việc cancel chỉ xảy ra khi payment đã completed hoặc pending (nếu lỗi sớm)
        // Logic này có thể cần điều chỉnh tùy theo yêu cầu SAGA
        if (this.status != PaymentStatus.PENDING && this.status != PaymentStatus.COMPLETED) {
            throw new PaymentDomainException("Payment is not in correct state for cancel operation!");
        }
        this.status = PaymentStatus.CANCELLED;
    }

    /**
     * Chuyển trạng thái sang FAILED.
     * Chỉ được gọi khi thanh toán thất bại (ví dụ: không đủ tiền).
     * Lưu ý: Trong logic hiện tại, FAILED event được tạo ở Application Service,
     * nhưng có thể bạn muốn cập nhật status ở đây nếu cần.
     */
    public void failPayment() {
        if (this.status != PaymentStatus.PENDING) {
            throw new PaymentDomainException("Payment is not in correct state for fail operation!");
        }
        this.status = PaymentStatus.FAILED;
    }


    // Private constructor, chỉ dùng qua Builder
    private Payment(Builder builder) {
        setId(builder.paymentId);
        orderId = builder.orderId;
        customerId = builder.customerId;
        price = builder.price;
        status = builder.status;
        createdAt = builder.createdAt;
    }

    // Getters
    public OrderId getOrderId() { return orderId; }
    public CustomerId getCustomerId() { return customerId; }
    public Money getPrice() { return price; }
    public PaymentStatus getStatus() { return status; }
    public ZonedDateTime getCreatedAt() { return createdAt; }


    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }
    public static final class Builder {
        private PaymentId paymentId;
        private OrderId orderId;
        private CustomerId customerId;
        private Money price;
        private PaymentStatus status;
        private ZonedDateTime createdAt;

        private Builder() {}

        public Builder id(PaymentId val) { paymentId = val; return this; }
        public Builder orderId(OrderId val) { orderId = val; return this; }
        public Builder customerId(CustomerId val) { customerId = val; return this; }
        public Builder price(Money val) { price = val; return this; }
        public Builder status(PaymentStatus val) { status = val; return this; }
        public Builder createdAt(ZonedDateTime val) { createdAt = val; return this; }

        public Payment build() {
            // SỬA LẠI: Gọi hàm initializePayment sau khi build để đảm bảo status và ID
            Payment payment = new Payment(this);
            return payment;
        }
    }
}
