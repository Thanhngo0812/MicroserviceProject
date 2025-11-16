package com.ct08SWA.paymentservice.paymentmessaging.mapper;
import com.ct08SWA.kafka.avro.model.PaymentCompensationAvroModel;
import com.ct08SWA.paymentservice.paymentapplicationservice.dto.inputdto.PaymentCompensationRequest;
import com.ct08SWA.paymentservice.paymentmessaging.dto.OrderCancelledEventDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneId;

@Component
public class PaymentCompensationMessageDataMapper {


    public static PaymentCompensationRequest toCompensationRequest(OrderCancelledEventDto dto) {
        return PaymentCompensationRequest.builder()
                .orderId(dto.getOrderId())
                .customerId(dto.getCustomerId())
                .price(dto.getPrice())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}