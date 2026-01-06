package com.ct08SWA.userservice.userapplicationservice.ports.outputports;

import com.ct08SWA.userservice.userdomaincore.event.UserEvent;

import java.util.UUID;

public interface UserOutboxRepository {
    public void save(UserEvent userEvent, UUID SagaId, String Topic);
}
