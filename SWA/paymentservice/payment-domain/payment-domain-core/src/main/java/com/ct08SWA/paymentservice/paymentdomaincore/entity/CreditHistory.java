package com.ct08SWA.paymentservice.paymentdomaincore.entity;

import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.*;

// Entity cho Credit History (không phải Aggregate Root)
import java.util.UUID;

/**
 * Entity: CreditHistory - Ghi lại lịch sử giao dịch tín dụng (Cộng/Trừ).
 */
public class CreditHistory extends BaseEntity<CreditHistoryId> {

    private final CustomerId customerId;
    private final OrderId orderId; // Thêm trường này để liên kết với Order
    private final Money amount;
    private final TransactionType transactionType;

    // --- Constructor, Getters, Builder ---
    private CreditHistory(Builder builder) {
        super.setId(builder.creditHistoryId);
        customerId = builder.customerId;
        orderId = builder.orderId; // Gán giá trị orderId
        amount = builder.amount;
        transactionType = builder.transactionType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public Money getAmount() {
        return amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }


    public static final class Builder {
        private CreditHistoryId creditHistoryId; // Phải có trường này
        private CustomerId customerId;
        private OrderId orderId; // Phải có trường này
        private Money amount;
        private TransactionType transactionType;

        private Builder() {
        }

        // ============================================
        // BỔ SUNG PHƯƠNG THỨC BỊ THIẾU
        // ============================================
        public Builder creditHistoryId(CreditHistoryId val) {
            creditHistoryId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder orderId(OrderId val) { // Phải có phương thức này
            orderId = val;
            return this;
        }

        public Builder amount(Money val) {
            amount = val;
            return this;
        }

        public Builder transactionType(TransactionType val) {
            transactionType = val;
            return this;
        }

        public CreditHistory build() {
            // Có thể thêm validation ở đây nếu cần
            return new CreditHistory(this);
        }
    }
}

