package com.ct08SWA.orderservice.orderdataaccess.repository;

import com.ct08SWA.orderservice.orderdataaccess.entity.OrderOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository cho OrderOutboxEntity.
 */
@Repository
public interface OrderOutboxJpaRepository extends JpaRepository<OrderOutboxEntity, UUID> {
}
