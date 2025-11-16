package com.ct08SWA.paymentservice.paymentdomaincore.valueobject;


import java.util.UUID;

public class CustomerId extends BaseId<UUID> {
    public CustomerId(UUID value) {
        super(value);
    }
}
