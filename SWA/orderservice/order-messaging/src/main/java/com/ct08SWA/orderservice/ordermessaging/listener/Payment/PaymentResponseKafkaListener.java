package com.ct08SWA.orderservice.ordermessaging.listener.Payment;

import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Payment.PaymentResponse;
import com.ct08SWA.orderservice.orderapplicationservice.ports.inputports.Listener.PaymentResponseMessageListener;
import com.ct08SWA.orderservice.ordermessaging.dto.DebeziumOutboxMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class PaymentResponseKafkaListener {

    private final PaymentResponseMessageListener paymentResponseMessageListener;
    private final ObjectMapper objectMapper;
    public PaymentResponseKafkaListener(PaymentResponseMessageListener paymentResponseMessageListener,
                                        ObjectMapper objectMapper) {
        this.paymentResponseMessageListener = paymentResponseMessageListener;
        this.objectMapper = objectMapper;
    }

    /**
     * Lắng nghe event phản hồi từ PaymentService.
     */
    @KafkaListener(
            // Lấy tên topic để lắng nghe (order.payment.response)
            topics = "${order-service.kafka.listen-payment-response-topic}",

            // Lấy Consumer Group ID để tham gia nhóm xử lý
            groupId = "${spring.kafka.consumer.group-id-payment}"
    )
    public void receive(ConsumerRecord<String, String> record) {
        String key = record.key();
        String jsonString = record.value();
        PaymentResponse paymentResponse = null;
        try {
            // 1. Parse JSON String
            DebeziumOutboxMessage outboxMessage = objectMapper.readValue(jsonString, DebeziumOutboxMessage.class);
            String innerJsonPayload = outboxMessage.payload;
            paymentResponse = objectMapper.readValue(innerJsonPayload, PaymentResponse.class);
            if (Objects.equals(paymentResponse.getStatus(), "COMPLETED")) {

                paymentResponseMessageListener.paymentCompleted(paymentResponse);

            } else if (Objects.equals(paymentResponse.getStatus(), "FAILED")) {

                // 4. Xử lý thất bại (CANCELLED)
                paymentResponseMessageListener.paymentFailed(paymentResponse);
            } else if (Objects.equals(paymentResponse.getStatus(), "CANCELLED")) {

                // 4. Xử lý thất bại (CANCELLED)
                paymentResponseMessageListener.paymentCancelled(paymentResponse);

            } else {
                log.error("Received unexpected PaymentStatus: {} for Order ID: {}",
                        paymentResponse.getStatus(), paymentResponse.getOrderId());
            }
        } catch (Exception e) {
            String orderId = (paymentResponse != null) ? paymentResponse.getOrderId().toString() : "UNKNOWN (Failed Parsing/Deserialization)";
            log.error("Error processing payment request for order id: {}. Error: {}",
                    orderId, e.getMessage(), e);
            // TODO: Xử lý lỗi
        }


    }
}
