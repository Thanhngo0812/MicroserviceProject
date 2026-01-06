package com.ct08SWA.userservice.userdataaccess.repository;
import com.ct08SWA.userservice.userdataaccess.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    // Tìm theo username (cho chức năng Login)
    Optional<UserEntity> findByUsername(String username);

    // Kiểm tra email tồn tại (cho chức năng Register)
    boolean existsByEmail(String email);
}