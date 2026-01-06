package com.ct08SWA.userservice.userapplicationservice.handler;


import com.ct08SWA.userservice.userapplicationservice.dto.inputdto.LoginCommand;
import com.ct08SWA.userservice.userapplicationservice.dto.inputdto.RegisterUserCommand;
import com.ct08SWA.userservice.userapplicationservice.dto.outputdto.TokenResponse;
import com.ct08SWA.userservice.userapplicationservice.dto.outputdto.UserResponse;
import com.ct08SWA.userservice.userapplicationservice.dto.outputdto.UserValidationResponse;
import com.ct08SWA.userservice.userapplicationservice.exception.UserApplicationException;
import com.ct08SWA.userservice.userapplicationservice.ports.inputports.UserApplicationService;
// Import Output Ports
import com.ct08SWA.userservice.userapplicationservice.ports.outputports.*;
// Import Domain
import com.ct08SWA.userservice.userdomaincore.entity.User;
import com.ct08SWA.userservice.userdomaincore.exception.UserNotFoundException;
import com.ct08SWA.userservice.userdomaincore.service.UserDomainService;
import com.ct08SWA.userservice.userdomaincore.valueobject.UserRole;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation của Use Case (UserApplicationService).
 * Điều phối luồng đăng ký và đăng nhập.
 */
@Service
public class UserApplicationServiceImpl implements UserApplicationService {

    private static final Logger log = LoggerFactory.getLogger(UserApplicationServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;
    private final UserDomainService userDomainService; // Inject Domain Service
    private final UserOutboxRepository  userOutboxRepository;
    private final UserSecurityPort userSecurityPort; // <-- GỌI QUA PORT
    @Value("${order-service.kafka.user-create-topic}")
    private String UserEventTopic;
    public UserApplicationServiceImpl(UserRepository userRepository,
                                      PasswordEncoderPort passwordEncoder,
                                      TokenProviderPort tokenProvider,
                                      UserDomainService userDomainService,UserSecurityPort userSecurityPort,UserOutboxRepository userOutboxRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.userDomainService = userDomainService;
        this.userSecurityPort = userSecurityPort;
        this.userOutboxRepository = userOutboxRepository;
    }

    @Override
    @Transactional
    public UserResponse registerUser(RegisterUserCommand command) {
        log.info("Processing registration for username: {}", command.getUsername());

        // 1. Kiểm tra trùng lặp (Database Check)
        if (userRepository.findByUsername(command.getUsername()).isPresent()) {
            throw new UserApplicationException("Username '" + command.getUsername() + "' already exists!");
        }
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new UserApplicationException("Email '" + command.getEmail() + "' is already used!");
        }

        // 2. Mã hóa mật khẩu (Infrastructure Logic)
        String encodedPassword = passwordEncoder.encode(command.getPassword());

        // 3. Tạo User Entity từ Command (Mapping thủ công)
        UserRole role;
        try {
            role = UserRole.valueOf(command.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UserApplicationException("Invalid role: " + command.getRole());
        }

        User user = User.builder()
                .username(command.getUsername())
                .password(encodedPassword) // Lưu pass đã mã hóa
                .email(command.getEmail())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .role(role)
                .active(true) // Mặc định active
                .build();

        // 4. Gọi Domain Service để khởi tạo/validate nghiệp vụ (Domain Logic)
        userDomainService.validateAndInitializeUser(user);

        // 5. Lưu vào CSDL
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId().getValue());

        // 6. Trả về response
        return new UserResponse(
                savedUser.getId().getValue(),
                savedUser.getUsername(),
                "User registered successfully"
        );
    }

    @Override
    public TokenResponse login(LoginCommand command) {
        log.info("Processing login for username: {}", command.getUsername());

        // 1. Tìm User trong DB
        User user = userRepository.findByUsername(command.getUsername())
                .orElseThrow(() -> new UserApplicationException("Invalid username or password!"));

        // 2. Kiểm tra mật khẩu (So sánh raw vs encoded)
        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            log.warn("Login failed: Incorrect password for username {}", command.getUsername());
            throw new UserApplicationException("Invalid username or password!");
        }

        // 3. Kiểm tra trạng thái active
        if (!user.isActive()) {
            throw new UserApplicationException("User account is inactive!");
        }

        // 4. Tạo JWT Token
        String token = tokenProvider.generateToken(user);

        log.info("Login successful for user: {}", user.getUsername());

        return new TokenResponse(
                token,
                user.getId().getValue(),
                user.getUsername(),
                user.getRole().name()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(UUID userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Could not find user with id: {}", userId);
                    return new UserNotFoundException("User with id " + userId + " not found!");
                });
        return findUser;

    }

    @Override
    public void blockUser(UUID userId) {
        User user = findUserById(userId);
        userDomainService.block(user);
        userRepository.save(user);

        // GỌI QUA PORT: Yêu cầu hạ tầng thêm ID này vào danh sách đen
        userSecurityPort.addToBlacklist(userId.toString());
        log.warn("User {} has been BLOCKED and added to blacklist for 30 minutes.", userId);
    }

    @Override
    public void unblockUser(UUID userId) {
        User user = findUserById(userId);
        userDomainService.unblock(user);
        //user.setActive(true);
        userRepository.save(user);
        // GỌI QUA PORT: Yêu cầu hạ tầng xóa ID này khỏi danh sách đen
        userSecurityPort.removeFromBlacklist(userId.toString());
        log.info("User {} has been UNBLOCKED. Blacklist key removed.", userId);
    }
}