package com.ct08SWA.restaurantservice.restaurantmessaging.listener;


import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.OrderCancelledCommand;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.RestaurantApprovalRequest;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.inputports.RestaurantApprovalMessageListener;
import com.ct08SWA.restaurantservice.restaurantmessaging.dto.DebeziumOutboxMessage;
import com.ct08SWA.restaurantservice.restaurantmessaging.dto.OrderCancelledEventDto;
import com.ct08SWA.restaurantservice.restaurantmessaging.dto.OrderCreatedEventDto;
import com.ct08SWA.restaurantservice.restaurantmessaging.mapper.RestaurantDataMessagingMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class OrderCancelledEventKafkaListener {
    private final RestaurantDataMessagingMapper restaurantDataMessagingMapper;
    private final RestaurantApprovalMessageListener restaurantApprovalMessageListener;
    private final ObjectMapper objectMapper;

    public OrderCancelledEventKafkaListener(RestaurantApprovalMessageListener restaurantApprovalMessageListener, ObjectMapper objectMapper,RestaurantDataMessagingMapper restaurantDataMessagingMapper) {
        this.restaurantApprovalMessageListener = restaurantApprovalMessageListener;
        this.objectMapper = objectMapper;
        this.restaurantDataMessagingMapper = restaurantDataMessagingMapper;

    }

    /**
     * Lắng nghe message OrderCreatedEvent từ OrderService.
     */
    @KafkaListener(
            topics = "${restaurant-service.kafka.listen-order-cancel-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )    public void receive(ConsumerRecord<String, String> record) {
        String key = record.key();
        String jsonString = record.value();
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
    }
}