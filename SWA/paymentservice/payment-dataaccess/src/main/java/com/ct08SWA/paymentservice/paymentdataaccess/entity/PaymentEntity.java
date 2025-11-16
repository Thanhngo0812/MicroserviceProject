package com.ct08SWA.paymentservice.paymentdataaccess.entity;


import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.PaymentStatus; // Dùng Enum từ domain
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments", schema = "payment") // Chỉ định schema
@Entity
public class PaymentEntity {

    @Id
    private UUID id;

    private UUID customerId;
    @Column(unique = true) // Đảm bảo orderId là unique
    private UUID orderId;
    private BigDecimal price;
    private ZonedDateTime createdAt;

    @Enumerated(EnumType.STRING) // Lưu Enum dưới dạng String
    private PaymentStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentEntity that = (PaymentEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

