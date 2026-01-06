package com.ct08SWA.userservice.userapplicationservice.ports.inputports;


import com.ct08SWA.userservice.userapplicationservice.dto.inputdto.LoginCommand;
import com.ct08SWA.userservice.userapplicationservice.dto.inputdto.RegisterUserCommand;
import com.ct08SWA.userservice.userapplicationservice.dto.outputdto.TokenResponse;
import com.ct08SWA.userservice.userapplicationservice.dto.outputdto.UserResponse;
import com.ct08SWA.userservice.userapplicationservice.dto.outputdto.UserValidationResponse;
import com.ct08SWA.userservice.userdomaincore.entity.User;

import java.util.UUID;

/**
 * Input Port: Định nghĩa các Use Case của UserService.
 */
public interface UserApplicationService {

    /**
     * Use Case: Đăng ký người dùng mới.
     */
    UserResponse registerUser(RegisterUserCommand command);

    /**
     * Use Case: Đăng nhập và lấy Token.
     */
    TokenResponse login(LoginCommand command);

    User findUserById(UUID userId);
    // public User findUserById(UUID userId);
     public void blockUser(UUID userId);
     public void unblockUser(UUID userId);
}