package com.ct08SWA.paymentservice.paymentdataaccess.repository;
import com.ct08SWA.paymentservice.paymentdataaccess.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {

    // Spring Data JPA tự tạo query dựa trên tên phương thức
    Optional<PaymentEntity> findByOrderId(UUID orderId);
}
