package com.ct08SWA.restaurantservice.restaurantdomaincore.entity;


import java.util.Objects;

/**
 * Lớp cơ sở (Base Class) cho tất cả các Thực thể (Entities).
 * Một Entity được định danh bằng ID của nó.
 * (Không dùng Lombok)
 */
public abstract class BaseEntity<ID> {
    private ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity<?> that = (BaseEntity<?>) o;
        // So sánh dựa trên ID
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        // Hash dựa trên ID
        return Objects.hash(id);
    }
}