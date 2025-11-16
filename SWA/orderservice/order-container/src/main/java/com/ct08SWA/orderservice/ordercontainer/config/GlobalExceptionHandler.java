package com.ct08SWA.orderservice.ordercontainer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ct08SWA.orderservice.orderdomaincore.exception.OrderDomainException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(OrderDomainException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleOrderDomainException(OrderDomainException ex) {
        log.error("Order domain exception: {}", ex.getMessage());
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "ORDER_VALIDATION_ERROR");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return errorResponse;
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Validation exception: {}", ex.getMessage());
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "VALIDATION_ERROR");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return errorResponse;
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "INTERNAL_SERVER_ERROR");
        errorResponse.put("message", "An unexpected error occurred");
        errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return errorResponse;
    }
}