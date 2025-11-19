package com.ct08SWA.restaurantservice.restaurantdataaccess.entity;


import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.ApprovalStatus;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA Entity: Ánh xạ bảng 'order_approvals'.
 * (Không Lombok)
 */
@Entity
@Table(name = "order_approvals", schema = "restaurant")
public class OrderApprovalEntity {

    @Id
    private UUID id;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status;

    @Column(name = "failure_messages")
    private String failureMessages;

    @Column(name = "warning_message")
    private String warnings; // Thêm trường

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    // (Bảng này không cần List<Product> vì chúng ta không lưu chi tiết
    //  món ăn của Order, chúng ta chỉ lưu trạng thái duyệt)

    // --- Constructors ---
    public OrderApprovalEntity() {}

    // --- Getters / Setters ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getRestaurantId() { return restaurantId; }
    public void setRestaurantId(UUID restaurantId) { this.restaurantId = restaurantId; }
    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public ApprovalStatus getStatus() { return status; }
    public void setStatus(ApprovalStatus status) { this.status = status; }
    public String getFailureMessages() { return failureMessages; }
    public void setFailureMessages(String failureMessages) { this.failureMessages = failureMessages; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    // Bổ sung Getter/Setter cho Warnings
    public String getWarnings() { return warnings; }
    public void setWarnings(String warnings) { this.warnings = warnings; }


    // --- Equals / HashCode ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderApprovalEntity that = (OrderApprovalEntity) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}