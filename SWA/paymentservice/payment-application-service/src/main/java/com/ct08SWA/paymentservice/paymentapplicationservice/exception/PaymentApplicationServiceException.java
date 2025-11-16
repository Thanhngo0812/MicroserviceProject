package com.ct08SWA.paymentservice.paymentapplicationservice.exception;

/**
 * Exception dùng trong tầng Application Service của Payment.
 */
public class PaymentApplicationServiceException extends RuntimeException {
    public PaymentApplicationServiceException(String message) {
        super(message);
    }

    public PaymentApplicationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
