package com.ct08SWA.paymentservice.paymentdomaincore.valueobject;

import java.util.UUID;

public class PaymentId extends BaseId<UUID> {
    public PaymentId(UUID value) {
        super(value);
    }
}
