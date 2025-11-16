package com.ct08SWA.restaurantservice.restaurantdomaincore.entity;

/**
 * Lớp cơ sở (Base Class) cho Aggregate Roots (Gốc Tập hợp).
 * Aggregate Root là một Entity đặc biệt, đóng vai trò là "cửa ngõ" (gateway)
 * cho một cụm (cluster) các đối tượng Domain khác.
 * (Không dùng Lombok)
 */
public abstract class AggregateRoot<ID> extends BaseEntity<ID> {

    // (Trong các thiết kế DDD phức tạp hơn,
    // lớp này có thể chứa logic để quản lý Domain Events,
    // nhưng hiện tại nó chỉ là một "marker" (đánh dấu) kế thừa từ BaseEntity)

}