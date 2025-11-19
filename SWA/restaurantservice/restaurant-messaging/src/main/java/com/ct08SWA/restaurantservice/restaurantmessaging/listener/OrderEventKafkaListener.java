package com.ct08SWA.restaurantservice.restaurantmessaging.listener;


import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.OrderCancelledCommand;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.OrderPaidCommand;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.RestaurantApprovalRequest;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.inputports.RestaurantApprovalMessageListener;
import com.ct08SWA.restaurantservice.restaurantmessaging.dto.DebeziumOutboxMessage;
import com.ct08SWA.restaurantservice.restaurantmessaging.dto.OrderCancelledEventDto;
import com.ct08SWA.restaurantservice.restaurantmessaging.dto.OrderCreatedEventDto;
import com.ct08SWA.restaurantservice.restaurantmessaging.dto.OrderPaidEventDto;
import com.ct08SWA.restaurantservice.restaurantmessaging.mapper.RestaurantDataMessagingMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class OrderEventKafkaListener {
    private final RestaurantDataMessagingMapper restaurantDataMessagingMapper;
    private final RestaurantApprovalMessageListener restaurantApprovalMessageListener;
    private final ObjectMapper objectMapper;

    public OrderEventKafkaListener(RestaurantApprovalMessageListener restaurantApprovalMessageListener, ObjectMapper objectMapper, RestaurantDataMessagingMapper restaurantDataMessagingMapper) {
        this.restaurantApprovalMessageListener = restaurantApprovalMessageListener;
        this.objectMapper = objectMapper;
        this.restaurantDataMessagingMapper = restaurantDataMessagingMapper;

    }

    /**
     * Lắng nghe message OrderCreatedEvent từ OrderService.
     */
    @KafkaListener(
            topics = "${restaurant-service.kafka.listen-order-state-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )    public void receive(ConsumerRecord<String, String> record) {
        String statusHeaderValue = null;

        // 1. Lấy đối tượng Headers từ record
        org.apache.kafka.common.header.Headers headers = record.headers();
        org.apache.kafka.common.header.Header statusHeader = headers.lastHeader("status");
        statusHeaderValue = new String(statusHeader.value(), java.nio.charset.StandardCharsets.UTF_8);
        //
        String key = record.key();
        String jsonString = record.value();

        if(statusHeaderValue.equals("CREATED")){
            OrderCreatedEventDto orderCreatedEventDto = null;
            RestaurantApprovalRequest restaurantApprovalRequest    = null;
            try {
                // 1. Parse JSON String
                DebeziumOutboxMessage outboxMessage = objectMapper.readValue(jsonString, DebeziumOutboxMessage.class);
                String innerJsonPayload = outboxMessage.payload;
                orderCreatedEventDto = objectMapper.readValue(innerJsonPayload, OrderCreatedEventDto.class);
                restaurantApprovalRequest = restaurantDataMessagingMapper.toRestaurantApprovalRequest(orderCreatedEventDto);
                restaurantApprovalMessageListener.processApprovalRequest(restaurantApprovalRequest);
                log.info("order request successfully processed for order id: {}", restaurantApprovalRequest.getOrderId());
            } catch (Exception e) {
                String orderId = (restaurantApprovalRequest != null) ? restaurantApprovalRequest.getOrderId().toString() : "UNKNOWN (Failed Parsing/Deserialization)";
                log.error("Error processing approval for order id: {}. Error: {}",
                        orderId, e.getMessage(), e);
                // TODO: Xử lý lỗi
            }

        } else if (statusHeaderValue.equals("CANCELLED")) {
            OrderCancelledEventDto orderCancelledEventDto = null;
            OrderCancelledCommand OrderCancelledCommand    = null;
            try {
                // 1. Parse JSON String
                DebeziumOutboxMessage outboxMessage = objectMapper.readValue(jsonString, DebeziumOutboxMessage.class);
                String innerJsonPayload = outboxMessage.payload;
                orderCancelledEventDto = objectMapper.readValue(innerJsonPayload, OrderCancelledEventDto.class);
                OrderCancelledCommand = restaurantDataMessagingMapper.toOrderCancelledCommand(orderCancelledEventDto);
                restaurantApprovalMessageListener.processOrderCancelled(OrderCancelledCommand);
                log.info("order cancelled successfully processed for order id: {}", OrderCancelledCommand.getOrderId());
            } catch (Exception e) {
                String orderId = (OrderCancelledCommand!= null) ? OrderCancelledCommand.getOrderId().toString() : "UNKNOWN (Failed Parsing/Deserialization)";
                log.error("Error processing approval for order id: {}. Error: {}",
                        orderId, e.getMessage(), e);
                // TODO: Xử lý lỗi
            }

        } else if(statusHeaderValue.equals("PAID")) {
            OrderPaidEventDto orderPaidEventDto = null;
            OrderPaidCommand orderPaidCommand    = null;
            try {
                // 1. Parse JSON String
                DebeziumOutboxMessage outboxMessage = objectMapper.readValue(jsonString, DebeziumOutboxMessage.class);
                String innerJsonPayload = outboxMessage.payload;
                orderPaidEventDto = objectMapper.readValue(innerJsonPayload, OrderPaidEventDto.class);
                orderPaidCommand = restaurantDataMessagingMapper.toOrderPaidCommand(orderPaidEventDto);
                restaurantApprovalMessageListener.processOrderPaid(orderPaidCommand);
                log.info("order paid successfully processed for order id: {}", orderPaidCommand.getOrderId());
            } catch (Exception e) {
                String orderId = (orderPaidCommand != null) ? orderPaidCommand.getOrderId().toString() : "UNKNOWN (Failed Parsing/Deserialization)";
                log.error("Error processing approval for order id: {}. Error: {}",
                        orderId, e.getMessage(), e);
                // TODO: Xử lý lỗi
            }

        }

    }
}