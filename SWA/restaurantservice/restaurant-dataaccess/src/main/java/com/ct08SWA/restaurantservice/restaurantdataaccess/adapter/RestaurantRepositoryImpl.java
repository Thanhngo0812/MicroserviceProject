package com.ct08SWA.restaurantservice.restaurantdataaccess.adapter;


import com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.outputports.RestaurantRepository;
import com.ct08SWA.restaurantservice.restaurantdataaccess.mapper.RestaurantDataAccessMapper;
import com.ct08SWA.restaurantservice.restaurantdataaccess.repository.RestaurantJpaRepository;
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.Restaurant;
import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter: Triển khai (implement) RestaurantRepository (Output Port "sạch").
 * (Không Lombok)
 */
@Component
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final RestaurantDataAccessMapper restaurantDataAccessMapper;

    public RestaurantRepositoryImpl(RestaurantJpaRepository jpaRepository,
                                    RestaurantDataAccessMapper mapper) {
        this.restaurantJpaRepository = jpaRepository;
        this.restaurantDataAccessMapper = mapper;
    }

    /**
     * Triển khai hàm tìm kiếm (dùng custom query đã tạo)
     */
    @Override
    public Optional<Restaurant> findRestaurantInformation(RestaurantId restaurantId) {
        // 1. Gọi Jpa Repo (dùng hàm custom @Query)
        return restaurantJpaRepository.findRestaurantInformation(restaurantId.getValue())
                // 2. Map (dịch) kết quả (JPA Entity) sang Domain Entity (sạch)
                .map(restaurantDataAccessMapper::restaurantEntityToRestaurant);
    }

    @Override
    public Optional<Restaurant> findById(UUID restaurantId) {
        // 1. Gọi Jpa Repo (dùng hàm custom @Query)
        return restaurantJpaRepository.findRestaurantInformation(restaurantId)
                // 2. Map (dịch) kết quả (JPA Entity) sang Domain Entity (sạch)
                .map(restaurantDataAccessMapper::restaurantEntityToRestaurant);
    }

}