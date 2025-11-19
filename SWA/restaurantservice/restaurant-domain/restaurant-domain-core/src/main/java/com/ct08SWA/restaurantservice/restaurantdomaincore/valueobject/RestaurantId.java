package com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject;


import java.util.UUID;

/**
 * Value Object (ID) "sạch" cho Restaurant.
 */
public class RestaurantId extends BaseId<UUID> {
    public RestaurantId(UUID value) {
        super(value);
    }
}