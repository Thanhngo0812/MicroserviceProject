package com.ct08SWA.paymentservice.paymentapplicationservice.mapper;

import com.ct08SWA.paymentservice.paymentapplicationservice.dto.inputdto.PaymentRequest;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.Payment;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.CustomerId;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.Money;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.OrderId;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper: Chuyển đổi giữa DTO và Domain Entity cho Payment.
 */
@Component
public class PaymentDataMapper {

    /**
     * Chuyển đổi từ PaymentRequest DTO sang Payment Entity.
     * @param paymentRequest DTO đầu vào.
     * @return Payment Entity.
     */
    public Payment paymentRequestToPayment(PaymentRequest paymentRequest) {
        return Payment.builder()
                .orderId(new OrderId(UUID.fromString(paymentRequest.getOrderId())))
                .customerId(new CustomerId(UUID.fromString(paymentRequest.getCustomerId())))
                .price(new Money(paymentRequest.getPrice()))
                .build();
    }

    // Có thể thêm các hàm map khác nếu cần (ví dụ: map Payment Entity sang PaymentResponse DTO)
}
