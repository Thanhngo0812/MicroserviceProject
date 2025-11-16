package com.ct08SWA.paymentservice.paymentdataaccess.entity;


// Import Enum "sạch" từ Domain Core

import jakarta.persistence.*; // Dùng jakarta
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA Entity, ánh xạ CSDL bảng "payment_outbox".
 * KHÔNG CÓ LOMBOK.
 */
@Entity
@Table(name = "payment_outbox", schema = "payment")
public class PaymentOutboxEntity {

    @Id
    private UUID id;

    @Column(name = "saga_id", nullable = false)
    private UUID sagaId;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON) // Cú pháp hiện đại cho Hibernate 6+
    @Column(name = "payload", columnDefinition = "JSON", nullable = false)
    private String payload; // Kiểu TEXT trong CSDL


    // --- Constructors ---

    // Constructor rỗng (Bắt buộc cho JPA)
    public PaymentOutboxEntity() {
    }

    // Constructor (thường dùng qua Builder)
    private PaymentOutboxEntity(Builder builder) {
        this.id = builder.id;
        this.sagaId = builder.sagaId;
        this.createdAt = builder.createdAt;
        this.eventType = builder.eventType;
        this.payload = builder.payload;
    }

    // --- Getters ---

    public UUID getId() { return id; }
    public UUID getSagaId() { return sagaId; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }

    // --- Setters ---

    public void setId(UUID id) { this.id = id; }
    public void setSagaId(UUID sagaId) { this.sagaId = sagaId; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setPayload(String payload) { this.payload = payload; }

    // --- equals & hashCode (Rất quan trọng cho JPA) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentOutboxEntity that = (PaymentOutboxEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // --- Static Builder Method ---

    public static Builder builder() {
        return new Builder();
    }

    // --- Builder Class (Pattern thủ công) ---

    public static final class Builder {
        private UUID id;
        private UUID sagaId;
        private ZonedDateTime createdAt;
        private String eventType;
        private String payload;

        private Builder() {}

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder sagaId(UUID sagaId) {
            this.sagaId = sagaId;
            return this;
        }

        public Builder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }


        public Builder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }



        public PaymentOutboxEntity build() {
            return new PaymentOutboxEntity(this);
        }
    }
}
