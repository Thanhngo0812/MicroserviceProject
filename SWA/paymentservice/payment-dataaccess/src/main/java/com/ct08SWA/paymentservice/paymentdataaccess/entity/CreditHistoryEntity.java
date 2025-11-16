package com.ct08SWA.paymentservice.paymentdataaccess.entity;

import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.TransactionType; // Dùng Enum từ domain
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "credit_history", schema = "payment")
@Entity
public class CreditHistoryEntity {

    @Id
    private UUID id;

    private UUID customerId;
    private UUID orderId; // Thêm orderId
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditHistoryEntity that = (CreditHistoryEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
