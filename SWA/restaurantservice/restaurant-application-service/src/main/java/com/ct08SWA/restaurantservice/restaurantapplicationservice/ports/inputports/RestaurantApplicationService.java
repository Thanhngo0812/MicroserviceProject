package com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.inputports;

import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.outpudto.RestaurantValidationResponse;
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.Restaurant;

import java.util.UUID;

public interface RestaurantApplicationService {
    RestaurantValidationResponse findRestaurantById(UUID restaurantId);
}
