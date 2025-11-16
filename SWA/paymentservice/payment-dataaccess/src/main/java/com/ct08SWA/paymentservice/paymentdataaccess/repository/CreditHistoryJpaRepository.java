package com.ct08SWA.paymentservice.paymentdataaccess.repository;

import com.ct08SWA.paymentservice.paymentdataaccess.entity.CreditHistoryEntity;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.TransactionType; // Import Enum
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CreditHistoryJpaRepository extends JpaRepository<CreditHistoryEntity, UUID> {

    // Tìm các giao dịch (DEBIT/CREDIT) cho một customer và order cụ thể
    List<CreditHistoryEntity> findByCustomerIdAndOrderIdAndType(UUID customerId, UUID orderId, TransactionType type);

    // Hoặc có thể tìm theo customerId thôi nếu cần
    List<CreditHistoryEntity> findByCustomerId(UUID customerId);
}
