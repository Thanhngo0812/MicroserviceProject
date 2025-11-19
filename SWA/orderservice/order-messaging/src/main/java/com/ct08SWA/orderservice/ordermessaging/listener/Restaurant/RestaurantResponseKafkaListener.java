package com.ct08SWA.orderservice.ordermessaging.listener.Restaurant;


import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Payment.PaymentResponse;
import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Restaurant.RestaurantResponse;
import com.ct08SWA.orderservice.orderapplicationservice.ports.inputports.Listener.PaymentResponseMessageListener;
import com.ct08SWA.orderservice.orderapplicationservice.ports.inputports.Listener.RestaurantResponseMessageListener;
import com.ct08SWA.orderservice.ordermessaging.dto.DebeziumOutboxMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class RestaurantResponseKafkaListener {

    private final RestaurantResponseMessageListener restaurantResponseMessageListener;
    private final ObjectMapper objectMapper;
    public RestaurantResponseKafkaListener(RestaurantResponseMessageListener restaurantResponseMessageListener,
                                        ObjectMapper objectMapper) {
        this.restaurantResponseMessageListener = restaurantResponseMessageListener;
        this.objectMapper = objectMapper;
    }

    /**
     * Lắng nghe event phản hồi từ PaymentService.
     */
    @KafkaListener(
            // Lấy tên topic để lắng nghe (order.payment.response)
            topics = "${order-service.kafka.listen-restaurant-response-topic}",

            // Lấy Consumer Group ID để tham gia nhóm xử lý
            groupId = "${spring.kafka.consumer.group-id-restaurant}"
    )
    public void receive(ConsumerRecord<String, String> record) {
        String key = record.key();
        String jsonString = record.value();
        RestaurantResponse restaurantResponse = null;
        try {
            // 1. Parse JSON String
            DebeziumOutboxMessage outboxMessage = objectMapper.readValue(jsonString, DebeziumOutboxMessage.class);
            String innerJsonPayload = outboxMessage.payload;
            restaurantResponse = objectMapper.readValue(innerJsonPayload, RestaurantResponse.class);
            if (Objects.equals(restaurantResponse.getStatus(), "APPROVED")) {

                restaurantResponseMessageListener.processApproved(restaurantResponse);

            } else if (Objects.equals(restaurantResponse.getStatus(), "REJECTED")) {


                restaurantResponseMessageListener.processCancelled(restaurantResponse);}
           else {
                log.error("Received unexpected RestaurantStatus: {} for Order ID: {}",
                        restaurantResponse.getStatus(), restaurantResponse.getOrderId());
            }
        } catch (Exception e) {
            String orderId = (restaurantResponse != null) ? restaurantResponse.getOrderId().toString() : "UNKNOWN (Failed Parsing/Deserialization)";
            log.error("Error processing payment request for order id: {}. Error: {}",
                    orderId, e.getMessage(), e);
            // TODO: Xử lý lỗi
        }


    }
}
