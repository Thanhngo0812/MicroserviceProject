package com.ct08SWA.orderservice.orderapplicationservice.exception;
/**
 * Exception dùng trong Domain Layer
 */
public class OrderDomainException extends RuntimeException {
    public OrderDomainException(String message) {
        super(message);
    }

    public OrderDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}