package com.ct08SWA.restaurantservice.restaurantdomaincore.entity;

import com.ct08SWA.restaurantservice.restaurantdomaincore.event.RestaurantApprovedEvent;
import com.ct08SWA.restaurantservice.restaurantdomaincore.event.RestaurantRejectedEvent;
import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;

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
    private final List<String> warnings = new ArrayList<>();

    // --- Constructor (private, dùng Builder) ---
    private OrderApproval(Builder builder) {
        super.setId(builder.id);
        this.restaurantId = builder.restaurantId;
        this.orderId = builder.orderId;
        this.products = builder.products;
        this.approvalStatus = builder.approvalStatus;
        this.failureMessages = builder.failureMessages;
        // warnings được tự khởi tạo, không cần builder
    }

    // --- Logic nghiệp vụ (Business Logic) ---

    /**
     * Khởi tạo (Status PENDING)
     */
    public void initialize() {
        setId(new OrderApprovalId(UUID.randomUUID()));
        this.approvalStatus = ApprovalStatus.WAITING;
    }

    /**
     * Đánh dấu (Mark) là đã duyệt (do Domain Service gọi)
     */
    public void approve() {
        this.approvalStatus = ApprovalStatus.APPROVED;
        this.addDomainEvent(new RestaurantApprovedEvent(
                this.getId().getValue(),
                this.getOrderId().getValue(),
                this.getRestaurantId().getValue(),
                "APPROVED",
                ZonedDateTime.now(UTC)
        ));
    }

    /**
     * Đánh dấu (Mark) là đã từ chối (do Domain Service gọi)
     */
    public void reject(List<String> failureMessages) {
        this.approvalStatus = ApprovalStatus.REJECTED;
        this.failureMessages = failureMessages;
        this.addDomainEvent( new RestaurantRejectedEvent(
                        this.getId().getValue(),
                        this.getOrderId().getValue(),
                        this.getRestaurantId().getValue(),
                        ZonedDateTime.now(UTC),
                        "REJECTED",
                        this.failureMessages
                ));
    }

    public void cancel(List<String> failureMessages) {
        this.approvalStatus = ApprovalStatus.CANCELLED;
        this.failureMessages = failureMessages;
    }


    public void paid() {
        this.approvalStatus = ApprovalStatus.PENDING;
    }

    // --- Logic Cảnh báo (Code của bạn) ---

    /**
     * Thêm một cảnh báo (non-fatal) trong quá trình validate.
     * Ví dụ: "Giá món ăn đã thay đổi".
     * (Được gọi bởi RestaurantDomainServiceImpl)
     */
    public void addWarning(String message) {
        if (message != null && !message.isEmpty()) {
            this.warnings.add(message);
        }
    }

    // --- Getters ---
    public RestaurantId getRestaurantId() { return restaurantId; }
    public OrderId getOrderId() { return orderId; }
    public List<OrderProduct> getProducts() { return products; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public List<String> getFailureMessages() { return failureMessages; }

    // Getter cho code của bạn
    public List<String> getWarnings() { return warnings; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private OrderApprovalId id;
        private RestaurantId restaurantId;
        private OrderId orderId;
        private List<OrderProduct> products;
        private ApprovalStatus approvalStatus;
        private List<String> failureMessages;
        // warnings không cần trong builder

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