package com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject;

import java.util.UUID;

/**
 * Value Object (ID) "sạch" cho OrderApproval.
 */
public class OrderApprovalId extends BaseId<UUID> {
    public OrderApprovalId(UUID value) {
        super(value);
    }
}