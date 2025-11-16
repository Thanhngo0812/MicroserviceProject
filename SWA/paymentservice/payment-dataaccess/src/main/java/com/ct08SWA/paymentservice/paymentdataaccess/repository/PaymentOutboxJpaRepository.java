package com.ct08SWA.paymentservice.paymentdataaccess.repository;

import com.ct08SWA.paymentservice.paymentdataaccess.entity.PaymentOutboxEntity;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.OutboxStatus; // Import Enum "sạch"
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

    /**
     * Tìm các message Outbox theo trạng thái.
     * Poller sẽ dùng hàm này để tìm các message 'PENDING'.
     */
    Optional<List<PaymentOutboxEntity>> findByStatus(OutboxStatus status);

    // (JPA tự động cung cấp save(), findById(), delete()...)
}
