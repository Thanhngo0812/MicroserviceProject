package com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject;

import java.util.UUID;

/**
 * Value Object (ID) "sạch" cho Order (tham chiếu từ bên ngoài).
 */
public class OrderId extends BaseId<UUID> {
    public OrderId(UUID value) {
        super(value);
    }
}