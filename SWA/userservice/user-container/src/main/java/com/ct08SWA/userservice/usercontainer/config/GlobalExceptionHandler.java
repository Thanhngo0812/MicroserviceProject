package com.ct08SWA.userservice.usercontainer.config;

import com.ct08SWA.userservice.userapplicationservice.exception.UserApplicationException;
import com.ct08SWA.userservice.userdomaincore.exception.BadCredentialsException;
import com.ct08SWA.userservice.userdomaincore.exception.UserDomainException;
import com.ct08SWA.userservice.userdomaincore.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UserDomainException.class)
    public ResponseEntity<Map<String,String>> handleDomainException(UserDomainException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UserApplicationException.class)
    public ResponseEntity<Map<String,String>> handleApplicationException(UserApplicationException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleUserNotFoundException(UserNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String,String>> handleBadCredentials(BadCredentialsException ex) {
        return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    private ResponseEntity<Map<String,String>> buildError(HttpStatus status, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", status.name());
        error.put("message", message);
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.status(status).body(error);
    }
}