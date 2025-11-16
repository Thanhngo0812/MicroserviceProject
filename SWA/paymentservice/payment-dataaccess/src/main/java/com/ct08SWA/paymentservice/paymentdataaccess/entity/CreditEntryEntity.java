package com.ct08SWA.paymentservice.paymentdataaccess.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "credit_entry", schema = "payment")
@Entity
public class CreditEntryEntity {

    @Id
    private UUID id;

    @Column(unique = true) // Đảm bảo customerId là unique
    private UUID customerId;

    private BigDecimal totalCreditAmount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditEntryEntity that = (CreditEntryEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
