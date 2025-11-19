package com.ct08SWA.restaurantservice.restaurantdataaccess.adapter;


import com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.outputports.OrderApprovalRepository;
import com.ct08SWA.restaurantservice.restaurantdataaccess.entity.OrderApprovalEntity;
import com.ct08SWA.restaurantservice.restaurantdataaccess.mapper.RestaurantDataAccessMapper;
import com.ct08SWA.restaurantservice.restaurantdataaccess.repository.OrderApprovalJpaRepository;
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.OrderApproval;
import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.OrderId;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter: Triển khai (implement) OrderApprovalRepository (Output Port "sạch").
 * (Không Lombok)
 */
@Component
public class OrderApprovalRepositoryImpl implements OrderApprovalRepository {

    private final OrderApprovalJpaRepository orderApprovalJpaRepository;
    private final RestaurantDataAccessMapper restaurantDataAccessMapper; // Dùng chung Mapper

    public OrderApprovalRepositoryImpl(OrderApprovalJpaRepository jpaRepository,
                                       RestaurantDataAccessMapper mapper) {
        this.orderApprovalJpaRepository = jpaRepository;
        this.restaurantDataAccessMapper = mapper;
    }

    @Override
    public OrderApproval save(OrderApproval orderApproval) {
        // 1. Domain -> JPA
        OrderApprovalEntity entity = restaurantDataAccessMapper
                .orderApprovalToOrderApprovalEntity(orderApproval);
        // 2. Save
        OrderApprovalEntity savedEntity = orderApprovalJpaRepository.save(entity);
        // 3. JPA -> Domain
        return restaurantDataAccessMapper.orderApprovalEntityToOrderApproval(savedEntity);
    }

    @Override
    public Optional<OrderApproval> findByOrderId(UUID orderId) {
        // 1. Gọi Jpa Repo
        return orderApprovalJpaRepository.findByOrderId(orderId)
                // 2. Map JPA -> Domain
                .map(restaurantDataAccessMapper::orderApprovalEntityToOrderApproval);
    }
}