package com.ct08SWA.userservice.userapplicationservice.ports.outputports;

/**
 * Output Port: Giao tiếp với thư viện mã hóa mật khẩu (ví dụ: BCrypt).
 * Giúp Application Service không phụ thuộc trực tiếp vào Spring Security.
 */
public interface PasswordEncoderPort {
    /**
     * Mã hóa mật khẩu thô.
     */
    String encode(String rawPassword);

    /**
     * Kiểm tra mật khẩu thô có khớp với mật khẩu đã mã hóa không.
     */
    boolean matches(String rawPassword, String encodedPassword);
}