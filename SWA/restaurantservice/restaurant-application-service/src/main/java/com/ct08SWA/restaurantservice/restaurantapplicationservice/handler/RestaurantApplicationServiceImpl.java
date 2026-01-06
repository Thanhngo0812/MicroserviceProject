package com.ct08SWA.restaurantservice.restaurantapplicationservice.handler;

import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.outpudto.RestaurantValidationResponse;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.inputports.RestaurantApplicationService;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.outputports.RestaurantRepository;
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.Restaurant;
import com.ct08SWA.restaurantservice.restaurantdomaincore.exception.RestaurantDomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class RestaurantApplicationServiceImpl implements RestaurantApplicationService {

        private final RestaurantRepository restaurantRepository;

        @Override
        @Transactional(readOnly = true)
        public RestaurantValidationResponse findRestaurantById(UUID restaurantId) {
            Restaurant findRestaurant= restaurantRepository.findById(restaurantId)
                    .orElseThrow(() -> {
                        log.warn("Could not find restaurant with id: {}", restaurantId);
                        return new RestaurantDomainException("Restaurant with id " + restaurantId + " not found!");
                    });
            // 2. Map dữ liệu
            return RestaurantValidationResponse.builder()
                    .id(findRestaurant.getId().getValue().toString())
                    .active(findRestaurant.isActive())
                    .build();
        }
    }


