package com.ct08SWA.userservice.userdomaincore.service;

//import com.ct08SWA.userservice.domain.event.UserCreatedEvent; // Cần tạo Event này

import com.ct08SWA.userservice.userdomaincore.entity.User;

public interface UserDomainService {

    /**
     * Validate và khởi tạo User.
     * @param user User entity (chứa raw password hoặc đã hash tùy logic)
     * @return UserCreatedEvent
     */
    void validateAndInitializeUser(User user);
    void block(User user);
    void unblock(User user);
}