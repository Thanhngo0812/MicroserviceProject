package com.ct08SWA.paymentservice.paymentapplicationservice.config;

import com.ct08SWA.paymentservice.paymentdomaincore.service.PaymentDomainService;
import com.ct08SWA.paymentservice.paymentdomaincore.service.PaymentDomainServiceImpl; // Import implementation
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class để tạo Bean cho các lớp từ Domain Core.
 */
@Configuration
public class PaymentApplicationServiceConfig {

    /**
     * Tạo Bean cho PaymentDomainService.
     * Spring sẽ inject implementation (PaymentDomainServiceImpl) vào đây.
     * @return Một instance của PaymentDomainServiceImpl.
     */
    @Bean
    public PaymentDomainService paymentDomainService() {
        return new PaymentDomainServiceImpl(); // Tạo instance của implementation
    }


}
