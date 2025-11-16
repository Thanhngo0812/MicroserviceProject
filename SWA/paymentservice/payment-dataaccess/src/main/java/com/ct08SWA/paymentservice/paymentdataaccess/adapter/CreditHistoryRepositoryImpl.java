package com.ct08SWA.paymentservice.paymentdataaccess.adapter;

import com.ct08SWA.paymentservice.paymentapplicationservice.ports.outputports.CreditHistoryRepository;
import com.ct08SWA.paymentservice.paymentdataaccess.entity.CreditHistoryEntity;
import com.ct08SWA.paymentservice.paymentdataaccess.repository.CreditHistoryJpaRepository;
import com.ct08SWA.paymentservice.paymentdataaccess.mapper.PaymentDataAccessMapper; // Dùng chung Mapper
import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditHistory;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.CustomerId;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.OrderId;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.TransactionType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreditHistoryRepositoryImpl implements CreditHistoryRepository {

    private final CreditHistoryJpaRepository creditHistoryJpaRepository;
    private final PaymentDataAccessMapper paymentDataAccessMapper;

    public CreditHistoryRepositoryImpl(CreditHistoryJpaRepository creditHistoryJpaRepository,
                                       PaymentDataAccessMapper paymentDataAccessMapper) {
        this.creditHistoryJpaRepository = creditHistoryJpaRepository;
        this.paymentDataAccessMapper = paymentDataAccessMapper;
    }

    @Override
    public CreditHistory save(CreditHistory creditHistory) {
        CreditHistoryEntity entity = paymentDataAccessMapper.creditHistoryToCreditHistoryEntity(creditHistory);
        CreditHistoryEntity savedEntity = creditHistoryJpaRepository.save(entity);
        return paymentDataAccessMapper.creditHistoryEntityToCreditHistory(savedEntity);
    }

    @Override
    public List<CreditHistory> findByCustomerId(CustomerId customerId) {
        List<CreditHistoryEntity> entities = creditHistoryJpaRepository
                .findByCustomerId(customerId.getValue());
        return paymentDataAccessMapper.creditHistoryEntityListToCreditHistoryList(entities);
    }
}

