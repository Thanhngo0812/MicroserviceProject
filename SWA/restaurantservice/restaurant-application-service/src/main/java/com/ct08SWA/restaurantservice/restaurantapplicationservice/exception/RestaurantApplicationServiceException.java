package com.ct08SWA.restaurantservice.restaurantapplicationservice.exception;

/**
 * Exception dùng trong tầng Application Service của Restaurant.
 */
public class RestaurantApplicationServiceException extends RuntimeException {
    public RestaurantApplicationServiceException(String message) {
        super(message);
    }

    public RestaurantApplicationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}