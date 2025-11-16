package com.ct08SWA.paymentservice.paymentapplicationservice.dto.inputdto;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * DTO (Input) cho yêu cầu hoàn tiền (Compensation).
 * Được map từ Avro Model.
 */
@Getter
@Builder
public class PaymentCompensationRequest {
    // Không cần ID của message
    private final UUID orderId;
    private final UUID customerId;
    private final BigDecimal price;
    private final ZonedDateTime createdAt;
}

