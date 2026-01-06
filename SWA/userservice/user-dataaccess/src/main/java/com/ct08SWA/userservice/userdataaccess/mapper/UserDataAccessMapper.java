package com.ct08SWA.userservice.userdataaccess.mapper;



import com.ct08SWA.userservice.userdataaccess.entity.UserEntity;
import com.ct08SWA.userservice.userdomaincore.entity.User;
import com.ct08SWA.userservice.userdomaincore.valueobject.UserId;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class UserDataAccessMapper {

    /**
     * Chuyển Domain Entity -> JPA Entity (để lưu vào DB)
     */
    public UserEntity userToUserEntity(User user) {
        return new UserEntity(
                user.getId().getValue(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.isActive(),
                ZonedDateTime.now(ZoneId.of("UTC")) // Hoặc lấy từ user nếu có trường createdAt
        );
    }

    /**
     * Chuyển JPA Entity -> Domain Entity (để xử lý logic)
     */
    public User userEntityToUser(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(new UserId(entity.getId()))
                .username(entity.getUsername())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .role(entity.getRole())
                .active(entity.isActive())
                .build();
    }
}