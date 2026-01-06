package com.ct08SWA.userservice.userapplicationservice.ports.outputports;

import com.ct08SWA.userservice.userdomaincore.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Output Port: Giao tiếp với CSDL User.
 */
public interface UserRepository {
    /**
     * Lưu user vào CSDL.
     */
    User save(User user);

    /**
     * Tìm user theo username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Kiểm tra email đã tồn tại chưa.
     */
    boolean existsByEmail(String email);
    Optional<User> findById(UUID userId);
}