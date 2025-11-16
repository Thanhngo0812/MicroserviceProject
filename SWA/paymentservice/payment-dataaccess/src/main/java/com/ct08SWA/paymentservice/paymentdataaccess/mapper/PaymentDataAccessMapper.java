package com.ct08SWA.paymentservice.paymentdataaccess.mapper;

import com.ct08SWA.paymentservice.paymentdataaccess.entity.CreditEntryEntity;
import com.ct08SWA.paymentservice.paymentdataaccess.entity.CreditHistoryEntity;
import com.ct08SWA.paymentservice.paymentdataaccess.entity.PaymentEntity;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditEntry;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditHistory;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.Payment;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.*; // Import các Value Objects
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component // Đánh dấu là Spring Bean
public class PaymentDataAccessMapper {

    // --- Payment Mapping ---

    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.getId().getValue())
                .customerId(payment.getCustomerId().getValue())
                .orderId(payment.getOrderId().getValue())
                .price(payment.getPrice().getAmount())
                .createdAt(payment.getCreatedAt())
                .status(payment.getStatus())
                .build();
    }

    public Payment paymentEntityToPayment(PaymentEntity paymentEntity) {
        return Payment.builder()
                .id(new PaymentId(paymentEntity.getId()))
                .customerId(new CustomerId(paymentEntity.getCustomerId()))
                .orderId(new OrderId(paymentEntity.getOrderId()))
                .price(new Money(paymentEntity.getPrice()))
                .createdAt(paymentEntity.getCreatedAt())
                .status(paymentEntity.getStatus())
                .build();
    }

    // --- CreditEntry Mapping ---

    public CreditEntryEntity creditEntryToCreditEntryEntity(CreditEntry creditEntry) {
        return CreditEntryEntity.builder()
                .id(creditEntry.getId().getValue())
                .customerId(creditEntry.getCustomerId().getValue())
                .totalCreditAmount(creditEntry.getTotalCreditAmount().getAmount())
                .build();
    }

    public CreditEntry creditEntryEntityToCreditEntry(CreditEntryEntity creditEntryEntity) {
        return CreditEntry.builder()
                .id(new CreditEntryId(creditEntryEntity.getId()))
                .customerId(new CustomerId(creditEntryEntity.getCustomerId()))
                .totalCreditAmount(new Money(creditEntryEntity.getTotalCreditAmount()))
                .build();
    }


    // --- CreditHistory Mapping ---

    public CreditHistoryEntity creditHistoryToCreditHistoryEntity(CreditHistory creditHistory) {
        return CreditHistoryEntity.builder()
                .id(creditHistory.getId().getValue())
                .customerId(creditHistory.getCustomerId().getValue())
                .orderId(creditHistory.getOrderId().getValue()) // Thêm orderId
                .amount(creditHistory.getAmount().getAmount())
                .type(creditHistory.getTransactionType())
                .build();
    }

    public CreditHistory creditHistoryEntityToCreditHistory(CreditHistoryEntity creditHistoryEntity) {
        return CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(creditHistoryEntity.getId()))
                .customerId(new CustomerId(creditHistoryEntity.getCustomerId()))
                .orderId(new OrderId(creditHistoryEntity.getOrderId())) // Thêm orderId
                .amount(new Money(creditHistoryEntity.getAmount()))
                .transactionType(creditHistoryEntity.getType())
                .build();
    }

    public List<CreditHistory> creditHistoryEntityListToCreditHistoryList(List<CreditHistoryEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::creditHistoryEntityToCreditHistory)
                .toList();
    }
}

