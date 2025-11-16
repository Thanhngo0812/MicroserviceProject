package com.ct08SWA.paymentservice.paymentmessaging.listener;
import com.ct08SWA.paymentservice.paymentmessaging.dto.DebeziumOutboxMessage;
import com.ct08SWA.paymentservice.paymentapplicationservice.dto.inputdto.PaymentRequest;
import com.ct08SWA.paymentservice.paymentapplicationservice.ports.inputports.PaymentRequestMessageListener;
import com.ct08SWA.paymentservice.paymentmessaging.dto.OrderCreatedEventDto;
import com.ct08SWA.paymentservice.paymentmessaging.mapper.PaymentMessagingDataMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;



@Slf4j
@Component
public class PaymentRequestKafkaListener {

    private final PaymentRequestMessageListener paymentMessageListener;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final ObjectMapper objectMapper;

    public PaymentRequestKafkaListener(PaymentRequestMessageListener listener,
                                       PaymentMessagingDataMapper mapper,ObjectMapper objectMapper) {
        this.paymentMessageListener = listener;
        this.paymentMessagingDataMapper = mapper;
        this.objectMapper = objectMapper;
    }



    /**
     * Lắng nghe message OrderCreatedEvent từ OrderService.
     */
    @KafkaListener(
            topics = "${payment-service.kafka.listen-order-create-topic}", // order.payment.request
            groupId = "${spring.kafka.consumer.group-id}"
    )    public void receive(ConsumerRecord<String, String> record) {
        String key = record.key();
        String jsonString = record.value();
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
}