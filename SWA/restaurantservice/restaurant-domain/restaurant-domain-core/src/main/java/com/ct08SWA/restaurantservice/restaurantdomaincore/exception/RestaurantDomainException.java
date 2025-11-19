package com.ct08SWA.restaurantservice.restaurantdomaincore.exception;
/**
 * Exception dùng trong Domain Layer
 */
public class RestaurantDomainException extends RuntimeException {
    public RestaurantDomainException(String message) {
        super(message);
    }

    public RestaurantDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}