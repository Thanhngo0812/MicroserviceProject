package com.ct08SWA.paymentservice.paymentmessaging.mapper;

import com.ct08SWA.kafka.avro.model.OrderCreatedAvroModel;
import com.ct08SWA.kafka.avro.model.PaymentResponseAvroModel;
// Import các Event "phẳng" (POJO)

import com.ct08SWA.paymentservice.paymentapplicationservice.dto.inputdto.PaymentRequest;
import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentCancelledEvent;
import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentCompletedEvent;
import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentEvent;
import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentFailedEvent;
import com.ct08SWA.paymentservice.paymentmessaging.dto.OrderCreatedEventDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.util.Collections;
import java.util.UUID;

/**
 * Mapper (lớp Infrastructure) được dùng bởi Kafka Publisher (do Poller gọi).
 * Nhiệm vụ: Chuyển đổi các Event POJO "phẳng" (Completed, Failed, Cancelled)
 * sang một Avro Model chung (PaymentResponseAvroModel) để gửi lên Kafka.
 */
@Component
public class PaymentMessagingDataMapper {

    public static PaymentRequest OrderCreatedEventDtotoPaymentRequest(OrderCreatedEventDto event) {
        return PaymentRequest.builder()
                .orderId(event.getOrderId().toString())
                .customerId(event.getCustomerId().toString())
                .price(event.getPrice())
                .build();
    }
}

