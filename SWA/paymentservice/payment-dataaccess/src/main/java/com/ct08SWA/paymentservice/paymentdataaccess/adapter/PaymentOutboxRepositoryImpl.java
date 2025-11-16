package com.ct08SWA.paymentservice.paymentdataaccess.adapter;


import com.ct08SWA.paymentservice.paymentapplicationservice.ports.outputports.PaymentOutboxRepository;
import com.ct08SWA.paymentservice.paymentdataaccess.entity.PaymentOutboxEntity;
import com.ct08SWA.paymentservice.paymentdataaccess.mapper.PaymentOutboxDataAccessMapper;
import com.ct08SWA.paymentservice.paymentdataaccess.repository.PaymentOutboxJpaRepository;
import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter (Secondary Adapter) triển khai Output Port 'PaymentOutboxRepository'.
 * Giao tiếp giữa Application Service và CSDL (thông qua JPA).
 * KHÔNG CÓ LOMBOK.
 */
@Component
public class PaymentOutboxRepositoryImpl implements PaymentOutboxRepository {

    private final PaymentOutboxJpaRepository paymentOutboxJpaRepository;
    private final PaymentOutboxDataAccessMapper paymentOutboxDataAccessMapper;

    public PaymentOutboxRepositoryImpl(PaymentOutboxJpaRepository jpaRepository,
                                       PaymentOutboxDataAccessMapper mapper) {
        this.paymentOutboxJpaRepository = jpaRepository;
        this.paymentOutboxDataAccessMapper = mapper;
    }

    @Override
    public void save(PaymentEvent paymentEvent, UUID SagaId,String Topic) {
        // 1. Domain -> JPA Entity (dùng Mapper)
        PaymentOutboxEntity entityToSave =
                paymentOutboxDataAccessMapper.paymentOutboxEntityToPaymentOutbox(paymentEvent,SagaId,Topic);
        // 2. Lưu vào CSDL
        PaymentOutboxEntity savedEntity = paymentOutboxJpaRepository.save(entityToSave);
    }



}
