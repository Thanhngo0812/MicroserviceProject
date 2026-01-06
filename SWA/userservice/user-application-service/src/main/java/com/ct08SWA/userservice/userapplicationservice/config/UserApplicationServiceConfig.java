package com.ct08SWA.userservice.userapplicationservice.config;



import com.ct08SWA.userservice.userdomaincore.service.UserDomainService;
import com.ct08SWA.userservice.userdomaincore.service.UserDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class để tạo Bean cho Domain Service "sạch".
 */
@Configuration
public class UserApplicationServiceConfig {

    /**
     * Tạo Bean cho RestaurantDomainService.
     */
    @Bean
    public UserDomainService userDomainService() {
        return new UserDomainServiceImpl();
    }
}
