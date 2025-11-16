package com.ct08SWA.orderservice.orderdataaccess.adapter;

import com.ct08SWA.orderservice.orderapplicationservice.ports.outputports.Repository.OrderOutboxRepository;
import com.ct08SWA.orderservice.orderdataaccess.entity.OrderOutboxEntity;
import com.ct08SWA.orderservice.orderdataaccess.mapper.OrderOutboxDataAccessMapper;
import com.ct08SWA.orderservice.orderdataaccess.repository.OrderOutboxJpaRepository;
import com.ct08SWA.orderservice.orderdomaincore.event.OrderEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adapter triển khai OrderOutboxRepository (Output Port).
 * Cầu nối giữa Application Service và cơ sở dữ liệu (JPA).
 */
@Component
public class OrderOutboxRepositoryImpl implements OrderOutboxRepository {

    private final OrderOutboxJpaRepository orderOutboxJpaRepository;
    private final OrderOutboxDataAccessMapper orderOutboxDataAccessMapper;

    public OrderOutboxRepositoryImpl(OrderOutboxJpaRepository orderOutboxJpaRepository,
                                     OrderOutboxDataAccessMapper orderOutboxDataAccessMapper) {
        this.orderOutboxJpaRepository = orderOutboxJpaRepository;
        this.orderOutboxDataAccessMapper = orderOutboxDataAccessMapper;
    }

    /**
     * Lưu một bản ghi Outbox.
     * Chuyển đổi Domain -> Entity, Lưu Entity, Chuyển đổi Entity -> Domain.
     */
    @Override
    public void save(OrderEvent orderEvent, UUID SagaId, String topic) {
        OrderOutboxEntity orderOutboxEntity =
                orderOutboxDataAccessMapper.domainToEntity(orderEvent,SagaId,topic);
        orderOutboxJpaRepository.save(orderOutboxEntity);
    }


}
