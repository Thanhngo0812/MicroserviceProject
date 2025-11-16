package com.ct08SWA.paymentservice.paymentapplicationservice.dto.inputdto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * DTO: Data Transfer Object cho yêu cầu thanh toán (nhận từ Order Service).
 */
@Getter
@Builder
public class PaymentRequest {

    private String orderId;
    private String customerId;
    private BigDecimal price;
    // Có thể thêm các trường khác nếu cần, ví dụ paymentOrderStatus
}
