package com.ct08SWA.paymentservice.paymentdataaccess.repository;

import com.ct08SWA.paymentservice.paymentdataaccess.entity.PaymentOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository cho PaymentOutboxEntity.
 */
@Repository
public interface PaymentOutboxJpaRepository extends JpaRepository<PaymentOutboxEntity, UUID> {
}
