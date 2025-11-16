package com.ct08SWA.paymentservice.paymentdataaccess.repository;


import com.ct08SWA.paymentservice.paymentdataaccess.entity.CreditEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditEntryJpaRepository extends JpaRepository<CreditEntryEntity, UUID> {

    Optional<CreditEntryEntity> findByCustomerId(UUID customerId);
}
