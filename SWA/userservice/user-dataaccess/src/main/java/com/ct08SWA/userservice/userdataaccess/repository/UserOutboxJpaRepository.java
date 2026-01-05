package com.ct08SWA.userservice.userdataaccess.repository;
import com.ct08SWA.userservice.userdataaccess.entity.UserOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserOutboxJpaRepository extends JpaRepository<UserOutboxEntity, UUID> {
}