package com.ct08SWA.restaurantservice.restaurantdataaccess.adapter;

import com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.outputports.RestaurantOutboxRepository;
import com.ct08SWA.restaurantservice.restaurantdataaccess.entity.RestaurantOutboxEntity;
import com.ct08SWA.restaurantservice.restaurantdataaccess.mapper.RestaurantOutboxDataAccessMapper;
import com.ct08SWA.restaurantservice.restaurantdataaccess.repository.RestaurantOutboxJpaRepository;
import com.ct08SWA.restaurantservice.restaurantdomaincore.event.RestaurantApprovalEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
public class RestaurantOutboxRepositoryImpl implements RestaurantOutboxRepository {
    private RestaurantOutboxJpaRepository restaurantOutboxJpaRepository;
    private RestaurantOutboxDataAccessMapper restaurantOutboxDataAccessMapper;
    public RestaurantOutboxRepositoryImpl(RestaurantOutboxDataAccessMapper restaurantOutboxDataAccessMapper, RestaurantOutboxJpaRepository restaurantOutboxJpaRepository) {
    this.restaurantOutboxDataAccessMapper = restaurantOutboxDataAccessMapper;
    this.restaurantOutboxJpaRepository = restaurantOutboxJpaRepository;
    }
    @Override
    public void save(RestaurantApprovalEvent restaurantApprovalEvent, UUID SagaId, String Topic) {
        // 1. Domain -> JPA Entity (dùng Mapper)
        RestaurantOutboxEntity entityToSave =
                restaurantOutboxDataAccessMapper.restaurantOutboxEntityToRestaurantOutbox(restaurantApprovalEvent,SagaId,Topic);
        // 2. Lưu vào CSDL
        RestaurantOutboxEntity savedEntity = restaurantOutboxJpaRepository.save(entityToSave);
    }
}
