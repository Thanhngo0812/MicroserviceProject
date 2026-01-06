package com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.outpudto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantValidationResponse {
    private String id;
    private boolean active; // Cửa hàng có bị ban không
}
