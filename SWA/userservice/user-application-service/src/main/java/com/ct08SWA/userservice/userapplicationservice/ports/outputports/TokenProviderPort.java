package com.ct08SWA.userservice.userapplicationservice.ports.outputports;


import com.ct08SWA.userservice.userdomaincore.entity.User;

/**
 * Output Port: Giao tiếp với thư viện tạo Token (ví dụ: JJWT).
 */
public interface TokenProviderPort {
    /**
     * Tạo JWT token từ thông tin User.
     */
    String generateToken(User user);
}