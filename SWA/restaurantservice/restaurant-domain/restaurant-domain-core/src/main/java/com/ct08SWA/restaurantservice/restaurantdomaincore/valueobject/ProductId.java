package com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject;

import java.util.UUID;

/**
 * Value Object (ID) "sạch" cho Product.
 */
public class ProductId extends BaseId<UUID> {
    public ProductId(UUID value) {
        super(value);
    }
}
