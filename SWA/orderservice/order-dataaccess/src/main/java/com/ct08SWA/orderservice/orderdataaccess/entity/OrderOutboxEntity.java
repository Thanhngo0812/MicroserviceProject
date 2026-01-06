package com.ct08SWA.orderservice.orderdataaccess.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA Entity, ánh xạ (map) tới bảng "order_outbox" trong schema "order".
 * SỬA LẠI: Tương thích với Debezium + Avro (dùng byte[]).
 */
@Entity
@Table(name = "order_outbox", schema = "orders")
public class OrderOutboxEntity {

    @Id
    private UUID id;

    @Column(name = "saga_id", nullable = false)
    private UUID sagaId;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "event_type", nullable = false)
    private String eventType;
    // SỬA LẠI: Đổi String sang byte[]
     // @Lob (Large Object) là cách tốt để báo cho JPA biết đây là cột lớn (TEXT/CLOB)
    @JdbcTypeCode(SqlTypes.JSON) // Cú pháp hiện đại cho Hibernate 6+
    @Column(name = "payload", columnDefinition = "JSON", nullable = false)
    private String payload; // Đổi sang byte[]
    @Column(name = "status", nullable = false)
    private String status;
    // --- Constructors ---

    public OrderOutboxEntity() {
    }

    /**
     * Constructor đầy đủ tham số (all-args)
     * SỬA LẠI: Nhận byte[]
     */
    public OrderOutboxEntity(UUID id, UUID sagaId, ZonedDateTime createdAt, String eventType, String  payload,String status) {
        this.id = id;
        this.sagaId = sagaId;
        this.createdAt = createdAt;
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
    }

    // --- Getters ---

    public UUID getId() {
        return id;
    }

    public UUID getSagaId() {
        return sagaId;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }


    public String getEventType() {
        return eventType;
    }

    // SỬA LẠI: Trả về byte[]
    public String getPayload() {
        return payload;
    }



    // --- Setters ---

    public void setId(UUID id) {
        this.id = id;
    }

    public void setSagaId(UUID sagaId) {
        this.sagaId = sagaId;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    // SỬA LẠI: Nhận byte[]
    public void setPayload(String  payload) {
        this.payload = payload;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    // --- Equals & HashCode (Không đổi) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderOutboxEntity that = (OrderOutboxEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // --- Builder Pattern (SỬA LẠI) ---

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID id;
        private UUID sagaId;
        private ZonedDateTime createdAt;
        private String eventType;
        private String  payload;
        private String status;
        private Builder() {
        }

        public Builder id(UUID val) {
            id = val;
            return this;
        }

        public Builder sagaId(UUID val) {
            sagaId = val;
            return this;
        }

        public Builder createdAt(ZonedDateTime val) {
            createdAt = val;
            return this;
        }



        public Builder eventType(String val) {
            eventType = val;
            return this;
        }

        // SỬA LẠI: Nhận byte[]
        public Builder payload(String  val) {
            payload = val;
            return this;
        }

        public Builder status(String  val) {
            status = val;
            return this;
        }


        public OrderOutboxEntity build() {
            // Gọi constructor all-args (đã sửa)
            return new OrderOutboxEntity(id, sagaId, createdAt, eventType, payload,status);
        }
    }
}