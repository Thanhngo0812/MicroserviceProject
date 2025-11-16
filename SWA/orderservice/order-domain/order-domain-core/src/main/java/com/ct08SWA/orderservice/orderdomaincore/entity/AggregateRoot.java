package com.ct08SWA.orderservice.orderdomaincore.entity;

import com.ct08SWA.orderservice.orderdomaincore.event.OrderEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot<ID> extends BaseEntity<ID> {
     private List<OrderEvent> domainEvents= new ArrayList<>();
    public void addDomainEvent(OrderEvent event) {
        this.domainEvents.add(event);
        // Bạn cũng có thể log ở đây nếu cần
    }

    /**
     * Dùng public để bên ngoài (Application Service, Event Publisher)
     * có thể lấy danh sách event ra.
     * Trả về một bản sao không thể thay đổi (unmodifiable).
     */
    public List<OrderEvent> getDomainEvents() {
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