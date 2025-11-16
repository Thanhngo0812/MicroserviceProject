package com.ct08SWA.paymentservice.paymentdomaincore.valueobject;

/**
 * Enum (Value Object) "sạch", định nghĩa trạng thái của message trong outbox.
 * Nằm trong Domain Core.
 */
public enum OutboxStatus {
    PENDING,   // Đang chờ Poller gửi
    COMPLETED, // Đã gửi thành công
    FAILED     // Gửi thất bại
}

