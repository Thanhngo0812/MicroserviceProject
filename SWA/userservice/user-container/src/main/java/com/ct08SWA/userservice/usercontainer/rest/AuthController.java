package com.ct08SWA.userservice.usercontainer.rest;


import com.ct08SWA.userservice.userapplicationservice.dto.inputdto.LoginCommand;
import com.ct08SWA.userservice.userapplicationservice.dto.inputdto.RegisterUserCommand;
import com.ct08SWA.userservice.userapplicationservice.dto.outputdto.TokenResponse;
import com.ct08SWA.userservice.userapplicationservice.dto.outputdto.UserResponse;
import com.ct08SWA.userservice.userapplicationservice.dto.outputdto.UserValidationResponse;
import com.ct08SWA.userservice.userapplicationservice.ports.inputports.UserApplicationService;
import com.ct08SWA.userservice.userdomaincore.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserApplicationService userApplicationService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterUserCommand command) {
        UserResponse response = userApplicationService.registerUser(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginCommand command) {
        TokenResponse response = userApplicationService.login(command);
        return ResponseEntity.ok(response);
    }


}