package com.ct08SWA.userservice.userapplicationservice.dto.outputdto;
import java.util.UUID;

/**
 * DTO trả về JWT Token (sau khi đăng nhập thành công).
 */
public class TokenResponse {
    private final String token;
    private final UUID userId;
    private final String username;
    private final String role;

    public TokenResponse(String token, UUID userId, String username, String role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public String getToken() { return token; }
    public UUID getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}