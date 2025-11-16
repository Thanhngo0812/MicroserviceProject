package com.ct08SWA.paymentservice.paymentdomaincore.exception;

// Exception dùng trong Domain Layer
public class PaymentDomainException extends RuntimeException {
    public PaymentDomainException(String message) {
        super(message);
    }

    public PaymentDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
