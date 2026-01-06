package com.ct08SWA.userservice.userdomaincore.exception;

public class UserNotFoundException extends UserDomainException {
    public UserNotFoundException(String message) {
        super(message);
    }
}