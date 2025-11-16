package com.ct08SWA.paymentservice.paymentdomaincore.valueobject;

import java.util.UUID;

public class OrderId extends BaseId<UUID> {
    public OrderId(UUID value) {
        super(value);
    }
}