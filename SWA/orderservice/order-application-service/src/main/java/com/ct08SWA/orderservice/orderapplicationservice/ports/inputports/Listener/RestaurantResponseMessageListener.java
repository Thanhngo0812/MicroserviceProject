package com.ct08SWA.orderservice.orderapplicationservice.ports.inputports.Listener;

import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Restaurant.RestaurantResponse;

public interface RestaurantResponseMessageListener {

    void processApproved(RestaurantResponse restaurantResponse);
    void processCancelled(RestaurantResponse restaurantResponse);
}
