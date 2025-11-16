package com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Payment;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO chứa thông tin phản hồi từ Payment Service.
 * Được sử dụng trong Application Layer của OrderService.
 */
@Getter
@Builder
public class PaymentResponse {

    // ID của giao dịch SAGA (chính là Order ID)
    private final UUID orderId;

    // ID của Payment được tạo bởi Payment Service
    private final UUID paymentId;

    // ID của khách hàng
    private final UUID customerId;

    // Giá trị giao dịch
    private final BigDecimal price;

    // Trạng thái thanh toán (COMPLETED/FAILED/CANCELLED)
    private final String paymentStatus;

    // Thời điểm sự kiện được tạo
    private final ZonedDateTime createdAt;

    // Danh sách lỗi (nếu Payment thất bại)
    private final List<String> failureMessages;
}