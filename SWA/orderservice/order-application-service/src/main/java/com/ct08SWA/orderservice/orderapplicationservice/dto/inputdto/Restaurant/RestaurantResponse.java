package com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Restaurant;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
public class RestaurantResponse {
    private UUID approvalId;
    private UUID orderId;
    private UUID restaurantId;
    private String status;
    private ZonedDateTime createdAt;
    private List<String> failureMessages;

}
