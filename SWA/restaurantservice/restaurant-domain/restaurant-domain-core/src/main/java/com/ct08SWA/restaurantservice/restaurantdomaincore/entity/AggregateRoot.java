package com.ct08SWA.restaurantservice.restaurantdomaincore.entity;

import com.ct08SWA.restaurantservice.restaurantdomaincore.event.RestaurantApprovalEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lớp cơ sở (Base Class) cho Aggregate Roots (Gốc Tập hợp).
 * Aggregate Root là một Entity đặc biệt, đóng vai trò là "cửa ngõ" (gateway)
 * cho một cụm (cluster) các đối tượng Domain khác.
 * (Không dùng Lombok)
 */
public abstract class AggregateRoot<ID> extends BaseEntity<ID> {

    private List<RestaurantApprovalEvent> domainEvents= new ArrayList<>();
    public void addDomainEvent(RestaurantApprovalEvent event) {
        this.domainEvents.add(event);
        // Bạn cũng có thể log ở đây nếu cần
    }

    /**
     * Dùng public để bên ngoài (Application Service, Event Publisher)
     * có thể lấy danh sách event ra.
     * Trả về một bản sao không thể thay đổi (unmodifiable).
     */
    public List<RestaurantApprovalEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * Dùng public để bên ngoài có thể xóa event
     * sau khi đã publish chúng (ví dụ: gửi lên Kafka).
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

}