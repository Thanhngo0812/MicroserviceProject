package com.ct08SWA.userservice.userdataaccess.adapter;

import com.ct08SWA.userservice.userapplicationservice.ports.outputports.UserOutboxRepository;
import com.ct08SWA.userservice.userdataaccess.entity.UserOutboxEntity;
import com.ct08SWA.userservice.userdataaccess.mapper.UserOutboxDataAccessMapper;
import com.ct08SWA.userservice.userdataaccess.repository.UserOutboxJpaRepository;
import com.ct08SWA.userservice.userdomaincore.event.UserEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserOutboxRepositoryImpl implements UserOutboxRepository {
    private final UserOutboxJpaRepository paymentOutboxJpaRepository;
    private final UserOutboxDataAccessMapper paymentOutboxDataAccessMapper;

    public UserOutboxRepositoryImpl(UserOutboxJpaRepository jpaRepository,
                                       UserOutboxDataAccessMapper mapper) {
        this.paymentOutboxJpaRepository = jpaRepository;
        this.paymentOutboxDataAccessMapper = mapper;
    }

    @Override
    public void save(UserEvent paymentEvent, UUID SagaId, String Topic) {
        // 1. Domain -> JPA Entity (dùng Mapper)
        UserOutboxEntity entityToSave =
                paymentOutboxDataAccessMapper.paymentOutboxEntityToPaymentOutbox(paymentEvent,SagaId,Topic);
        // 2. Lưu vào CSDL
        UserOutboxEntity savedEntity = paymentOutboxJpaRepository.save(entityToSave);
    }

}
