package com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject;


import java.util.Objects;
import java.util.UUID; // Thêm UUID (vì hầu hết ID đều dùng nó)

/**
 * Lớp cơ sở (Base Class) cho tất cả các ID (Value Object).
 * (Không dùng Lombok)
 */
public abstract class BaseId<T> {
    private final T value;

    protected BaseId(T value) {
        if (value == null) {
            throw new IllegalArgumentException("ID value cannot be null");
        }
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseId<?> baseId = (BaseId<?>) o;
        return Objects.equals(value, baseId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}