package com.ct08SWA.paymentservice.paymentmessaging.listener;

// Import Avro Model (Hợp đồng)

import com.ct08SWA.paymentservice.paymentapplicationservice.dto.inputdto.PaymentRequest;
import com.ct08SWA.paymentservice.paymentapplicationservice.ports.inputports.PaymentRequestMessageListener;
import com.ct08SWA.paymentservice.paymentmessaging.dto.DebeziumOutboxMessage;
import com.ct08SWA.paymentservice.paymentapplicationservice.dto.inputdto.PaymentCompensationRequest;
import com.ct08SWA.paymentservice.paymentapplicationservice.ports.inputports.PaymentCompensationMessageListener;
import com.ct08SWA.paymentservice.paymentmessaging.dto.OrderCancelledEventDto;
import com.ct08SWA.paymentservice.paymentmessaging.dto.OrderCreatedEventDto;
import com.ct08SWA.paymentservice.paymentmessaging.mapper.PaymentCompensationMessageDataMapper;
import com.ct08SWA.paymentservice.paymentmessaging.mapper.PaymentMessagingDataMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentRequestKafkaListener {


    private static final int SCHEMA_REGISTRY_ENVELOPE_SIZE = 5; // 1 byte Magic + 4 bytes Schema ID
    private final PaymentRequestMessageListener paymentMessageListener;
    private final PaymentCompensationMessageListener paymentCompensationMessageListener; // Input Port
    private final PaymentCompensationMessageDataMapper mapper;
    private final ObjectMapper objectMapper;

    public PaymentRequestKafkaListener(PaymentCompensationMessageListener paymentCompensationMessageListener,
                                       PaymentCompensationMessageDataMapper mapper, ObjectMapper objectMapper, PaymentRequestMessageListener paymentMessageListener) {
        this.paymentCompensationMessageListener = paymentCompensationMessageListener;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.paymentMessageListener = paymentMessageListener;
    }

    @KafkaListener(
            topics = "${payment-service.kafka.listen-order-state-topic}", // order.cancellation.request
            groupId = "${spring.kafka.consumer.group-id}"
    )    public void receive(ConsumerRecord<String, String> record) { // Nhận String (JSON)
        String statusHeaderValue = null;

        // 1. Lấy đối tượng Headers từ record
        org.apache.kafka.common.header.Headers headers = record.headers();
        org.apache.kafka.common.header.Header statusHeader = headers.lastHeader("status");
        statusHeaderValue = new String(statusHeader.value(), java.nio.charset.StandardCharsets.UTF_8);
        String key = record.key();
        String jsonString = record.value(); // Toàn bộ JSON String nhận được
        if(statusHeaderValue.equals("CREATED")) {
            OrderCreatedEventDto orderCreatedEventDto = null;
            PaymentRequest paymentRequest = null;
            try {
                // 1. Parse JSON String
                DebeziumOutboxMessage outboxMessage = objectMapper.readValue(jsonString, DebeziumOutboxMessage.class);
                String innerJsonPayload = outboxMessage.payload;
                orderCreatedEventDto = objectMapper.readValue(innerJsonPayload, OrderCreatedEventDto.class);
                paymentRequest= PaymentMessagingDataMapper.OrderCreatedEventDtotoPaymentRequest(orderCreatedEventDto);
                paymentMessageListener.processPayment(paymentRequest);
                log.info("Payment request successfully processed for order id: {}", paymentRequest.getOrderId());
            } catch (Exception e) {
                String orderId = (paymentRequest != null) ? paymentRequest.getOrderId() : "UNKNOWN (Failed Parsing/Deserialization)";
                log.error("Error processing payment request for order id: {}. Error: {}",
                        orderId, e.getMessage(), e);
                // TODO: Xử lý lỗi
            }
        }
        else if(statusHeaderValue.equals("CANCELLED")) {
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
                paymentCompensationMessageListener.processCompensation(request);

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
}