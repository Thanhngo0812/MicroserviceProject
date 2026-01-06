package com.ct08SWA.userservice.userdomaincore.event;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;


public class UserCreatedEvent implements UserEvent {
    private String userId;
    public UserCreatedEvent() {}
    public UserCreatedEvent(String userId) {
        this.userId = userId;
    }
}