package com.ct08SWA.userservice.userdomaincore.service;


import com.ct08SWA.userservice.userdomaincore.entity.User;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class UserDomainServiceImpl implements UserDomainService {

    private final ZoneId UTC = ZoneId.of("UTC");

    @Override
    public void validateAndInitializeUser(User user) {
        user.validate();
        user.initializeUser();
        // (Lưu ý: Việc hash password nên được thực hiện TRƯỚC khi gọi hàm này,
        //  hoặc Domain Service này phải nhận vào một PasswordHasher interface)

       // return new UserCreatedEvent(user, ZonedDateTime.now(UTC));
    }

    @Override
    public void block(User user) {
        user.block();
    }

    @Override
    public void unblock(User user) {
        user.unblock();
    }


}