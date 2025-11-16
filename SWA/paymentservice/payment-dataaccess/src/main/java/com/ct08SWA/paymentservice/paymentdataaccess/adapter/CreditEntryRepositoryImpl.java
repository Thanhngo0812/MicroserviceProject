package com.ct08SWA.paymentservice.paymentdataaccess.adapter;

import com.ct08SWA.paymentservice.paymentapplicationservice.ports.outputports.CreditEntryRepository;
import com.ct08SWA.paymentservice.paymentdataaccess.entity.CreditEntryEntity;
import com.ct08SWA.paymentservice.paymentdataaccess.repository.CreditEntryJpaRepository;
import com.ct08SWA.paymentservice.paymentdataaccess.mapper.PaymentDataAccessMapper; // Dùng chung Mapper
import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditEntry;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.CustomerId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CreditEntryRepositoryImpl implements CreditEntryRepository {

    private final CreditEntryJpaRepository creditEntryJpaRepository;
    private final PaymentDataAccessMapper paymentDataAccessMapper;

    public CreditEntryRepositoryImpl(CreditEntryJpaRepository creditEntryJpaRepository,
                                     PaymentDataAccessMapper paymentDataAccessMapper) {
        this.creditEntryJpaRepository = creditEntryJpaRepository;
        this.paymentDataAccessMapper = paymentDataAccessMapper;
    }

    @Override
    public CreditEntry save(CreditEntry creditEntry) {
        CreditEntryEntity entity = paymentDataAccessMapper.creditEntryToCreditEntryEntity(creditEntry);
        CreditEntryEntity savedEntity = creditEntryJpaRepository.save(entity);
        return paymentDataAccessMapper.creditEntryEntityToCreditEntry(savedEntity);
    }

    @Override
    public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
        return creditEntryJpaRepository.findByCustomerId(customerId.getValue())
                .map(paymentDataAccessMapper::creditEntryEntityToCreditEntry);
    }
}
