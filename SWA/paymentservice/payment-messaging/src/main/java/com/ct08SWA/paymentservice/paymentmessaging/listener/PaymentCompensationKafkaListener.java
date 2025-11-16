package com.ct08SWA.paymentservice.paymentmessaging.listener;

// Import Avro Model (Hợp đồng)

import com.ct08SWA.paymentservice.paymentmessaging.dto.DebeziumOutboxMessage;
import com.ct08SWA.paymentservice.paymentapplicationservice.dto.inputdto.PaymentCompensationRequest;
import com.ct08SWA.paymentservice.paymentapplicationservice.ports.inputports.PaymentCompensationMessageListener;
import com.ct08SWA.paymentservice.paymentmessaging.dto.OrderCancelledEventDto;
import com.ct08SWA.paymentservice.paymentmessaging.mapper.PaymentCompensationMessageDataMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentCompensationKafkaListener {


    private static final int SCHEMA_REGISTRY_ENVELOPE_SIZE = 5; // 1 byte Magic + 4 bytes Schema ID

    private final PaymentCompensationMessageListener paymentMessageListener; // Input Port
    private final PaymentCompensationMessageDataMapper mapper;
    private final ObjectMapper objectMapper;

    public PaymentCompensationKafkaListener(PaymentCompensationMessageListener paymentMessageListener,
                                            PaymentCompensationMessageDataMapper mapper,ObjectMapper objectMapper) {
        this.paymentMessageListener = paymentMessageListener;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "${payment-service.kafka.listen-order-cancel-topic}", // order.cancellation.request
            groupId = "${spring.kafka.consumer.group-id}"
    )    public void receive(ConsumerRecord<String, String> record) { // Nhận String (JSON)
        String key = record.key();
        String jsonString = record.value(); // Toàn bộ JSON String nhận được
        OrderCancelledEventDto orderCancelledEventDto = null;
        PaymentCompensationRequest request = null;

        try {
// BƯỚC 1: Deserialize JSON bên ngoài
            DebeziumOutboxMessage outboxMessage = objectMapper.readValue(jsonString, DebeziumOutboxMessage.class);

            // BƯỚC 2: Lấy chuỗi JSON bên trong từ trường "payload"
            String innerJsonPayload = outboxMessage.payload;

            // BƯỚC 3: Deserialize chuỗi JSON bên trong thành DTO thật
            orderCancelledEventDto = objectMapper.readValue(innerJsonPayload, OrderCancelledEventDto.class);
            request = PaymentCompensationMessageDataMapper.toCompensationRequest(orderCancelledEventDto);
            paymentMessageListener.processCompensation(request);

            log.info("Received payment compensation request for order id: {}", request.getOrderId());
            log.debug("Payload: {}", request.toString());


            log.info("Payment compensation request successfully processed for order id: {}", request.getOrderId());
        } catch (Exception e) {
            String orderId = (request != null) ? request.getOrderId().toString() : "UNKNOWN (Failed Parsing/Deserialization)";
            log.error("Error processing payment compensation for order id: {}. Error: {}",
                    orderId, e.getMessage(), e);
            // TODO: Xử lý lỗi
        }
    }
}