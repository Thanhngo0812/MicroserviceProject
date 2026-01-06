package com.ct08SWA.userservice.userapplicationservice.handler;



// ... imports

import com.ct08SWA.userservice.userapplicationservice.dto.inputdto.LoginCommand;
import com.ct08SWA.userservice.userapplicationservice.dto.outputdto.TokenResponse;
import com.ct08SWA.userservice.userapplicationservice.exception.UserApplicationException;
import com.ct08SWA.userservice.userapplicationservice.ports.outputports.PasswordEncoderPort;
import com.ct08SWA.userservice.userapplicationservice.ports.outputports.TokenProviderPort;
import com.ct08SWA.userservice.userapplicationservice.ports.outputports.UserRepository;
import com.ct08SWA.userservice.userdomaincore.entity.User;
import com.ct08SWA.userservice.userdomaincore.exception.BadCredentialsException;
import com.ct08SWA.userservice.userdomaincore.exception.UserNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserLoginCommandHandler {

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder; // Interface mã hóa pass
    private final TokenProviderPort jwtProvider;

   public UserLoginCommandHandler(UserRepository userRepository, PasswordEncoderPort passwordEncoder, TokenProviderPort jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
   }

    public TokenResponse login(LoginCommand command) {
        // 1. Tìm user
        User user = userRepository.findByUsername(command.getUsername())
                .orElseThrow(() -> new UserNotFoundException("can not find user"));
        if (!user.isActive()) {
            throw new UserApplicationException(
                    "Account is blocked"
            );
        }
        // 2. Kiểm tra password
        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Password is not true");
        }

        // 3. Tạo JWT
        String token = jwtProvider.generateToken(user);

        return new TokenResponse(token, user.getId().getValue(), user.getUsername(),user.getRole().name());
    }
}