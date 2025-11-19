package com.ct08SWA.paymentservice.paymentdomaincore.entity;


import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.CreditEntryId;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.CustomerId;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.Money;

// Aggregate Root cho Credit Entry (số dư)
public class CreditEntry extends AggregateRoot<CreditEntryId> {
    private final CustomerId customerId;
    private Money totalCreditAmount;

    // Logic nghiệp vụ: Cộng tiền (hoàn tiền)
    public void addCreditAmount(Money amount) {
        totalCreditAmount = totalCreditAmount.add(amount);
    }

    // Logic nghiệp vụ: Trừ tiền (thanh toán)
    public void subtractCreditAmount(Money amount) {
        totalCreditAmount = totalCreditAmount.subtract(amount);
        System.out.println(totalCreditAmount.toString());
        if (!totalCreditAmount.isGreaterThanZero()) {
            throw new com.ct08SWA.paymentservice.paymentdomaincore.exception.PaymentDomainException("Customer credit balance cannot be negative!");
        }
    }


    // Private constructor, dùng qua Builder
    private CreditEntry(Builder builder) {
        setId(builder.creditEntryId);
        customerId = builder.customerId;
        totalCreditAmount = builder.totalCreditAmount;
    }

    // Getters
    public CustomerId getCustomerId() { return customerId; }
    public Money getTotalCreditAmount() { return totalCreditAmount; }


    // Builder Pattern
    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private CreditEntryId creditEntryId;
        private CustomerId customerId;
        private Money totalCreditAmount;

        private Builder() {}

        public Builder id(CreditEntryId val) { creditEntryId = val; return this; }
        public Builder customerId(CustomerId val) { customerId = val; return this; }
        public Builder totalCreditAmount(Money val) { totalCreditAmount = val; return this; }

        public CreditEntry build() {
            return new CreditEntry(this);
        }
    }
}
