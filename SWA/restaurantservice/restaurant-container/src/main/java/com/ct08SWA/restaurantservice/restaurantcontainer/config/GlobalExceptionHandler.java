package com.ct08SWA.restaurantservice.restaurantcontainer.config;

import com.ct08SWA.restaurantservice.restaurantapplicationservice.exception.RestaurantApplicationServiceException;
import com.ct08SWA.restaurantservice.restaurantdomaincore.exception.RestaurantDomainException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestaurantDomainException.class)
    public ResponseEntity<String> handleDomainException(RestaurantDomainException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(RestaurantApplicationServiceException.class)
    public ResponseEntity<String> handleRestaurantApplicationServiceException(RestaurantApplicationServiceException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}