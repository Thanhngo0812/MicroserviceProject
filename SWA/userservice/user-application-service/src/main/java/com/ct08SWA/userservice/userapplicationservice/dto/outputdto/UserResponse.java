package com.ct08SWA.userservice.userapplicationservice.dto.outputdto;
import java.util.UUID;

/**
 * DTO trả về thông tin user (sau khi đăng ký thành công).
 * Không bao giờ trả về password!
 */
public class UserResponse {
    private final UUID userId;
    private final String username;
    private final String message;

    public UserResponse(UUID userId, String username, String message) {
        this.userId = userId;
        this.username = username;
        this.message = message;
    }

    public UUID getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getMessage() { return message; }
}