package com.ct08SWA.restaurantservice.restaurantapplicationservice.config;


import com.ct08SWA.restaurantservice.restaurantdomaincore.service.RestaurantDomainService;
import com.ct08SWA.restaurantservice.restaurantdomaincore.service.RestaurantDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class để tạo Bean cho Domain Service "sạch".
 */
@Configuration
public class RestaurantApplicationServiceConfig {

    /**
     * Tạo Bean cho RestaurantDomainService.
     */
    @Bean
    public RestaurantDomainService restaurantDomainService() {
        return new RestaurantDomainServiceImpl();
    }
}
