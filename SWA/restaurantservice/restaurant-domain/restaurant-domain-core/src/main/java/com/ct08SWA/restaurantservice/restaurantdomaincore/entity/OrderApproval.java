package com.ct08SWA.restaurantservice.restaurantdomaincore.entity;


import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root: OrderApproval (Phiếu duyệt đơn hàng).
 * Dùng để theo dõi trạng thái SAGA (Idempotency).
 * KHÔNG CÓ LOMBOK.
 */
public class OrderApproval extends AggregateRoot<OrderApprovalId> {

    private final RestaurantId restaurantId;
    private final OrderId orderId;
    // Danh sách các món ăn trong Order (chỉ chứa thông tin cần thiết)
    private final List<OrderProduct> products;
    private ApprovalStatus approvalStatus;
    private List<String> failureMessages;

    // Constructor (private, dùng Builder)
    private OrderApproval(Builder builder) {
        super.setId(builder.id);
        this.restaurantId = builder.restaurantId;
        this.orderId = builder.orderId;
        this.products = builder.products;
        this.approvalStatus = builder.approvalStatus;
        this.failureMessages = builder.failureMessages;
    }

    // --- Logic nghiệp vụ (Business Logic) ---

    /**
     * Khởi tạo (Status PENDING)
     */
    public void initialize() {
        setId(new OrderApprovalId(UUID.randomUUID()));
        this.approvalStatus = ApprovalStatus.PENDING;
    }

    /**
     * Đánh dấu (Mark) là đã duyệt (do Domain Service gọi)
     */
    public void approve() {
        this.approvalStatus = ApprovalStatus.APPROVED;
    }

    /**
     * Đánh dấu (Mark) là đã từ chối (do Domain Service gọi)
     */
    public void reject(List<String> failureMessages) {
        this.approvalStatus = ApprovalStatus.REJECTED;
        this.failureMessages = failureMessages;
    }

    // --- Getters ---
    public RestaurantId getRestaurantId() { return restaurantId; }
    public OrderId getOrderId() { return orderId; }
    public List<OrderProduct> getProducts() { return products; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public List<String> getFailureMessages() { return failureMessages; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private OrderApprovalId id;
        private RestaurantId restaurantId;
        private OrderId orderId;
        private List<OrderProduct> products;
        private ApprovalStatus approvalStatus;
        private List<String> failureMessages;

        private Builder() {}

        public Builder id(OrderApprovalId val) { id = val; return this; }
        public Builder restaurantId(RestaurantId val) { restaurantId = val; return this; }
        public Builder orderId(OrderId val) { orderId = val; return this; }
        public Builder products(List<OrderProduct> val) { products = val; return this; }
        public Builder approvalStatus(ApprovalStatus val) { approvalStatus = val; return this; }
        public Builder failureMessages(List<String> val) { failureMessages = val; return this; }

        public OrderApproval build() {
            return new OrderApproval(this);
        }
    }
}